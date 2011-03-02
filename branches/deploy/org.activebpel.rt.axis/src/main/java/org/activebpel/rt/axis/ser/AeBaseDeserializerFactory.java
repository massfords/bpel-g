// $Header$
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.axis.ser;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;

import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;

/**
 * A base class for AE deserializer factories. This class causes the general purpose and specialized cases to
 * be skipped (since they are never used).
 */
public class AeBaseDeserializerFactory extends BaseDeserializerFactory
{
   /**
    * Creates a deserializer factory with the given java type and xml type.
    */
   public AeBaseDeserializerFactory(Class aClass, QName xmlType, Class javaType)
   {
      super(aClass, xmlType, javaType);
   }

   /**
    * @see org.apache.axis.encoding.ser.BaseDeserializerFactory#getDeserializerAs(java.lang.String)
    */
   public javax.xml.rpc.encoding.Deserializer getDeserializerAs(String aMechanismType) throws JAXRPCException
   {
      return super.getDeserializerAs(aMechanismType);
   }

   /**
    * Overrides method to return null, since we know that none of our schema types have
    * specialized deserializers.  This was found when profiling on .NET - Axis will look for
    * the specialized deserializer using introspection, and will throw a NoSuchMethodException
    * when it's invariably not found.  Exceptions are expensive in the .NET port, so this 
    * change was made to reduce the number of them.
    * 
    * @see org.apache.axis.encoding.ser.BaseDeserializerFactory#getSpecialized(java.lang.String)
    */
   protected Deserializer getSpecialized(String aMechanismType)
   {
      return null;
   }
}
