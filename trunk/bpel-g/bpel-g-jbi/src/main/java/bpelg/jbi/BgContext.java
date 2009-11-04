package bpelg.jbi;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jbi.component.ComponentContext;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import bpelg.jbi.exchange.BgMessageExchangeProcessor;
import bpelg.jbi.exchange.IBgMessageExchangeProcessor;
import bpelg.jbi.util.BgWSDLFlattener;

/**
 * Singleton context for all things JBI and bpel-g
 * 
 * @author markford
 */
public class BgContext {
	/** our singleton instance */
	private static final BgContext sInstance = new BgContext();
	private static final Log sLog = LogFactory.getLog(BgContext.class);

	/** context from the JBI container */
	private ComponentContext mComponentContext;
	/** handles the message processing when a message arrives for this component or one of its su's  */
	private IBgMessageExchangeProcessor mMessageExchangeProcessor;
	/** maps process qname to all of the services exposed by that process. Used to undeploy all of the services when the process is undeployed */
	private ConcurrentHashMap<QName,Collection<BgBpelService>> mProcessToServicesMap = new ConcurrentHashMap();
	/** maps endpoint key to the bpel service */
	private ConcurrentHashMap<BgServiceTupleKey,BgBpelService> mEndpointToBpelServiceMap = new ConcurrentHashMap();
	/** cache of WSDL documents for a given port type. */
	private BgDescriptorCache mDescriptorCache = new BgDescriptorCache();
	
	private BgContext() {
	    setMessageExchangeProcessor(new BgMessageExchangeProcessor());
	}
	
	public static synchronized BgContext getInstance() {
		return sInstance;
	}
	
	public ComponentContext getComponentContext() {
		return mComponentContext;
	}

	protected void setComponentContext(ComponentContext aComponentContext) {
		mComponentContext = aComponentContext;
	}

    public IBgMessageExchangeProcessor getMessageExchangeProcessor() {
        return mMessageExchangeProcessor;
    }
    
    /**
     * Records the service deployment and also builds a WSDL doc that describes the service
     * 
     * @param aDeployment
     * @param aService
     * @throws Exception 
     */
    public void addService(IAeProcessDeployment aDeployment, BgBpelService aService) throws Exception {
        if (sLog.isDebugEnabled())
            sLog.debug("adding service to descriptor cache: " + aService );
        Collection<BgBpelService> coll = mProcessToServicesMap.get(aService.getProcessName());
        if (coll == null) {
            coll = new LinkedList();
            Collection c2 = mProcessToServicesMap.putIfAbsent(aService.getProcessName(), coll);
            coll = c2 != null ? c2 : coll;
        }
        coll.add(aService);
        mDescriptorCache.add(aDeployment, aService.getPortType());
        BgServiceTupleKey key = createKey(aService.getServiceName(), aService.getEndpoint());
        mEndpointToBpelServiceMap.put(key, aService);
    }

    public Collection<BgBpelService> removeServicesByProcessName(QName aProcessName) {
        Collection<BgBpelService> coll = mProcessToServicesMap.remove(aProcessName);
        if (sLog.isDebugEnabled()) {
            sLog.debug("removing cached deployment info for bpel services: " + aProcessName + " count=" + coll.size());
        }
        for(BgBpelService service : coll) {
            mDescriptorCache.remove(service.getServiceName());
            BgServiceTupleKey key = createKey(service.getServiceName(), service.getEndpoint());
            mEndpointToBpelServiceMap.remove(key);
        }
        return coll;
    }
    
    public Document getServiceDescription(QName aPortType) {
        return mDescriptorCache.get(aPortType);
    }
    
    public BgBpelService getBpelService(ServiceEndpoint aServiceEndpoint) {
        BgServiceTupleKey key = createKey(aServiceEndpoint.getServiceName(), aServiceEndpoint.getEndpointName());
        return mEndpointToBpelServiceMap.get(key);
    }

    protected void setMessageExchangeProcessor(IBgMessageExchangeProcessor aMessageExchangeProcessor) {
        mMessageExchangeProcessor = aMessageExchangeProcessor;
    }
    
    private static class BgDescriptorCache {
        
        private Map<QName,BgDocumentRef> mMap = new HashMap();

        public synchronized Document add(IAeProcessDeployment aDeployment, QName aPortType) throws Exception {
            BgDocumentRef docRef = mMap.get(aPortType);
            if (docRef != null) {
                docRef.referenceCount++;
            } else {
                AeBPELExtendedWSDLDef wsdlDef = new AeBPELExtendedWSDLDef( AeWSDLDefHelper.getWSDLDefinitionForPortType(aDeployment, aPortType));
                BgWSDLFlattener flattener = new BgWSDLFlattener(wsdlDef.getWSDLDef());
                Definition def = flattener.getDefinition(aPortType);
                Document doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(def);
                    
                docRef = new BgDocumentRef(doc);
                mMap.put(aPortType, docRef);
            }
            return docRef.document;
        }
        
        public synchronized Document get(QName aPortType) {
            return mMap.get(aPortType).document;
        }
        
        public synchronized void remove(QName aPortType) {
            BgDocumentRef docRef = mMap.get(aPortType);
            docRef.referenceCount--;
            if (docRef.referenceCount == 0) {
                sLog.debug("reference count has reached zero, removing wsdl from cache");
                mMap.remove(aPortType);
            }
        }
        
        private static class BgDocumentRef {
            public Document document;
            public int referenceCount = 1;
            
            public BgDocumentRef(Document aDocument) {
                document = aDocument;
            }
        }
    }

    private BgServiceTupleKey createKey(QName aServiceName, String aEndpoint) {
        return new BgServiceTupleKey(aServiceName, aEndpoint);
    }
    
    private static class BgServiceTupleKey {
        protected QName mServiceName;
        protected String mEndpoint;
        private int mHashCode;
        public BgServiceTupleKey(QName aServiceName, String aEndpoint) {
            mServiceName = aServiceName;
            mEndpoint = aEndpoint;
            mHashCode = (mServiceName + mEndpoint).hashCode();
        }
        public int hashCode() {
            return mHashCode; 
        }
        public boolean equals(Object aObject) {
            if (aObject instanceof BgServiceTupleKey) {
                BgServiceTupleKey other = (BgServiceTupleKey) aObject;
                return other.mServiceName.equals(mServiceName) &&
                    other.mEndpoint.equals(mEndpoint);
            }
            return false;
        }
    }
}
