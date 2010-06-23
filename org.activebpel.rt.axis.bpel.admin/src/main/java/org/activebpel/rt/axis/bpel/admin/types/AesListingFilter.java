/**
 * AesListingFilter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.admin.types;

public class AesListingFilter  implements java.io.Serializable {
    private int listStart;

    private int maxReturn;

    public AesListingFilter() {
    }

    public AesListingFilter(
           int listStart,
           int maxReturn) {
           this.listStart = listStart;
           this.maxReturn = maxReturn;
    }


    /**
     * Gets the listStart value for this AesListingFilter.
     * 
     * @return listStart
     */
    public int getListStart() {
        return listStart;
    }


    /**
     * Sets the listStart value for this AesListingFilter.
     * 
     * @param listStart
     */
    public void setListStart(int listStart) {
        this.listStart = listStart;
    }


    /**
     * Gets the maxReturn value for this AesListingFilter.
     * 
     * @return maxReturn
     */
    public int getMaxReturn() {
        return maxReturn;
    }


    /**
     * Sets the maxReturn value for this AesListingFilter.
     * 
     * @param maxReturn
     */
    public void setMaxReturn(int maxReturn) {
        this.maxReturn = maxReturn;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AesListingFilter)) return false;
        AesListingFilter other = (AesListingFilter) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.listStart == other.getListStart() &&
            this.maxReturn == other.getMaxReturn();
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
        _hashCode += getListStart();
        _hashCode += getMaxReturn();
        __hashCodeCalc = false;
        return _hashCode;
    }

}
