//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/AeXMLSerializerBase.java,v 1.1 2008/02/02 18:58:19 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Base class used to serialize Java objects to xml elements.
 */
public abstract class AeXMLSerializerBase
{

   /**
    * Serializes data and returns serialized element.
    * @return serialized element
    * @throws AeException
    */
   public Element serialize() throws AeException
   {
      return serialize(null);
   }

   /**
    * Serializes data and returns serialized element.
    * @param aParentElement optional container (parent) element.
    * @return serialized element
    * @throws AeException
    */
   public abstract Element serialize(Element aParentElement) throws AeException;   
   
   /**
    * Creates a new document with the identity service name ns element.
    * @param aDocumentElementName
    * @return document
    */
   protected Document createDocumentWithElement(String aNameSpace, String aPrefix, String aDocumentElementName)
   {
      Document doc = AeXmlUtil.newDocument();
      Element docEle = doc.createElementNS(aNameSpace, aPrefix + ":" + aDocumentElementName); //$NON-NLS-1$
      docEle.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:" + aPrefix, aNameSpace); //$NON-NLS-1$
      doc.appendChild(docEle);
      return doc;
   }

   /**
    * Creates a NS element and adds it to the parent element.
    * @param aParentEle
    * @param aNameSpace
    * @param aPrefix
    * @param aElementName
    * @return element
    */
   protected Element createElement(Element aParentEle, String aNameSpace, String aPrefix,
         String aElementName)
   {
      return createElementWithText(aParentEle, aNameSpace, aPrefix, aElementName, null);
   }  
   
   /**
    * Creates a NS element and adds it to the parent element.
    * @param aParentEle
    * @param aNameSpace
    * @param aPrefix
    * @param aElementName
    * @param aText
    * @return element
    */
   protected Element createElementWithText(Element aParentEle, String aNameSpace, String aPrefix,
         String aElementName, String aText)
   {

      Element ele = AeXmlUtil.addElementNS(aParentEle, aNameSpace,
            aPrefix + ":" + aElementName, aText); //$NON-NLS-1$
      ele.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:" + aPrefix, aNameSpace); //$NON-NLS-1$
      return ele;
   }

   /**
    * Convenience method for creating the root element for
    * the serialized data.
    * @param aParentElement optional container (parent) element.
    * @param aNamespaceUri element namespace URI.
    * @param aNamespacePrefix element name prefix.
    * @param aLocalName element node name.
    * @return serialized element
    */
   protected Element createRootElement(Element aParentElement, String aNamespaceUri, String aNamespacePrefix, String aLocalName)
   {
      Element rval = null;
      if (aParentElement != null)
      {
         rval = createElement(aParentElement, aNamespaceUri, aNamespacePrefix, aLocalName);
      }
      else
      {
         Document doc = createDocumentWithElement(aNamespaceUri, aNamespacePrefix, aLocalName);
         rval = doc.getDocumentElement();
      }
      return rval;
   }

}
