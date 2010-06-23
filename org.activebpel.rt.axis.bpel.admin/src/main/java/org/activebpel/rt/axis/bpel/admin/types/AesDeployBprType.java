/**
 * AesDeployBprType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.admin.types;

public class AesDeployBprType  implements java.io.Serializable {
    private java.lang.String bprFilename;

    private java.lang.String base64File;

    public AesDeployBprType() {
    }

    public AesDeployBprType(
           java.lang.String bprFilename,
           java.lang.String base64File) {
           this.bprFilename = bprFilename;
           this.base64File = base64File;
    }


    /**
     * Gets the bprFilename value for this AesDeployBprType.
     * 
     * @return bprFilename
     */
    public java.lang.String getBprFilename() {
        return bprFilename;
    }


    /**
     * Sets the bprFilename value for this AesDeployBprType.
     * 
     * @param bprFilename
     */
    public void setBprFilename(java.lang.String bprFilename) {
        this.bprFilename = bprFilename;
    }


    /**
     * Gets the base64File value for this AesDeployBprType.
     * 
     * @return base64File
     */
    public java.lang.String getBase64File() {
        return base64File;
    }


    /**
     * Sets the base64File value for this AesDeployBprType.
     * 
     * @param base64File
     */
    public void setBase64File(java.lang.String base64File) {
        this.base64File = base64File;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AesDeployBprType)) return false;
        AesDeployBprType other = (AesDeployBprType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.bprFilename==null && other.getBprFilename()==null) || 
             (this.bprFilename!=null &&
              this.bprFilename.equals(other.getBprFilename()))) &&
            ((this.base64File==null && other.getBase64File()==null) || 
             (this.base64File!=null &&
              this.base64File.equals(other.getBase64File())));
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
        if (getBprFilename() != null) {
            _hashCode += getBprFilename().hashCode();
        }
        if (getBase64File() != null) {
            _hashCode += getBase64File().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
