//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/to/AeToPartnerLink.java,v 1.3 2006/07/14 15:46:50 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.to; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;

/**
 * Returns the partner link as a target for the copy 
 */
public class AeToPartnerLink extends AeToBase
{
   /** partner link name */
   private String mPartnerLink;
   
   /**
    * Ctor accepts def 
    * 
    * @param aToDef
    */
   public AeToPartnerLink(AeToDef aToDef)
   {
      this(aToDef.getPartnerLink());
   }
   
   /**
    * Ctor accepts plink name 
    * 
    * @param aPartnerLinkName
    */
   public AeToPartnerLink(String aPartnerLinkName)
   {
      super();
      setPartnerLink(aPartnerLinkName);
   }

   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeTo#getTarget()
    */
   public Object getTarget() throws AeBusinessProcessException
   {
      return getCopyOperation().getContext().getPartnerLinkForUpdate(getPartnerLink());
   }

   /**
    * @return Returns the partnerLink.
    */
   public String getPartnerLink()
   {
      return mPartnerLink;
   }

   /**
    * @param aPartnerLink The partnerLink to set.
    */
   public void setPartnerLink(String aPartnerLink)
   {
      mPartnerLink = aPartnerLink;
   }
}
 