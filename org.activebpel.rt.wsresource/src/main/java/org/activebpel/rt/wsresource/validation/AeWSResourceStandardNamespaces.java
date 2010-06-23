// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/AeWSResourceStandardNamespaces.java,v 1.1 2007/12/17 16:41:42 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.wsresource.validation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.wsdl.def.IAeBPELExtendedWSDLConst;

/**
 * Simple implementation of standard namespaces.
 */
public class AeWSResourceStandardNamespaces implements IAeWSResourceStandardNamespaces
{
   private static Map sPrefixToNamespaceMap = new HashMap();
   private static Map sNamespaceToPrefixMap = new HashMap();

   /**
    * Creates a prefix/namespace mapping in the maps.
    *
    * @param aPrefix
    * @param aNamespace
    */
   private static void createMapping(String aPrefix, String aNamespace)
   {
      sPrefixToNamespaceMap.put(aPrefix, aNamespace);
      sNamespaceToPrefixMap.put(aNamespace, aPrefix);
   }

   static
   {
      createMapping("wsdl", IAeConstants.WSDL_NAMESPACE); //$NON-NLS-1$
      createMapping(IAeBPELConstants.BPWS_PREFIX, IAeBPELConstants.BPWS_NAMESPACE_URI);
      createMapping(IAeBPELConstants.WSBPEL_2_0_PREFIX, IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI);
      createMapping("xsd", IAeConstants.W3C_XML_SCHEMA); //$NON-NLS-1$
      createMapping(IAeBPELExtendedWSDLConst.PARTNER_LINK_PREFIX, IAeBPELExtendedWSDLConst.PARTNER_LINK_NAMESPACE);
      createMapping(IAeBPELExtendedWSDLConst.WSBPEL_PARTNER_LINK_PREFIX, IAeBPELExtendedWSDLConst.WSBPEL_PARTNER_LINK_NAMESPACE);
      createMapping(IAeBPELExtendedWSDLConst.PROPERTY_2_0_PREFIX, IAeBPELExtendedWSDLConst.PROPERTY_2_0_NAMESPACE);

      // FIXMEQ (builders) add pdd, pdef namespace mappings here
   }

   /**
    * C'tor.
    */
   public AeWSResourceStandardNamespaces()
   {
   }
   
   /**
    * @see org.activebpel.rt.wsresource.validation.IAeWSResourceStandardNamespaces#resolveNamespaceToPrefix(java.lang.String)
    */
   public String resolveNamespaceToPrefix(String aNamespace)
   {
      return (String) sNamespaceToPrefixMap.get(aNamespace);
   }
   
   /**
    * @see org.activebpel.rt.xml.IAeNamespaceContext#resolveNamespaceToPrefixes(java.lang.String)
    */
   public Set resolveNamespaceToPrefixes(String aNamespace)
   {
      return Collections.singleton(resolveNamespaceToPrefix(aNamespace));
   }

   /**
    * @see org.activebpel.rt.wsresource.validation.IAeWSResourceStandardNamespaces#resolvePrefixToNamespace(java.lang.String)
    */
   public String resolvePrefixToNamespace(String aPrefix)
   {
      return (String) sPrefixToNamespaceMap.get(aPrefix);
   }
   
   /**
    * @see org.activebpel.rt.wsresource.validation.IAeWSResourceStandardNamespaces#getAllNamespaces()
    */
   public Collection getAllNamespaces()
   {
      return sNamespaceToPrefixMap.keySet();
   }
}
