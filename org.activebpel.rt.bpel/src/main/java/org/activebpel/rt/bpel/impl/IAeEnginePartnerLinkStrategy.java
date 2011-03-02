//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeEnginePartnerLinkStrategy.java,v 1.3 2006/09/27 00:36:26 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.wsio.receive.IAeMessageContext;

/**
 * Defines an interface for how a business process engine should manage
 * partner links.
 */
public interface IAeEnginePartnerLinkStrategy
{
   /**
    * Initialize a single partner link.  This is called when a scope executes and needs to init its
    * local partner links.
    * 
    * @param aPartnerLink
    * @param aPlan
    * @throws AeBusinessProcessException
    */
   public void initPartnerLink(AePartnerLink aPartnerLink, IAeProcessPlan aPlan)  throws AeBusinessProcessException;

   /**
    * Updates the partner link object with the data from the inbound receive.
    *
    * @param aPartnerLink
    * @param aPlan
    * @param aMessageContext
    * @throws AeBusinessProcessException
    */
   public void updatePartnerLink(AePartnerLink aPartnerLink, IAeProcessPlan aPlan, IAeMessageContext aMessageContext) throws AeBusinessProcessException;
}
