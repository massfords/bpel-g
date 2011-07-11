//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/AeServerIdentityServiceManager.java,v 1.1 2008/02/02 19:44:32 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity;

import java.io.IOException;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentUtil;

/**
 * Identity Service manager which is dependent on a server
 * to deploy custom BPEL processes.
 */
public class AeServerIdentityServiceManager extends AeIdentityServiceManager
{
   /**
    * Default constructor.
    */
   public AeServerIdentityServiceManager(Map aConfig)
   {
      super(aConfig);      
   }
   
   /** 
    * Overrides method to deploy bpr. 
    * @see org.activebpel.rt.bpel.impl.AeManagerAdapter#prepareToStart()
    */
   public void prepareToStart() throws Exception
   {
      deployBprs();
   }   
   
   /**
    * Deploys bpr
    * @throws IOException
    * @throws AeException
    */
   protected void deployBprs() throws IOException, AeException
   {
      deployBpr("aeidentitysvc.bpr"); //$NON-NLS-1$
   }

   /**
    * Deploys named bpr. The bpr should exist in the class path.
    * @param aBprName
    * @throws IOException
    * @throws AeException
    */
   protected void deployBpr(String aBprName) throws IOException, AeException
   {
      AeDeploymentUtil.deployBprWithErrorCheck(getClass(), aBprName);
   }   
}
