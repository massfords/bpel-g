package bpelg.process.tests.camel;

import bpelg.services.deploy.types.UndeploymentRequest;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.services.jaxws.AeProcessFixture;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Node;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;

/**
 * This test adapts a request-response BPEL process into an asynchronous exchange with JMS bindings.
 */
@Ignore
public class AeCamelJMSIntegrationTest extends CamelTestSupport {
    private final AeProcessFixture pfix = new AeProcessFixture();
    private final BrokerService brokerSvc = new BrokerService();
    private CamelFixture cfix;


    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        brokerSvc.setBrokerName("TestBroker");
        brokerSvc.addConnector("tcp://localhost:61616?broker.persistent=false");
        brokerSvc.start();

        cfix = new CamelFixture("activemq:queue:bpel-outbound");
        cfix.start();

        // deploy the zip
        pfix.deploySingle(new File("target/dependency/camel-activemq-test-bundle.zip"));
    }

    /**
     * @throws Exception
     */
    @Test
    public void deploy() throws Exception {
        // prepare payload
        String body = "<createProcess xmlns='http://www.example.org/simple/'>" +
                "   <message>hello from junit</message>" +
                "</createProcess>";

        cfix.send("activemq:queue:bpel-inbound", body);

        // assert response is received - hopefully it'll complete w/in 20 seconds
        cfix.assertIsSatisfied();

        String response = cfix.getReceivedPayload(0);
        assertNotNull(response);
        Node doc = pfix.toNode(new StreamSource(new StringReader(response)));
        assertEquals("hello from junit", AeXPathUtil.selectText(doc, "//*[local-name() = 'message']", null));
    }

    /**
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        brokerSvc.stop();
        // undeploy
        pfix.getDeployer().undeploy(new UndeploymentRequest().withDeploymentContainerId("camel-activemq-test-bundle.zip"));
    }

}
