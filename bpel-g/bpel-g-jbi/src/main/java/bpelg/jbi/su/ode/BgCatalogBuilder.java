package bpelg.jbi.su.ode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Document;


/**
 * Walks the service unit root and constructs the catalog.xml file that is needed to deploy the set of processes.
 * 
 * @author markford
 */
public class BgCatalogBuilder {
    private File mServiceUnitRoot;
    private Collection<BgCatalogTuple> mCollection = new ArrayList();
    private String mPhysicalPathPrefix;
    
    public BgCatalogBuilder(File aRoot) {
        assert aRoot.isDirectory();
        mServiceUnitRoot = aRoot;
        mPhysicalPathPrefix = "project://" + mServiceUnitRoot.getName() + "/";
    }
    
    public void build() throws Exception {
        addFiles(mServiceUnitRoot, "");
    }
    
    protected void addFiles(File aCurrentDir, String aPath) throws Exception {
        File[] files = aCurrentDir.listFiles();
        for(File file : files) {
            String path = aPath + file.getName();

            if (file.isDirectory()) {
                addFiles(file, path + "/");
            } else {
                BgCatalogTuple item = null;
                String name = file.getName().toLowerCase();
                if (name.endsWith(".wsdl")) {
                    String namespace = getTargetNamespace(file);
                    item = new BgCatalogTuple(path, mPhysicalPathPrefix + path, namespace, IAeConstants.WSDL_NAMESPACE);
                } else if (name.endsWith(".xsd")) {
                    String namespace = getTargetNamespace(file);
                    item = new BgCatalogTuple(path, mPhysicalPathPrefix + path, namespace, IAeConstants.W3C_XML_SCHEMA);
                } else if (name.endsWith(".xsl")) {
                    item = new BgCatalogTuple(path, mPhysicalPathPrefix + path, null, IAeConstants.XSL_NAMESPACE);
                }
                
                if (item !=null)
                    mCollection.add(item);
            }
        }
    }
    
    protected String getTargetNamespace(File aFile) throws Exception {
        Document doc = AeXmlUtil.toDoc(aFile, null);
        return doc.getDocumentElement().getAttribute("targetNamespace");
    }
    
    public Collection<BgCatalogTuple> getItems() {
        return mCollection;
    }
}
