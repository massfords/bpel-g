package bpelg.jbi;

import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AePartnerLinkDefKey;

public class BgBpelService {
    private QName mProcessName;
    private AePartnerLinkDefKey mPartnerLinkDefKey;
    private ServiceEndpoint mServiceEndpoint;
    
    public BgBpelService(QName aProcesName, AePartnerLinkDefKey aPlinkKey, ServiceEndpoint aServiceEndpoint) {
        setProcessName(aProcesName);
        setPartnerLinkDefKey(aPlinkKey);
        setServiceEndpoint(aServiceEndpoint);
    }

    public QName getProcessName() {
        return mProcessName;
    }

    protected void setProcessName(QName aProcessName) {
        mProcessName = aProcessName;
    }

    public AePartnerLinkDefKey getPartnerLinkDefKey() {
        return mPartnerLinkDefKey;
    }

    protected void setPartnerLinkDefKey(AePartnerLinkDefKey aPartnerLinkDefKey) {
        mPartnerLinkDefKey = aPartnerLinkDefKey;
    }

    public ServiceEndpoint getServiceEndpoint() {
        return mServiceEndpoint;
    }

    protected void setServiceEndpoint(ServiceEndpoint aServiceEndpoint) {
        mServiceEndpoint = aServiceEndpoint;
    }
}
