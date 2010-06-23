/**
 * AesAttachmentItemNumberList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.admin.types;

public class AesAttachmentItemNumberList  implements java.io.Serializable {
    private java.lang.Integer[] itemNumber;

    public AesAttachmentItemNumberList() {
    }

    public AesAttachmentItemNumberList(
           java.lang.Integer[] itemNumber) {
           this.itemNumber = itemNumber;
    }


    /**
     * Gets the itemNumber value for this AesAttachmentItemNumberList.
     * 
     * @return itemNumber
     */
    public java.lang.Integer[] getItemNumber() {
        return itemNumber;
    }


    /**
     * Sets the itemNumber value for this AesAttachmentItemNumberList.
     * 
     * @param itemNumber
     */
    public void setItemNumber(java.lang.Integer[] itemNumber) {
        this.itemNumber = itemNumber;
    }

    public java.lang.Integer getItemNumber(int i) {
        return this.itemNumber[i];
    }

    public void setItemNumber(int i, java.lang.Integer _value) {
        this.itemNumber[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AesAttachmentItemNumberList)) return false;
        AesAttachmentItemNumberList other = (AesAttachmentItemNumberList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.itemNumber==null && other.getItemNumber()==null) || 
             (this.itemNumber!=null &&
              java.util.Arrays.equals(this.itemNumber, other.getItemNumber())));
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
        if (getItemNumber() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getItemNumber());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getItemNumber(), i);
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
