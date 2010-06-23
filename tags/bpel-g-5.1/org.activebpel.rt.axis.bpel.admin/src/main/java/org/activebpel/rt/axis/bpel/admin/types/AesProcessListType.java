/**
 * AesProcessListType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.admin.types;

public class AesProcessListType  implements java.io.Serializable {
    private org.activebpel.rt.axis.bpel.admin.types.AesProcessListResult response;

    public AesProcessListType() {
    }

    public AesProcessListType(
           org.activebpel.rt.axis.bpel.admin.types.AesProcessListResult response) {
           this.response = response;
    }


    /**
     * Gets the response value for this AesProcessListType.
     * 
     * @return response
     */
    public org.activebpel.rt.axis.bpel.admin.types.AesProcessListResult getResponse() {
        return response;
    }


    /**
     * Sets the response value for this AesProcessListType.
     * 
     * @param response
     */
    public void setResponse(org.activebpel.rt.axis.bpel.admin.types.AesProcessListResult response) {
        this.response = response;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AesProcessListType)) return false;
        AesProcessListType other = (AesProcessListType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.response==null && other.getResponse()==null) || 
             (this.response!=null &&
              this.response.equals(other.getResponse())));
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
        if (getResponse() != null) {
            _hashCode += getResponse().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
