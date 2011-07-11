package org.activebpel.rt.bpel;

import java.io.ByteArrayOutputStream;
import java.util.ServiceLoader;
import java.util.prefs.PreferencesFactory;

import org.junit.Test;

public class AePreferencesTest {

	@Test
	public void file() throws Exception {
	    ServiceLoader<PreferencesFactory> sl = ServiceLoader.load(PreferencesFactory.class);
	    for(PreferencesFactory pf : sl) {
	        System.out.println(pf.getClass().getName());
	    }
	}
	
	@Test
	public void serialize() throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		AePreferences.root().exportSubtree(bout);
		String s = new String(bout.toByteArray());
		System.out.println(s);
	}
}
