package org.activebpel.services.jaxws;

import bpelg.services.deploy.types.UndeploymentRequest;
import org.activebpel.rt.util.AeXPathUtil;
import org.apache.commons.io.IOUtils;
import org.junit.*;
import org.w3c.dom.Document;

import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Collections;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * @author markford
 *         Date: 4/6/12
 */
public class CorrelationIntegrationTest extends Assert {
    AeProcessFixture pfix = new AeProcessFixture();

    @Before
    public void setUp() throws Exception {

        // zip the bpel and such
        JarOutputStream jout = new JarOutputStream(new FileOutputStream("target/correlation.zip"));

        addToZip(jout, new File("src/test/resources/correlation/correlation.bpel"),
                        new File("src/test/resources/correlation/correlation.wsdl"),
                        new File("src/test/resources/correlation/deploy.xml"));
        jout.flush();
        jout.close();

        // deploy
        pfix.deploySingle(new File("target/correlation.zip"));
    }

    @Test
    public void test() throws Exception {

        // invoke the service and get our quote back
        Document quote = pfix.invoke(new File("src/test/resources/correlation/requestForQuote.xml"),
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
        pfix.getDeployer().undeploy(new UndeploymentRequest().withDeploymentContainerId("correlation.zip"));
    }

    private void addToZip(JarOutputStream jout, File...files) throws IOException {
        for(File file : files) {
            jout.putNextEntry(new ZipEntry(file.getName()));
            jout.write(IOUtils.toByteArray(new FileInputStream(file)));
            jout.closeEntry();
        }
    }

}
