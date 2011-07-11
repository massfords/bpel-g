//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/AeWSBPELPartnerLinkTypeIO.java,v 1.2 2006/06/26 16:46:43 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def; 

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;

import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;


/**
 * Reads and writes a partner link type def for WS BPEL 2.0
 */
public class AeWSBPELPartnerLinkTypeIO extends AePartnerLinkTypeIO implements IAeBPELExtendedWSDLConst
{
   /**
    * @see org.activebpel.rt.wsdl.def.AePartnerLinkTypeIO#appendRole(org.activebpel.rt.wsdl.def.AeRoleImpl, org.w3c.dom.Element, javax.wsdl.Definition)
    */
   protected Element appendRole(AeRoleImpl aRole, Element aPartnerLinkElement, Definition aDefinition)
   {
      //<plnk:partnerLinkType name="NCName">
      //   <plnk:role name="NCName" portType="QName"/>
      //   <plnk:role name="NCName" portType="QName"/>?
      // </plnk:partnerLinkType>
      
      Element roleElement = aPartnerLinkElement.getOwnerDocument().createElementNS(aPartnerLinkElement.getNamespaceURI(), ROLE_TAG);
      roleElement.setAttribute(NAME_ATTRIB, aRole.getName());
      roleElement.setPrefix(aPartnerLinkElement.getPrefix());

      String ptQName = aDefinition.getPrefix( aRole.getPortType().getQName().getNamespaceURI() ) 
                              + ":" + aRole.getPortType().getQName().getLocalPart(); //$NON-NLS-1$
      
      roleElement.setAttribute(PORT_TYPE_TAG, ptQName);
      
      return roleElement;
   }

   /**
    * @see org.activebpel.rt.wsdl.def.AePartnerLinkTypeIO#readRole(org.w3c.dom.Element, javax.wsdl.Definition)
    */
   protected IAeRole readRole(Element aRoleElement, Definition aDefinition) throws WSDLException
   {
      AeRoleImpl role = new AeRoleImpl(aRoleElement.getAttribute(NAME_ATTRIB));
      role.setPortType(new AePortTypeImpl(AeXmlUtil.createQName(aRoleElement, aRoleElement.getAttribute(PORT_TYPE_TAG))));
      return role;
   }
}
 