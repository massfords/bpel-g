// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AeDefaultValidationHandler.java,v 1.5 2005/06/13 17:54:06 PCollins Exp $
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
 * The default validation handler for the system.
 */
public class AeDefaultValidationHandler implements IAeValidationHandler
{
   
   /** The top level predeployment validator */
   private static final IAePredeploymentValidator PREDEPLOY_VALIDATOR =
      AePredeploymentValidator.createDefault();

   /**
    * @see org.activebpel.rt.bpel.server.deploy.validate.IAeValidationHandler#doPredeploymentValidation(org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr, org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter)
    */
   public void doPredeploymentValidation(
      IAeBpr aBpr,
      IAeBaseErrorReporter aReporter)
      throws AeException
   {
      PREDEPLOY_VALIDATOR.validate( aBpr, aReporter );
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.validate.IAeValidationHandler#doDeploymentValidation(java.lang.String, org.activebpel.rt.bpel.server.IAeProcessDeployment, org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter)
    */
   public void doDeploymentValidation(
      String aPddLocation,
      IAeProcessDeployment aDeployment,
      IAeBaseErrorReporter aReporter)
      throws AeException
   {
      AeDeploymentValidator deploymentValidator = 
         new AeDeploymentValidator(aPddLocation,aDeployment,aReporter);
      
      deploymentValidator.validate();
   }
}
