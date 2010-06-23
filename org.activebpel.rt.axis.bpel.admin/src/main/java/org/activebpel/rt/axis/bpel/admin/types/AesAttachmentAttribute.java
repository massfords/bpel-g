/**
 * AesAttachmentAttribute.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.admin.types;

public class AesAttachmentAttribute  implements java.io.Serializable {
    private java.lang.String attributeName;

    private java.lang.String attributeValue;

    public AesAttachmentAttribute() {
    }

    public AesAttachmentAttribute(
           java.lang.String attributeName,
           java.lang.String attributeValue) {
           this.attributeName = attributeName;
           this.attributeValue = attributeValue;
    }


    /**
     * Gets the attributeName value for this AesAttachmentAttribute.
     * 
     * @return attributeName
     */
    public java.lang.String getAttributeName() {
        return attributeName;
    }


    /**
     * Sets the attributeName value for this AesAttachmentAttribute.
     * 
     * @param attributeName
     */
    public void setAttributeName(java.lang.String attributeName) {
        this.attributeName = attributeName;
    }


    /**
     * Gets the attributeValue value for this AesAttachmentAttribute.
     * 
     * @return attributeValue
     */
    public java.lang.String getAttributeValue() {
        return attributeValue;
    }


    /**
     * Sets the attributeValue value for this AesAttachmentAttribute.
     * 
     * @param attributeValue
     */
    public void setAttributeValue(java.lang.String attributeValue) {
        this.attributeValue = attributeValue;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AesAttachmentAttribute)) return false;
        AesAttachmentAttribute other = (AesAttachmentAttribute) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.attributeName==null && other.getAttributeName()==null) || 
             (this.attributeName!=null &&
              this.attributeName.equals(other.getAttributeName()))) &&
            ((this.attributeValue==null && other.getAttributeValue()==null) || 
             (this.attributeValue!=null &&
              this.attributeValue.equals(other.getAttributeValue())));
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
        if (getAttributeName() != null) {
            _hashCode += getAttributeName().hashCode();
        }
        if (getAttributeValue() != null) {
            _hashCode += getAttributeValue().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
