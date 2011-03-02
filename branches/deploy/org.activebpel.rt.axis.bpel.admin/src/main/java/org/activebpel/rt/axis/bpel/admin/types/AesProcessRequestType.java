/**
 * AesProcessRequestType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.admin.types;

public class AesProcessRequestType  implements java.io.Serializable {
    private long cid;

    private long pid;

    private java.lang.String endpointURL;

    public AesProcessRequestType() {
    }

    public AesProcessRequestType(
           long cid,
           long pid,
           java.lang.String endpointURL) {
           this.cid = cid;
           this.pid = pid;
           this.endpointURL = endpointURL;
    }


    /**
     * Gets the cid value for this AesProcessRequestType.
     * 
     * @return cid
     */
    public long getCid() {
        return cid;
    }


    /**
     * Sets the cid value for this AesProcessRequestType.
     * 
     * @param cid
     */
    public void setCid(long cid) {
        this.cid = cid;
    }


    /**
     * Gets the pid value for this AesProcessRequestType.
     * 
     * @return pid
     */
    public long getPid() {
        return pid;
    }


    /**
     * Sets the pid value for this AesProcessRequestType.
     * 
     * @param pid
     */
    public void setPid(long pid) {
        this.pid = pid;
    }


    /**
     * Gets the endpointURL value for this AesProcessRequestType.
     * 
     * @return endpointURL
     */
    public java.lang.String getEndpointURL() {
        return endpointURL;
    }


    /**
     * Sets the endpointURL value for this AesProcessRequestType.
     * 
     * @param endpointURL
     */
    public void setEndpointURL(java.lang.String endpointURL) {
        this.endpointURL = endpointURL;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AesProcessRequestType)) return false;
        AesProcessRequestType other = (AesProcessRequestType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.cid == other.getCid() &&
            this.pid == other.getPid() &&
            ((this.endpointURL==null && other.getEndpointURL()==null) || 
             (this.endpointURL!=null &&
              this.endpointURL.equals(other.getEndpointURL())));
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
        _hashCode += new Long(getCid()).hashCode();
        _hashCode += new Long(getPid()).hashCode();
        if (getEndpointURL() != null) {
            _hashCode += getEndpointURL().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
