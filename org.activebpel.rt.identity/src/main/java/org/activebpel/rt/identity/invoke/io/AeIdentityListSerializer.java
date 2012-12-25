//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/invoke/io/AeIdentityListSerializer.java,v 1.3 2008/02/02 19:44:32 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.invoke.io;

import org.activebpel.rt.AeException;
import org.activebpel.rt.identity.IAeIdentity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Serializes a list of IAeIdentity objects in the form of
 * identity.wsdl identityListOutput message type.
 */
public class AeIdentityListSerializer extends AeIdentitySerializerBase
{
   /**
    * List of IAeIdentity objects to be serialized.
    */
   private IAeIdentity mIdentities[];
   
   /**
    * Default ctor.
    * @param aIdentities
    */
   public AeIdentityListSerializer(IAeIdentity aIdentities[])
   {
      mIdentities = aIdentities;
   }
   
   /**
    * Overrides method to serialize a list of IAeIdentity objects as identityList element.
    * @see org.activebpel.rt.xml.AeXMLSerializerBase#serialize(org.w3c.dom.Element)
    */
   public Element serialize(Element aParentElement) throws AeException
   {
      Document document = createIdentityServiceNSDocument("identityList");  //$NON-NLS-1$
      for (int i = 0; i < mIdentities.length; i++)
      {
         createIdentityElement(document.getDocumentElement(), mIdentities[i]);
      }
      return document.getDocumentElement();
   }

}
