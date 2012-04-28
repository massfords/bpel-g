package bpelg.packaging.ode;

import bpelg.services.deploy.types.pdd.PersistenceType;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BgPddInfo {
    private PersistenceType persistenceType = PersistenceType.FULL;
    private final AeProcessDef processDef;
    private String location;
    private final Map<String,BgPlink> partnerLinks = new HashMap<String,BgPlink>();
    
    public BgPddInfo(AeProcessDef processDef, String location) {
        this.processDef = processDef;
        setLocation(location);
    }

    public PersistenceType getPersistenceType() {
        return persistenceType;
    }

    public void setPersistenceType(PersistenceType persistenceType) {
        this.persistenceType = persistenceType;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public AeProcessDef getProcessDef() {
        return processDef;
    }
    
    public QName getProcessName() {
        return processDef.getQName();
    }
    
    public String getLocation() {
        return location;
    }
    
    public void addProvide(String aPlinkName, QName aService, String aEndpoint, List<Element> aPolicies) {
        BgPlink plink = getOrCreate(aPlinkName);
        plink.myService = aService;
        plink.myEndpoint = aEndpoint;
        plink.myPolicies = aPolicies;
    }
    
    public void addInvoke(String aPlinkName, QName aService, String aEndpoint, List<Element> aPolicies, Element aEpr) {
        BgPlink plink = getOrCreate(aPlinkName);
        plink.partnerService = aService;
        plink.partnerEndpoint = aEndpoint;
        plink.partnerPolicies = aPolicies;
        plink.epr = aEpr;
    }
    
    public BgPlink getBgPlink(String aName) {
        return partnerLinks.get(aName);
    }
    
    public Collection<BgPlink> getBgPlinks() {
        return partnerLinks.values();
    }
    
    protected BgPlink getOrCreate(String aName) {
        BgPlink plink = getBgPlink(aName);
        if (plink == null) {
            plink = new BgPlink();
            plink.name = aName;
            partnerLinks.put(aName, plink);
        }
        return plink;
        
    }
    
    public static class BgPlink {
        public Element epr;
		public String name;
        public QName myService;
        public String myEndpoint;
        public QName partnerService;
        public String partnerEndpoint;
        public List<Element> myPolicies;
        public List<Element> partnerPolicies;
        
        public boolean hasMyRole() {
            return myService != null;
        }
        
        public boolean hasPartnerRole() {
            return partnerService != null;
        }
    }
}
