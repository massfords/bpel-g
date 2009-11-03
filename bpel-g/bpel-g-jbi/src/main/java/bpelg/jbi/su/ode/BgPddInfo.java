package bpelg.jbi.su.ode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

public class BgPddInfo {
    private QName mProcessName;
    private String mLocation;
    private Map<String,BgPlink> mPartnerLinks = new HashMap();
    
    public BgPddInfo(QName aProcessName, String aLocation) { 
        mProcessName = aProcessName;
        mLocation = aLocation;
    }
    
    public QName getProcessName() {
        return mProcessName;
    }
    
    public String getLocation() {
        return mLocation;
    }
    
    public void addProvide(String aPlinkName, QName aService, String aEndpoint) {
        BgPlink plink = getOrCreate(aPlinkName);
        plink.myService = aService;
        plink.myEndpoint = aEndpoint;
    }
    
    public void addInvoke(String aPlinkName, QName aService, String aEndpoint) {
        BgPlink plink = getOrCreate(aPlinkName);
        plink.partnerService = aService;
        plink.partnerEndpoint = aEndpoint;
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
        
        public boolean hasMyRole() {
            return myService != null;
        }
        
        public boolean hasPartnerRole() {
            return partnerService != null;
        }
    }
}
