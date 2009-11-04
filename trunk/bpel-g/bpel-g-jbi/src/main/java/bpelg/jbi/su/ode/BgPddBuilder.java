package bpelg.jbi.su.ode;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
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
    
    private static final Map NAMESPACES = Collections.singletonMap("ode", "http://www.apache.org/ode/schemas/dd/2007/03");
    
    private File mServiceUnitRoot;
    
    private Map<QName, BgPddInfo> mDeployments = new HashMap();
    private Map<String,BgPddInfo> mPddFileNameToPddInfo = new HashMap();
    
    public BgPddBuilder(File aServiceUnitRoot) {
        assert aServiceUnitRoot.isDirectory();
        assert new File(aServiceUnitRoot, "deploy.xml").isFile();
        
        mServiceUnitRoot = aServiceUnitRoot;
    }
    
    public Collection<String> getPddNames() {
        return mPddFileNameToPddInfo.keySet();
    }
    
    public void writePddDocument(String aName, Collection<BgCatalogTuple> aCatalog) throws IOException {
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
                myRole.setAttribute("binding", "MSG");
                String encodedService = AeXmlUtil.encodeQName(plink.myService, myRole, "mysvc");
                // space delimited value of Service QName + endpoint
                myRole.setAttribute("service", encodedService + " " + plink.myEndpoint + " " + UUID.randomUUID().toString());
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
            }
        }
        
        Element refs = AeXmlUtil.addElementNS(pdd, PDD, "pdd:references");
        for(BgCatalogTuple catalogEntry : aCatalog) {
            Element entry = null;
            if (catalogEntry.isWsdl()) {
                entry = AeXmlUtil.addElementNS(refs, PDD, "pdd:wsdl");
            } else if (catalogEntry.isXsd()) {
                entry = AeXmlUtil.addElementNS(refs, PDD, "pdd:schema");
            } else {
                entry = AeXmlUtil.addElementNS(refs, PDD, "pdd:other");
            }
            
            entry.setAttribute("location", catalogEntry.logicalLocation);
            entry.setAttribute("namespace", AeUtil.getSafeString(catalogEntry.namespace));
        }
        return doc;
    }
    
    public void build() throws Exception {
        Map<QName,String> processNameToLoc = buildLocationMap();

        Document deploy = AeXmlUtil.toDoc(new File(mServiceUnitRoot, "deploy.xml"), null);
        
        List<Element> processes = AeXPathUtil.selectNodes(deploy, "/ode:deploy/ode:process", NAMESPACES);
        for(Element process : processes) {
            
            QName processName = AeXmlUtil.getAttributeQName(process, "name");
            String location = processNameToLoc.get(processName);
            
            BgPddInfo pddInfo = new BgPddInfo(processName, location);
            mDeployments.put(pddInfo.getProcessName(), pddInfo);
            mPddFileNameToPddInfo.put(pddInfo.getLocation() + ".pdd", pddInfo);
            
            // add provides
            List<Element> providesService = AeXPathUtil.selectNodes(process, "ode:provide/ode:service", NAMESPACES);
            for(Element provideService : providesService) {
                String plinkName = (String) AeXPathUtil.selectSingleObject(provideService, "string(../@partnerLink)", NAMESPACES);
                QName myService = AeXmlUtil.getAttributeQName(provideService, "name");
                String myEndpoint = provideService.getAttribute("port");
                pddInfo.addProvide(plinkName, myService, myEndpoint);
            }

            // add invokes
            List<Element> invokesService = AeXPathUtil.selectNodes(process, "ode:invoke/ode:service", NAMESPACES);
            for(Element invokeService : invokesService) {
                String plinkName = (String) AeXPathUtil.selectSingleObject(invokeService, "string(../@partnerLink)", NAMESPACES);
                QName partnerService = AeXmlUtil.getAttributeQName(invokeService, "name");
                String partnerEndpoint = invokeService.getAttribute("port");
                pddInfo.addInvoke(plinkName, partnerService, partnerEndpoint);
            }
        }
    }
    
    protected Map<QName,String> buildLocationMap() throws AeException {
        
        Map<QName,String> locationMap = new HashMap();
        
        File[] files = getBpelFiles();
        
        for(File file : files) {
            Document doc = AeXmlUtil.toDoc(file, null);
            QName processName = new QName(doc.getDocumentElement().getAttribute("targetNamespace"), doc.getDocumentElement().getAttribute("name"));
            String location = file.getName();
            locationMap.put(processName, location);
        }
        return locationMap;
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
