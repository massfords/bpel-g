package org.activebpel.rt.bpel.server.spring;

import java.net.URL;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentHandler;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.xml.sax.InputSource;

public class AeSpringDeploymentHandler implements IAeDeploymentHandler {

	private AeSpringManager mSpringManager;
	
	@Override
	public void deploy(IAeDeploymentContainer aContainer,
			IAeDeploymentLogger aLogger) throws AeException {
		URL context = aContainer.getResourceURL("META-INF/applicationContext.xml");
		if (context != null) {
			URL location = aContainer.getDeploymentLocation();
			GenericApplicationContext ac = new GenericApplicationContext();
			ac.setResourceLoader(new DefaultResourceLoader(aContainer.getResourceClassLoader()));
			XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ac);
			xmlReader.loadBeanDefinitions(new InputSource(location.toExternalForm()));
			ac.refresh();
			getSpringManager().add(location.toExternalForm(), ac);
		}
	}

	@Override
	public void undeploy(IAeDeploymentContainer aContainer) throws AeException {
		URL context = aContainer.getResourceURL("META-INF/applicationContext.xml");
		if (context != null) {
			URL location = aContainer.getDeploymentLocation();
			GenericApplicationContext ac = getSpringManager().remove(location.toExternalForm());
			if (ac != null) {
				ac.close();
			}
		}
	}

	public AeSpringManager getSpringManager() {
		return mSpringManager;
	}

	public void setSpringManager(AeSpringManager aSpringManager) {
		mSpringManager = aSpringManager;
	}

}
