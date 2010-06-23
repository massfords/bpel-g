package bpelg.jbi.su.ode;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.def.AeImportDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.io.AeBpelIO;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bpelg.jbi.su.ode.BgPddInfo.BgPlink;

public class BgPddBuilder {
    
    private static final String PDD = "http://schemas.active-endpoints.com/pdd/2006/08/pdd.xsd";
    private static final String WSA = "http://www.w3.org/2005/08/addressing";
    private static final String WSP = "http://schemas.xmlsoap.org/ws/2004/09/policy";
    
    private static final Map NAMESPACES = new HashMap<String,String>();
    static {
        NAMESPACES.put("ode", "http://www.apache.org/ode/schemas/dd/2007/03");
        NAMESPACES.put("wsp", WSP);
    }
    
    private File mServiceUnitRoot;
    
    private Map<QName, BgPddInfo> mDeployments = new HashMap();
    private Map<String,BgPddInfo> mPddFileNameToPddInfo = new HashMap();
    private Document mDeployXml;
    private Set<BgCatalogTuple> mReferenced = new HashSet();
    
    public BgPddBuilder(File aServiceUnitRoot) throws AeException {
        assert aServiceUnitRoot.isDirectory();
        
        mServiceUnitRoot = aServiceUnitRoot;
        File deployFile = new File(mServiceUnitRoot, "deploy.xml");
        if (deployFile.isFile()) {
            mDeployXml = AeXmlUtil.toDoc(deployFile, null);
        }
    }
    
    public Set<BgCatalogTuple> getReferenced() {
        return mReferenced;
    }
    
    public Collection<String> getPddNames() {
        return mPddFileNameToPddInfo.keySet();
    }
    
    public void writeDocuments(BgCatalogBuilder aCatalogBuilder) throws IOException {
        // create each of the pdd files within the service unit root
        for(String pddName : getPddNames()) {
            writePddDocument(pddName, aCatalogBuilder.getItems());
        }
    }

    protected void writePddDocument(String aName, Collection<BgCatalogTuple> aCatalog) throws IOException {
        Document doc = createPddDocument(aName, aCatalog);
        String pdd = AeXMLParserBase.documentToString(doc, true);
        FileWriter fw = new FileWriter(new File(mServiceUnitRoot, aName));
        try {
            fw.write(pdd);
        } finally {
            AeCloser.close(fw);
        }
    }
    
    protected Document createPddDocument(String aName, Collection<BgCatalogTuple> aCatalog) {
        BgPddInfo info = mPddFileNameToPddInfo.get(aName);
        Document doc = AeXmlUtil.newDocument();
        Element pdd = AeXmlUtil.addElementNS(doc, PDD, "pdd:process");
        pdd.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:pdd", PDD);
        pdd.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:bpelns", info.getProcessName().getNamespaceURI());
        pdd.setAttribute("name", "bpelns:" + info.getProcessName().getLocalPart());
        pdd.setAttribute("platform", "opensource");
        pdd.setAttribute("location", aName.substring(0, aName.lastIndexOf('.')));
        
        Element plinks = AeXmlUtil.addElementNS(pdd, PDD, "pdd:partnerLinks");

        for(BgPlink plink : info.getBgPlinks()) {
            Element plinkEl = AeXmlUtil.addElementNS(plinks, PDD, "pdd:partnerLink");
            plinkEl.setAttribute("name", plink.name);
            if (plink.hasMyRole()) {
                Element myRole = AeXmlUtil.addElementNS(plinkEl, PDD, "pdd:myRole");
                myRole.setAttribute("allowedRoles", "");
                myRole.setAttribute("binding", "EXTERNAL");
                String encodedService = AeXmlUtil.encodeQName(plink.myService, myRole, "mysvc");
                // space delimited value of Service QName + endpoint
                myRole.setAttribute("service", encodedService + " " + plink.myEndpoint + " " + UUID.randomUUID().toString());
                addPolicies(myRole, plink.myPolicies);
            }
            if (plink.hasPartnerRole()) {
                Element partnerRole = AeXmlUtil.addElementNS(plinkEl, PDD, "pdd:partnerRole");
                partnerRole.setAttribute("endpointReference", "static");
                partnerRole.setAttribute("invokeHandler", "default:Service");
                Element epr = AeXmlUtil.addElementNS(partnerRole, WSA, "wsa:EndpointReference");
                epr.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:wsa", WSA);
                AeXmlUtil.addElementNS(epr, WSA, "wsa:Address", "None");
                Element metadata = AeXmlUtil.addElementNS(epr, WSA, "wsa:Metadata");
                String encodedService = AeXmlUtil.encodeQName(plink.partnerService, metadata, "psvc");
                Element serviceName = AeXmlUtil.addElementNS(metadata, WSA, "wsa:ServiceName", encodedService);
                serviceName.setAttribute("PortName", plink.partnerEndpoint);
                addPolicies(metadata, plink.partnerPolicies);
            }
        }
        
        // build the referenced wsdl's
        Element refs = AeXmlUtil.addElementNS(pdd, PDD, "pdd:references");
        for (Iterator<AeImportDef> iter = info.getProcessDef().getImportDefs(); iter.hasNext();) {
            AeImportDef def = iter.next();
            // each wsdl or xsd that is in the bpel file needs to be in the
            // pdd:references
            Element entry = null;
            if (def.isWSDL()) {
                entry = AeXmlUtil.addElementNS(refs, PDD, "pdd:wsdl");
            } else if (def.isSchema()) {
                entry = AeXmlUtil.addElementNS(refs, PDD, "pdd:schema");
            } else {
                entry = AeXmlUtil.addElementNS(refs, PDD, "pdd:other");
            }

            // the location should come from the catalog if present
            entry.setAttribute("location", getLogicalLocation(def, aCatalog));
            entry.setAttribute("namespace", AeUtil.getSafeString(def.getNamespace()));
        }
        
        return doc;
    }

