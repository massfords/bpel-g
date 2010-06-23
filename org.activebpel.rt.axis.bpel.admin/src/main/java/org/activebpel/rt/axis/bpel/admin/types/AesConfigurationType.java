/**
 * AesConfigurationType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.admin.types;

public class AesConfigurationType  implements java.io.Serializable {
    private java.lang.String xmlString;

    public AesConfigurationType() {
    }

    public AesConfigurationType(
           java.lang.String xmlString) {
           this.xmlString = xmlString;
    }


    /**
     * Gets the xmlString value for this AesConfigurationType.
     * 
     * @return xmlString
     */
    public java.lang.String getXmlString() {
        return xmlString;
    }


    /**
     * Sets the xmlString value for this AesConfigurationType.
     * 
     * @param xmlString
     */
    public void setXmlString(java.lang.String xmlString) {
        this.xmlString = xmlString;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AesConfigurationType)) return false;
        AesConfigurationType other = (AesConfigurationType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.xmlString==null && other.getXmlString()==null) || 
             (this.xmlString!=null &&
              this.xmlString.equals(other.getXmlString())));
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
        if (getXmlString() != null) {
            _hashCode += getXmlString().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
