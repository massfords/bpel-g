package bpelg.process.tests;

import bpelg.services.deploy.types.DeploymentResponse;
import bpelg.services.deploy.types.MessageType;
import bpelg.services.deploy.types.Msg;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.services.jaxws.AeProcessFixture;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ma21633
 *         Date: 12/21/12
 */
public class BetsyBugReportsIntegrationTest extends Assert {
    private final AeProcessFixture pfix = new AeProcessFixture();

    @Test
    public void test() throws Exception {
        //target/dependency/
//        String pathname = "../betsy-bug-reports/target/betsy-bug-reports-5.3-SNAPSHOT.jar";
        String pathname = "target/dependency/betsy-bug-reports.jar";
        DeploymentResponse response = pfix.deploy(new File(pathname));
        assertNotNull(response);

        Map<String,DeploymentResponse.DeploymentInfo> infos = new HashMap<String,DeploymentResponse.DeploymentInfo>();
        for(DeploymentResponse.DeploymentInfo info : response.getDeploymentInfo()) {
            infos.put(info.getName(), info);
        }

        assertStartError(infos.get("start-error.bpel.pdd"));
        assertDupeVar(infos.get("dupe-variable.bpel.pdd"));
        assertTrue(infos.get("pick-onAlarm-for.bpel.pdd").isDeployed());
        assertTrue(infos.get("pick-onAlarm-until.bpel.pdd").isDeployed());
        assertCorrelation("http://localhost:8080/bpel-g/services/betsyQuoteService");
        assertCorrelation("http://localhost:8080/bpel-g/services/betsyQuoteUntilService");
        assertTrue(infos.get("wait.bpel.pdd").isDeployed());
        assertWait();
        assertTrue(infos.get("event-onAlarm-for.bpel.pdd").isDeployed());
        assertTrue(infos.get("event-onAlarm-until.bpel.pdd").isDeployed());
        assertCorrelation("http://localhost:8080/bpel-g/services/betsyEventService");
        assertCorrelation("http://localhost:8080/bpel-g/services/betsyEventUntilService");
        assertTrue(infos.get("termination.bpel.pdd").isDeployed());
        assertCorrelation("http://localhost:8080/bpel-g/services/betsyTermService");
    }

    private void assertWait() throws Exception {
        String body = "<createProcess xmlns='http://www.example.org/betsy/'>" +
                "   <message>hello from junit</message>" +
                "</createProcess>";

        Document quote = pfix.invoke(new StreamSource(new StringReader(body)), "http://localhost:8080/bpel-g/services/betsyWaitService");
        assertNotNull(quote);
    }

    private void assertCorrelation(String endpoint) throws Exception {
        String request =
                "<requestForQuote xmlns='http://www.example.org/correlation/'>" +
                        "    <customerId>junit</customerId>" +
                        "    <productId>100</productId>" +
                        "    <quantity>5</quantity>" +
                        "</requestForQuote>";

        // invoke the service and get our quote back
        Document quote = pfix.invoke(new StreamSource(new StringReader(request)), endpoint);
        assertNotNull(quote);

        Thread.sleep(10*1000);

        // send another message checking on the status
        Map<String, String> nsMap = Collections.singletonMap("q", "http://www.example.org/correlation/");
        String quoteId = AeXPathUtil.selectText(quote, "//q:quoteId",
                nsMap);

        String xml = "<statusRequest xmlns='http://www.example.org/correlation/'>" +
                "<quoteId>" + quoteId + "</quoteId>" +
                "</statusRequest>";

        Document status = pfix.invoke(new StreamSource(new StringReader(xml)),
                endpoint);

        int s = AeXPathUtil.selectInt(status, "//q:status", nsMap);
        assertEquals(2, s);
    }

    private void assertStartError(DeploymentResponse.DeploymentInfo info) {
        assertFalse(info.isDeployed());
        boolean foundError = false;
        for(Msg m : info.getLog().getMsg()) {
            if (m.getType()== MessageType.ERROR) {
                foundError |= m.getValue().contains("start activity");
            }
        }
        assertTrue(foundError);
    }

    private void assertDupeVar(DeploymentResponse.DeploymentInfo info) {
        assertFalse(info.isDeployed());
        boolean foundError = false;
        for(Msg m : info.getLog().getMsg()) {
//            System.out.println(m.getValue());
            if (m.getType()== MessageType.ERROR) {
                foundError |= m.getValue().contains("A variable with name processCreated already exists");
            }
        }
        assertTrue(foundError);
    }
}
