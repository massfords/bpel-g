/**
 * AesAttachmentAttributeList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.admin.types;

public class AesAttachmentAttributeList  implements java.io.Serializable {
    private org.activebpel.rt.axis.bpel.admin.types.AesAttachmentAttribute[] attribute;

    public AesAttachmentAttributeList() {
    }

    public AesAttachmentAttributeList(
           org.activebpel.rt.axis.bpel.admin.types.AesAttachmentAttribute[] attribute) {
           this.attribute = attribute;
    }


    /**
     * Gets the attribute value for this AesAttachmentAttributeList.
     * 
     * @return attribute
     */
    public org.activebpel.rt.axis.bpel.admin.types.AesAttachmentAttribute[] getAttribute() {
        return attribute;
    }


    /**
     * Sets the attribute value for this AesAttachmentAttributeList.
     * 
     * @param attribute
     */
    public void setAttribute(org.activebpel.rt.axis.bpel.admin.types.AesAttachmentAttribute[] attribute) {
        this.attribute = attribute;
    }

    public org.activebpel.rt.axis.bpel.admin.types.AesAttachmentAttribute getAttribute(int i) {
        return this.attribute[i];
    }

    public void setAttribute(int i, org.activebpel.rt.axis.bpel.admin.types.AesAttachmentAttribute _value) {
        this.attribute[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AesAttachmentAttributeList)) return false;
        AesAttachmentAttributeList other = (AesAttachmentAttributeList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.attribute==null && other.getAttribute()==null) || 
             (this.attribute!=null &&
              java.util.Arrays.equals(this.attribute, other.getAttribute())));
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
        if (getAttribute() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAttribute());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAttribute(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
