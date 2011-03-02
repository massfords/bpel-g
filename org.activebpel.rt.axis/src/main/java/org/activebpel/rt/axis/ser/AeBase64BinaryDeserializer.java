//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/ser/AeBase64BinaryDeserializer.java,v 1.1 2006/09/07 15:19:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.ser; 

import javax.xml.namespace.QName;

import org.activebpel.rt.xml.schema.AeSchemaBase64Binary;
import org.apache.axis.encoding.ser.SimpleDeserializer;

/**
 * Deserializer for schema type xsd:base64Binary
 */
public class AeBase64BinaryDeserializer extends SimpleDeserializer
{
   /**
    * The Deserializer is constructed with the xmlType and javaType
    */
   public AeBase64BinaryDeserializer(Class javaType, QName xmlType)
   {
      super(javaType, xmlType);
   }

   /**
    * @see org.activebpel.rt.axis.ser.AeAbstractSchemaTypeDeserializer#makeValueInternal(java.lang.String)
    */
   protected Object makeValueInternal(String aSource)
   {
      return new AeSchemaBase64Binary(aSource);
   }
}
 