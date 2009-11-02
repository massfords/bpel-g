package bpelg.jbi.su.ode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;

import org.activebpel.rt.IAeConstants;
import org.junit.Test;

import bpelg.jbi.su.ode.BgCatalogBuilder;
import bpelg.jbi.su.ode.BgCatalogTuple;

public class BgCatalogBuilderTest {

    @Test
    public void testBuild() throws Exception {
        File file = new File("src/test/resources/example-su");
        assertTrue(file.isDirectory());
        
        BgCatalogBuilder builder = new BgCatalogBuilder(file);
        builder.build();
        Collection<BgCatalogTuple> items = builder.getItems();
        assertEquals(3, items.size());
        
        BgCatalogTuple expectedWSDL = new BgCatalogTuple("wsdl/example.wsdl", "project://example-su/wsdl/example.wsdl", "http://www.example.org/test/", IAeConstants.WSDL_NAMESPACE);
        BgCatalogTuple expectedXSD = new BgCatalogTuple("xsd/example.xsd", "project://example-su/xsd/example.xsd", "http://www.example.org/test/", IAeConstants.W3C_XML_SCHEMA);
        BgCatalogTuple expectedXSL = new BgCatalogTuple("path/to/xsl/example.xsl", "project://example-su/path/to/xsl/example.xsl", null, IAeConstants.XSL_NAMESPACE);
        
        for(BgCatalogTuple tuple : items) {
            if (tuple.type.equals(IAeConstants.WSDL_NAMESPACE)) { 
                assertEquals(expectedWSDL, tuple);
            } else if (tuple.type.equals(IAeConstants.W3C_XML_SCHEMA)) {
                assertEquals(expectedXSD, tuple);
            } else if (tuple.type.equals(IAeConstants.XSL_NAMESPACE)) {
                assertEquals(expectedXSL, tuple);
            } else {
                fail("unexpected type: " + tuple.type);
            }
        }
    }
}
