/**
 * AesAddAttachmentResponseType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.admin.types;

public class AesAddAttachmentResponseType  implements java.io.Serializable {
    private long attachmentId;

    private org.activebpel.rt.axis.bpel.admin.types.AesAttachmentAttributeList attachmentAttributes;

    public AesAddAttachmentResponseType() {
    }

    public AesAddAttachmentResponseType(
           long attachmentId,
           org.activebpel.rt.axis.bpel.admin.types.AesAttachmentAttributeList attachmentAttributes) {
           this.attachmentId = attachmentId;
           this.attachmentAttributes = attachmentAttributes;
    }


    /**
     * Gets the attachmentId value for this AesAddAttachmentResponseType.
     * 
     * @return attachmentId
     */
    public long getAttachmentId() {
        return attachmentId;
    }


    /**
     * Sets the attachmentId value for this AesAddAttachmentResponseType.
     * 
     * @param attachmentId
     */
    public void setAttachmentId(long attachmentId) {
        this.attachmentId = attachmentId;
    }


    /**
     * Gets the attachmentAttributes value for this AesAddAttachmentResponseType.
     * 
     * @return attachmentAttributes
     */
    public org.activebpel.rt.axis.bpel.admin.types.AesAttachmentAttributeList getAttachmentAttributes() {
        return attachmentAttributes;
    }


    /**
     * Sets the attachmentAttributes value for this AesAddAttachmentResponseType.
     * 
     * @param attachmentAttributes
     */
    public void setAttachmentAttributes(org.activebpel.rt.axis.bpel.admin.types.AesAttachmentAttributeList attachmentAttributes) {
        this.attachmentAttributes = attachmentAttributes;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AesAddAttachmentResponseType)) return false;
        AesAddAttachmentResponseType other = (AesAddAttachmentResponseType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.attachmentId == other.getAttachmentId() &&
            ((this.attachmentAttributes==null && other.getAttachmentAttributes()==null) || 
             (this.attachmentAttributes!=null &&
              this.attachmentAttributes.equals(other.getAttachmentAttributes())));
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
        _hashCode += new Long(getAttachmentId()).hashCode();
        if (getAttachmentAttributes() != null) {
            _hashCode += getAttachmentAttributes().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
