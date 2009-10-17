/**
 * AesDigestType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.admin.types;

public class AesDigestType  implements java.io.Serializable {
    private byte[] digest;

    public AesDigestType() {
    }

    public AesDigestType(
           byte[] digest) {
           this.digest = digest;
    }


    /**
     * Gets the digest value for this AesDigestType.
     * 
     * @return digest
     */
    public byte[] getDigest() {
        return digest;
    }


    /**
     * Sets the digest value for this AesDigestType.
     * 
     * @param digest
     */
    public void setDigest(byte[] digest) {
        this.digest = digest;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AesDigestType)) return false;
        AesDigestType other = (AesDigestType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.digest==null && other.getDigest()==null) || 
             (this.digest!=null &&
              java.util.Arrays.equals(this.digest, other.getDigest())));
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
        if (getDigest() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDigest());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDigest(), i);
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
