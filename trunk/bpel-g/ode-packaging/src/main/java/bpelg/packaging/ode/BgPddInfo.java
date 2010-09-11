package bpelg.packaging.ode;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AeProcessDef;
import org.w3c.dom.Element;

public class BgPddInfo {
    private AeProcessDef mProcessDef;
    private String mLocation;
    private Map<String,BgPlink> mPartnerLinks = new HashMap();
    
    public BgPddInfo(AeProcessDef aProcessDef, String aLocation) { 
        mProcessDef = aProcessDef;
        setLocation(aLocation);
    }
    
    public void setLocation(String aLocation) {
        mLocation = aLocation;
    }
    
    public AeProcessDef getProcessDef() {
        return mProcessDef;
    }
    
    public QName getProcessName() {
        return mProcessDef.getQName();
    }
    
    public String getLocation() {
        return mLocation;
    }
    
    public void addProvide(String aPlinkName, QName aService, String aEndpoint, List<Element> aPolicies) {
        BgPlink plink = getOrCreate(aPlinkName);
        plink.myService = aService;
        plink.myEndpoint = aEndpoint;
        plink.myPolicies = aPolicies;
    }
    
    public void addInvoke(String aPlinkName, QName aService, String aEndpoint, List<Element> aPolicies) {
        BgPlink plink = getOrCreate(aPlinkName);
        plink.partnerService = aService;
        plink.partnerEndpoint = aEndpoint;
        plink.partnerPolicies = aPolicies;
    }
    
    public BgPlink getBgPlink(String aName) {
        return mPartnerLinks.get(aName);
    }
    
    public Collection<BgPlink> getBgPlinks() {
        return mPartnerLinks.values();
    }
    
    protected BgPlink getOrCreate(String aName) {
        BgPlink plink = getBgPlink(aName);
        if (plink == null) {
            plink = new BgPlink();
            plink.name = aName;
            mPartnerLinks.put(aName, plink);
        }
        return plink;
        
    }
    
    public static class BgPlink {
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
