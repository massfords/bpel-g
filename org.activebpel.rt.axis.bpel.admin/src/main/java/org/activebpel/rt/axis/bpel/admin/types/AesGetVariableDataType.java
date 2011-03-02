/**
 * AesGetVariableDataType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.admin.types;

public class AesGetVariableDataType  implements java.io.Serializable {
    private long pid;

    private java.lang.String variablePath;

    public AesGetVariableDataType() {
    }

    public AesGetVariableDataType(
           long pid,
           java.lang.String variablePath) {
           this.pid = pid;
           this.variablePath = variablePath;
    }


    /**
     * Gets the pid value for this AesGetVariableDataType.
     * 
     * @return pid
     */
    public long getPid() {
        return pid;
    }


    /**
     * Sets the pid value for this AesGetVariableDataType.
     * 
     * @param pid
     */
    public void setPid(long pid) {
        this.pid = pid;
    }


    /**
     * Gets the variablePath value for this AesGetVariableDataType.
     * 
     * @return variablePath
     */
    public java.lang.String getVariablePath() {
        return variablePath;
    }


    /**
     * Sets the variablePath value for this AesGetVariableDataType.
     * 
     * @param variablePath
     */
    public void setVariablePath(java.lang.String variablePath) {
        this.variablePath = variablePath;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AesGetVariableDataType)) return false;
        AesGetVariableDataType other = (AesGetVariableDataType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.pid == other.getPid() &&
            ((this.variablePath==null && other.getVariablePath()==null) || 
             (this.variablePath!=null &&
              this.variablePath.equals(other.getVariablePath())));
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
        _hashCode += new Long(getPid()).hashCode();
        if (getVariablePath() != null) {
            _hashCode += getVariablePath().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
