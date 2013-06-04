package bpelg.packaging.ode;

import bpelg.packaging.ode.BgPddInfo.BgPlink;
import bpelg.services.deploy.types.MessageType;
import bpelg.services.deploy.types.Msg;
import bpelg.services.deploy.types.catalog.Catalog;
import bpelg.services.deploy.types.pdd.Pdd;
import bpelg.services.processes.types.ServiceDeployments;
import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.server.deploy.*;
import org.activebpel.rt.bpel.server.deploy.bpr.AeBprDeploymentSource;
import org.activebpel.rt.bpel.server.deploy.bpr.AePddResource;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class BgDeploymentContainer implements IAeDeploymentContainer {

    private static final Logger log = LoggerFactory.getLogger(BgDeploymentContainer.class);
    
    private final File serviceUnitRoot;
    private ServiceDeployments serviceDeploymentInfos;
    private final ClassLoader classLoader;
    private final BgCatalogBuilder catalogBuilder;
    private final BgPddBuilder pddBuilder;
    private final Map<Pdd,IAeDeploymentSource> deploymentSources = new HashMap<>();
    private final Collection<AePddResource> pdds;
    
    public BgDeploymentContainer(File serviceUnitRoot, IAeDeploymentLogger logger) throws Exception {
        this.serviceUnitRoot = serviceUnitRoot;
        classLoader = AeBprClasspathBuilder.build(serviceUnitRoot.toURI().toURL());
        
        catalogBuilder = new BgCatalogBuilder(this.serviceUnitRoot);
        catalogBuilder.build();

        pddBuilder = new BgPddBuilder(this.serviceUnitRoot);
        pddBuilder.build();
        pdds = pddBuilder.getPdds(catalogBuilder);
        
        // now that we've written all of the pdd's, we know what each bpel is importing
        // we're now able to build a collection of just those tuples that are referenced
        Set<BgCatalogTuple> referenced = pddBuilder.getReferenced();
        catalogBuilder.setReferenced(referenced);

        // let's check to make sure there are no extra bpel's
        Set<QName> bpels = new HashSet<>();
        for(File file : serviceUnitRoot.listFiles()) {
            if (file.getName().endsWith(".bpel")) {
                // add the name to the set
                try {
                    Document doc = AeXmlUtil.toDoc(file, null);
                    Element docElement = doc.getDocumentElement();
                    QName name = new QName(docElement.getAttribute("targetNamespace"), "name");
                    bpels.add(name);
                } catch (Exception e) {
                    log.error("Error loading bpel", e);
                }
            }
        }

        if (logger != null) {
            // report any extra bpels
            for (AePddResource resource : pdds) {
                bpels.remove(resource.getPdd().getName());
            }

            // anything left over is an extra bpel
            for(QName name : bpels) {
                logger.addContainerMessage(
                        new Msg().withType(MessageType.WARNING)
                                 .withValue("Extra bpel file without an entry in deploy.xml:" + name.getLocalPart()));
            }
        }

    }
    
    public BgPlink getPlink(QName processName, String plinkName) {
     return pddBuilder.getDeployments().get(processName).getBgPlink(plinkName);
    }

    @Override
    public IAeDeploymentSource getDeploymentSource(Pdd pdd) throws AeException {
    	// FIXME this looks wrong
        IAeDeploymentSource source = deploymentSources.get(pdd);
        if (source == null) {
            source = new AeBprDeploymentSource(pdd, this);
            deploymentSources.put(pdd, source);
        }
        return source;
    }
    
    @Override
    public String getShortName() {
        return serviceUnitRoot.getName();
    }

    @Override
    public ServiceDeployments getServiceDeploymentInfo() throws AeException {
    	if (serviceDeploymentInfos == null) {
    		serviceDeploymentInfos = new ServiceDeployments();
    		for(AePddResource pddr : pdds) {
    			IAeDeploymentSource source = getDeploymentSource(pddr.getPdd());
				serviceDeploymentInfos.withServiceDeployment(getServiceInfo(source).getServiceDeployment());
    		}
    	}
        return new ServiceDeployments().withServiceDeployment(serviceDeploymentInfos.getServiceDeployment());
    }
	/**
	 * Gets the service deployment info from a source
	 * 
	 * @param source
	 * @throws AeDeploymentException
	 */
	protected ServiceDeployments getServiceInfo(
			IAeDeploymentSource source) throws AeDeploymentException {
		// Get the service info
		AeProcessDef processDef = source.getProcessDef();
		return AeServiceDeploymentUtil
				.getServices(processDef, source.getPdd());
	}

    @Override
    public boolean exists(String resourceName) {
        return getResourceURL(resourceName) != null;
    }

    @Override
    public String getBprFileName() {
        return serviceUnitRoot.getName();
    }

    @Override
    public IAeDeploymentContext getDeploymentContext() {
        return this;
    }

    @Override
    public InputStream getResourceAsStream(String resourceName) {
        return getResourceClassLoader().getResourceAsStream(resourceName);
    }

    @Override
    public IAeDeploymentId getDeploymentId() {
        return new AeDeploymentId(getBprFileName());
    }

    @Override
    public URL getDeploymentLocation() {
        try {
            return serviceUnitRoot.toURI().toURL();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public ClassLoader getResourceClassLoader() {
        return classLoader;
    }

    @Override
    public URL getResourceURL(String resourceName) {
        return getResourceClassLoader().getResource(resourceName);
    }

    @Override
    public URL getTempDeploymentLocation() {
        return null;
    }

    @Override
    public Catalog getCatalogDocument() throws AeException {
        try {
            return catalogBuilder.getCatalog();
        } catch (Exception e) {
            throw new AeException(e);
        }
    }

    @Override
    public Collection<AePddResource> getPddResources() {
        return pdds;
    }

    @Override
    public Document getResourceAsDocument(String resourceName) throws AeException {
        InputStream in = getResourceAsStream(resourceName);
        AeXMLParserBase parser = new AeXMLParserBase(true, false);
        try {
            in = getResourceAsStream(resourceName);
            return parser.loadDocument(in, null);
        } finally {
            AeCloser.close(in);
        }
    }
}
