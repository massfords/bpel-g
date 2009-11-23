package bpelg.jbi.su.ode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Set;

import org.activebpel.rt.bpel.def.AeImportDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.junit.Before;
import org.junit.Test;

public class BgReferencedPathsTest {

    private BgPddBuilder mBuilder;

    @Before
    public void setUp() throws Exception {
        File file = new File("src/test/resources/example-su");
        assertTrue(file.isDirectory());
        mBuilder = new BgPddBuilder(file);
    }
    
    @Test
    public void testGetReferencedPaths() throws Exception {
        AeProcessDef processDef = new AeProcessDef();
        AeImportDef importDef = new AeImportDef();
        importDef.setLocation("wsdl/example.wsdl");
        processDef.addImportDef(importDef);
        Set<String> paths = mBuilder.getReferencedPaths(processDef);

        assertEquals(1, paths.size());
        assertTrue(paths.contains("wsdl/example.wsdl"));
    }
}
