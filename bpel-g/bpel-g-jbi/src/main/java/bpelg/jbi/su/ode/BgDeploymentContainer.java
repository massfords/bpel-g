package bpelg.jbi.su.ode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.addressing.pdef.IAePartnerDefInfo;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentId;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentId;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource;
import org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo;
import org.w3c.dom.Document;

public class BgDeploymentContainer implements IAeDeploymentContainer {
    
    private File mServiceUnitRoot;
    private IAeServiceDeploymentInfo[] mServiceDeploymentInfos;
    private ClassLoader mClassLoader;
    
    public BgDeploymentContainer(File aServiceUnitRoot) throws IOException {
        mServiceUnitRoot = aServiceUnitRoot;
        mClassLoader = URLClassLoader.newInstance(new URL[] {aServiceUnitRoot.toURI().toURL()});
    }

    @Override
    public Document getCatalogDocument() throws AeException {
        // FIXME impl with BgCatalogBuilder
        return null;
    }

    @Override
    public IAeDeploymentSource getDeploymentSource(String aPddName) throws AeException {
        // FIXME impl
        return null;
    }

    @Override
    public Collection<String> getPddResources() {
        // FIXME get from BgPddBuilder
        return null;
    }

    @Override
    public Document getResourceAsDocument(String aResourceName) throws AeException {
        // FIXME simple util method
        return null;
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
        mServiceDeploymentInfos = aServiceInfo;
    }

    @Override
    public IAeServiceDeploymentInfo[] getServiceDeploymentInfo() {
        return mServiceDeploymentInfos;
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
        throw new UnsupportedOperationException("not implemented");
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
}
