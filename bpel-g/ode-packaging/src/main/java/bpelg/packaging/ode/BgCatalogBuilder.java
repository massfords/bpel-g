package bpelg.packaging.ode;

import bpelg.services.deploy.types.catalog.BaseCatalogEntryType;
import bpelg.services.deploy.types.catalog.Catalog;
import bpelg.services.deploy.types.catalog.OtherEntryType;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


/**
 * Walks the service unit root and constructs the catalog.xml file that is needed to deploy the set of processes.
 * 
 * Contains two collections that model the wsdl, xsd, xsl, and other resources found in the service unit.
 * The main collection contains tuples for all of the the catalog entries found.
 * 
 * The locations tuple contains a set of all of the locations of top level resources imported into a bpel.
 * This secondary collection is used to filter out wsdl's or xsd's that are not directly or transitively imported
 * into a bpel. This is done in an effort to limit what's deployed to only what's needed. 
 * 
 * @author markford
 */
public class BgCatalogBuilder {
    
    private static final Log sLog = LogFactory.getLog(BgCatalogBuilder.class);
    
    private static final Map<String,String> NS = new HashMap<String,String>();
    static {
        NS.put("wsdl", IAeConstants.WSDL_NAMESPACE);
        NS.put("xs", XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }
    
    private File serviceUnitRoot;
    private Collection<BgCatalogTuple> collection = new ArrayList<BgCatalogTuple>();
    private String logicalPathPrefix;
    private Catalog catalog;
    private boolean replaceExisting;
    // location paths relative to the service unit root that are referenced directly or transitively by the bpel. These paths will be included in the deployment.
    private Set<String> locations = new HashSet<String>();
    
    public BgCatalogBuilder(File root) {
        assert root.isDirectory();
        serviceUnitRoot = root;
        setReplaceExisting( new File(root, "replace.existing").isFile() );
        logicalPathPrefix = "project:/" + serviceUnitRoot.getName() + "/";
    }

    /**
     * Set the collection of catalog entries that have been imported into BPEL's
     * @param referenced
     * @throws Exception
     */
    public void setReferenced(Collection<BgCatalogTuple> referenced) throws Exception {
        for(BgCatalogTuple tuple : referenced) {
            findLocations(new File(serviceUnitRoot, tuple.physicalLocation));
        }
    }
    
    public void build() throws Exception {
        addFiles(serviceUnitRoot, "");
    }
    
    /**
     * Builds the catalog xml file for the service unit. 
     * 
     * We will only include wsdl's or xsd's that are directly imported into BPEL's
     * or ones that are transitively imported. After building each of the PDD's,
     * we'll know which resources are being imported into BPEL's. From that set,
     * we'll scan those resources for imports/includes and that's the final set
     * that makes the catalog.
     */
    protected void buildCatalog() throws Exception {
    	Catalog doc = new Catalog().withReplaceExisting(isReplaceExisting());
        for(BgCatalogTuple tuple : collection) {
        	BaseCatalogEntryType entry = null;
            if (tuple.isWsdl()) {
                if (isReferenced(tuple)) {
                    entry = new BaseCatalogEntryType();
                    doc.withWsdlEntry(entry);
                }
            } else if (tuple.isXsd()) {
                if (isReferenced(tuple)) {
                    entry = new BaseCatalogEntryType();
                    doc.withSchemaEntry(entry);
                }
            } else {
                entry = new OtherEntryType().withTypeURI(tuple.type);
                doc.withOtherEntry((OtherEntryType)entry);
            }

            if (entry != null) {
                entry.setLocation(tuple.logicalLocation);
                entry.setClasspath(tuple.physicalLocation);
            } else {
                sLog.warn("The following catalog item will not be deployed since it is not imported by any of the bpel's or their wsdl/xsd imports:" + tuple.physicalLocation);
            }
        }
        catalog = doc;
    }

    /**
     * @param tuple
     */
    protected boolean isReferenced(BgCatalogTuple tuple) {
        return locations.isEmpty() || locations.contains(tuple.physicalLocation);
    }
    
    protected Catalog getCatalog() throws Exception {
        if (catalog == null)
            buildCatalog();
        return catalog;
    }
    
    protected void addFiles(File currentDir, String p) throws Exception {
        File[] files = currentDir.listFiles();
        for(File file : files) {
            String path = p + file.getName();

            if (file.isDirectory()) {
                addFiles(file, path + "/");
            } else {
                BgCatalogTuple item = null;
                String name = file.getName().toLowerCase();
                if (name.endsWith(".wsdl")) {
                    String namespace = getTargetNamespace(file);
                    item = new BgCatalogTuple(logicalPathPrefix + path, path, namespace, IAeConstants.WSDL_NAMESPACE);
                } else if (name.endsWith(".xsd")) {
                    String namespace = getTargetNamespace(file);
                    item = new BgCatalogTuple(logicalPathPrefix + path, path, namespace, XMLConstants.W3C_XML_SCHEMA_NS_URI);
                } else if (name.endsWith(".xsl")) {
                    item = new BgCatalogTuple(logicalPathPrefix + path, path, null, IAeConstants.XSL_NAMESPACE);
                }
                
                if (item !=null)
                    collection.add(item);
            }
        }
    }
    
    protected String getTargetNamespace(File file) throws Exception {
        Document doc = AeXmlUtil.toDoc(file, null);
        return doc.getDocumentElement().getAttribute("targetNamespace");
    }
    
    public Collection<BgCatalogTuple> getItems() {
        return collection;
    }

    public boolean isReplaceExisting() {
        return replaceExisting;
    }

    public void setReplaceExisting(boolean replaceExisting) {
        this.replaceExisting = replaceExisting;
    }

    /**
     * Loads the WSDL or XSD referenced by this file and adds it and any of its
     * imported/included resources to the set of resources we're tracking for the catalog.
     * @param file
     * @throws Exception
     */
    public void findLocations(File file) throws Exception {
        String physicalLocation = toPhysicalLocation(file);
        if (!getLocations().add(physicalLocation)) {
            return;
        }
        Document doc = AeXmlUtil.toDoc(file, null);
        if (isWsdl(doc)) {
            findLocations(file, doc, "/wsdl:definitions/wsdl:import/@location");
            findLocations(file, doc, "/wsdl:definitions/wsdl:types/xs:schema/xs:import/@schemaLocation");
        } else if (isSchema(doc)) {
            findLocations(file, doc, "/xs:schema/xs:include/@schemaLocation");
            findLocations(file, doc, "/xs:schema/xs:import/@schemaLocation");
        }
    }

    @SuppressWarnings("unchecked")
	private void findLocations(File file, Document doc, String xpath) throws Exception {
        List<Node> nodes = AeXPathUtil.selectNodes(doc, xpath, NS);
        for(Node node : nodes) {
            String location = node.getNodeValue();
            if (!AeUtil.isUrlLocation(location)) {
                // it's a relative import
                File f = new File(file.getParentFile(), location);
                findLocations(f);
            }
        }
    }

    private String toPhysicalLocation(File file) throws URISyntaxException {
    	String uriStr = file.getPath().substring(serviceUnitRoot.getPath().length()+1);
    	uriStr = uriStr.replaceAll("\\\\", "/");
    	URI uri = URI.create(uriStr);
    	String physicalLocation = uri.normalize().toString();
        return physicalLocation;
    }
    
    private boolean isWsdl(Document doc) {
        return doc.getDocumentElement().getNamespaceURI().equals(IAeConstants.WSDL_NAMESPACE);
    }
    
    private boolean isSchema(Document doc) {
        return doc.getDocumentElement().getNamespaceURI().equals(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }

    public Set<String> getLocations() {
        return locations;
    }
}
