/**
 * AesEngineAlertHandlerInput.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.eventhandler.types;

public class AesEngineAlertHandlerInput  implements java.io.Serializable {
    private long contextId;

    private long processId;

    private int eventType;

    private javax.xml.namespace.QName processName;

    private java.lang.String location;

    private javax.xml.namespace.QName faultName;

    private java.lang.String details;

    private java.util.Calendar timestamp;

    public AesEngineAlertHandlerInput() {
    }

    public AesEngineAlertHandlerInput(
           long contextId,
           long processId,
           int eventType,
           javax.xml.namespace.QName processName,
           java.lang.String location,
           javax.xml.namespace.QName faultName,
           java.lang.String details,
           java.util.Calendar timestamp) {
           this.contextId = contextId;
           this.processId = processId;
           this.eventType = eventType;
           this.processName = processName;
           this.location = location;
           this.faultName = faultName;
           this.details = details;
           this.timestamp = timestamp;
    }


    /**
     * Gets the contextId value for this AesEngineAlertHandlerInput.
     * 
     * @return contextId
     */
    public long getContextId() {
        return contextId;
    }


    /**
     * Sets the contextId value for this AesEngineAlertHandlerInput.
     * 
     * @param contextId
     */
    public void setContextId(long contextId) {
        this.contextId = contextId;
    }


    /**
     * Gets the processId value for this AesEngineAlertHandlerInput.
     * 
     * @return processId
     */
    public long getProcessId() {
        return processId;
    }


    /**
     * Sets the processId value for this AesEngineAlertHandlerInput.
     * 
     * @param processId
     */
    public void setProcessId(long processId) {
        this.processId = processId;
    }


    /**
     * Gets the eventType value for this AesEngineAlertHandlerInput.
     * 
     * @return eventType
     */
    public int getEventType() {
        return eventType;
    }


    /**
     * Sets the eventType value for this AesEngineAlertHandlerInput.
     * 
     * @param eventType
     */
    public void setEventType(int eventType) {
        this.eventType = eventType;
    }


    /**
     * Gets the processName value for this AesEngineAlertHandlerInput.
     * 
     * @return processName
     */
    public javax.xml.namespace.QName getProcessName() {
        return processName;
    }


    /**
     * Sets the processName value for this AesEngineAlertHandlerInput.
     * 
     * @param processName
     */
    public void setProcessName(javax.xml.namespace.QName processName) {
        this.processName = processName;
    }


    /**
     * Gets the location value for this AesEngineAlertHandlerInput.
     * 
     * @return location
     */
    public java.lang.String getLocation() {
        return location;
    }


    /**
     * Sets the location value for this AesEngineAlertHandlerInput.
     * 
     * @param location
     */
    public void setLocation(java.lang.String location) {
        this.location = location;
    }


    /**
     * Gets the faultName value for this AesEngineAlertHandlerInput.
     * 
     * @return faultName
     */
    public javax.xml.namespace.QName getFaultName() {
        return faultName;
    }


    /**
     * Sets the faultName value for this AesEngineAlertHandlerInput.
     * 
     * @param faultName
     */
    public void setFaultName(javax.xml.namespace.QName faultName) {
        this.faultName = faultName;
    }


    /**
     * Gets the details value for this AesEngineAlertHandlerInput.
     * 
     * @return details
     */
    public java.lang.String getDetails() {
        return details;
    }


    /**
     * Sets the details value for this AesEngineAlertHandlerInput.
     * 
     * @param details
     */
    public void setDetails(java.lang.String details) {
        this.details = details;
    }


    /**
     * Gets the timestamp value for this AesEngineAlertHandlerInput.
     * 
     * @return timestamp
     */
    public java.util.Calendar getTimestamp() {
        return timestamp;
    }


    /**
     * Sets the timestamp value for this AesEngineAlertHandlerInput.
     * 
     * @param timestamp
     */
    public void setTimestamp(java.util.Calendar timestamp) {
        this.timestamp = timestamp;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AesEngineAlertHandlerInput)) return false;
        AesEngineAlertHandlerInput other = (AesEngineAlertHandlerInput) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.contextId == other.getContextId() &&
            this.processId == other.getProcessId() &&
            this.eventType == other.getEventType() &&
            ((this.processName==null && other.getProcessName()==null) || 
             (this.processName!=null &&
              this.processName.equals(other.getProcessName()))) &&
            ((this.location==null && other.getLocation()==null) || 
             (this.location!=null &&
              this.location.equals(other.getLocation()))) &&
            ((this.faultName==null && other.getFaultName()==null) || 
             (this.faultName!=null &&
              this.faultName.equals(other.getFaultName()))) &&
            ((this.details==null && other.getDetails()==null) || 
             (this.details!=null &&
              this.details.equals(other.getDetails()))) &&
            ((this.timestamp==null && other.getTimestamp()==null) || 
             (this.timestamp!=null &&
              this.timestamp.equals(other.getTimestamp())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += new Long(getContextId()).hashCode();
        _hashCode += new Long(getProcessId()).hashCode();
        _hashCode += getEventType();
        if (getProcessName() != null) {
            _hashCode += getProcessName().hashCode();
        }
        if (getLocation() != null) {
            _hashCode += getLocation().hashCode();
        }
        if (getFaultName() != null) {
            _hashCode += getFaultName().hashCode();
        }
        if (getDetails() != null) {
            _hashCode += getDetails().hashCode();
        }
        if (getTimestamp() != null) {
            _hashCode += getTimestamp().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
