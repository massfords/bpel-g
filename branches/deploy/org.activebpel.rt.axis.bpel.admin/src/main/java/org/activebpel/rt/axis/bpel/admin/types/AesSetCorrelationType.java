/**
 * AesSetCorrelationType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.admin.types;

public class AesSetCorrelationType  implements java.io.Serializable {
    private long pid;

    private java.lang.String locationPath;

    private java.lang.String data;

    public AesSetCorrelationType() {
    }

    public AesSetCorrelationType(
           long pid,
           java.lang.String locationPath,
           java.lang.String data) {
           this.pid = pid;
           this.locationPath = locationPath;
           this.data = data;
    }


    /**
     * Gets the pid value for this AesSetCorrelationType.
     * 
     * @return pid
     */
    public long getPid() {
        return pid;
    }


    /**
     * Sets the pid value for this AesSetCorrelationType.
     * 
     * @param pid
     */
    public void setPid(long pid) {
        this.pid = pid;
    }


    /**
     * Gets the locationPath value for this AesSetCorrelationType.
     * 
     * @return locationPath
     */
    public java.lang.String getLocationPath() {
        return locationPath;
    }


    /**
     * Sets the locationPath value for this AesSetCorrelationType.
     * 
     * @param locationPath
     */
    public void setLocationPath(java.lang.String locationPath) {
        this.locationPath = locationPath;
    }


    /**
     * Gets the data value for this AesSetCorrelationType.
     * 
     * @return data
     */
    public java.lang.String getData() {
        return data;
    }


    /**
     * Sets the data value for this AesSetCorrelationType.
     * 
     * @param data
     */
    public void setData(java.lang.String data) {
        this.data = data;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AesSetCorrelationType)) return false;
        AesSetCorrelationType other = (AesSetCorrelationType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.pid == other.getPid() &&
            ((this.locationPath==null && other.getLocationPath()==null) || 
             (this.locationPath!=null &&
              this.locationPath.equals(other.getLocationPath()))) &&
            ((this.data==null && other.getData()==null) || 
             (this.data!=null &&
              this.data.equals(other.getData())));
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
        if (getLocationPath() != null) {
            _hashCode += getLocationPath().hashCode();
        }
        if (getData() != null) {
            _hashCode += getData().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
