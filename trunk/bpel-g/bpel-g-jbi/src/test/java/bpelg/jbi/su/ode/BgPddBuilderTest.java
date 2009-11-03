package bpelg.jbi.su.ode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.Test;

import bpelg.jbi.su.ode.BgPddInfo.BgPlink;

public class BgPddBuilderTest {
    
    @Test
    public void testBuild() throws Exception {
        File file = new File("src/test/resources/example-su");
        assertTrue(file.isDirectory());
        
        BgPddBuilder builder = new BgPddBuilder(file);
        builder.build();
        
        Map<QName,BgPddInfo> deployments = builder.getDeployments();
        
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
}
