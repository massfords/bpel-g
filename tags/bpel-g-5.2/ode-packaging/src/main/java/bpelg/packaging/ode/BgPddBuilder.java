package bpelg.packaging.ode;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.AeImportDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.io.AeBpelIO;
import org.activebpel.rt.bpel.server.deploy.bpr.AePddResource;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bpelg.packaging.ode.BgPddInfo.BgPlink;
import bpelg.services.deploy.types.pdd.MyRoleBindingType;
import bpelg.services.deploy.types.pdd.MyRoleType;
import bpelg.services.deploy.types.pdd.PartnerLinkType;
import bpelg.services.deploy.types.pdd.PartnerRoleEndpointReferenceType;
import bpelg.services.deploy.types.pdd.PartnerRoleType;
import bpelg.services.deploy.types.pdd.Pdd;
import bpelg.services.deploy.types.pdd.Pdd.PartnerLinks;
import bpelg.services.deploy.types.pdd.Pdd.References;
import bpelg.services.deploy.types.pdd.PlatformType;
import bpelg.services.deploy.types.pdd.ReferenceType;

public class BgPddBuilder {
    
    private static final String WSP = "http://schemas.xmlsoap.org/ws/2004/09/policy";
    
    private static final Map<String,String> NAMESPACES = new HashMap<String,String>();
    static {
        NAMESPACES.put("ode", "http://www.apache.org/ode/schemas/dd/2007/03");
        NAMESPACES.put("wsp", WSP);
        NAMESPACES.put("wsa", "http://www.w3.org/2005/08/addressing");
        NAMESPACES.put("ae", "urn:org.activebpel.deploy");
    }
    
    private File mServiceUnitRoot;
    
    private Map<QName, BgPddInfo> mDeployments = new HashMap<QName, BgPddInfo>();
    private Map<String,BgPddInfo> mPddFileNameToPddInfo = new HashMap<String,BgPddInfo>();
    private Document mDeployXml;
    private Set<BgCatalogTuple> mReferenced = new HashSet<BgCatalogTuple>();
    
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
    
    public Collection<AePddResource> getPdds(BgCatalogBuilder aCatalogBuilder) {
    	Collection<AePddResource> list = new LinkedList<AePddResource>();
        for(String pddName : getPddNames()) {
        	Pdd pdd = createPddDocument(pddName, aCatalogBuilder.getItems());
        	list.add(new AePddResource(pddName, pdd));
        }
        return list;
    }

    protected Pdd createPddDocument(String aName, Collection<BgCatalogTuple> aCatalog) {
        BgPddInfo info = mPddFileNameToPddInfo.get(aName);
        Pdd pdd = new Pdd()
        		.withName(info.getProcessName())
        		.withPlatform(PlatformType.OPENSOURCE)
        		.withLocation(aName.substring(0, aName.lastIndexOf('.')));
        
        PartnerLinks plinks = new PartnerLinks();
        pdd.setPartnerLinks(plinks);
        
        for(BgPlink plink : info.getBgPlinks()) {

            PartnerLinkType partnerLink = new PartnerLinkType().withName(plink.name);
			plinks.withPartnerLink(partnerLink);
            if (plink.hasMyRole()) {
            	partnerLink.withMyRole(
            			new MyRoleType().withAllowedRoles("")
            				// FIXME revisit to allow this to be configurable
            				.withBinding(MyRoleBindingType.MSG)
            				// FIXME was randomly generating a service name. 
            				.withService(plink.myService.getLocalPart())
            				.withAny(plink.myPolicies));
            }
            if (plink.hasPartnerRole()) {
                if (plink.epr != null) {
                	partnerLink.withPartnerRole(new PartnerRoleType()
					.withEndpointReference(PartnerRoleEndpointReferenceType.STATIC)
					.withInvokeHandler(AeXPathUtil.selectText(plink.epr, "//ae:invokeHandler", NAMESPACES))
	                .withAny(plink.epr));
                } else {
                	partnerLink.withPartnerRole(new PartnerRoleType()
					.withEndpointReference(PartnerRoleEndpointReferenceType.DYNAMIC)
					.withInvokeHandler("default:Address"));
                }
                // FIXME deploy - clean this up, need to rationalize the dropping of policies
            }
        }
        
        // build the referenced wsdl's
        References refs = new References();
        pdd.setReferences(refs);
        for (Iterator<AeImportDef> iter = info.getProcessDef().getImportDefs(); iter.hasNext();) {
            AeImportDef def = iter.next();
            // each wsdl or xsd that is in the bpel file needs to be in the
            // pdd:references
            ReferenceType entry = new ReferenceType()
            							.withLocation(getLogicalLocation(def, aCatalog))
            							.withNamespace(AeUtil.getSafeString(def.getNamespace()));
            if (def.isWSDL()) {
            	refs.withWsdl(entry);
            } else if (def.isSchema()) {
            	refs.withSchema(entry);
            } else {
            	refs.withOther(entry);
            }
        }
        
        return pdd;
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
    
    @SuppressWarnings("unchecked")
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
                Element epr = getEpr(invokeService);
                pddInfo.addInvoke(plinkName, partnerService, partnerEndpoint, policies, epr);
            }
        }
    }

    private Element getEpr(Element aInvokeService) throws AeException {
		return (Element) AeXPathUtil.selectSingleNode(aInvokeService, "wsa:EndpointReference", NAMESPACES);
	}

	@SuppressWarnings("unchecked")
	private List<Element> getPolicies(Element aService) throws AeException {
        List<Element> policies = AeXPathUtil.selectNodes(aService, "wsp:Policy", NAMESPACES);
        return policies;
    }

    @SuppressWarnings("unchecked")
	private List<Element> getProcesses() throws AeException {
        if (mDeployXml == null)
            return Collections.<Element>emptyList();
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
