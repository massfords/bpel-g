//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AePartnerLinkValidator.java,v 1.6 2007/06/06 20:26:03 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;


import java.util.Collection;
import java.util.Iterator;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.io.AeBpelIO;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.def.visitors.AePartnerLinkValidationVisitor;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;
import org.activebpel.rt.util.AeUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Issue warnings if that any partner links defined in the bpel are not defined
 * in the process deployment descriptor.
 * 
 * Fail validation if any partner links (partnerrole or myrole) are present
 * in the pdd BUT NOT in the bpel process.
 */
public class AePartnerLinkValidator extends AeAbstractPddIterator
{
   /** Missing BPEL error message template */
   private static final String MISSING_PARTNER_LINK = AeMessages.getString("AePartnerLinkValidator.0"); //$NON-NLS-1$
   private static final String NO_PARTNERLINK_IN_BPEL = AeMessages.getString("AePartnerLinkValidator.1"); //$NON-NLS-1$
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.validate.AeAbstractPddIterator#validateImpl(org.activebpel.rt.bpel.server.deploy.validate.AePddInfo, org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr, org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter)
    */
   protected void validateImpl(AePddInfo aPddInfo, IAeBpr aBprFile,
         IAeBaseErrorReporter aReporter) throws AeException
   {
      Document bpelDOM = aBprFile.getResourceAsDocument( aPddInfo.getBpelLocation() );
      NodeList bpelPartnerLinks = bpelDOM.getElementsByTagNameNS( bpelDOM.getDocumentElement().getNamespaceURI(), PARTNER_LINK_ELEMENT );

      warnIfBpelHasPartnerLinksThatAreNotInPdd(bpelDOM, aPddInfo, bpelPartnerLinks, aReporter );
      validateBpelContainsPddPartnerLinks( aPddInfo, bpelPartnerLinks, aReporter );
   }
   
   /**
    * Issue warnings if there are partner links defined in the bpel that
    * are not referenced in the pdd.
    * @param aBpelDOM
    * @param aPddInfo
    * @param aBpelPartnerLinks
    * @param aReporter
    * @throws AeException
    */
   protected void warnIfBpelHasPartnerLinksThatAreNotInPdd(
         Document aBpelDOM,
         AePddInfo aPddInfo, 
         NodeList aBpelPartnerLinks, 
         IAeBaseErrorReporter aReporter )
   throws AeException
   {
      Collection myRoleLinks = aPddInfo.getMyRolePartnerLinkNames();
      Collection partnerRoleLinks = aPddInfo.getPartnerRolePartnerLinkNames();
      
      // Create instance of the process from DOM
      AeProcessDef def = AeBpelIO.deserialize(aBpelDOM);

      for( int i = 0; i < aBpelPartnerLinks.getLength(); i++ )
      {
         Element partnerLinkElement = (Element) aBpelPartnerLinks.item(i);
         String pLinkName = partnerLinkElement.getAttribute( NAME_ATTR );
         
         if( partnerLinkElement.hasAttribute(IAeBPELConstants.TAG_MY_ROLE) && !myRoleLinks.contains(pLinkName) )
         {
            String[] args = {pLinkName, aPddInfo.getBpelLocation(), aPddInfo.getName() };
            aReporter.addWarning( MISSING_PARTNER_LINK, args, null );
         }

         // If we have a partner role with no PDD reference, determine if it should be a warning or error
         if( partnerLinkElement.hasAttribute(IAeBPELConstants.TAG_PARTNER_ROLE) && ! partnerRoleLinks.contains(pLinkName) )
         {
            // Use visitor to determine if the partner role is actually referenced in an invoke
            AePartnerLinkValidationVisitor visitor = new AePartnerLinkValidationVisitor(pLinkName);
            visitor.visit(def);
            
            // Error if reference was found otherwise just a warning
            String[] args = {pLinkName, aPddInfo.getBpelLocation(), aPddInfo.getName() };
            if (visitor.isFound())
               aReporter.addError(MISSING_PARTNER_LINK, args, null);
            else
               aReporter.addWarning(MISSING_PARTNER_LINK, args, null);
         }
      }
   }
   
   /**
    * Validate that any parnter links defined in the pdd are also present
    * in the bpel.
    * @param aPddInfo
    * @param aBpelPartnerLinks
    * @param aReporter
    */
   protected void validateBpelContainsPddPartnerLinks( AePddInfo aPddInfo, NodeList aBpelPartnerLinks, IAeBaseErrorReporter aReporter )
   {
      for( Iterator iter = aPddInfo.getMyRolePartnerLinkNames().iterator(); iter.hasNext(); )
      {
         validateMatchingPartnerLinkElement( 
               aPddInfo.getName(), 
               aPddInfo.getBpelLocation(), 
               (String)iter.next(), 
               aBpelPartnerLinks, 
               "myRole",  //$NON-NLS-1$
               aReporter );
      }

      for( Iterator iter = aPddInfo.getPartnerRolePartnerLinkNames().iterator(); iter.hasNext(); )
      {
         validateMatchingPartnerLinkElement( 
               aPddInfo.getName(), 
               aPddInfo.getBpelLocation(), 
               (String)iter.next(), 
               aBpelPartnerLinks, 
               "partnerRole",  //$NON-NLS-1$
               aReporter );
      }
   }
   
   /**
    * Find the matching partner link type in the bpel with the given type.
    * @param aPddName
    * @param aBpelLocation
    * @param aName
    * @param aBpelPartnerLinks
    * @param aRoleType
    * @param aReporter
    */
   protected void validateMatchingPartnerLinkElement( 
         String aPddName, 
         String aBpelLocation, 
         String aName, 
         NodeList aBpelPartnerLinks, 
         String aRoleType, 
         IAeBaseErrorReporter aReporter )
   {
      Element partnerLinkMatch = null;
      for( int i = 0; i < aBpelPartnerLinks.getLength(); i++ )
      {
         Element partnerLinkEl = (Element)aBpelPartnerLinks.item(i);
         String name = partnerLinkEl.getAttribute( NAME_ATTR );
         if( aName.equals(name) )
         {
            partnerLinkMatch = partnerLinkEl;
            break;
         }
      }
      
      if( partnerLinkMatch == null || AeUtil.isNullOrEmpty( partnerLinkMatch.getAttribute( aRoleType) ) )
      {
         String[] args = {aPddName, aBpelLocation, aName, aRoleType  };
         aReporter.addError( NO_PARTNERLINK_IN_BPEL, args, null );
      }
   }
}
