//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/ser/IAeTypeMapper.java,v 1.1 2005/04/20 19:22:46 mford Exp $
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

/**
 * Provides a single interface for registering types. In Axis, the MessageContext 
 * and Call object have a different way of registering types.
 */
public interface IAeTypeMapper
{
   /**
    * Registers the serializer/deserializer with Axis' type mapping registry.
    * 
    * @param aJavaType
    * @param aQName
    * @param aSerializerFactory
    * @param aDeserializerFactory
    */
   public void register(Class aJavaType, QName aQName, SerializerFactory aSerializerFactory, DeserializerFactory aDeserializerFactory);
}
 