// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/addressing/pdef/AeXmlPartnerDefInfoReader.java,v 1.4 2005/08/25 23:20:17 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.addressing.pdef;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.server.addressing.IAePartnerAddressing;
import org.activebpel.rt.util.AeXmlUtil;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Utility class for reading in pdef xml files.
 */
public class AeXmlPartnerDefInfoReader
{

   /**
    * Deserialize the pdef xml into the IAePartnerDefInfo.
    * @param aPdefXml
    * @param aAddressing
    * @throws AeBusinessProcessException
    */
   public static IAePartnerDefInfo read( Document aPdefXml, 
      IAePartnerAddressing aAddressing ) throws AeBusinessProcessException
                                             
   {
      String principal = aPdefXml.getDocumentElement().getAttribute("principal"); //$NON-NLS-1$
      AePartnerDefInfo info = new AePartnerDefInfo(principal);
      
      NodeList partnerLinkTypes = aPdefXml.getDocumentElement().getElementsByTagNameNS(aPdefXml.getDocumentElement().getNamespaceURI(), "partnerLinkType"); //$NON-NLS-1$
      int max = partnerLinkTypes.getLength();
      for( int i = 0; i < max; i++ )
      {
         Element pLinkElement = (Element)partnerLinkTypes.item(i);
         String pLinkName = pLinkElement.getAttribute("name"); //$NON-NLS-1$
         QName pLinkQName = AeXmlUtil.createQName( pLinkElement, pLinkName );
         
         Element roleElement = AeXmlUtil.getFirstSubElement(pLinkElement);
         String roleName = roleElement.getAttribute("name"); //$NON-NLS-1$
         IAeEndpointReference ref = aAddressing.readFromDeployment( roleElement );
   
         info.addInfo( pLinkQName, roleName, ref );
      }
      
      return info;
   }
}
