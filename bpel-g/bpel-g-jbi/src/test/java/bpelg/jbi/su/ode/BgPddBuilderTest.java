package bpelg.jbi.su.ode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.util.AeXmlUtil;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import bpelg.jbi.su.ode.BgPddInfo.BgPlink;

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
        
        Document testPdd = mBuilder.createPddDocument("test.bpel.pdd", mCatalogBuilder.getItems());
        
        String expectedXml = 
            "<pdd:process xmlns:bpelns='test' xmlns:pdd='http://schemas.active-endpoints.com/pdd/2006/08/pdd.xsd' location='test.bpel' name='bpelns:test' platform='opensource'>" + 
        		"<pdd:partnerLinks>" + 
        		    "<pdd:partnerLink name='testPartnerLinkType'>" + 
        		        "<pdd:myRole allowedRoles='' binding='EXTERNAL' service='mysvc:test test'/>" + 
        		    "</pdd:partnerLink>" + 
        		"</pdd:partnerLinks>" + 
        		"<pdd:references>" + 
        		    "<pdd:wsdl location='project:/example-su/wsdl/example.wsdl' namespace='http://www.example.org/test/'/>" + 
        	   "</pdd:references>" + 
    		"</pdd:process>";
        Document expected = AeXmlUtil.toDoc(expectedXml);
        
        BgXmlAssert.assertXml(expected, testPdd, "/process[1]/partnerLinks[1]/partnerLink[1]/myRole[1]/@service");
    }

    @Test
    public void testGetPdd_testInvokeBpel() throws Exception {
        
        Document testPdd = mBuilder.createPddDocument("testInvoke.bpel.pdd", mCatalogBuilder.getItems());
        
        String expectedXml = 
            "<pdd:process xmlns:bpelns='test' xmlns:pdd='http://schemas.active-endpoints.com/pdd/2006/08/pdd.xsd' location='testInvoke.bpel' name='bpelns:testInvoke' platform='opensource'>" + 
                "<pdd:partnerLinks>" + 
                    "<pdd:partnerLink name='testPartnerLinkType'>" + 
                        "<pdd:myRole allowedRoles='' binding='EXTERNAL' service='mysvc:test testInvoke'/>" + 
                    "</pdd:partnerLink>" + 
                    "<pdd:partnerLink name='testPartnerLinkType2'>" + 
                        "<pdd:partnerRole endpointReference='static' invokeHandler='default:Service'>" + 
                            "<wsa:EndpointReference xmlns:wsa='http://www.w3.org/2005/08/addressing' xmlns:psvc='http://www.example.org/test/'>" + 
                                "<wsa:Address>None</wsa:Address>" + 
                                "<wsa:Metadata>" + 
                                    "<wsa:ServiceName PortName='testInvokeBpelReceiver'>psvc:test2</wsa:ServiceName>" + 
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
        
        BgXmlAssert.assertXml(expected, testPdd, "/process[1]/partnerLinks[1]/partnerLink[1]/myRole[1]/@service");
    }
}
