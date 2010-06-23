// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/IAeWebServicesDeployer.java,v 1.5 2008/02/17 21:38:45 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import org.activebpel.rt.AeException;

/**
 * Handle the platform specific details of deploying a web service.
 */
public interface IAeWebServicesDeployer
{
   /**
    * Deploy all web services in the container 
    * 
    * @param aContainer
    * @param aLoader
    * @throws AeException
    */
   public void deployToWebServiceContainer(IAeDeploymentContainer aContainer, ClassLoader aLoader) throws AeException;
   
   /**
    * Undeploy all web services in the container
    * 
    * @param aContainer
    * @throws AeException
    */
   public void undeployFromWebServiceContainer(IAeDeploymentContainer aContainer) throws AeException;
   
   /**
    * Deploy one web service 
    * 
    * @param aService
    * @param aLoader
    * @throws AeException
    */
   public void deployToWebServiceContainer(IAeServiceDeploymentInfo aService, ClassLoader aLoader) throws AeException;
   
   /**
    * Undeploy one web service
    * 
    * @param aService
    * @throws AeException
    */
   public void undeployFromWebServiceContainer(IAeServiceDeploymentInfo aService) throws AeException;
}
