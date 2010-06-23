// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/IAeValidationHandler.java,v 1.2 2005/06/13 17:54:06 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;

/**
 * Interface used by the deployment code.  Allows the 
 * deployment code to validate the bpr as a whole 
 * (doPredeploymentValidation) and then validate each
 * IAeProcessDeployment before adding it to the engine.
 */
public interface IAeValidationHandler
{
   
   /**
    * Validate the bpr file.
    * @param aBpr The deployment unit.
    * @param aReporter Absorbs error and warning messages.
    * @throws AeException
    */
   public void doPredeploymentValidation( 
      IAeBpr aBpr,
      IAeBaseErrorReporter aReporter )
   throws AeException;
   
   /**
    * Validate the process deployment.
    * @param aPddLocation The source of the process deployment.
    * @param aDeployment The deployment object.
    * @param aReporter Absorbs error and warning messages.
    * @throws AeException
    */
   public void doDeploymentValidation( 
      String aPddLocation, 
      IAeProcessDeployment aDeployment, 
      IAeBaseErrorReporter aReporter )
   throws AeException;

}
