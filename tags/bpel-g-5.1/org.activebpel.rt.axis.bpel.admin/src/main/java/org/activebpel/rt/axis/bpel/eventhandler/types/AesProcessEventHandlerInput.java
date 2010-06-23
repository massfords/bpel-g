/**
 * AesProcessEventHandlerInput.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.eventhandler.types;

public class AesProcessEventHandlerInput  implements java.io.Serializable {
    private long contextId;

    private long processId;

    private java.lang.String path;

    private int eventType;

    private java.lang.String faultName;

    private java.lang.String text;

    private javax.xml.namespace.QName name;

    private java.util.Calendar timestamp;

    public AesProcessEventHandlerInput() {
    }

    public AesProcessEventHandlerInput(
           long contextId,
           long processId,
           java.lang.String path,
           int eventType,
           java.lang.String faultName,
           java.lang.String text,
           javax.xml.namespace.QName name,
           java.util.Calendar timestamp) {
           this.contextId = contextId;
           this.processId = processId;
           this.path = path;
           this.eventType = eventType;
           this.faultName = faultName;
           this.text = text;
           this.name = name;
           this.timestamp = timestamp;
    }


    /**
     * Gets the contextId value for this AesProcessEventHandlerInput.
     * 
     * @return contextId
     */
    public long getContextId() {
        return contextId;
    }


    /**
     * Sets the contextId value for this AesProcessEventHandlerInput.
     * 
     * @param contextId
     */
    public void setContextId(long contextId) {
        this.contextId = contextId;
    }


    /**
     * Gets the processId value for this AesProcessEventHandlerInput.
     * 
     * @return processId
     */
    public long getProcessId() {
        return processId;
    }


    /**
     * Sets the processId value for this AesProcessEventHandlerInput.
     * 
     * @param processId
     */
    public void setProcessId(long processId) {
        this.processId = processId;
    }


    /**
     * Gets the path value for this AesProcessEventHandlerInput.
     * 
     * @return path
     */
    public java.lang.String getPath() {
        return path;
    }


    /**
     * Sets the path value for this AesProcessEventHandlerInput.
     * 
     * @param path
     */
    public void setPath(java.lang.String path) {
        this.path = path;
    }


    /**
     * Gets the eventType value for this AesProcessEventHandlerInput.
     * 
     * @return eventType
     */
    public int getEventType() {
        return eventType;
    }


    /**
     * Sets the eventType value for this AesProcessEventHandlerInput.
     * 
     * @param eventType
     */
    public void setEventType(int eventType) {
        this.eventType = eventType;
    }


    /**
     * Gets the faultName value for this AesProcessEventHandlerInput.
     * 
     * @return faultName
     */
    public java.lang.String getFaultName() {
        return faultName;
    }


    /**
     * Sets the faultName value for this AesProcessEventHandlerInput.
     * 
     * @param faultName
     */
    public void setFaultName(java.lang.String faultName) {
        this.faultName = faultName;
    }


    /**
     * Gets the text value for this AesProcessEventHandlerInput.
     * 
     * @return text
     */
    public java.lang.String getText() {
        return text;
    }


    /**
     * Sets the text value for this AesProcessEventHandlerInput.
     * 
     * @param text
     */
    public void setText(java.lang.String text) {
        this.text = text;
    }


    /**
     * Gets the name value for this AesProcessEventHandlerInput.
     * 
     * @return name
     */
    public javax.xml.namespace.QName getName() {
        return name;
    }


    /**
     * Sets the name value for this AesProcessEventHandlerInput.
     * 
     * @param name
     */
    public void setName(javax.xml.namespace.QName name) {
        this.name = name;
    }


    /**
     * Gets the timestamp value for this AesProcessEventHandlerInput.
     * 
     * @return timestamp
     */
    public java.util.Calendar getTimestamp() {
        return timestamp;
    }


    /**
     * Sets the timestamp value for this AesProcessEventHandlerInput.
     * 
     * @param timestamp
     */
    public void setTimestamp(java.util.Calendar timestamp) {
        this.timestamp = timestamp;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AesProcessEventHandlerInput)) return false;
        AesProcessEventHandlerInput other = (AesProcessEventHandlerInput) obj;
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
            ((this.path==null && other.getPath()==null) || 
             (this.path!=null &&
              this.path.equals(other.getPath()))) &&
            this.eventType == other.getEventType() &&
            ((this.faultName==null && other.getFaultName()==null) || 
             (this.faultName!=null &&
              this.faultName.equals(other.getFaultName()))) &&
            ((this.text==null && other.getText()==null) || 
             (this.text!=null &&
              this.text.equals(other.getText()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
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
        if (getPath() != null) {
            _hashCode += getPath().hashCode();
        }
        _hashCode += getEventType();
        if (getFaultName() != null) {
            _hashCode += getFaultName().hashCode();
        }
        if (getText() != null) {
            _hashCode += getText().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getTimestamp() != null) {
            _hashCode += getTimestamp().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
