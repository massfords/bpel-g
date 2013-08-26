// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AePreDeploymentValidationHandler.java,v 1.5 2005/06/13 17:54:06 PCollins Exp $
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
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentHandler;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;

/**
 * The default validation handler for the system.
 */
public class AePreDeploymentValidationHandler implements IAeDeploymentHandler {

    /**
     * The top level predeployment validator
     */
    private static final IAePredeploymentValidator PREDEPLOY_VALIDATOR = AePredeploymentValidator.createDefault();

    @Override
    public void deploy(IAeDeploymentContainer aContainer, IAeDeploymentLogger aLogger)
            throws AeException {
        boolean skipValidation = aContainer.exists("skip.validation");
        if (!skipValidation) {
            PREDEPLOY_VALIDATOR.validate(aContainer, aLogger);
            if (aLogger.hasErrors()) {
                throw new AeException("errors during pre-deployment validation");
            }
        }
    }

    @Override
    public void undeploy(IAeDeploymentContainer aContainer) throws AeException {
        // no op
    }
}
