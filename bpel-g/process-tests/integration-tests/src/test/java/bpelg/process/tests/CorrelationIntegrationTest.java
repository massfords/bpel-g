package bpelg.process.tests;

import bpelg.services.deploy.types.DeploymentResponse;
import bpelg.services.deploy.types.UndeploymentRequest;
import bpelg.services.preferences.types.GetPreferencesRequest;
import bpelg.services.preferences.types.PreferencesType;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.services.jaxws.AeProcessFixture;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author markford
 *         Date: 4/6/12
 */
@RunWith(Parameterized.class)
public class CorrelationIntegrationTest extends Assert {

    private static final AeProcessFixture pfix = new AeProcessFixture();
    private final long processRelease;
    private final long sleepTime;
    private long originalRelease;

    @BeforeClass
    public static void deployProcess() throws Exception {
        // deploy
        DeploymentResponse response = pfix.deployAll(new File("target/dependency/correlation-test.jar"));
        DeploymentResponse.DeploymentInfo info = response.getDeploymentInfo().get(0);
        assertNotNull(info.getLog());
        System.out.println(info.getLog());
    }

    @AfterClass
    public static void undeployProcess() throws Exception {
        pfix.getDeployer().undeploy(new UndeploymentRequest().withDeploymentContainerId("correlation-test.jar"));
    }

    @Parameterized.Parameters
    public static List<Object[]> params() throws Exception {
        List<Object[]> p = new ArrayList<Object[]>();
        // no sleep, process stays in memory
        p.add(new Object[] {TimeUnit.SECONDS.toMillis(10), 0});
        // some sleep, process stays in memory
        p.add(new Object[] {TimeUnit.SECONDS.toMillis(10), TimeUnit.SECONDS.toMillis(1)});
        // sleep past the persistence time to force a write to disk
        p.add(new Object[] {TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(5)});
        return p;
    }

    public CorrelationIntegrationTest(long processRelease, long sleepTime) {
        this.processRelease = processRelease;
        this.sleepTime = sleepTime;
    }

    @Before
    public void setUp() throws Exception {
        // update the process lag
        String catalina_port = pfix.getCatalinaPort();
        PreferencesType prefs = pfix.getPreferencesService().getPreferences(new GetPreferencesRequest());
        originalRelease = prefs.getProcesses().getReleaseLag();
        prefs.getProcesses().setReleaseLag(processRelease);
        pfix.getPreferencesService().setPreferences(prefs);
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

        Thread.sleep(sleepTime);

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
        // restore the release
        PreferencesType prefs = pfix.getPreferencesService().getPreferences(new GetPreferencesRequest());
        prefs.getProcesses().setReleaseLag(originalRelease);
        pfix.getPreferencesService().setPreferences(prefs);
    }
}
