package bpelg.jbi.su.ode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.util.AeXmlUtil;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

public class BgCatalogBuilderTest {
    
    BgCatalogBuilder builder;

    @Before
    public void setUp() throws Exception {
        File file = new File("src/test/resources/example-su");
        assertTrue(file.isDirectory());
        
        builder = new BgCatalogBuilder(file);
        builder.build();
    }

    @Test
    public void testBuild() throws Exception {
        Collection<BgCatalogTuple> items = builder.getItems();
        assertEquals(4, items.size());
        
        Set<BgCatalogTuple> actual = new HashSet(items);
        
        Set<BgCatalogTuple> expected = new HashSet();
        expected.add(new BgCatalogTuple("project:/example-su/wsdl/example.wsdl", "wsdl/example.wsdl", "http://www.example.org/test/", IAeConstants.WSDL_NAMESPACE));
        expected.add(new BgCatalogTuple("project:/example-su/xsd/example.xsd", "xsd/example.xsd", "http://www.example.org/test/", IAeConstants.W3C_XML_SCHEMA));
        expected.add(new BgCatalogTuple("project:/example-su/xsd/example-not-used.xsd", "xsd/example-not-used.xsd", "http://www.example.org/test/", IAeConstants.W3C_XML_SCHEMA));
        expected.add(new BgCatalogTuple("project:/example-su/path/to/xsl/example.xsl", "path/to/xsl/example.xsl", null, IAeConstants.XSL_NAMESPACE));

        assertTrue(expected.containsAll(actual));
    }
    
    @Test
    public void testGetCatalog() throws Exception {
        
        // record the files that are imported into the bpel's directly
        Set<BgCatalogTuple> referenced = new HashSet();
        referenced.add(new BgCatalogTuple("project:/example-su/wsdl/example.wsdl", "wsdl/example.wsdl", "http://www.example.org/test/", IAeConstants.WSDL_NAMESPACE));
        builder.setReferenced(referenced);
        
        String expected = 
            "<catalog xmlns='http://schemas.active-endpoints.com/catalog/2006/07/catalog.xsd'>" + 
                "<otherEntry location='project:/example-su/path/to/xsl/example.xsl' classpath='path/to/xsl/example.xsl' type='http://www.w3.org/1999/XSL/Transform'/>" + 
                "<wsdlEntry location='project:/example-su/wsdl/example.wsdl' classpath='wsdl/example.wsdl' />" +
                "<schemaEntry location='project:/example-su/xsd/example.xsd' classpath='xsd/example.xsd' />" + 
            "</catalog>";
        Document expectedCatalog = AeXmlUtil.toDoc(expected);
        
        Document catalog = builder.getCatalog();
        assertNotNull(catalog);
        
        BgXmlAssert.assertXml(expectedCatalog, catalog);
    }
    
}
