//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/invoke/io/AeIdentityServiceFaultSerializer.java,v 1.3 2008/02/02 19:44:32 PJayanetti Exp $
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
import org.activebpel.rt.identity.AeIdentityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Serializes a identity service fault.
 */
public class AeIdentityServiceFaultSerializer extends AeIdentityServiceMessageSerializerBase
{
   /** Root cause of the fault.*/
   private AeIdentityException mException ;

   /**
    * Default ctor.
    * @param aException
    */
   public AeIdentityServiceFaultSerializer(AeIdentityException aException)
   {
      mException = aException;
   }

   /**
    * Overrides method to serialized the exception as a identity.wsdl searchFault message type.
    * @see org.activebpel.rt.xml.AeXMLSerializerBase#serialize(org.w3c.dom.Element)
    */
   public Element serialize(Element aParentElement) throws AeException
   {
      Document document = createIdentityServiceNSDocument("identityFault");  //$NON-NLS-1$
      Element identityFaultEle = document.getDocumentElement();
      createIdentityServiceNSElement(identityFaultEle, "code", String.valueOf(mException.getCode()) ); //$NON-NLS-1$
      createIdentityServiceNSElement(identityFaultEle, "message", String.valueOf(mException.getInfo()) ); //$NON-NLS-1$
      return document.getDocumentElement();
   }
}
