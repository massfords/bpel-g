// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/convert/AeBpelDefConverterFactory.java,v 1.3.16.1 2008/04/21 16:09:44 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.convert;

import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.IAeBPELConstants;

/**
 * A factory for creating BPEL def converters.  This factory takes the 'from' bpel namespace 
 * and the 'to' bpel namespace and returns a converter that will convert a Process def.
 */
public class AeBpelDefConverterFactory
{
   /** A map of converters. */
   private static Map sConverters;
   
   static
   {
      sConverters = new HashMap();
      // bpws 1.1 -> wsbpel 2.x
      sConverters.put(
            new AeBpelConverterKey(IAeBPELConstants.BPWS_NAMESPACE_URI, IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI),
            AeBPWSToWSBPELConverter.class);
      // bpws 1.1 abstract process  -> wsbpel 2.x abstract process.
      sConverters.put(
            new AeBpelConverterKey(IAeBPELConstants.BPWS_NAMESPACE_URI, IAeBPELConstants.WSBPEL_2_0_ABSTRACT_NAMESPACE_URI),
            AeBPWSToWSBPELAbstractProcessConverter.class);
   }
   
   /**
    * Given the BPEL namespace to convert <strong>from</strong> and the BPEL namespace to
    * convert <strong>to</strong>, return a BPEL def converter.
    * 
    * @param aFromNamespace
    * @param aToNamespace
    */
   public static IAeBpelDefConverter createConverter(String aFromNamespace, String aToNamespace)
   {
      IAeBpelDefConverter converter = null;

      AeBpelConverterKey key = new AeBpelConverterKey(aFromNamespace, aToNamespace);
      Class clazz = (Class) sConverters.get(key);
      if (clazz != null)
      {
         try
         {
            converter = (IAeBpelDefConverter) clazz.newInstance();
         }
         catch (Throwable t)
         {
            AeException.logError(t);
         }
      }
      
      return converter;
   }
   
   /**
    * A simple inner class that represents the key into the map of converters.
    */
   protected static class AeBpelConverterKey
   {
      /** The 'from' namespace. */
      private String mFromNS;
      /** The 'to' namespace. */
      private String mToNS;
      
      /**
       * C'tor.
       * 
       * @param aFromNS
       * @param aToNS
       */
      public AeBpelConverterKey(String aFromNS, String aToNS)
      {
         setFromNS(aFromNS);
         setToNS(aToNS);
      }
      
      /**
       * @return Returns the fromNS.
       */
      public String getFromNS()
      {
         return mFromNS;
      }
      /**
       * @param aFromNS The fromNS to set.
       */
      public void setFromNS(String aFromNS)
      {
         mFromNS = aFromNS;
      }
      /**
       * @return Returns the toNS.
       */
      public String getToNS()
      {
         return mToNS;
      }
      /**
       * @param aToNS The toNS to set.
       */
      public void setToNS(String aToNS)
      {
         mToNS = aToNS;
      }
      
      /**
       * @see java.lang.Object#hashCode()
       */
      public int hashCode()
      {
         return getFromNS().hashCode() ^ getToNS().hashCode();
      }
      
      /**
       * @see java.lang.Object#equals(java.lang.Object)
       */
      public boolean equals(Object aObject)
      {
         if (aObject instanceof AeBpelConverterKey)
         {
            AeBpelConverterKey other = (AeBpelConverterKey) aObject;
            return getFromNS().equals(other.getFromNS()) && getToNS().equals(other.getToNS());
         }
         else
         {
            return false;
         }
      }
   }
}
