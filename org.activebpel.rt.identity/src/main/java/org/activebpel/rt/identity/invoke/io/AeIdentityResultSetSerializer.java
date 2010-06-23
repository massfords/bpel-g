//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/invoke/io/AeIdentityResultSetSerializer.java,v 1.3 2008/02/02 19:44:32 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.invoke.io;

import java.util.Iterator;

import org.activebpel.rt.AeException;
import org.activebpel.rt.identity.IAeIdentity;
import org.activebpel.rt.identity.search.AeIdentityResultSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Serializes AeIdentityResultSet object in the form of
 * identity.wsdl identityQueryOutput message type.
 */
public class AeIdentityResultSetSerializer extends AeIdentitySerializerBase
{
   /**
    * result set to be serialized.
    */
   private AeIdentityResultSet mResultSet;
   
   /**
    * Default ctor.
    * @param aResultSet
    */
   public AeIdentityResultSetSerializer(AeIdentityResultSet aResultSet)
   {
      mResultSet = aResultSet;
   }
   
   /**
    * 
    * Overrides method to serialize as a identity.wsdl identityQueryOutput message type.
    * @see org.activebpel.rt.xml.AeXMLSerializerBase#serialize(org.w3c.dom.Element)
    */
   public Element serialize(Element aParentElement) throws AeException
   {
      Document document = createIdentityTypeNSDocument("identityResultSet");  //$NON-NLS-1$
      createIdentityTypeNSElement(document.getDocumentElement(), "totalRowCount", String.valueOf(mResultSet.getTotalMatched()) ); //$NON-NLS-1$
      //fixme (PJ) need AeIdentityResultSet::isRowCountComplete() when paging and 'identityListingFilter' is implemented.
      createIdentityTypeNSElement(document.getDocumentElement(), "completeRowCount", String.valueOf(true) ); //$NON-NLS-1$
      Element identitesEle = createIdentityTypeNSElement(document.getDocumentElement(), "identities", null ); //$NON-NLS-1$
      
      Iterator it = mResultSet.getIdentities().iterator();
      while ( it.hasNext() )
      {
         IAeIdentity identity = (IAeIdentity) it.next();
         createIdentityElement(identitesEle, identity);
      }      
      return document.getDocumentElement();
   }

}
