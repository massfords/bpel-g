//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/AeWSDLPrefixes.java,v 1.7 2007/07/27 18:08:54 kpease Exp $
////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.wsdl.def; 

/**
 * WSDL prefix helper utilities. 
 */
public class AeWSDLPrefixes
{

   /**
    * Static helper method for returning the preferred BPEL prefix String for the given BPEL namespace.
    * @param aBpelNamespace the BPEL version namespace.
    * @return String the BPEL prefix. 
    */
   public static String getBpelPrefix(String aBpelNamespace)
   {
      if ( IAeBPELExtendedWSDLConst.BPWS_NAMESPACE_URI.equals(aBpelNamespace) )
      {
         // BPEL4WS 1.1
         return IAeBPELExtendedWSDLConst.BPWS_PREFIX;   
      }
      else if ( IAeBPELExtendedWSDLConst.WSBPEL_2_0_ABSTRACT_NAMESPACE_URI.equals(aBpelNamespace) )
      {
         // WSBPEL 2.0 Abstract Process 
         return IAeBPELExtendedWSDLConst.ABSTRACT_PROC_PREFIX;   
      }      
      else
      {
         // Assume WSBPEL 2.0
         return IAeBPELExtendedWSDLConst.WSBPEL_2_0_PREFIX;  
      }
   }

   /**
    * Static helper method for returning the preferred Partner Link Type prefix String for the given 
    * Partner Link Type namespace.
    * @param aPLTNamespace the BPEL version namespace.
    * @return String the partner link type prefix. 
    */
   public static String getPltPrefix(String aPLTNamespace)
   {
      if ( IAeBPELExtendedWSDLConst.PARTNER_LINK_NAMESPACE.equals(aPLTNamespace) )
      {
         // BPEL4WS 1.1 PLT prefix.
         return IAeBPELExtendedWSDLConst.PARTNER_LINK_PREFIX;   
      }
      else
      {
         // Assume WSBPEL 2.0 PLT prefix
         return IAeBPELExtendedWSDLConst.WSBPEL_PARTNER_LINK_PREFIX;  
      }
   }

   /**
    * Static helper method for returning the correct Partner Link Type extension namespace
    * associated with the given BPEL namespace.
    * @param aBpelNamespace the BPEL version namespace.
    * @return String the correct partner link type namespace.
    */
   public static String getPltNamespace(String aBpelNamespace)
   {
      if ( IAeBPELExtendedWSDLConst.BPWS_NAMESPACE_URI.equals(aBpelNamespace) )
      {
         // BPEL4WS 1.1 PLT namespace. 
         return IAeBPELExtendedWSDLConst.PARTNER_LINK_NAMESPACE;   
      }
      else
      {
         // assume WSBPEL 2.0 PLT namespace.
         return IAeBPELExtendedWSDLConst.WSBPEL_PARTNER_LINK_NAMESPACE;  
      }
   }

   /**
    * Static helper method for returning the preferred prefix String for the given 
    * Property/PropertyAlias namespace..
    * @param aPropNamespace the property version namespace.
    * @return String the property prefix. 
    */
   public static String getPropertyPrefix(String aPropNamespace)
   {
      if ( IAeBPELExtendedWSDLConst.BPWS_NAMESPACE_URI.equals(aPropNamespace) )
      {
         // BPEL4WS 1.1
         return IAeBPELExtendedWSDLConst.BPWS_PREFIX;   
      }
      else
      {
         // Assume WSBPEL 2.0
         return IAeBPELExtendedWSDLConst.PROPERTY_2_0_PREFIX;  
      }
   }

   /**
    * Static helper method for returning the preferred prefix String for the given 
    * policy namespace..
    * @param aPolicyNamespace the property version namespace.
    * @return String the property prefix. 
    */
   public static String getPolicyPrefix(String aPolicyNamespace)
   {
      return IAeBPELExtendedWSDLConst.POLICY_PREFIX;
   }
   
   /**
    * Static helper method for returning the preferred prefix String for the given 
    * policy namespace..
    * @param aNamespace the property version namespace.
    * @return String the property prefix. 
    */
   public static String getSecUtilPrefix(String aNamespace)
   {
      return IAeBPELExtendedWSDLConst.WSUTIL_PREFIX;
   }
   
}
 
