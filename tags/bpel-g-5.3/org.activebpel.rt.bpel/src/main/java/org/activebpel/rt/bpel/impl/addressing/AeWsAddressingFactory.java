// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/addressing/AeWsAddressingFactory.java,v 1.4 2006/12/20 23:36:36 KPease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.addressing;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.util.AeUtil;

/**
 * Factory for returning the serializers and deserializers for WS-Addressing Headers
 */
public class AeWsAddressingFactory implements IAeWsAddressingFactory
{
   private static final IAeWsAddressingFactory sInstance = new AeWsAddressingFactory();
   
   /**
    * Private constructor for singleton
    */
   private AeWsAddressingFactory()
   {
      
   }

   /**
    * @return the singleton addressing factory instance
    */
   public static IAeWsAddressingFactory getInstance()
   {
      return sInstance;
   }
   
   /**
    * Returns the WS-Addressing deserializer for a given namespace.  
    * The default deserializer is returned if the namespace parameter is null.
    * @param aNamespace
    * @return the Deserializer
    */
   public IAeAddressingDeserializer getDeserializer(String aNamespace)
   {
      if (AeUtil.isNullOrEmpty( aNamespace) || IAeConstants.WSA_NAMESPACE_URI.equals(aNamespace))
      {
         return AeWSAddressingDeserializer.getInstance();
      }
      else 
      {
         return AeWSAddressingDeserializer.getInstance(aNamespace);         
      }
      
   }

   /**
    * Returns the WS-Addressing serializer for a given namespace.  
    * The default serializer is returned if the namespace parameter is null.
    * @param aNamespace
    */
   public IAeAddressingSerializer getSerializer(String aNamespace)
   {
      if (IAeBPELConstants.WSA_NAMESPACE_URI.equals(aNamespace) || AeUtil.isNullOrEmpty(aNamespace))
      {
         return AeWSAddressingSerializer.getInstance();
      }
      else 
      {
         return AeWSAddressingSerializer.getInstance(aNamespace);         
      }
   }
}
