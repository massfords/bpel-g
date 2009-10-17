// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AePartnerDetailBean.java,v 1.4 2005/01/26 22:23:25 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.server.addressing.pdef.IAePartnerDefInfo;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

/**
 *  Wraps the info for a specific pdef.
 */
public class AePartnerDetailBean implements Serializable
{
   /** The specific principal */
   protected String mPrincipal;
   /** The pdef details associated with the principal. */
   protected AePartnerDetailWrapper[] mDetails;
   
   /**
    * Constructor.
    */
   public AePartnerDetailBean()
   {
      
   }
   
   /**
    * Setter for the principal.
    * @param aPrincipal
    */
   public void setPrincipal( String aPrincipal )
   {
      mPrincipal = aPrincipal;
      initDetails();
   }
   
   /**
    * Getter for the principal.
    */
   public String getPrincipal()
   {
      return mPrincipal;
   }
   
   /**
    * Load up the partner def details.
    */
   protected void initDetails()
   {
      IAePartnerDefInfo info = 
         AeEngineFactory.getEngineAdministration().getPartnerAddressingAdmin().getPartnerInfo( getPrincipal() );

      List details = new ArrayList();
      
      for (Iterator iter = info.getPartnerLinkTypes(); iter.hasNext();)
      {
         QName partnerLinkType = (QName) iter.next();
         String role = info.getRoleName( partnerLinkType );
         QName endpoint = info.getEndpointReference( partnerLinkType ).getServiceName();

         // TODO Z! - this needs to change to something other than a deployment context
         IAeDeploymentContext[] contexts = new IAeDeploymentContext[0]; 
         details.add( new AePartnerDetailWrapper(partnerLinkType,role,endpoint,contexts) );
         mDetails = (AePartnerDetailWrapper[])details.toArray( new AePartnerDetailWrapper[details.size()]); 
      }
   }
   
   /**
    * Indexed accessor for the partner detail rows wrapper.
    * @param aIndex
    */
   public AePartnerDetailWrapper getDetail( int aIndex )
   {
      return mDetails[aIndex];
   }
   
   /**
    * Accessor for the size of the detail rows.
    */
   public int getDetailSize()
   {
      if( mDetails == null )
      {
         return 0;
      }
      return mDetails.length;
   }
   
   /**
    * Returns true if there are details available.
    */
   public boolean isHasDetails()
   {
      return getDetailSize() > 0;
   }
}
