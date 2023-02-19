package net.rd.bpmn.reader;

import org.camunda.bpm.engine.impl.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static net.rd.bpmn.reader.BpmnReaderApp.fail;

/**
 * JSON file getter and XML extractor
 */
public class FileGetter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String FILE_URL = "https://n35ro2ic4d.execute-api.eu-central-1.amazonaws.com/prod/engine-rest/process-definition/key/invoice/xml";
    public static final String FILE_NAME_JSON = "invoice.json";
    public static final String FILE_NAME_XML = "invoice.xml";
    public static final String JSON_ELEMENT_KEY = "bpmn20Xml";

    public FileGetter() {
    }

    public void downloadFile() throws URISyntaxException, IOException {
        File jsonFile = new File(FILE_NAME_JSON);
        if(jsonFile.exists() && jsonFile.canRead()) {
            log.debug("JSON file exists, skipping download");
            return;
        }

        try (BufferedInputStream in = new BufferedInputStream(new URL(FILE_URL).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(FILE_NAME_JSON)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
    }

    public void extractXml() throws IOException {
        File jsonFile = new File(FILE_NAME_JSON);
        if(!jsonFile.exists() || !jsonFile.canRead())
            fail("JSON file does not exist");

        File xmlFile = new File(FILE_NAME_XML);
        if(xmlFile.exists() && xmlFile.canRead())
            log.debug("XML file exist, skipping extracting");

        String jsonInput = Files.readString(Path.of(FILE_NAME_JSON));
        JSONObject jsonObject = new JSONObject(jsonInput);
        String jsonElementValue = jsonObject.getString(JSON_ELEMENT_KEY);

        Path fileXmlPath = Path.of(FILE_NAME_XML);
        Files.deleteIfExists(fileXmlPath);
        Files.createFile(fileXmlPath);
        Files.writeString(fileXmlPath, jsonElementValue, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public InputStream readXmlFile() throws IOException {
        return Files.newInputStream(Path.of(FILE_NAME_XML));
    }
}
