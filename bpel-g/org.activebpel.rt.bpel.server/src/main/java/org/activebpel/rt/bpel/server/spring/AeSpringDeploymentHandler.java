package org.activebpel.rt.bpel.server.spring;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentHandler;
import org.activebpel.rt.bpel.server.deploy.bpr.AePddResource;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.UrlResource;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles the deployment of a spring context within a deployment container.
 * For now, the context MUST be located at META-INF/applicationContext.xml
 * 
 * @author mford
 */
public class AeSpringDeploymentHandler implements IAeDeploymentHandler {

	/** reference to the manager that holds onto the context */
    @Inject
	private AeSpringManager mSpringManager;
	
	@Override
	public void deploy(IAeDeploymentContainer aContainer,
			IAeDeploymentLogger aLogger) throws AeException {
		// check to see if there's a context
		URL context = aContainer.getResourceURL("META-INF/applicationContext.xml");
		// if we don't find one, there's nothing to do
		if (context != null) {
			// create a GenericApplicationContext. Using a the generic in order to be able
			// to set the resource loader in place so it uses the classloader bound to this
			// directory.
			GenericApplicationContext ac = new GenericApplicationContext();
			ac.setResourceLoader(new DefaultResourceLoader(aContainer.getResourceClassLoader()));
			XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ac);
			xmlReader.loadBeanDefinitions(new UrlResource(context));
			ac.refresh();
			Set<QName> processNames = new HashSet<QName>();
			for(AePddResource pdd : aContainer.getPddResources()) {
			    processNames.add(pdd.getPdd().getName());
			}
			getSpringManager().add(processNames,context.toExternalForm(), ac);
		}
	}

	@Override
	public void undeploy(IAeDeploymentContainer aContainer) throws AeException {
		URL context = aContainer.getResourceURL("META-INF/applicationContext.xml");
		if (context != null) {
			GenericApplicationContext ac = getSpringManager().remove(context.toExternalForm());
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
