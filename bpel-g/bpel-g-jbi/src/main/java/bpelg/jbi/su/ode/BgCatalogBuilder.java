package bpelg.jbi.su.ode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Walks the service unit root and constructs the catalog.xml file that is needed to deploy the set of processes.
 * 
 * @author markford
 */
public class BgCatalogBuilder {
    private File mServiceUnitRoot;
    private Collection<BgCatalogTuple> mCollection = new ArrayList();
    private String mLogicalPathPrefix;
    private Document mCatalog;
    private boolean mReplaceExisting;
    
    public BgCatalogBuilder(File aRoot) {
        assert aRoot.isDirectory();
        mServiceUnitRoot = aRoot;
        mLogicalPathPrefix = "project:/" + mServiceUnitRoot.getName() + "/";
    }
    
    public void build() throws Exception {
        addFiles(mServiceUnitRoot, "");
        buildCatalog();
    }
    
    protected void buildCatalog() throws Exception {
        Document doc = AeXmlUtil.newDocument();
        String catalogNS = "http://schemas.active-endpoints.com/catalog/2006/07/catalog.xsd";
        Element catalog = AeXmlUtil.addElementNS(doc, catalogNS, "catalog", null);
        catalog.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns", catalogNS);
        if (isReplaceExisting())
            catalog.setAttribute("replace.existing", "true");
        for(BgCatalogTuple tuple : mCollection) {
            Element entry = null;
            if (tuple.isWsdl()) {
                entry = AeXmlUtil.addElementNS(catalog, catalogNS, "wsdlEntry");
            } else if (tuple.isXsd()) {
                entry = AeXmlUtil.addElementNS(catalog, catalogNS, "schemaEntry");
            } else {
                entry = AeXmlUtil.addElementNS(catalog, catalogNS, "otherEntry");
                entry.setAttribute("type", tuple.type);
            }
            entry.setAttribute("location", tuple.logicalLocation);
            entry.setAttribute("classpath", tuple.physicalLocation);
        }
        mCatalog = doc;
    }
    
    protected Document getCatalog() {
        return mCatalog;
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
                    item = new BgCatalogTuple(mLogicalPathPrefix + path, path, namespace, IAeConstants.WSDL_NAMESPACE);
                } else if (name.endsWith(".xsd")) {
                    String namespace = getTargetNamespace(file);
                    item = new BgCatalogTuple(mLogicalPathPrefix + path, path, namespace, IAeConstants.W3C_XML_SCHEMA);
                } else if (name.endsWith(".xsl")) {
                    item = new BgCatalogTuple(mLogicalPathPrefix + path, path, null, IAeConstants.XSL_NAMESPACE);
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

    public boolean isReplaceExisting() {
        return mReplaceExisting;
    }

    public void setReplaceExisting(boolean aReplaceExisting) {
        mReplaceExisting = aReplaceExisting;
    }
}
