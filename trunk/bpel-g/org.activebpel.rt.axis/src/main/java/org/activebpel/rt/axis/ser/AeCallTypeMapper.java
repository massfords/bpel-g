//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/ser/AeCallTypeMapper.java,v 1.1 2005/04/20 19:22:46 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.ser; 

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.DeserializerFactory;
import javax.xml.rpc.encoding.SerializerFactory;

import org.apache.axis.client.Call;

/**
 * Implements the type mapper interface for an Axis Call object. This object only
 * works with Axis specific Serializer and Deserializer factories.
 */
public class AeCallTypeMapper implements IAeTypeMapper
{
   /** Call that contains the type mapping info */
   private Call mCall;
   
   /**
    * Ctor creates the mapper with the call object.
    * 
    * @param aCall
    */
   public AeCallTypeMapper(Call aCall)
   {
      mCall = aCall;
   }
   
   /**
    * @see org.activebpel.rt.axis.ser.IAeTypeMapper#register(java.lang.Class, javax.xml.namespace.QName, javax.xml.rpc.encoding.SerializerFactory, javax.xml.rpc.encoding.DeserializerFactory)
    */
   public void register(Class aJavaType, QName aQName,
         SerializerFactory aSerializerFactory,
         DeserializerFactory aDeserializerFactory)
   {
      getCall().registerTypeMapping(aJavaType, aQName, 
            (org.apache.axis.encoding.SerializerFactory)aSerializerFactory, 
            (org.apache.axis.encoding.DeserializerFactory)aDeserializerFactory);
   }

   /**
    * Getter for the call
    */
   protected Call getCall()
   {
      return mCall;
   }

}
 