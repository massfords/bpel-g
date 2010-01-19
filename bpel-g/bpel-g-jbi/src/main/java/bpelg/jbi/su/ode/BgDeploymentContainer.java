package bpelg.jbi.su.ode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.addressing.pdef.IAePartnerDefInfo;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentId;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentId;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource;
import org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo;
import org.activebpel.rt.bpel.server.deploy.bpr.AeBprDeploymentSource;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Document;

import bpelg.jbi.su.ode.BgPddInfo.BgPlink;

public class BgDeploymentContainer implements IAeDeploymentContainer {
    
    private File mServiceUnitRoot;
    private List<IAeServiceDeploymentInfo> mServiceDeploymentInfos = new ArrayList();
    private ClassLoader mClassLoader;
    private BgCatalogBuilder mCatalogBuilder;
    private BgPddBuilder mPddBuilder;
    private Map<String,IAeDeploymentSource> mDeploymentSources = new HashMap();
    
    public BgDeploymentContainer(File aServiceUnitRoot) throws Exception {
        mServiceUnitRoot = aServiceUnitRoot;
        mClassLoader = URLClassLoader.newInstance(new URL[] {aServiceUnitRoot.toURI().toURL()});
        
        mCatalogBuilder = new BgCatalogBuilder(mServiceUnitRoot);
        mCatalogBuilder.build();

        mPddBuilder = new BgPddBuilder(mServiceUnitRoot);
        mPddBuilder.build();
        mPddBuilder.writeDocuments(mCatalogBuilder);
        
        // now that we've written all of the pdd's, we know what each bpel is importing
        // we're now able to build a collection of just those tuples that are referenced
        Set<BgCatalogTuple> referenced = mPddBuilder.getReferenced();
        mCatalogBuilder.setReferenced(referenced);
        
    }
    
    public BgPlink getPlink(QName aProcessName, String aPlinkName) {
     return mPddBuilder.getDeployments().get(aProcessName).getBgPlink(aPlinkName);   
    }

    @Override
    public IAeDeploymentSource getDeploymentSource(String aPddName) throws AeException {
        IAeDeploymentSource source = mDeploymentSources.get(aPddName);
        if (source == null) {
            source = buildSource(aPddName);
            mDeploymentSources.put(aPddName, source);
        }
        return source;
    }
    
    protected IAeDeploymentSource buildSource(String aPddName) throws AeException {
        AeXMLParserBase parser = new AeXMLParserBase(true,false);
        Document doc;
        try {
            doc = parser.loadDocument(new FileReader(new File(mServiceUnitRoot, aPddName)), null);
        } catch (FileNotFoundException e) {
            throw new AeException(e);
        }
        IAeDeploymentSource source = new AeBprDeploymentSource(aPddName, doc, this);
        return source;
    }

    @Override
    public String getShortName() {
        return mServiceUnitRoot.getName();
    }


    @Override
    public String getDeploymentType() {
        return "bpel";
    }

    @Override
    public void addServiceDeploymentInfo(IAeServiceDeploymentInfo[] aServiceInfo) {
        mServiceDeploymentInfos.addAll(Arrays.asList(aServiceInfo));
    }

    @Override
    public IAeServiceDeploymentInfo[] getServiceDeploymentInfo() {
        return mServiceDeploymentInfos.toArray(new IAeServiceDeploymentInfo[mServiceDeploymentInfos.size()]);
    }

    @Override
    public ClassLoader getWebServicesClassLoader() {
        return mClassLoader;
    }

    @Override
    public Document getWsddData() {
        return null;
    }

    @Override
    public void setServiceDeploymentInfo(IAeServiceDeploymentInfo[] aServiceInfo) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void setWsddData(Document aDocument) {
        throw new UnsupportedOperationException("only bpel deployments are imlpemented");
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
    public IAePartnerDefInfo getPartnerDefInfo(String aPdefResource) throws AeException {
        throw new UnsupportedOperationException("partner defs not implemented");
    }

    @Override
    public Collection getPdefResources() {
        return Collections.EMPTY_LIST; 
    }

    @Override
    public InputStream getResourceAsStream(String aResourceName) {
        return getResourceClassLoader().getResourceAsStream(aResourceName);
    }

    @Override
    public String getWsddResource() {
        return null;
    }

    @Override
    public boolean isWsddDeployment() {
        return false;
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
    public Document getCatalogDocument() throws AeException {
        try {
            return mCatalogBuilder.getCatalog();
        } catch (Exception e) {
            throw new AeException(e);
        }
    }

    @Override
    public Collection<String> getPddResources() {
        return mPddBuilder.getPddNames();
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
