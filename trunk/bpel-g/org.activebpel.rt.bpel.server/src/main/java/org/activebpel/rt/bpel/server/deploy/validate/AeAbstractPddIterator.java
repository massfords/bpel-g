//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AeAbstractPddIterator.java,v 1.4 2006/07/18 20:05:32 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.deploy.bpr.AePddResource;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;

/**
 * Base class for validators that need to iterator over all pdd files in the
 * bpr.
 */
public abstract class AeAbstractPddIterator implements
        IAePredeploymentValidator {

    /**
     * @see org.activebpel.rt.bpel.server.deploy.validate.IAePredeploymentValidator#validate(org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr,
     *      org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter)
     */
    public void validate(IAeBpr aBprFile, IAeBaseErrorReporter aReporter)
            throws AeException {
        for (AePddResource pdd : aBprFile.getPddResources()) {
            validateImpl(pdd, aBprFile, aReporter);
        }
    }

    /**
     * Perform the actual validation logic.
     *
     * @param aPdd
     * @param aBprFile
     * @param aReporter
     * @throws AeException
     */
    protected abstract void validateImpl(AePddResource aPdd, IAeBpr aBprFile,
                                         IAeBaseErrorReporter aReporter) throws AeException;

}
