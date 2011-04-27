package org.activebpel.rt.bpel.server.admin.jmx;

import java.beans.ConstructorProperties;

import org.activebpel.rt.xml.AeQName;

// FIXME delete
public class AeServiceDeploymentBean {
    private String mServiceName;
    private AeQName mProcessName;
    private String mPartnerLinkName;
    private String mBinding;

    @ConstructorProperties({"serviceName", "processName", "partnerLinkName", "binding"})
    public AeServiceDeploymentBean(String aServiceName, AeQName aProcessName,
            String aPartnerLinkName, String aBinding) {
        super();
        mServiceName = aServiceName;
        mProcessName = aProcessName;
        mPartnerLinkName = aPartnerLinkName;
        mBinding = aBinding;
    }

    public String getServiceName() {
        return mServiceName;
    }
    public void setServiceName(String aServiceName) {
        mServiceName = aServiceName;
    }
    public AeQName getProcessName() {
        return mProcessName;
    }
    public void setProcessName(AeQName aProcessName) {
        mProcessName = aProcessName;
    }
    public String getPartnerLinkName() {
        return mPartnerLinkName;
    }
    public void setPartnerLinkName(String aPartnerLinkName) {
        mPartnerLinkName = aPartnerLinkName;
    }
    public String getBinding() {
        return mBinding;
    }
    public void setBinding(String aBinding) {
        mBinding = aBinding;
    }
}
