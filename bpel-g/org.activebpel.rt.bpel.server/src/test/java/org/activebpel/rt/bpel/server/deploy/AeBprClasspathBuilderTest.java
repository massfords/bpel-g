package org.activebpel.rt.bpel.server.deploy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class AeBprClasspathBuilderTest {
	@Test
	public void build() throws Exception {
		File dir = new File("src/test/resources/dummy-project/");
		URL url = dir.toURI().toURL();
		URLClassLoader cl = AeBprClasspathBuilder.build(url, getClass().getClassLoader());
		assertNotNull(cl);
		URL[] path = cl.getURLs();
		assertEquals(3, path.length);
		Set<URL> urls = new HashSet<URL>();
		urls.addAll(Arrays.asList(path));
		assertEquals(3, urls.size());
		assertTrue(urls.contains(new File(dir, "foo.jar").toURI().toURL()));
		assertTrue(urls.contains(new File(dir, "bar.jar").toURI().toURL()));
		assertTrue(urls.contains(new URL(dir.toURI().toURL(), ".")));
		
		assertSame(Thread.currentThread().getContextClassLoader(), cl.getParent());
	}
}
