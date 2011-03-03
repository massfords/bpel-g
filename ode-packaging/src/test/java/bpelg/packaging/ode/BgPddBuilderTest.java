package bpelg.packaging.ode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMResult;

import org.activebpel.rt.util.AeXmlUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

import bpelg.packaging.ode.BgPddInfo.BgPlink;
import bpelg.services.deploy.types.pdd.Pdd;

public class BgPddBuilderTest {
    
    private BgPddBuilder mBuilder;
    private BgCatalogBuilder mCatalogBuilder;
    
    @Before
    public void setUp() throws Exception {
        File file = new File("src/test/resources/example-su");
        assertTrue(file.isDirectory());
        
        mBuilder = new BgPddBuilder(file);
        mBuilder.build();
        
        mCatalogBuilder = new BgCatalogBuilder(file);
        mCatalogBuilder.build();
    }
    
    @Test
    public void testBuild() throws Exception {
        Map<QName,BgPddInfo> deployments = mBuilder.getDeployments();
        
        assertEquals(3, deployments.size());

        // test
        BgPddInfo testProc = deployments.get(new QName("urn:bpelg:test", "test"));
        assertNotNull(testProc);
        BgPlink bgPlink = testProc.getBgPlink("testPartnerLinkType");
        assertEquals(new QName("http://www.example.org/test/", "test"), bgPlink.myService);
        assertEquals("test", bgPlink.myEndpoint);

        // testInvoke
        BgPddInfo testInvoke = deployments.get(new QName("urn:bpelg:test", "testInvoke"));
        assertNotNull(testInvoke);
        bgPlink = testInvoke.getBgPlink("testPartnerLinkType");
        assertEquals(new QName("http://www.example.org/test/", "test"), bgPlink.myService);
        assertEquals("testInvoke", bgPlink.myEndpoint);
        bgPlink = testInvoke.getBgPlink("testPartnerLinkType2");
        assertEquals(new QName("http://www.example.org/test/", "test2"), bgPlink.partnerService);
        assertEquals("testInvokeBpelReceiver", bgPlink.partnerEndpoint);

        // testInvokeBpelReceiver
        BgPddInfo testInvokeBpelReceiver = deployments.get(new QName("urn:bpelg:test", "testInvokeBpelReceiver"));
        assertNotNull(testProc);
        bgPlink = testInvokeBpelReceiver.getBgPlink("testPartnerLinkType2");
        assertEquals(new QName("http://www.example.org/test/", "test2"), bgPlink.myService);
        assertEquals("testInvokeBpelReceiver", bgPlink.myEndpoint);
    }
    
    @Test
    public void testGetPdd_testBpel() throws Exception {
        
        Pdd testPdd = mBuilder.createPddDocument("test.bpel.pdd", mCatalogBuilder.getItems());
        
        String expectedXml = 
            "<pdd:process xmlns:bpelns='test' xmlns:pdd='http://schemas.active-endpoints.com/pdd/2006/08/pdd.xsd' location='test.bpel' name='ns2:test' platform='opensource'>" + 
        		"<pdd:partnerLinks>" + 
        		    "<pdd:partnerLink name='testPartnerLinkType'>" + 
        		        "<pdd:myRole allowedRoles='' binding='MSG' service='test'/>" + 
        		    "</pdd:partnerLink>" + 
        		"</pdd:partnerLinks>" + 
        		"<pdd:references>" + 
        		    "<pdd:wsdl location='project:/example-su/wsdl/example.wsdl' namespace='http://www.example.org/test/'/>" + 
        	   "</pdd:references>" + 
    		"</pdd:process>";
        Document expected = AeXmlUtil.toDoc(expectedXml);
        
        Document actual = serialize(testPdd);
        BgXmlAssert.assertXml(expected, actual, "/process[1]/partnerLinks[1]/partnerLink[1]/myRole[1]/@service");
    }

    // FIXME deploy restore this test
    @Ignore
    public void testGetPdd_testInvokeBpel() throws Exception {
        
        Pdd testPdd = mBuilder.createPddDocument("testInvoke.bpel.pdd", mCatalogBuilder.getItems());
        
        String expectedXml = 
            "<pdd:process xmlns:bpelns='test' xmlns:pdd='http://schemas.active-endpoints.com/pdd/2006/08/pdd.xsd' location='testInvoke.bpel' name='ns2:testInvoke' platform='opensource'>" + 
                "<pdd:partnerLinks>" + 
                    "<pdd:partnerLink name='testPartnerLinkType'>" + 
                        "<pdd:myRole allowedRoles='' binding='MSG' service='testInvoke'>" +
                            "<wsp:Policy xmlns:abp='http://schemas.active-endpoints.com/ws/2005/12/policy' xmlns:wsp='http://schemas.xmlsoap.org/ws/2004/09/policy'>" + 
                                "<abp:Validation direction='none'/>" + 
                            "</wsp:Policy>" + 
                        "</pdd:myRole>" + 
                    "</pdd:partnerLink>" + 
                    "<pdd:partnerLink name='testPartnerLinkType2'>" + 
                        "<pdd:partnerRole endpointReference='static' invokeHandler='default:Service'>" + 
                            "<wsa:EndpointReference xmlns:wsa='http://www.w3.org/2005/08/addressing' xmlns:psvc='http://www.example.org/test/'>" + 
                                "<wsa:Address>None</wsa:Address>" + 
                                "<wsa:Metadata>" + 
                                    "<wsa:ServiceName PortName='testInvokeBpelReceiver'>psvc:test2</wsa:ServiceName>" + 
//                                    "<wsp:Policy xmlns:abp='http://schemas.active-endpoints.com/ws/2005/12/policy' xmlns:wsp='http://schemas.xmlsoap.org/ws/2004/09/policy'>" + 
//                                        "<abp:Validation direction='both'/>" + 
//                                    "</wsp:Policy>" + 
                                "</wsa:Metadata>" + 
                           "</wsa:EndpointReference>" + 
                        "</pdd:partnerRole>" + 
                    "</pdd:partnerLink>" + 
                "</pdd:partnerLinks>" + 
                "<pdd:references>" + 
                    "<pdd:wsdl location='project:/example-su/wsdl/example.wsdl' namespace='http://www.example.org/test/'/>" + 
                "</pdd:references>" + 
            "</pdd:process>"; 
        Document expected = AeXmlUtil.toDoc(expectedXml);
        
        Document actual = serialize(testPdd);
        
        BgXmlAssert.assertXml(expected, actual, "/process[1]/partnerLinks[1]/partnerLink[1]/myRole[1]/@service");
    }

	protected Document serialize(Pdd testPdd) throws JAXBException {
		Document actual = null;
        JAXBContext context = JAXBContext.newInstance(Pdd.class);
        Marshaller m = context.createMarshaller();
        DOMResult result = new DOMResult();
        m.marshal(testPdd, result);
        actual = (Document) result.getNode();
		return actual;
	}
}
