/**
 * AesProcessEventHandlerOutput.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.eventhandler.types;

public class AesProcessEventHandlerOutput  implements java.io.Serializable {
    private boolean response;

    public AesProcessEventHandlerOutput() {
    }

    public AesProcessEventHandlerOutput(
           boolean response) {
           this.response = response;
    }


    /**
     * Gets the response value for this AesProcessEventHandlerOutput.
     * 
     * @return response
     */
    public boolean isResponse() {
        return response;
    }


    /**
     * Sets the response value for this AesProcessEventHandlerOutput.
     * 
     * @param response
     */
    public void setResponse(boolean response) {
        this.response = response;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AesProcessEventHandlerOutput)) return false;
        AesProcessEventHandlerOutput other = (AesProcessEventHandlerOutput) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.response == other.isResponse();
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
        _hashCode += (isResponse() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

}
