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

import java.text.MessageFormat;
import java.util.Iterator;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeDeploymentProvider;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.deploy.validate.AeDeploymentValidator;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;
import org.activebpel.rt.expr.validation.functions.IAeFunctionValidatorFactory;
import org.activebpel.rt.util.AeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

/**
 * IAeBpelDeployer impl.
 */
public class AeBpelDeployer implements IAeDeploymentHandler {

    private static Log sLog = LogFactory.getLog(AeBpelDeployer.class);

    private IAeExpressionLanguageFactory mExpressionLanguageFactory;
    private IAeFunctionValidatorFactory mFunctionValidatorFactory;

    @Override
    public void deploy(IAeDeploymentContainer aContainer, IAeDeploymentLogger aLogger)
            throws AeException {
        for (Iterator iter = aContainer.getPddResources().iterator(); iter.hasNext();) {
            boolean success = false;
            String pddName = (String) iter.next();
            String shortName = AeUtil.getFilename(pddName);
            aLogger.setPddName(shortName);
            boolean skipValidation = aContainer.exists("skip.validation");
            try {
                IAeDeploymentSource source = aContainer.getDeploymentSource(pddName);
                sLog.debug("Deploying BPEL for " + source.getProcessName() + " from " + pddName); //$NON-NLS-1$ //$NON-NLS-2$
                deployBpel(source, aLogger, skipValidation);

                if (!aLogger.hasErrors()) {
                    // Get the service info for undeployment
                    IAeServiceDeploymentInfo[] services = getServiceInfo(source);
                    aContainer.addServiceDeploymentInfo(services);

                    if (aLogger.hasWarnings()) {
                        sLog.warn(MessageFormat.format(
                                AeMessages.getString("AeDeploymentHandler.4"), new Object[] { pddName })); //$NON-NLS-1$
                    }
                    aLogger.addInfo(
                            AeMessages.getString("AeDeploymentHandler.SUCCESSFULLY_DEPLOYED_PDD"), new Object[] {}, null); //$NON-NLS-1$
                    success = true;
                } else {
                    sLog.error(MessageFormat.format(
                            AeMessages.getString("AeDeploymentHandler.ERROR_8"), new Object[] { pddName })); //$NON-NLS-1$
                }
            } catch (Throwable t) {
                AeException.logError(t, t.getLocalizedMessage());
                aLogger.addError(
                        AeMessages.getString("AeDeploymentHandler.ERROR_15"), new Object[] { pddName, t.getLocalizedMessage() }, null); //$NON-NLS-1$
            } finally {
                aLogger.processDeploymentFinished(success);
            }

            aLogger.resetWarningAndErrorFlags();
        }
        aLogger.setPddName(null);
    }

    @Override
    public void undeploy(IAeDeploymentContainer aContainer) throws AeException {
        for (Iterator iter = aContainer.getPddResources().iterator(); iter.hasNext();) {
            String pddName = (String) iter.next();
            try {
                IAeDeploymentSource source = aContainer.getDeploymentSource(pddName);
                sLog.debug("Undeploying bpel: " + source.getProcessName() + " from " + pddName); //$NON-NLS-1$ //$NON-NLS-2$
                AeEngineFactory.getBean(IAeDeploymentProvider.class).removeDeploymentPlan(
                        source.getProcessName());
            } catch (AeException e) {
                sLog.error(
                        MessageFormat.format(
                                AeMessages.getString("AeDeploymentHandler.ERROR_9"), new Object[] { pddName }), e); //$NON-NLS-1$
            }
        }

    }

    public void deployBpel(IAeDeploymentSource aSource, IAeBaseErrorReporter aReporter,
            boolean aSkipValidation) throws AeException {
        IAeProcessDeployment deployment = create(aSource);
        if (!aSkipValidation) {
            AeDeploymentValidator deploymentValidator = new AeDeploymentValidator(
                    aSource.getPddLocation(), deployment, aReporter);
            deploymentValidator.setExpressionLanguageFactory(getExpressionLanguageFactory());
            deploymentValidator.setFunctionValidatorFactory(getFunctionValidatorFactory());
            deploymentValidator.validate();
        }
        if (aSkipValidation || !aReporter.hasErrors()) {
            AeEngineFactory.getBean(IAeDeploymentProvider.class).addDeploymentPlan(deployment);
        }
    }

    /**
     * Gets the service deployment info from a source
     * 
     * @param aSource
     * @throws AeDeploymentException
     */
    protected IAeServiceDeploymentInfo[] getServiceInfo(IAeDeploymentSource aSource)
            throws AeDeploymentException {
        // Get the service info
        Element pddElement = aSource.getProcessSourceElement();
        AeProcessDef processDef = aSource.getProcessDef();
        return AeServiceDeploymentUtil.getServices(processDef, pddElement);
    }

    /**
     * Create the process deployment.
     * 
     * @param aSource
     * @throws AeDeploymentException
     */
    public IAeProcessDeployment create(IAeDeploymentSource aSource) throws AeDeploymentException {
        return AeProcessDeploymentFactory.getInstance().newInstance(aSource,
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
