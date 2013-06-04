package bpelg.packaging.ode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.XMLConstants;

import org.activebpel.rt.IAeConstants;
import org.junit.Before;
import org.junit.Test;

import bpelg.services.deploy.types.catalog.Catalog;

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
        
        Set<BgCatalogTuple> actual = new HashSet<>(items);
        
        Set<BgCatalogTuple> expected = new HashSet<>();
        expected.add(new BgCatalogTuple("project:/example-su/wsdl/example.wsdl", "wsdl/example.wsdl", "http://www.example.org/test/", IAeConstants.WSDL_NAMESPACE));
        expected.add(new BgCatalogTuple("project:/example-su/xsd/example.xsd", "xsd/example.xsd", "http://www.example.org/test/", XMLConstants.W3C_XML_SCHEMA_NS_URI));
        expected.add(new BgCatalogTuple("project:/example-su/xsd/example-not-used.xsd", "xsd/example-not-used.xsd", "http://www.example.org/test/", XMLConstants.W3C_XML_SCHEMA_NS_URI));
        expected.add(new BgCatalogTuple("project:/example-su/path/to/xsl/example.xsl", "path/to/xsl/example.xsl", null, IAeConstants.XSL_NAMESPACE));

        assertTrue(expected.containsAll(actual));
    }
    
    @Test
    public void testGetCatalog() throws Exception {
        
        // record the files that are imported into the bpel's directly
        Set<BgCatalogTuple> referenced = new HashSet<>();
        referenced.add(new BgCatalogTuple("project:/example-su/wsdl/example.wsdl", "wsdl/example.wsdl", "http://www.example.org/test/", IAeConstants.WSDL_NAMESPACE));
        builder.setReferenced(referenced);
        
        Catalog catalog = builder.getCatalog();
        assertNotNull(catalog);
        assertEquals(1, catalog.getOtherEntry().size());
        assertEquals("project:/example-su/path/to/xsl/example.xsl", catalog.getOtherEntry().get(0).getLocation());
        assertEquals("path/to/xsl/example.xsl", catalog.getOtherEntry().get(0).getClasspath());
        assertEquals("http://www.w3.org/1999/XSL/Transform", catalog.getOtherEntry().get(0).getTypeURI());
        assertEquals(1, catalog.getWsdlEntry().size());
        assertEquals("project:/example-su/wsdl/example.wsdl", catalog.getWsdlEntry().get(0).getLocation());
        assertEquals("wsdl/example.wsdl", catalog.getWsdlEntry().get(0).getClasspath());
        assertEquals(1, catalog.getSchemaEntry().size());
        assertEquals("project:/example-su/xsd/example.xsd", catalog.getSchemaEntry().get(0).getLocation());
        assertEquals("xsd/example.xsd", catalog.getSchemaEntry().get(0).getClasspath());
        
    }
    
    @Test
    public void testGetCatalog_nothingReferenced() throws Exception {
    	Catalog catalog = builder.getCatalog();
        assertNotNull(catalog);
        
        // if nothing is referenced at all, deploy everything 
        
        assertEquals(4, catalog.getOtherEntry().size() + catalog.getWsdlEntry().size() + catalog.getSchemaEntry().size());
    }
    
}
