//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/ser/AeSimpleSerializer.java,v 1.1 2005/04/15 13:44:03 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.ser;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.ser.SimpleSerializer;
import org.xml.sax.Attributes;

/**
 * Subclass of Axis's SimpleSerializer that unwraps the value it was passed before
 * calling super.serialize(). This class also overcomes a bug in Axis's implementation
 * where it was not clearing the attributes it set and would pollute the attributes
 * object for the next Serializer invoked. 
 */
public class AeSimpleSerializer extends SimpleSerializer
{

   /**
    * Ctor
    * 
    * @param javaType
    * @param xmlType
    */
   public AeSimpleSerializer(Class javaType, QName xmlType)
   {
      super(javaType, xmlType);
   }

   /**
    * Ctor 
    * 
    * @param aJavaType
    * @param aXmlType
    * @param aTypeDesc
    */
   public AeSimpleSerializer(Class aJavaType, QName aXmlType, TypeDesc aTypeDesc)
   {
      super(aJavaType, aXmlType, aTypeDesc);
   }

   /**
    * The value should be an AeSimpleValueWrapper.
    * 
    * @see org.apache.axis.encoding.Serializer#serialize(javax.xml.namespace.QName, org.xml.sax.Attributes, java.lang.Object, org.apache.axis.encoding.SerializationContext)
    */
   public void serialize(QName aName, Attributes attributes, Object aValue,
         SerializationContext aContext) throws IOException
   {
      Object value = ((AeSimpleValueWrapper) aValue).getDelegate();
      super.serialize(aName, attributes, value, aContext);
   }
}