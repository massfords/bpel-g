// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/catalog/resource/IAeCatalogResourceDeployment.java,v 1.1 2006/07/18 20:05:33 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.catalog.resource;

import org.activebpel.rt.bpel.server.deploy.IAeDeploymentId;

/**
 * Wraps a catalog deployment.
 */
public interface IAeCatalogResourceDeployment
{
   /**
    * Getter for the location hint.
    */
   public String getLocationHint();
   
   /**
    * Getter for the deployment id.
    */
   public IAeDeploymentId getDeploymentId();
}
