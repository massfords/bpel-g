package bpelg.packaging.ode;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.server.deploy.AeBprClasspathBuilder;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentException;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentId;
import org.activebpel.rt.bpel.server.deploy.AeServiceDeploymentUtil;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentId;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource;
import org.activebpel.rt.bpel.server.deploy.bpr.AeBprDeploymentSource;
import org.activebpel.rt.bpel.server.deploy.bpr.AePddResource;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Document;

import bpelg.packaging.ode.BgPddInfo.BgPlink;
import bpelg.services.deploy.types.catalog.Catalog;
import bpelg.services.deploy.types.pdd.Pdd;
import bpelg.services.processes.types.ServiceDeployments;

public class BgDeploymentContainer implements IAeDeploymentContainer {
    
    private final File mServiceUnitRoot;
    private ServiceDeployments mServiceDeploymentInfos;
    private final ClassLoader mClassLoader;
    private final BgCatalogBuilder mCatalogBuilder;
    private final BgPddBuilder mPddBuilder;
    private final Map<Pdd,IAeDeploymentSource> mDeploymentSources = new HashMap();
    private final Collection<AePddResource> mPdds;
    
    public BgDeploymentContainer(File aServiceUnitRoot) throws Exception {
        mServiceUnitRoot = aServiceUnitRoot;
        mClassLoader = AeBprClasspathBuilder.build(aServiceUnitRoot.toURI().toURL());
        
        mCatalogBuilder = new BgCatalogBuilder(mServiceUnitRoot);
        mCatalogBuilder.build();

        mPddBuilder = new BgPddBuilder(mServiceUnitRoot);
        mPddBuilder.build();
        mPdds = mPddBuilder.getPdds(mCatalogBuilder);
        
        // now that we've written all of the pdd's, we know what each bpel is importing
        // we're now able to build a collection of just those tuples that are referenced
        Set<BgCatalogTuple> referenced = mPddBuilder.getReferenced();
        mCatalogBuilder.setReferenced(referenced);
        
    }
    
    public BgPlink getPlink(QName aProcessName, String aPlinkName) {
     return mPddBuilder.getDeployments().get(aProcessName).getBgPlink(aPlinkName);   
    }

    @Override
    public IAeDeploymentSource getDeploymentSource(Pdd aPdd) throws AeException {
    	// FIXME this looks wrong
        IAeDeploymentSource source = mDeploymentSources.get(aPdd);
        if (source == null) {
            source = new AeBprDeploymentSource(aPdd, this);
            mDeploymentSources.put(aPdd, source);
        }
        return source;
    }
    
    @Override
    public String getShortName() {
        return mServiceUnitRoot.getName();
    }

    @Override
    public ServiceDeployments getServiceDeploymentInfo() throws AeException {
    	if (mServiceDeploymentInfos == null) {
    		mServiceDeploymentInfos = new ServiceDeployments();
    		for(AePddResource pddr : mPdds) {
    			IAeDeploymentSource source = getDeploymentSource(pddr.getPdd());
				mServiceDeploymentInfos.withServiceDeployment(getServiceInfo(source).getServiceDeployment());
    		}
    	}
        return new ServiceDeployments().withServiceDeployment(mServiceDeploymentInfos.getServiceDeployment());
    }
	/**
	 * Gets the service deployment info from a source
	 * 
	 * @param aSource
	 * @throws AeDeploymentException
	 */
	protected ServiceDeployments getServiceInfo(
			IAeDeploymentSource aSource) throws AeDeploymentException {
		// Get the service info
		AeProcessDef processDef = aSource.getProcessDef();
		return AeServiceDeploymentUtil
				.getServices(processDef, aSource.getPdd());
	}

    @Override
    public boolean exists(String aResourceName) {
        return getResourceURL(aResourceName) != null;
    }

    @Override
    public String getBprFileName() {
        return mServiceUnitRoot.getName();
    }

    @Override
    public IAeDeploymentContext getDeploymentContext() {
        return this;
    }

    @Override
    public InputStream getResourceAsStream(String aResourceName) {
        return getResourceClassLoader().getResourceAsStream(aResourceName);
    }

    @Override
    public IAeDeploymentId getDeploymentId() {
        return new AeDeploymentId(getBprFileName());
    }

    @Override
    public URL getDeploymentLocation() {
        try {
            return mServiceUnitRoot.toURI().toURL();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public ClassLoader getResourceClassLoader() {
        return mClassLoader;
    }

    @Override
    public URL getResourceURL(String aResourceName) {
        return getResourceClassLoader().getResource(aResourceName);
    }

    @Override
    public URL getTempDeploymentLocation() {
        return null;
    }

    @Override
    public Catalog getCatalogDocument() throws AeException {
        try {
            return mCatalogBuilder.getCatalog();
        } catch (Exception e) {
            throw new AeException(e);
        }
    }

    @Override
    public Collection<AePddResource> getPddResources() {
        return mPdds;
    }

    @Override
    public Document getResourceAsDocument(String aResourceName) throws AeException {
        InputStream in = getResourceAsStream(aResourceName);
        AeXMLParserBase parser = new AeXMLParserBase(true, false);
        try {
            in = getResourceAsStream(aResourceName);
            return parser.loadDocument(in, null);
        } finally {
            AeCloser.close(in);
        }
    }
}