    private void addPolicies(Element aPolicyParentElement, List<Element> aPolicies) {
        if (AeUtil.notNullOrEmpty(aPolicies)) {
            // add the policies
            for(Element policy : aPolicies) {
                Element cloned = AeXmlUtil.cloneElement(policy, aPolicyParentElement.getOwnerDocument());
                aPolicyParentElement.appendChild(cloned);
            }
        }
    }
    
    private String getLogicalLocation(AeImportDef aImportDef, Collection<BgCatalogTuple> aTupleColl) {
        for(BgCatalogTuple tuple : aTupleColl) {
            if (tuple.physicalLocation.equals(aImportDef.getLocation())) {
                mReferenced.add(tuple);
                return tuple.logicalLocation;
            }
        }
        return aImportDef.getLocation();
    }
    
    public void build() throws Exception {
        buildDeploymentMap();

        List<Element> processes = getProcesses();
        for(Element process : processes) {
            
            QName processName = AeXmlUtil.getAttributeQName(process, "name");
            
            BgPddInfo pddInfo = mDeployments.get(processName);
            mDeployments.put(pddInfo.getProcessName(), pddInfo);
            mPddFileNameToPddInfo.put(pddInfo.getLocation() + ".pdd", pddInfo);
            
            // add provides
            List<Element> providesService = AeXPathUtil.selectNodes(process, "ode:provide/ode:service", NAMESPACES);
            for(Element provideService : providesService) {
                String plinkName = (String) AeXPathUtil.selectSingleObject(provideService, "string(../@partnerLink)", NAMESPACES);
                QName myService = AeXmlUtil.getAttributeQName(provideService, "name");
                String myEndpoint = provideService.getAttribute("port");
                List<Element> policies = getPolicies(provideService);
                pddInfo.addProvide(plinkName, myService, myEndpoint, policies);
            }

            // add invokes
            List<Element> invokesService = AeXPathUtil.selectNodes(process, "ode:invoke/ode:service", NAMESPACES);
            for(Element invokeService : invokesService) {
                String plinkName = (String) AeXPathUtil.selectSingleObject(invokeService, "string(../@partnerLink)", NAMESPACES);
                QName partnerService = AeXmlUtil.getAttributeQName(invokeService, "name");
                String partnerEndpoint = invokeService.getAttribute("port");
                List<Element> policies = getPolicies(invokeService);
                pddInfo.addInvoke(plinkName, partnerService, partnerEndpoint, policies);
            }
        }
    }

    private List<Element> getPolicies(Element aService) throws AeException {
        List<Element> policies = AeXPathUtil.selectNodes(aService, "wsp:Policy", NAMESPACES);
        return policies;
    }

    private List<Element> getProcesses() throws AeException {
        if (mDeployXml == null)
            return Collections.EMPTY_LIST;
        List<Element> processes = AeXPathUtil.selectNodes(mDeployXml, "/ode:deploy/ode:process", NAMESPACES);
        return processes;
    }
    
    /**
     * Loads all of the bpel files found into a map by their 
     * QName to a BgPddInfo object
     * 
     * @throws AeException
     */
    protected void buildDeploymentMap() throws AeException {
        
        File[] files = getBpelFiles();
        
        for(File file : files) {
            Document doc = AeXmlUtil.toDoc(file, null);
            AeProcessDef processDef = AeBpelIO.deserialize(doc);
            QName processName = processDef.getQName();
            String location = file.getName();
            BgPddInfo pddInfo = new BgPddInfo(processDef, location);
            mDeployments.put(processName, pddInfo);
        }
    }

    private File[] getBpelFiles() {
        File[] files = mServiceUnitRoot.listFiles(new FileFilter() {

            @Override
            public boolean accept(File aFile) {
                return aFile.getName().endsWith(".bpel");
            }
        });
        return files;
    }
    
    protected Map<QName,BgPddInfo> getDeployments() {
        return mDeployments;
    }
}
