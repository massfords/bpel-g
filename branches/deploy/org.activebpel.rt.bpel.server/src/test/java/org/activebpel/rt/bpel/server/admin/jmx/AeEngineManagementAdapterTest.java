package org.activebpel.rt.bpel.server.admin.jmx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

public class AeEngineManagementAdapterTest {

    private static final String LOG = "1234567890";
    private AeProcessLogPart part = new AeProcessLogPart();

    @Test
    public void testSkipAndRead_allAtOnce() throws Exception {
        read(1024);
        assertEquals(LOG, part.getLog());
        assertFalse(part.isMoreAvailable());
    }

    @Test
    public void testSkipAndRead_inBits() throws Exception {
        for(int i=0; i<10; i++) {
            part.setPart(i);
            read(1);
            assertEquals(LOG.charAt(i)+"", part.getLog());
            assertTrue(part.isMoreAvailable());
        }
        // one final read where we get nothing
        part.setPart(10);
        read(1);
        assertNull(part.getLog());
        assertFalse(part.isMoreAvailable());
    }

    private void read(int bufferSize) throws IOException {
        AeEngineManagementAdapter.skipAndRead(part, new StringReader(LOG), bufferSize);
    }
    
}
