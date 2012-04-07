package bpelg.process.tests.correlation;

import bpelg.services.deploy.types.UndeploymentRequest;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.services.jaxws.AeProcessFixture;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.util.Collections;

/**
 * @author markford
 *         Date: 4/6/12
 */
public class CorrelationIntegrationTest extends Assert {
    AeProcessFixture pfix = new AeProcessFixture();

    @Before
    public void setUp() throws Exception {
        // deploy
        pfix.deploySingle(new File("target/dependency/correlation-test.jar"));
    }

    @Test
    public void test() throws Exception {

        String request =
                "<requestForQuote xmlns='http://www.example.org/correlation/'>" +
                "    <customerId>junit</customerId>" +
                "    <productId>100</productId>" +
                "    <quantity>5</quantity>" +
                "</requestForQuote>";

        // invoke the service and get our quote back
        Document quote = pfix.invoke(new StreamSource(new StringReader(request)),
                "http://localhost:8080/bpel-g/services/quoteService");
        assertNotNull(quote);

        // send another message saying that we agree to the quote
        String quoteId = AeXPathUtil.selectText(quote, "//q:quoteId",
                Collections.singletonMap("q", "http://www.example.org/correlation/"));

        String xml = "<customerResponseToQuote xmlns='http://www.example.org/correlation/'>" +
                        "<quoteId>" + quoteId + "</quoteId>" +
                        "<accept>true</accept>" +
                     "</customerResponseToQuote>";

        Document confirmed = pfix.invoke(new StreamSource(new StringReader(xml)),
                "http://localhost:8080/bpel-g/services/quoteService");

        assertNotNull(confirmed);
    }

    @After
    public void tearDown() throws Exception {
        pfix.getDeployer().undeploy(new UndeploymentRequest().withDeploymentContainerId("correlation-test.jar"));
    }
}
