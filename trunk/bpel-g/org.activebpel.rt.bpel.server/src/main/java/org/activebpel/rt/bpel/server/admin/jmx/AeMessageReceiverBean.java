package org.activebpel.rt.bpel.server.admin.jmx;

import java.beans.ConstructorProperties;
import java.util.Map;

import org.activebpel.rt.xml.AeQName;

public class AeMessageReceiverBean {

    private String mPartnerLinkName;
    private AeQName mPortType;
    private String mOperation;
    private long mProcessId;
    private Map<AeQName, String> mCorrelationData;
    private int mLocationPathId;

    public AeMessageReceiverBean() {

    }

    @ConstructorProperties({"processId", "partnerLinkName", "portType", "operation", "correlationData", "locationPathId"})
    public AeMessageReceiverBean(long aProcessId, String aPartnerLinkName, AeQName aPortType,
                                 String aOperation, Map<AeQName, String> aCorrelationData, int aLocationPathId) {
        super();
        mProcessId = aProcessId;
        mPartnerLinkName = aPartnerLinkName;
        mPortType = aPortType;
        mOperation = aOperation;
        mCorrelationData = aCorrelationData;
        mLocationPathId = aLocationPathId;
    }

    public String getPartnerLinkName() {
        return mPartnerLinkName;
    }

    public void setPartnerLinkName(String aPartnerLinkName) {
        mPartnerLinkName = aPartnerLinkName;
    }

    public AeQName getPortType() {
        return mPortType;
    }

    public void setPortType(AeQName aPortType) {
        mPortType = aPortType;
    }

    public String getOperation() {
        return mOperation;
    }

    public void setOperation(String aOperation) {
        mOperation = aOperation;
    }

    public long getProcessId() {
        return mProcessId;
    }

    public void setProcessId(long aProcessId) {
        mProcessId = aProcessId;
    }

    public Map<AeQName, String> getCorrelationData() {
        return mCorrelationData;
    }

    public void setCorrelationData(Map<AeQName, String> aCorrelationData) {
        mCorrelationData = aCorrelationData;
    }

    public int getLocationPathId() {
        return mLocationPathId;
    }

    public void setLocationPathId(int aLocationPathId) {
        mLocationPathId = aLocationPathId;
    }
}
