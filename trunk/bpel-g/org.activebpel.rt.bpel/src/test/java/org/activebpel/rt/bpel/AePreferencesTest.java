package org.activebpel.rt.bpel;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

public class AePreferencesTest {

	
	// FIXME add some real tests here
	
	@Test
	public void serialize() throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		AePreferences.root().exportSubtree(bout);
		String s = new String(bout.toByteArray());
		System.out.println(s);
	}
}
