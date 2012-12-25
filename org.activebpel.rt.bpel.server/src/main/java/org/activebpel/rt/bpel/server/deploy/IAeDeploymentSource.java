// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/IAeDeploymentSource.java,v 1.21 2007/11/21 03:26:02 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.Collection;

import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.server.deploy.pdd.AePartnerLinkDescriptor;

import bpelg.services.deploy.types.pdd.Pdd;
import bpelg.services.processes.types.ServiceDeployments;

/**
 * Interface for deploying bpel process to the engine.
 */
public interface IAeDeploymentSource
{
   /**
    * Gets the plan in id for this deployment source. Only applies when versioning is enabled.
    */
   public int getPlanId();

   /**
    * AeProcessDef for the bpel process.
    */
   public AeProcessDef getProcessDef();
   
   /**
    * Return the collection of partner link descriptors.
    */
   public Collection<AePartnerLinkDescriptor> getPartnerLinkDescriptors();

   /**
    * Gets the services for the plan
    */
   public ServiceDeployments getServices() throws AeDeploymentException;

   public Pdd getPdd();
}
