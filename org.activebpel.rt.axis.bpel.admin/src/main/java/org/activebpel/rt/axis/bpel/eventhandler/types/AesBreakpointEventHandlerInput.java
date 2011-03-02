/**
 * AesBreakpointEventHandlerInput.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.eventhandler.types;

public class AesBreakpointEventHandlerInput  implements java.io.Serializable {
    private long contextId;

    private long processId;

    private java.lang.String path;

    private javax.xml.namespace.QName processName;

    private java.util.Calendar timestamp;

    public AesBreakpointEventHandlerInput() {
    }

    public AesBreakpointEventHandlerInput(
           long contextId,
           long processId,
           java.lang.String path,
           javax.xml.namespace.QName processName,
           java.util.Calendar timestamp) {
           this.contextId = contextId;
           this.processId = processId;
           this.path = path;
           this.processName = processName;
           this.timestamp = timestamp;
    }


    /**
     * Gets the contextId value for this AesBreakpointEventHandlerInput.
     * 
     * @return contextId
     */
    public long getContextId() {
        return contextId;
    }


    /**
     * Sets the contextId value for this AesBreakpointEventHandlerInput.
     * 
     * @param contextId
     */
    public void setContextId(long contextId) {
        this.contextId = contextId;
    }


    /**
     * Gets the processId value for this AesBreakpointEventHandlerInput.
     * 
     * @return processId
     */
    public long getProcessId() {
        return processId;
    }


    /**
     * Sets the processId value for this AesBreakpointEventHandlerInput.
     * 
     * @param processId
     */
    public void setProcessId(long processId) {
        this.processId = processId;
    }


    /**
     * Gets the path value for this AesBreakpointEventHandlerInput.
     * 
     * @return path
     */
    public java.lang.String getPath() {
        return path;
    }


    /**
     * Sets the path value for this AesBreakpointEventHandlerInput.
     * 
     * @param path
     */
    public void setPath(java.lang.String path) {
        this.path = path;
    }


    /**
     * Gets the processName value for this AesBreakpointEventHandlerInput.
     * 
     * @return processName
     */
    public javax.xml.namespace.QName getProcessName() {
        return processName;
    }


    /**
     * Sets the processName value for this AesBreakpointEventHandlerInput.
     * 
     * @param processName
     */
    public void setProcessName(javax.xml.namespace.QName processName) {
        this.processName = processName;
    }


    /**
     * Gets the timestamp value for this AesBreakpointEventHandlerInput.
     * 
     * @return timestamp
     */
    public java.util.Calendar getTimestamp() {
        return timestamp;
    }


    /**
     * Sets the timestamp value for this AesBreakpointEventHandlerInput.
     * 
     * @param timestamp
     */
    public void setTimestamp(java.util.Calendar timestamp) {
        this.timestamp = timestamp;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AesBreakpointEventHandlerInput)) return false;
        AesBreakpointEventHandlerInput other = (AesBreakpointEventHandlerInput) obj;
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
            ((this.processName==null && other.getProcessName()==null) || 
             (this.processName!=null &&
              this.processName.equals(other.getProcessName()))) &&
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
        if (getProcessName() != null) {
            _hashCode += getProcessName().hashCode();
        }
        if (getTimestamp() != null) {
            _hashCode += getTimestamp().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
