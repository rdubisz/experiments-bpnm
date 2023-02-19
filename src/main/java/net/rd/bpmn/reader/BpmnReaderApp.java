package net.rd.bpmn.reader;

import edu.uci.ics.jung.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;


/**
 * BPMN invoice reader
 */
public class BpmnReaderApp {

    private static final Logger log = LoggerFactory.getLogger(BpmnReaderApp.class);

    protected String start;
    protected String end;
    public static void main(String[] args) {
        log.debug("BPMN graph reader starting");

        BpmnReaderApp app = new BpmnReaderApp();
        app.parseArgs(args);

        InputStream inputStream = app.fileOps();
        String path = app.process(inputStream);
        if(path == null)
            fail("No path between " + app.start + " and " + app.end);
        log.info("The path from {} to {} is: {}", app.start, app.end, path);

        log.debug("Exited");
    }

    public static void fail(String error) {
        log.error(error);
        System.exit(-1);
    }

    public String process(InputStream stream) {
        BpmnGraphReader bpmnGraphReader = new BpmnGraphReader(stream);
        Graph<String, Integer> graph = bpmnGraphReader.readGraph();
        log.debug(graph.toString());

        String path = bpmnGraphReader.findPath(start, end);
        return path;
    }

    private InputStream fileOps() {
        FileGetter fg = new FileGetter();
        try {
            fg.downloadFile();
            fg.extractXml();
            return fg.readXmlFile();
        } catch (URISyntaxException | IOException e) {
            fail(e.getMessage());
            return null;
        }
    }


    private void parseArgs(String[] args) {
        if(args.length == 0)
            fail("Start and end node IDs should be given as parameters");

        start = args[0];
        end = args[1];
    }

}
