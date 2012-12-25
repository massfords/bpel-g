package org.activebpel.rt.wsdl.def.castor;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.exolab.castor.xml.schema.Schema;
import org.junit.Test;

public class AeSchemaParserUtilTest {

    @Test
    public void loadSchema() throws Exception {
        Schema s = AeSchemaParserUtil.loadSchema(new File("src/test/resources/ws-humantask.xsd"));
        assertNotNull(s);
    }
}
