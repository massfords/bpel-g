package org.activebpel.services.jaxws;

import bpelg.services.deploy.types.UndeploymentRequest;
import org.activebpel.rt.util.AeXPathUtil;
import org.apache.commons.io.IOUtils;
import org.junit.*;
import org.w3c.dom.Document;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.Collections;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * @author markford
 *         Date: 4/6/12
 */
@Ignore
public class CorrelationIntegrationTest extends Assert {
    AeProcessFixture pfix = new AeProcessFixture();

    @Before
    public void setUp() throws Exception {
        // zip the bpel and such
        JarOutputStream jout = new JarOutputStream(new FileOutputStream("target/correlation.zip"));
        jout.putNextEntry(new ZipEntry("correlation.bpel"));
        jout.write(IOUtils.toByteArray(new FileInputStream("src/test/resources/correlation/correlation.bpel")));
        jout.closeEntry();

        jout.putNextEntry(new ZipEntry("correlation.wsdl"));
        jout.write(IOUtils.toByteArray(new FileInputStream("src/test/resources/correlation/correlation.wsdl")));
        jout.closeEntry();

        jout.putNextEntry(new ZipEntry("skip.validation"));
        jout.write("skip".getBytes());
        jout.closeEntry();

        jout.flush();
        jout.close();

        // deploy
        pfix.deploySingle(new File("target/correlation.zip"));
    }

    @Test
    public void test() throws Exception {

        Document quote = pfix.invoke(new File("src/test/resources/correlation/requestForQuote.xml"),
                "http://localhost:8080/bpel-g/services/quoteService");

        assertNotNull(quote);

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
        pfix.getDeployer().undeploy(new UndeploymentRequest().withDeploymentContainerId("correlation.zip"));
    }
}
