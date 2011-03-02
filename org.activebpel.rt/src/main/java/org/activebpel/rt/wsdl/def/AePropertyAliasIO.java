// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/AePropertyAliasIO.java,v 1.14 2006/10/25 16:07:03 ckeller Exp $
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
import java.util.HashMap;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.xml.namespace.QName;

import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;

/**
 * This class serializes and deserializes a Message Property Alias extension
 * implementation.
 */
public class AePropertyAliasIO extends AeWSDLExtensionIO implements IAeBPELExtendedWSDLConst
{
   /**
    * Serialize a Message Property Alias extension element into a the
    * PrintWriter.
    * 
    * @param aParentType a class object indicating where in the WSDL document
    * this extensibility element was encountered. 
    * @param aQname the QName of this extension element.
    * @param aExtElement the extensibility element to be serialized.
    * @param aWriter the PrintWriter where we're serializing to.
    * @param aDefinition the Definition that this extensibility element was
    * encountered in.
    * @param aExtReg the associated ExtensionRegistry.
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
      IAePropertyAlias lPropAlias = (IAePropertyAlias)aExtElement;
      
      Element lPropAliasDomElem = createElement(aQname);

      // Determine the Namespace prefix of the Property Alias extension.
      String preferredPrefix = AeWSDLPrefixes.getPropertyPrefix(aQname.getNamespaceURI());
      String prefix = getPrefixOrCreateLocally(aQname.getNamespaceURI(), aDefinition, lPropAliasDomElem, preferredPrefix); 
      lPropAliasDomElem.setPrefix(prefix);
      
      writePropertyAlias(lPropAlias, lPropAliasDomElem, aDefinition);

      writeElement(lPropAliasDomElem, aWriter);
   }

   /**
    * Writes the property alias to the element
    * 
    * @param aPropAlias
    * @param aElement
    * @param aDefinition
    */
   protected void writePropertyAlias(IAePropertyAlias aPropAlias, Element aElement, Definition aDefinition)
   {
      // Set Attributes
      addPropertyName(aPropAlias, aElement, aDefinition);
      addMessageData(aPropAlias, aElement, aDefinition);
      addQuery(aPropAlias, aElement);
   }

   /**
    * Writes the query to the element
    * 
    * @param aPropAlias
    * @param aElement
    */
   protected void addQuery(IAePropertyAlias aPropAlias, Element aElement)
   {
      // Don't write out empty query - not required.
      //
      if ( !AeUtil.isNullOrEmpty( aPropAlias.getQuery() ))
         aElement.setAttribute(QUERY_ATTRIB, aPropAlias.getQuery());
   }

   /**
    * Writes the message data including part to the element
    * 
    * @param aPropAlias
    * @param aElement
    * @param aDefinition
    */
   protected void addMessageData(IAePropertyAlias aPropAlias, Element aElement, Definition aDefinition)
   {
      String lMsgTypeQName = toString(aDefinition, aPropAlias.getMessageName()); 
      aElement.setAttribute(MESSAGE_TYPE_ATTRIB, lMsgTypeQName);

      if ( !AeUtil.isNullOrEmpty( aPropAlias.getPart() ))
         aElement.setAttribute(PART_ATTRIB, aPropAlias.getPart());
   }

   /**
    * Writes the property name to the element
    * 
    * @param aPropAlias
    * @param aElement
    * @param aDefinition
    */
   protected void addPropertyName(IAePropertyAlias aPropAlias, Element aElement, Definition aDefinition)
   {
      String lPropNameQName = toString(aDefinition, aPropAlias.getPropertyName()); 
      aElement.setAttribute(PROPERTY_NAME_ATTRIB, lPropNameQName);
   }

   /**
    * Deserialize a Message Property Alias extension element into a
    * AePropertyAliasImpl implementation object.
    * 
    * @param aParentType a class object indicating where in the WSDL document
    * this extensibility element was encountered. 
    * @param aQName the QName of this extension element.
    * @param aPropertyElem the extensibility DOM element to be deserialized.
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
      Element aPropertyElem,
      Definition aDefinition,
      ExtensionRegistry aExtReg)
      throws WSDLException
   {
      // Create a new Property Alais implementation object.
      AePropertyAliasImpl propAlias = new AePropertyAliasImpl();

      updatePropAliasData(propAlias, aPropertyElem);

      propAlias.setRequired(Boolean.TRUE);
      propAlias.setElementType(aQName);

      Map namespaces = new HashMap();
      AeXmlUtil.getDeclaredNamespaces(aPropertyElem, namespaces);
      propAlias.setNamespaces(namespaces);

      return propAlias;
   }

   /**
    * Reads the prop alias data from the element
    * @param aPropAlias
    * @param aPropertyElem
    */
   protected void updatePropAliasData(AePropertyAliasImpl aPropAlias, Element aPropertyElem)
   {
      aPropAlias.setPropertyName( AeXmlUtil.createQName(aPropertyElem, aPropertyElem.getAttribute(PROPERTY_NAME_ATTRIB)));
      aPropAlias.setMessageName(AeXmlUtil.createQName(aPropertyElem, aPropertyElem.getAttribute(MESSAGE_TYPE_ATTRIB)));
      aPropAlias.setPart(aPropertyElem.getAttribute(PART_ATTRIB));
      aPropAlias.setQuery(aPropertyElem.getAttribute(QUERY_ATTRIB));
   }
}
