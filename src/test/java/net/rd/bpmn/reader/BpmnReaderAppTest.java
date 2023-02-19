package net.rd.bpmn.reader;

import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class BpmnReaderAppTest {

    private final BpmnReaderApp app = new BpmnReaderApp();
    private final InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(FileGetter.FILE_NAME_XML);

    @Test
    void testOk() {
        app.start = "approveInvoice";
        app.end = "invoiceProcessed";
        String path = app.process(stream);

        assertEquals("[approveInvoice -> invoice_approved -> prepareBankTransfer -> ServiceTask_1 -> invoiceProcessed]", path);
    }

    @Test
    void testNonExistingNode() {
        app.start = "approveInvoice";
        app.end = "abc";
        String path = app.process(stream);

        assertNull(path);
    }

    @Test
    void testNonExistingPath() {
        app.start = "invoiceProcessed";
        app.end = "approveInvoice";
        String path = app.process(stream);

        assertNull(path);
    }
}
