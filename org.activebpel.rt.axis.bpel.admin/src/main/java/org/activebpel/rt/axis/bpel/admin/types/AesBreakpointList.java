/**
 * AesBreakpointList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.admin.types;

public class AesBreakpointList  implements java.io.Serializable {
    private org.activebpel.rt.axis.bpel.admin.types.AesBreakpointListRowDetails rowDetails;

    private int totalRowCount;

    public AesBreakpointList() {
    }

    public AesBreakpointList(
           org.activebpel.rt.axis.bpel.admin.types.AesBreakpointListRowDetails rowDetails,
           int totalRowCount) {
           this.rowDetails = rowDetails;
           this.totalRowCount = totalRowCount;
    }


    /**
     * Gets the rowDetails value for this AesBreakpointList.
     * 
     * @return rowDetails
     */
    public org.activebpel.rt.axis.bpel.admin.types.AesBreakpointListRowDetails getRowDetails() {
        return rowDetails;
    }


    /**
     * Sets the rowDetails value for this AesBreakpointList.
     * 
     * @param rowDetails
     */
    public void setRowDetails(org.activebpel.rt.axis.bpel.admin.types.AesBreakpointListRowDetails rowDetails) {
        this.rowDetails = rowDetails;
    }


    /**
     * Gets the totalRowCount value for this AesBreakpointList.
     * 
     * @return totalRowCount
     */
    public int getTotalRowCount() {
        return totalRowCount;
    }


    /**
     * Sets the totalRowCount value for this AesBreakpointList.
     * 
     * @param totalRowCount
     */
    public void setTotalRowCount(int totalRowCount) {
        this.totalRowCount = totalRowCount;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AesBreakpointList)) return false;
        AesBreakpointList other = (AesBreakpointList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.rowDetails==null && other.getRowDetails()==null) || 
             (this.rowDetails!=null &&
              this.rowDetails.equals(other.getRowDetails()))) &&
            this.totalRowCount == other.getTotalRowCount();
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
        if (getRowDetails() != null) {
            _hashCode += getRowDetails().hashCode();
        }
        _hashCode += getTotalRowCount();
        __hashCodeCalc = false;
        return _hashCode;
    }

}
