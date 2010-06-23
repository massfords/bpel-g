// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/IAeDeploymentProvider.java,v 1.15 2007/09/02 17:17:15 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAePlanManager;
import org.activebpel.rt.bpel.server.deploy.AeOperationNotImplementedException;
import org.activebpel.rt.bpel.server.deploy.AeRoutingInfo;
import org.activebpel.rt.bpel.server.deploy.AeServiceNotFoundException;

/**
 * Manages the deployment descriptors which have been deployed for BPEL processes.
 */
public interface IAeDeploymentProvider extends IAePlanManager
{
   /**
    * Add a new plan deployment.
    * @param aDeployment
    */
   public void addDeploymentPlan( IAeProcessDeployment aDeployment );
   
   /**
    * Returns the deployment plan for the plan capable of creating a new instance
    * fo the provided process name.
    * @param aProcessName the process we want the deployment plan for.
    */
   public IAeProcessDeployment findCurrentDeployment(QName aProcessName) throws AeBusinessProcessException;
   
   /**
    * Gets the deployment plan for the given process id.
    * @param aProcessId
    * @param aProcessName
    */
   public IAeProcessDeployment findDeploymentPlan(long aProcessId, QName aProcessName) throws AeBusinessProcessException;

   /**
    * Gets an Iterator of IAeProcessDeployment for all the deployed plans.
    */
   public Iterator getDeployedPlans();

   /**
    * Remove the deployment for the specified process qname.
    * @param aProcessName
    */
   public void removeDeploymentPlan( QName aProcessName );
   
   /**
    * Gets the routing information for the given service name
    * 
    * @param aServiceName
    * @throws AeBusinessProcessException if not found
    */
   public AeRoutingInfo getRoutingInfoByServiceName(String aServiceName) throws AeBusinessProcessException;
   
   /**
    * Gets the routing info for a process that provides a service with the given 
    * name, port type and operation.
    * @param aService
    * @param aPortType
    * @param aOperation
    * @throws AeServiceNotFoundException
    * @throws AeOperationNotImplementedException
    */
   public AeRoutingInfo findService(String aService, QName aPortType, String aOperation) throws AeServiceNotFoundException, AeOperationNotImplementedException;
}
