//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/invoke/io/AeIdentityServiceMessageSerializerBase.java,v 1.2 2008/02/02 19:44:32 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.invoke.io;

import org.activebpel.rt.identity.IAeIdentityServiceConstants;
import org.activebpel.rt.xml.AeXMLSerializerBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Base class for the identity service message serializers.
 */
public abstract class AeIdentityServiceMessageSerializerBase extends AeXMLSerializerBase
{

   /**
    * Creates a new document with the identity service name ns element.
    * @param aDocumentElementName
    * @return document
    */
   protected Document createIdentityServiceNSDocument(String aDocumentElementName)
   {
      Document doc = createDocumentWithElement(IAeIdentityServiceConstants.IDENTITY_SERVICE_NS,
               IAeIdentityServiceConstants.IDENTITY_SERVICE_PREFIX, aDocumentElementName);
      return doc;
   }

   /**
    * Creates and returns a element in identity service NS.
    * @param aParentElement
    * @param aElementName
    * @param aText
    * @return element
    */
   protected Element createIdentityServiceNSElement(Element aParentElement, String aElementName, String aText)
   {
      return createElementWithText(aParentElement, IAeIdentityServiceConstants.IDENTITY_SERVICE_NS,
            IAeIdentityServiceConstants.IDENTITY_SERVICE_PREFIX, aElementName, aText);
   }

   /**
    * Creates a new document with the identity ns element.
    * @param aDocumentElementName
    * @return document
    */
   protected Document createIdentityTypeNSDocument(String aDocumentElementName)
   {
      Document doc = createDocumentWithElement(IAeIdentityServiceConstants.IDENTITY_TYPES_NS,
               IAeIdentityServiceConstants.IDENTITY_TYPES_PREFIX, aDocumentElementName);
      return doc;
   }

   /**
    * Creates and returns a element in identity NS.
    * @param aParentElement
    * @param aElementName
    * @param aText
    * @return element
    */
   protected Element createIdentityTypeNSElement(Element aParentElement, String aElementName, String aText)
   {
      return createElementWithText(aParentElement, IAeIdentityServiceConstants.IDENTITY_TYPES_NS,
            IAeIdentityServiceConstants.IDENTITY_TYPES_PREFIX, aElementName, aText);
   }   
   
}
