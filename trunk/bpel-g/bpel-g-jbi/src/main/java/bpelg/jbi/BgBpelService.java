package bpelg.jbi;

import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AePartnerLinkDefKey;

/**
 * Simple struct that bridges the deployment info between JBI and bpel-g
 * 
 * @author markford
 */
public class BgBpelService {
    
    /** the process qname is necessary to route the message in bpel-g */
    private QName mProcessName;
    /** uniquely identifies the plink within the process (a string isn't sufficient since plink names aren't unique with scoped plinks) */
    private AePartnerLinkDefKey mPartnerLinkDefKey;
    /** QName of the JBI service */
    private QName mServiceName;
    /** name of the JBI endpoint */
    private String mEndpoint;
    /** artifact from JBI once the endpoint has been activated in the container. Needed for undeployment */
    private ServiceEndpoint mServiceEndpoint;
    private QName mPortType;
    
    public BgBpelService(QName aProcesName, AePartnerLinkDefKey aPlinkKey, QName aServiceName, String aEndpoint, QName aPortType) {
        setProcessName(aProcesName);
        setServiceName(aServiceName);
        setPartnerLinkDefKey(aPlinkKey);
        setEndpoint(aEndpoint);
        setPortType(aPortType);
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

    public void setServiceEndpoint(ServiceEndpoint aServiceEndpoint) {
        mServiceEndpoint = aServiceEndpoint;
    }

    public QName getServiceName() {
        return mServiceName;
    }

    protected void setServiceName(QName aServiceName) {
        mServiceName = aServiceName;
    }

    public String getEndpoint() {
        return mEndpoint;
    }

    protected void setEndpoint(String aEndpoint) {
        mEndpoint = aEndpoint;
    }
    
    protected void setPortType(QName aPortType) {
        mPortType = aPortType;
    }
    public QName getPortType() {
        return mPortType;
    }
}
