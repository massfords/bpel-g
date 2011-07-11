// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/AePartnerLinkTypeIO.java,v 1.10 2006/10/25 16:07:03 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def;

import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Iterator;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeMessages;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class serializes and deserializes a Partner Link Type extension
 * implementation.
 */
public class AePartnerLinkTypeIO extends AeWSDLExtensionIO implements IAeBPELExtendedWSDLConst
{
   /**
    * Serialize a Partner Link Type extension element into a the PrintWriter.
    * 
    * @param aParentType a class object indicating where in the WSDL document
    * this extensibility element was encountered. 
    * @param aQname the QName of this extension element.
    * @param aExtElement the extensibility element to be serialized.
    * @param aWriter the PrintWriter where we're serializing to.
    * @param aDefinition the Definition that this extensibility element was encountered in.
    * @param aExtReg the associated ExtensionRegistry.
    * @throws WSDLException
    */
   public void marshall(
      Class aParentType,
      QName aQname,
      ExtensibilityElement aExtElement,
      PrintWriter aWriter,
      Definition aDefinition,
      ExtensionRegistry aExtReg)
      throws WSDLException
   {
      AePartnerLinkTypeImpl partnerLink = (AePartnerLinkTypeImpl) aExtElement;

      Element partnerLinkElement = createElement(aQname);

      // Determine the Namespace prefix of the Partner Link Type extension.
      String prefix = getPrefixOrCreateLocally(aQname.getNamespaceURI(), aDefinition, partnerLinkElement, "plnk"); //$NON-NLS-1$
      partnerLinkElement.setPrefix(prefix);
      partnerLinkElement.setAttribute(NAME_ATTRIB, partnerLink.getName());

      // Add Role elements.
      for (Iterator iter = partnerLink.getRoleList(); iter.hasNext();)
      {
         AeRoleImpl role = (AeRoleImpl) iter.next();
         Element roleElement = appendRole(role, partnerLinkElement, aDefinition);

         // Add this Role element to the Partner Link Type element.
         partnerLinkElement.appendChild(roleElement);
      }

      writeElement(partnerLinkElement, aWriter);
   }

   /**
    * Appends the role to the element.
    * 
    * @param aRole
    * @param aPartnerLinkElement
    * @param aDefinition
    */
   protected Element appendRole(AeRoleImpl aRole, Element aPartnerLinkElement, Definition aDefinition)
   {
      Element roleElement = aPartnerLinkElement.getOwnerDocument().createElementNS(aPartnerLinkElement.getNamespaceURI(), ROLE_TAG);
      roleElement.setPrefix(aPartnerLinkElement.getPrefix());
      roleElement.setAttribute(NAME_ATTRIB, aRole.getName());

      // Add PortType elements under this Role.
      IAePortType portType = aRole.getPortType();
      QName ptName = new QName(aPartnerLinkElement.getNamespaceURI(), PORT_TYPE_TAG);  
      Element ptElement = aPartnerLinkElement.getOwnerDocument().createElementNS(ptName.getNamespaceURI(), ptName.getLocalPart());
      ptElement.setPrefix(aPartnerLinkElement.getPrefix());
      
      // Convert portType QName object to a string.
      String ptQName = toString(aDefinition, portType.getQName());
      ptElement.setAttribute(NAME_ATTRIB, ptQName);
      // Add this PortType element to the Role element parent.
      roleElement.appendChild(ptElement);
      return roleElement;
   }

   /** 
    * Deserialize a Partner Link Type extension element into a AePartnerLinkTypeImpl 
    * implementation object.
    * 
    * @param aParentType a class object indicating where in the WSDL document
    * this extensibility element was encountered. 
    * @param aQName the QName of this extension element.
    * @param aPartnerLinkElem the extensibility DOM element to be deserialized.
    * @param aDefinition the Definition that this extensibility element was
    * encountered in.
    * @param aExtReg the associated ExtensionRegistry.
    * 
    * @return ExtensibilyElement the implementation mapping class for this
    * extension.
    */
   public ExtensibilityElement unmarshall(
      Class aParentType,
      QName aQName,
      Element aPartnerLinkElem,
      Definition aDefinition,
      ExtensionRegistry aExtReg)
      throws WSDLException
   {
      // Create a new Partner Link Type implementation object.
      AePartnerLinkTypeImpl partnerLink = new AePartnerLinkTypeImpl();
      partnerLink.setName(aPartnerLinkElem.getAttribute(NAME_ATTRIB));

      // Retrieve and save the Role elements within this Partner Link Type.

      NodeList lNodeList =
         aPartnerLinkElem.getElementsByTagNameNS(aPartnerLinkElem.getNamespaceURI(), ROLE_TAG);

      int lLen = lNodeList.getLength();
      
      // validate the number of Role element occurences. The minimum is 1 the maximum is 2.
      if ( lLen < 1 || lLen > 2 )
      {
         throw new WSDLException(WSDLException.INVALID_WSDL, 
                                 MessageFormat.format(AeMessages.getString("AePartnerLinkTypeImpl.ERROR_0"), //$NON-NLS-1$
                                                      new Object[] {new Integer(lLen), partnerLink.getName()}));
      }

      for (int i = 0; i < lLen; i++)
      {
         // Create a Role object and add to our map.
         IAeRole role = readRole((Element) lNodeList.item(i), aDefinition);
         partnerLink.addRole(role);
      }
      
      partnerLink.setRequired(Boolean.TRUE);
      partnerLink.setElementType(aQName);
      return partnerLink;
   }
   
   /**
    * Creates a role impl from the data in the element
    * 
    * @param aRoleElement
    * @param aDefinition
    * @throws WSDLException
    */
   protected IAeRole readRole(Element aRoleElement, Definition aDefinition) throws WSDLException
   {
      AeRoleImpl role = new AeRoleImpl(aRoleElement.getAttribute(NAME_ATTRIB));

      // Retrieve and save the PortType elements within this Role.
      NodeList nodes = aRoleElement.getElementsByTagNameNS(aRoleElement.getNamespaceURI(), PORT_TYPE_TAG);
      
      // validate the number of PortType element occurences. One and only one is required.
      if ( nodes.getLength() != 1 )
      {
         throw new WSDLException(WSDLException.INVALID_WSDL, 
                                 MessageFormat.format(AeMessages.getString("AeRoleImpl.ERROR_0"), //$NON-NLS-1$
                                                      new Object[] {new Integer(nodes.getLength()), role.getName()}));
      }

      role.setPortType(readPortType((Element)nodes.item(0), aDefinition));
      return role;
   }
   
   /**
    * Creates a port type from the element
    * 
    * @param aPortTypeElement
    * @param aDefinition
    * @throws WSDLException
    */
   protected IAePortType readPortType(Element aPortTypeElement, Definition aDefinition) throws WSDLException
   {
      return new AePortTypeImpl(AeXmlUtil.createQName(aPortTypeElement, aPortTypeElement.getAttribute(NAME_ATTRIB)));
   }
}
