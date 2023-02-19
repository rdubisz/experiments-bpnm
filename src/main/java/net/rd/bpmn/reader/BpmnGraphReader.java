package net.rd.bpmn.reader;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.algorithms.shortestpath.ShortestPathUtils;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

/**
 * BPMN model reader
 */
public class BpmnGraphReader {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final InputStream stream;
    private BpmnModelInstance model;

    private Graph<String, Integer> graph;

    public BpmnGraphReader(InputStream stream) {
        this.stream = stream;
        readModel();
    }

    protected void readModel() {
        model = Bpmn.readModelFromStream(stream);
    }

    public Graph<String, Integer> readGraph() {
        log.debug("Reading graph");
        int edgeId = 0;

        ModelElementType type = model.getModel().getType(FlowNode.class);
        Collection<ModelElementInstance> elements = model.getModelElementsByType(type);

        graph = new SparseGraph<>();

        // Create vertices
        for (ModelElementInstance element : elements) {
            FlowNode flowElement = (FlowNode) element;
            graph.addVertex(flowElement.getId());
        }

        //Create edges
        for (ModelElementInstance element : elements) {
            FlowNode flowElement = (FlowNode) element;
            String sourceId = flowElement.getId();
            Collection<SequenceFlow> outgoingNodes = flowElement.getOutgoing();
            for (SequenceFlow outgoing : outgoingNodes) {
                FlowNode target = outgoing.getTarget();
                String targetId = target.getId();
                graph.addEdge(edgeId++, sourceId, targetId, EdgeType.DIRECTED);
            }
        }

        return graph;
    }

    public String findPath(String start, String end) {
        DijkstraShortestPath<String, Integer> sp = new DijkstraShortestPath<>(graph);
        List<Integer> edges = ShortestPathUtils.getPath(graph, sp, start, end);
        if(edges.isEmpty())
            return null;
        StringJoiner sj = new StringJoiner(" -> ", "[", "]");
        for (Integer edge : edges) {
            sj.add(graph.getSource(edge));
        }
        sj.add(end);

        return sj.toString();
    }

}
