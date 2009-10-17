//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/invoke/io/AeAssertionQueryResponseSerializer.java,v 1.2 2008/02/02 19:44:32 PJayanetti Exp $
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
import org.activebpel.rt.identity.search.AeAssertionQueryResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * serializes the response object for an assertion query.
 */
public class AeAssertionQueryResponseSerializer extends AeIdentitySerializerBase
{
   /** response */
   private AeAssertionQueryResponse mResponse;
   
   /**
    * Ctor
    * @param aResponse
    */
   public AeAssertionQueryResponseSerializer(AeAssertionQueryResponse aResponse)
   {
      mResponse = aResponse;
   }

   /**
    * @see org.activebpel.rt.xml.AeXMLSerializerBase#serialize(org.w3c.dom.Element)
    */
   public Element serialize(Element aParentElement) throws AeException
   {
      Document document = createIdentityServiceNSDocument("assertionQueryResponse");  //$NON-NLS-1$
      Node node = document.createTextNode(String.valueOf(mResponse.getResult()));
      document.getDocumentElement().appendChild(node);
      return document.getDocumentElement();
   }
}
 