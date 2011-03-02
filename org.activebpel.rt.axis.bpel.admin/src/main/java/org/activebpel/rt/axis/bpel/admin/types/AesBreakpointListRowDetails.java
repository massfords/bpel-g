/**
 * AesBreakpointListRowDetails.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.admin.types;

public class AesBreakpointListRowDetails  implements java.io.Serializable {
    private org.activebpel.rt.axis.bpel.admin.types.AesBreakpointInstanceDetail[] item;

    public AesBreakpointListRowDetails() {
    }

    public AesBreakpointListRowDetails(
           org.activebpel.rt.axis.bpel.admin.types.AesBreakpointInstanceDetail[] item) {
           this.item = item;
    }


    /**
     * Gets the item value for this AesBreakpointListRowDetails.
     * 
     * @return item
     */
    public org.activebpel.rt.axis.bpel.admin.types.AesBreakpointInstanceDetail[] getItem() {
        return item;
    }


    /**
     * Sets the item value for this AesBreakpointListRowDetails.
     * 
     * @param item
     */
    public void setItem(org.activebpel.rt.axis.bpel.admin.types.AesBreakpointInstanceDetail[] item) {
        this.item = item;
    }

    public org.activebpel.rt.axis.bpel.admin.types.AesBreakpointInstanceDetail getItem(int i) {
        return this.item[i];
    }

    public void setItem(int i, org.activebpel.rt.axis.bpel.admin.types.AesBreakpointInstanceDetail _value) {
        this.item[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AesBreakpointListRowDetails)) return false;
        AesBreakpointListRowDetails other = (AesBreakpointListRowDetails) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.item==null && other.getItem()==null) || 
             (this.item!=null &&
              java.util.Arrays.equals(this.item, other.getItem())));
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
        if (getItem() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getItem());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getItem(), i);
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
