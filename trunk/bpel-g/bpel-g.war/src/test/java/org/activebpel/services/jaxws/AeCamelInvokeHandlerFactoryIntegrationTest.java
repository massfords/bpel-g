package org.activebpel.services.jaxws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.Assert;

import org.activebpel.rt.xml.AeXMLParserBase;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import bpelg.services.deploy.types.UndeploymentRequest;
import bpelg.services.urnresolver.types.AddMappingRequest;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;
import de.schlichtherle.truezip.file.TFileWriter;

public class AeCamelInvokeHandlerFactoryIntegrationTest extends Assert {

    AeProcessFixture pfix = new AeProcessFixture();

    @Before
    public void setUp() throws Exception {
        pfix.getResolver().addMapping(new AddMappingRequest().withName(
        "urn:x-vos:loancompany").withValue(
        "http://localhost:" + pfix.getCatalinaPort() + "/bpel-g/services/${urn.4}"));
        
        // copy the full bpr to a temp location in target
        IOUtils.copy(new FileInputStream("src/test/resources/loanProcessCompleted.bpr"), new FileOutputStream("target/camel-invoke.zip"));
        // read the pdd
        TFile pdd = new TFile("target/camel-invoke.zip/META-INF/pdd/loan_approval_integrated/deploy/loanProcessCompleted.pdd");
        InputStream in = new TFileInputStream(pdd);
        // xform
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer(new StreamSource("src/test/resources/camel-invoke/change-risk-assessment-epr.xsl"));
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        t.transform(new StreamSource(in), result);
        System.out.println(sw);
        in.close();
        // update the pdd
        Writer w = new TFileWriter(pdd);
        w.write(sw.toString());
        w.flush();
        w.close();
        
        // add the META-INF/applicationContext.xml
        TFile context = new TFile("target/camel-invoke.zip/META-INF/applicationContext.xml");
        TFile.cp(new File("src/test/resources/camel-invoke/META-INF/applicationContext.xml"), context);
    }
    
    @After
    public void tearDown() throws Exception {
        // undeploy
        pfix.getDeployer().undeploy(new UndeploymentRequest().withDeploymentContainerId("loanProcessCompleted.bpr"));
        pfix.getDeployer().undeploy(new UndeploymentRequest().withDeploymentContainerId("loanApproval.bpr"));
    }
    
    @Test
    public void deployBPR() throws Exception {
        pfix.deploySingle(new File("src/test/resources/loanProcessCompleted.bpr"));
        pfix.deploySingle(new File("src/test/resources/loanApproval.bpr"));

        // run a simple test
        Document response = pfix.invoke(new File("src/test/resources/credInfo_Smith15001.xml"), 
                "http://localhost:8080/bpel-g/services/LoanProcessCompletedService");
        Document expectedNode = (Document) pfix.toNode(new StreamSource(new FileInputStream(
                "src/test/resources/expected-response.xml")));

        System.out.println(AeXMLParserBase.documentToString(response));

        pfix.assertXMLIgnorePrefix("failed to match", expectedNode, response);
    }
    
}
