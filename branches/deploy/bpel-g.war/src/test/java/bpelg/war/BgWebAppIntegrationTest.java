package bpelg.war;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.activebpel.rt.util.AeXPathUtil;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.mock.web.MockServletContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//@Ignore
public class BgWebAppIntegrationTest {

	/**
	 * Tests the init of the web app and the spring config in a unit test so I don't have to 
	 * build, deploy, and restart tomcat to work out any issues.
	 */
	@Test
	public void init() throws Exception {
		File wardir = new File("target/bpel-g").getAbsoluteFile();
		System.setProperty("catalina.home", wardir.getParentFile().toString());
		MockServletContext context = new MockServletContext(wardir.toURI().toString(), new DefaultResourceLoader(BgWebAppIntegrationTest.class.getClassLoader()));
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setValidating(false);
		DocumentBuilder db = dbf.newDocumentBuilder();
		db.setEntityResolver(new EntityResolver() {

			@Override
			public InputSource resolveEntity(String aArg0, String aArg1)
					throws SAXException, IOException {
				return new InputSource(new FileInputStream("src/test/resources/web-app.dtd"));
			}});
		Document doc = db.parse(new File("target/bpel-g/WEB-INF/web.xml"));
		List<Element> params = AeXPathUtil.selectNodes(doc, "//context-param");
		for(Element param : params) {
			context.addInitParameter(AeXPathUtil.selectText(param, "param-name", null), AeXPathUtil.selectText(param, "param-value", null));
		}
		List<Element> listeners = AeXPathUtil.selectNodes(doc, "//listener-class");
		List<ServletContextListener> contextListeners = new ArrayList();
		for(Element listener : listeners) {
			contextListeners.add((ServletContextListener) Class.forName(AeXPathUtil.selectText(listener, ".", null)).newInstance());
		}
		
		ServletContextEvent contextEvent = new ServletContextEvent(context);
		for(ServletContextListener scl : contextListeners) {
			scl.contextInitialized(contextEvent);
		}
	}
	
}
