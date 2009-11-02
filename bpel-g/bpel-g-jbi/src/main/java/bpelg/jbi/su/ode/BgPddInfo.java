package bpelg.jbi.su.ode;

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
    
    protected BgPlink getOrCreate(String aName) {
        BgPlink plink = getBgPlink(aName);
        if (plink == null) {
            plink = new BgPlink();
            mPartnerLinks.put(aName, plink);
        }
        return plink;
        
    }
    
    protected static class BgPlink {
        protected String name;
        protected QName myService;
        protected String myEndpoint;
        protected QName partnerService;
        protected String partnerEndpoint;
    }
}
