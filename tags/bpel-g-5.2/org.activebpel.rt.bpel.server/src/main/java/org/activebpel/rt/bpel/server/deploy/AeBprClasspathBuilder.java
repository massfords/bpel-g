package org.activebpel.rt.bpel.server.deploy;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;

public class AeBprClasspathBuilder {
	
	public static URLClassLoader build(URL aURL) throws MalformedURLException {
		return build(aURL, Thread.currentThread().getContextClassLoader());
	}
	
	public static URLClassLoader build(URL aURL, ClassLoader aParent) throws MalformedURLException {
	   List<URL> urls = new LinkedList<URL>();
	   
	   urls.add(new URL(aURL, "."));
	   // add all of the *.jar files
	   File dir = new File(aURL.getFile());
	   File[] files = dir.listFiles(new FileFilter() {
		@Override
		public boolean accept(File aFile) {
			return aFile.getName().endsWith(".jar");
		}
	   });
	   for(File file : files) {
		   urls.add(file.toURI().toURL());
	   }
	   
	   return URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]), aParent);
	}
}
