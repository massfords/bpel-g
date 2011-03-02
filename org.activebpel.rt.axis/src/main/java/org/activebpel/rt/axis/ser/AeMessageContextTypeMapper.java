//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/ser/AeMessageContextTypeMapper.java,v 1.4 2006/09/07 15:19:54 ewittmann Exp $
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
import javax.xml.rpc.encoding.TypeMapping;

import org.apache.axis.MessageContext;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.TypeMappingRegistryImpl;

/**
 * Implements the type mapper interface for an Axis MessageContext.
 * 
 */
public class AeMessageContextTypeMapper implements IAeTypeMapper
{
   /** MessageContext that contains the type mapping information */
   private MessageContext mContext;
   /** Gets set once we've installed our own type mapping registry */
   private TypeMapping mTypeMapping;
   
   /**
    * Creates the mapper with the context
    * 
    * @param aContext
    */
   public AeMessageContextTypeMapper(MessageContext aContext)
   {
      mContext = aContext;
   }

   /**
    * @see org.activebpel.rt.axis.ser.IAeTypeMapper#register(java.lang.Class, javax.xml.namespace.QName, javax.xml.rpc.encoding.SerializerFactory, javax.xml.rpc.encoding.DeserializerFactory)
    */
   public void register(Class aJavaType, QName aQName,
         SerializerFactory aSerializerFactory,
         DeserializerFactory aDeserializerFactory)
   {
      getTypeMapping().register(aJavaType, aQName, aSerializerFactory, aDeserializerFactory);
   }
   

   /**
    * Getter for the type mapping object.
    * Installs a type mapping reigstry on the context. This registry will house
    * our mappings and delegate all other requests to the default registry in
    * the context.
    */
   protected TypeMapping getTypeMapping()
   {
      if (mTypeMapping == null)
      {
         TypeMappingRegistry tmr = new TypeMappingRegistryImpl();

         // Set the new type mapping registry to delegate to the message
         // context's current registry.
         tmr.delegate(getMessageContext().getTypeMappingRegistry());

         // Replace the message context's registry with our new one.
         getMessageContext().setTypeMappingRegistry(tmr);

         // The type mapping that we get here will actually be the empty type
         // mapping that we created above.
         mTypeMapping = getMessageContext().getTypeMapping();
      }
      return mTypeMapping;
   }
   
   /**
    * Getter for the message context
    */
   protected MessageContext getMessageContext()
   {
      return mContext;
   }
}
 