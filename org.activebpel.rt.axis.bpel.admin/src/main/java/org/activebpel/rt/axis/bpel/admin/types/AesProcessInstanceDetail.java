/**
 * AesProcessInstanceDetail.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.admin.types;

public class AesProcessInstanceDetail  implements java.io.Serializable {
    private java.util.Calendar ended;

    private javax.xml.namespace.QName name;

    private long processId;

    private java.util.Calendar started;

    private int state;

    private int stateReason;

    public AesProcessInstanceDetail() {
    }

    public AesProcessInstanceDetail(
           java.util.Calendar ended,
           javax.xml.namespace.QName name,
           long processId,
           java.util.Calendar started,
           int state,
           int stateReason) {
           this.ended = ended;
           this.name = name;
           this.processId = processId;
           this.started = started;
           this.state = state;
           this.stateReason = stateReason;
    }


    /**
     * Gets the ended value for this AesProcessInstanceDetail.
     * 
     * @return ended
     */
    public java.util.Calendar getEnded() {
        return ended;
    }


    /**
     * Sets the ended value for this AesProcessInstanceDetail.
     * 
     * @param ended
     */
    public void setEnded(java.util.Calendar ended) {
        this.ended = ended;
    }


    /**
     * Gets the name value for this AesProcessInstanceDetail.
     * 
     * @return name
     */
    public javax.xml.namespace.QName getName() {
        return name;
    }


    /**
     * Sets the name value for this AesProcessInstanceDetail.
     * 
     * @param name
     */
    public void setName(javax.xml.namespace.QName name) {
        this.name = name;
    }


    /**
     * Gets the processId value for this AesProcessInstanceDetail.
     * 
     * @return processId
     */
    public long getProcessId() {
        return processId;
    }


    /**
     * Sets the processId value for this AesProcessInstanceDetail.
     * 
     * @param processId
     */
    public void setProcessId(long processId) {
        this.processId = processId;
    }


    /**
     * Gets the started value for this AesProcessInstanceDetail.
     * 
     * @return started
     */
    public java.util.Calendar getStarted() {
        return started;
    }


    /**
     * Sets the started value for this AesProcessInstanceDetail.
     * 
     * @param started
     */
    public void setStarted(java.util.Calendar started) {
        this.started = started;
    }


    /**
     * Gets the state value for this AesProcessInstanceDetail.
     * 
     * @return state
     */
    public int getState() {
        return state;
    }


    /**
     * Sets the state value for this AesProcessInstanceDetail.
     * 
     * @param state
     */
    public void setState(int state) {
        this.state = state;
    }


    /**
     * Gets the stateReason value for this AesProcessInstanceDetail.
     * 
     * @return stateReason
     */
    public int getStateReason() {
        return stateReason;
    }


    /**
     * Sets the stateReason value for this AesProcessInstanceDetail.
     * 
     * @param stateReason
     */
    public void setStateReason(int stateReason) {
        this.stateReason = stateReason;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AesProcessInstanceDetail)) return false;
        AesProcessInstanceDetail other = (AesProcessInstanceDetail) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.ended==null && other.getEnded()==null) || 
             (this.ended!=null &&
              this.ended.equals(other.getEnded()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            this.processId == other.getProcessId() &&
            ((this.started==null && other.getStarted()==null) || 
             (this.started!=null &&
              this.started.equals(other.getStarted()))) &&
            this.state == other.getState() &&
            this.stateReason == other.getStateReason();
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
        if (getEnded() != null) {
            _hashCode += getEnded().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        _hashCode += new Long(getProcessId()).hashCode();
        if (getStarted() != null) {
            _hashCode += getStarted().hashCode();
        }
        _hashCode += getState();
        _hashCode += getStateReason();
        __hashCodeCalc = false;
        return _hashCode;
    }

}
