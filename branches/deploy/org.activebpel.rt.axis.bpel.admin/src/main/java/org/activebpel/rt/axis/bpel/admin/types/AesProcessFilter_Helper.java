/**
 * AesProcessFilter_Helper.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.admin.types;

public class AesProcessFilter_Helper {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AesProcessFilter.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesProcessFilter"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processCompleteEnd");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "processCompleteEnd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processCompleteStart");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "processCompleteStart"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processCreateEnd");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "processCreateEnd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processCreateStart");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "processCreateStart"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "processName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "QName"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processState");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "processState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("advancedQuery");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "advancedQuery"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
