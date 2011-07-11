package bpelg.packaging.ode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class BgGatherImportsTest {

    @Test
    public void testGatherImports() throws Exception {
        File root = new File("src/test/resources/gatherImports");
        BgCatalogBuilder builder = new BgCatalogBuilder(root);
        builder.findLocations(new File(root, "wsdl/example.wsdl"));
        Set<String> actual = builder.getLocations();
        
        System.out.println(actual);
        
        Set<String> expected = new HashSet<String>();
        expected.add("wsdl/example.wsdl");
        expected.add("wsdl/messages.wsdl");
        expected.add("wsdl/port-types.wsdl");
        expected.add("xsd/example.xsd");
        expected.add("xsd/example-imported.xsd");
        expected.add("xsd/example-included.xsd");
        assertTrue(expected.containsAll(actual));
        assertEquals(expected.size(), actual.size());
        
    }
}
