package org.activebpel.rt.bpel.server.admin.jmx;

import java.beans.ConstructorProperties;

import javax.xml.namespace.QName;

public class AeServiceDeploymentBean {
    private String mServiceName;
    private QName mProcessName;
    private String mPartnerLinkName;
    private String mBinding;

    @ConstructorProperties({"serviceName", "processName", "partnerLinkName", "binding"})
    public AeServiceDeploymentBean(String aServiceName, QName aProcessName,
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
    public QName getProcessName() {
        return mProcessName;
    }
    public void setProcessName(QName aProcessName) {
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
