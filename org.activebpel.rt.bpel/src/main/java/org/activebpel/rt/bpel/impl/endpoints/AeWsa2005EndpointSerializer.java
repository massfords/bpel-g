//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/endpoints/AeWsa2005EndpointSerializer.java,v 1.1.16.1 2008/04/24 14:49:50 KPease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.endpoints;

import java.util.Map;

import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.wsio.IAeWebServiceEndpointReference;
import org.w3c.dom.Element;

/**
 * Endpoint serializer for WS-Addressing endpoints in the 2005 namespace. 
 */
public class AeWsa2005EndpointSerializer extends AeWSAddressingEndpointSerializer
{
   /**
    * ctor with namespace
    */
   protected AeWsa2005EndpointSerializer(String aNamespace)
   {
      super(aNamespace);
   }   

   /**
    * Overrides method to create a wsa:ReferenceParameters element instead of a wsa:ReferenceProperties element
    * @see org.activebpel.rt.bpel.impl.endpoints.AeWSAddressingEndpointSerializer#addReferenceProps(org.activebpel.wsio.IAeWebServiceEndpointReference, org.w3c.dom.Element)
    */
   protected void serializeReferenceProps(IAeWebServiceEndpointReference aRef, Element aElement)
   {
      if (!aRef.getReferenceProperties().isEmpty())
      {
         Element props = AeXmlUtil.addElementNS(aElement, aElement.getNamespaceURI(), "wsa:ReferenceParameters", null); //$NON-NLS-1$
         addReferenceProps(aRef, props);
      }
   }
   
   /**
    * Overrides method to put policy and extensibility elements in a wsa:Metadata element as defined in the 2005 spec 
    * @see org.activebpel.rt.bpel.impl.endpoints.AeWSAddressingEndpointSerializer#addMetadata(org.activebpel.wsio.IAeWebServiceEndpointReference, org.w3c.dom.Element)
    */
   protected void addMetadata(IAeWebServiceEndpointReference aRef, Element aElement, Map aQNamePrefixMap)
   {
      if (!AeUtil.isNullOrEmpty(aRef.getServiceName()) || aRef.getExtensibilityElements().hasNext() || !aRef.getPolicies().isEmpty())
      {
         Element meta = AeXmlUtil.addElementNS(aElement, aElement.getNamespaceURI(), "wsa:Metadata", null); //$NON-NLS-1$
         super.addMetadata(aRef, meta, aQNamePrefixMap);
      }
   }
}
