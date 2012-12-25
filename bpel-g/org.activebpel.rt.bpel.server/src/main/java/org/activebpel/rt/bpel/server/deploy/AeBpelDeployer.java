// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeBpelDeployer.java,v 1.7 2005/06/08 13:30:31 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import bpelg.services.processes.types.ProcessFilterType;
import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeDeploymentProvider;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.deploy.bpr.AePddResource;
import org.activebpel.rt.bpel.server.deploy.validate.AeDeploymentValidator;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;
import org.activebpel.rt.expr.validation.functions.IAeFunctionValidatorFactory;
import org.activebpel.rt.util.AeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import java.text.MessageFormat;

/**
 * IAeBpelDeployer impl.
 */
public class AeBpelDeployer implements IAeDeploymentHandler {

    private static final Log sLog = LogFactory.getLog(AeBpelDeployer.class);

    @Inject
    private IAeExpressionLanguageFactory mExpressionLanguageFactory;
    @Inject
    private IAeFunctionValidatorFactory mFunctionValidatorFactory;

    @Override
    public void deploy(IAeDeploymentContainer aContainer, IAeDeploymentLogger aLogger)
            throws AeException {
        for (AePddResource pdd : aContainer.getPddResources()) {
            boolean success = false;
            String pddName = pdd.getName();
            String shortName = AeUtil.getFilename(pdd.getName());
            aLogger.setPddName(shortName);
            boolean skipValidation = aContainer.exists("skip.validation");
            try {
                IAeDeploymentSource source = aContainer.getDeploymentSource(pdd.getPdd());
                sLog.debug("Deploying BPEL for " + source.getPdd().getName() + " from " + pddName); //$NON-NLS-1$ //$NON-NLS-2$
                deployBpel(aContainer.getDeploymentId().getId(), source, aLogger, skipValidation);

                if (!aLogger.hasErrors()) {
                    if (aLogger.hasWarnings()) {
                        sLog.warn(MessageFormat.format(
                                AeMessages.getString("AeDeploymentHandler.4"), pddName)); //$NON-NLS-1$
                    }
                    aLogger.addInfo(
                            AeMessages.getString("AeDeploymentHandler.SUCCESSFULLY_DEPLOYED_PDD"), new Object[] {}, null); //$NON-NLS-1$
                    success = true;
                } else {
                    sLog.error(MessageFormat.format(
                            AeMessages.getString("AeDeploymentHandler.ERROR_8"), pddName)); //$NON-NLS-1$
                }
            } catch (Throwable t) {
                AeException.logError(t, t.getLocalizedMessage());
                aLogger.addError(
                        AeMessages.getString("AeDeploymentHandler.ERROR_15"), new Object[] { pddName, t.getLocalizedMessage() }, null); //$NON-NLS-1$
            } finally {
                aLogger.processDeploymentFinished(success);
            }
        }
    }

    @Override
    public void undeploy(IAeDeploymentContainer aContainer) throws AeException {
        for (AePddResource pdd : aContainer.getPddResources()) {
            try {
                IAeDeploymentSource source = aContainer.getDeploymentSource(pdd.getPdd());
                sLog.debug("Undeploying bpel: " + pdd.getName() + " from " + pdd.getPdd().getLocation()); //$NON-NLS-1$ //$NON-NLS-2$
                AeEngineFactory.getBean(IAeDeploymentProvider.class).removeDeploymentPlan(
                        source.getPdd().getName());
                IAeProcessManager pm = AeEngineFactory.getBean(IAeProcessManager.class);
                ProcessFilterType filter = new ProcessFilterType().withProcessName(source.getPdd().getName());
				pm.removeProcesses(filter);
            } catch (AeException e) {
                sLog.error(
                        MessageFormat.format(
                                AeMessages.getString("AeDeploymentHandler.ERROR_9"), pdd.getName()), e); //$NON-NLS-1$
            }
        }

    }

    public void deployBpel(String containerId, IAeDeploymentSource aSource, IAeBaseErrorReporter aReporter,
            boolean aSkipValidation) throws AeException {
        IAeProcessDeployment deployment = create(containerId, aSource);
        if (!aSkipValidation) {
            AeDeploymentValidator deploymentValidator = new AeDeploymentValidator(
                    aSource.getPdd().getLocation(), deployment, aReporter);
            deploymentValidator.setExpressionLanguageFactory(getExpressionLanguageFactory());
            deploymentValidator.setFunctionValidatorFactory(getFunctionValidatorFactory());
            deploymentValidator.validate();
        }
        if (aSkipValidation || !aReporter.hasErrors()) {
            AeEngineFactory.getBean(IAeDeploymentProvider.class).addDeploymentPlan(deployment);
        }
    }

    /**
     * Create the process deployment.
     * 
     * @param aSource
     * @throws AeDeploymentException
     */
    public IAeProcessDeployment create(String containerId, IAeDeploymentSource aSource) throws AeDeploymentException {
        return AeProcessDeploymentFactory.getInstance().newInstance(containerId, aSource,
                getExpressionLanguageFactory());
    }

    public IAeExpressionLanguageFactory getExpressionLanguageFactory() {
        return mExpressionLanguageFactory;
    }

    public void setExpressionLanguageFactory(IAeExpressionLanguageFactory aExpressionLanguageFactory) {
        mExpressionLanguageFactory = aExpressionLanguageFactory;
    }

    public IAeFunctionValidatorFactory getFunctionValidatorFactory() {
        return mFunctionValidatorFactory;
    }

    public void setFunctionValidatorFactory(IAeFunctionValidatorFactory aFunctionValidatorFactory) {
        mFunctionValidatorFactory = aFunctionValidatorFactory;
    }
}
