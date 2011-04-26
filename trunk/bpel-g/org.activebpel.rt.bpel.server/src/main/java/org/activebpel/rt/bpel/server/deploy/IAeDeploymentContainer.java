// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/IAeDeploymentContainer.java,v 1.5 2007/02/13 15:26:59 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.List;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;

/**
 * Top level interface for wrapping of the deployment details.
 */
public interface IAeDeploymentContainer extends IAeBpr, IAeDeploymentContext {
	/**
	 * @return service deployment information
	 * @throws AeException 
	 */
	public List<IAeServiceDeploymentInfo> getServiceDeploymentInfo() throws AeException;
}
