// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/IAeProcessDeployment.java,v 1.23 2007/02/13 15:27:00 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server;

import java.util.Set;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.IAePartnerLink;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.server.addressing.AeEndpointReferenceSourceType;
import org.activebpel.rt.bpel.server.deploy.AeProcessPersistenceType;
import org.activebpel.rt.bpel.server.deploy.AeProcessTransactionType;
import org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo;
import org.activebpel.wsio.receive.IAeMessageContext;
import org.w3c.dom.Element;

/**
 * Interface of process deployment descriptor.
 */
public interface IAeProcessDeployment extends IAeProcessPlan
{
   /**
    * Returns an endpoint reference for the given partner link for partnerRole, or null if not found.
    * 
    * @param aPartnerLink the partner link we are looking for
    */
   public IAeEndpointReference getPartnerEndpointRef(String aPartnerLink);

   /**
    * Set of keys for resource imports associated with this deployment.
    * @return Set of AeResourceKey objects.
    */
   public Set getResourceKeys();
   
   /**
    * Gets the source element for this process deployment data
    * todo changing activebpel admin console to display nicely formatted data would remove need for src element on interface
    */
   public Element getSourceElement();

   /**
    * Updates the partner link with any endpoint reference data available from the
    * partner addressing layer or from the message context's headers. If the partner link
    * was deployed with the principal source, then it'll use the principal.
    * If the endpoint source was set to invoker then it'll use the embedded endpoint
    * that was extracted from the transport layer. If neither, then no changes are
    * made to the partner link.
    *
    * @param aPartnerLink
    * @param aMessageContext
    */
   public void updatePartnerLink(IAePartnerLink aPartnerLink, IAeMessageContext aMessageContext) throws AeBusinessProcessException;

   /**
    * Returns the source for the specified partner link name.
    *
    * @param aPartnerLink
    */
   public AeEndpointReferenceSourceType getEndpointSourceType(String aPartnerLink);

   /**
    * Gets the source xml for the bpel process.
    */
   public String getBpelSource();

   /**
    * Accessor for the plan id.
    */
   public int getPlanId();

   /**
    * Set up the def object's initial state.
    * @throws AeBusinessProcessException
    */
   public void preProcessDefinition() throws AeBusinessProcessException;

   /**
    * Returns process persistence type.
    */
   public AeProcessPersistenceType getPersistenceType();

   /**
    * Returns process transaction type.
    */
   public AeProcessTransactionType getTransactionType();

   /**
    * Return the custom invoke handler uri string for this partner link or null if none is found.
    * @param aPartnerLink
    */
   public String getInvokeHandler(String aPartnerLink);
   
   /**
    * Gets the info for the partner link
    * @param aPartnerLink
    */
   public IAeServiceDeploymentInfo getServiceInfo(String aPartnerLink);
}
