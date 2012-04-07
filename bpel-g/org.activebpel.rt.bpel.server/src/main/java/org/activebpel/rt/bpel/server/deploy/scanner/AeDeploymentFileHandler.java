//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/scanner/AeDeploymentFileHandler.java,v 1.4 2005/11/18 23:37:12 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.scanner;

import bpelg.services.deploy.types.UndeploymentRequest;
import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.*;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLoggerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Singleton;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

/**
 * Active bprl impl of the <code>IAeDeploymentFileHandler</code>.
 */
@Singleton
public class AeDeploymentFileHandler implements IAeDeploymentFileHandler, IAeScannerListener {
    private Log sLog = LogFactory.getLog(AeDeploymentFileHandler.class);
    private IAeDeploymentContainerFactory mDeploymentContainerFactory;
    /** The directory scanner. */
    protected AeDirectoryScanner mScanner;
    /** The scan interval for the scanner. */
    protected long mScanInterval;

    /**
     * Constructor.
     */
    public AeDeploymentFileHandler() {
        AeUnpackedDeploymentStager.init(AeDeploymentFileInfo.getStagingDirectory());
    }

    /**
     * Return <code>FilenameFilter</code> for deployment files.
     */
    private FilenameFilter getDeploymentFileFilter() {
        return new FilenameFilter() {
            public boolean accept(File aDir, String aFileName) {
                return aFileName.endsWith(AeDeploymentFileInfo.BPR_SUFFIX)
                        || aFileName.endsWith(".zip") || aFileName.endsWith(".jar");
            }
        };
    }

    /**
     * Returns the unpacked deployment stager instance to use.
     */
    private AeUnpackedDeploymentStager getUnpackedDeploymentStager() {
        return AeUnpackedDeploymentStager.getInstance();
    }

    /**
     * @see org.activebpel.rt.bpel.server.deploy.scanner.IAeDeploymentFileHandler#startScanning()
     */
    public void startScanning() {
        getScanner().start();
    }

    /**
     * @see org.activebpel.rt.bpel.server.deploy.scanner.IAeDeploymentFileHandler#stopScanning()
     */
    public void stopScanning() {
        getScanner().stop();
    }

    /**
     * Process any deployments necessary before starting the BPEL engine.
     */
    public void handleInitialDeployments() {
        createScanner();
        File[] deploymentFiles = getScanner().prime();

        if (deploymentFiles != null) {
            for (int i = 0; i < deploymentFiles.length; i++) {
                try {
                    URL url = deploymentFiles[i].toURI().toURL();
                    handleAdd(url, null);
                } catch (MalformedURLException mru) {
                    // this should never happen
                    AeException.logError(
                            mru,
                            MessageFormat.format(
                                    AeMessages.getString("AeDeploymentFileHandler.ERROR_0"), new Object[] { deploymentFiles[i] })); //$NON-NLS-1$
                }
            }
        }
    }

    /**
     * @see org.activebpel.rt.bpel.server.deploy.scanner.IAeDeploymentFileHandler#handleDeployment(java.io.File,
     *      java.lang.String,
     *      org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger)
     */
    // FIXME deployment - the synchronization here is confusing. Clean it up
    // steps for deployment from web service:
    // 1. acquire lock here
    // 2. add file to scanner
    // 3. scanner acquires its lock
    // 4. scanner fires remove event if needed 
    // 5. --> remove event propagates here and undeploys existing version
    // 6. scanner file is copied from temp dir to main dir 
    // 7. scanner records file name as a deployment
    // 8. scanner fires add event
    // 9. --> add event propagates here and deploys resource
    // 10. any errors are reported back to the caller via the deployment logger 
    //     (which is passed in as java.lang.Object and propagated w/ the events)
    // 11. blech :(
    public synchronized void handleDeployment(File aFile, String aFilename,
            IAeDeploymentLogger aLogger) throws AeException {
        try {
            // Tell the scanner to deploy the file.
            getScanner().addDeploymentFile(aFile, aFilename, aLogger);
        } catch (AeException ae) {
            throw ae;
        } catch (Exception e) {
            throw new AeException(e);
        }
    }

    /**
     * Create the directory scanner.
     */
    private void createScanner() {
        File deploymentDir = AeDeploymentFileInfo.getDeploymentDirectory();
        AeDirectoryScanner scanner = new AeDirectoryScanner(deploymentDir, mScanInterval,
                getDeploymentFileFilter(), null);
        scanner.addListener(this);
        setScanner(scanner);
    }

    /**
     * @see org.activebpel.rt.bpel.server.deploy.scanner.IAeScannerListener#onChange(org.activebpel.rt.bpel.server.deploy.scanner.AeScanEvent)
     */
    public void onChange(AeScanEvent aEvent) {
        if (aEvent.isAddEvent()) {
            IAeDeploymentLogger logger = null;
            if (aEvent.getUserData() instanceof IAeDeploymentLogger) {
                logger = (IAeDeploymentLogger) aEvent.getUserData();
            }
            handleAdd(aEvent.getURL(), logger);
        } else {
            handleRemove(aEvent.getURL());
        }
    }

    /**
     * Handle file additions.
     * 
     * @param aURL
     */
    private void handleAdd(URL aURL, IAeDeploymentLogger aLogger) {
        URL tempURL = unpackDeployment(aURL);

        if (tempURL != null && AeDeploymentFileInfo.isBprFile(aURL)) {
            IAeDeploymentLogger logger = aLogger;
			try {
			    AeNewDeploymentInfo info = new AeNewDeploymentInfo();
			    info.setURL(aURL);
			    info.setTempURL(getUnpackedDeploymentStager().getTempURL(aURL));
			    IAeDeploymentContainer deployContainer = getDeploymentContainerFactory().createDeploymentContainer(
			            info);
			
			    // If the logger is null, used the factory to create a new one.
			    if (logger == null) {
			        logger = AeEngineFactory.getBean(IAeDeploymentLoggerFactory.class).createLogger();
			    }
			    logger.setContainerName(deployContainer.getShortName());
			
			    // If the file type is valid, then use the deployment handler to
			    // deploy the BPR.
			    if (!logger.hasErrors()) {
			        IAeDeploymentHandler handler = getDeploymentHandler();
			        handler.deploy(deployContainer, logger);
			    } else {
			        sLog.info(MessageFormat.format(AeMessages.getString("AeDeploymentFileHandler.1"), //$NON-NLS-1$
			                new Object[] { deployContainer.getShortName() }));
			    }
			} catch (Throwable t) {
			    sLog.error(
			            MessageFormat.format(
			                    AeMessages.getString("AeDeploymentFileHandler.ERROR_2"), new Object[] { aURL }), t); //$NON-NLS-1$
			    if (logger != null) {
			        logger.addInfo(
			                AeMessages.getString("AeDeploymentFileHandler.ERROR_DEPLOYING_BPR"), new Object[] { aURL.toString(), t.getLocalizedMessage() }, null); //$NON-NLS-1$
			    }
			} finally {
			    if (logger != null) {
			        logger.close();
			    }
			}
        }
    }

    /**
     * Creates the <code>IAeDeploymentContainer</code> for Web Service and BPR
     * deployments and deploys them via the <code>IAeDeploymentHandler</code>.
     * 
     * @param aFileUrl
     * @param aLogger
     *            The deployment logger to use, if null a new one is created.
     */
    protected void handleAddInternal(URL aFileUrl, IAeDeploymentLogger aLogger) {
        IAeDeploymentLogger logger = aLogger;
        try {
            AeNewDeploymentInfo info = new AeNewDeploymentInfo();
            info.setURL(aFileUrl);
            info.setTempURL(getUnpackedDeploymentStager().getTempURL(aFileUrl));
            IAeDeploymentContainer deployContainer = getDeploymentContainerFactory().createDeploymentContainer(
                    info);

            // If the logger is null, used the factory to create a new one.
            if (logger == null) {
                logger = AeEngineFactory.getBean(IAeDeploymentLoggerFactory.class).createLogger();
            }
            logger.setContainerName(deployContainer.getShortName());

            // If the file type is valid, then use the deployment handler to
            // deploy the BPR.
            if (!logger.hasErrors()) {
                IAeDeploymentHandler handler = getDeploymentHandler();
                handler.deploy(deployContainer, logger);
            } else {
                sLog.info(MessageFormat.format(AeMessages.getString("AeDeploymentFileHandler.1"), //$NON-NLS-1$
                        new Object[] { deployContainer.getShortName() }));
            }
        } catch (Throwable t) {
            sLog.error(
                    MessageFormat.format(
                            AeMessages.getString("AeDeploymentFileHandler.ERROR_2"), new Object[] { aFileUrl }), t); //$NON-NLS-1$
            if (logger != null) {
                logger.addInfo(
                        AeMessages.getString("AeDeploymentFileHandler.ERROR_DEPLOYING_BPR"), new Object[] { aFileUrl.toString(), t.getLocalizedMessage() }, null); //$NON-NLS-1$
            }
        } finally {
            if (logger != null) {
                logger.close();
            }
        }
    }

    /**
     * Convenience accessor for <code>IAeDeploymentHandler</code>.
     */
    protected IAeDeploymentHandler getDeploymentHandler() {
        return AeEngineFactory.getBean(AeDelegatingDeploymentHandler.class);
    }

    /**
     * Unpacks the file deployment in the staging directory.
     * 
     * @param aFileUrl
     * @return The temp (staging) URL.
     */
    protected URL unpackDeployment(URL aFileUrl) {
        try {
            return getUnpackedDeploymentStager().deploy(aFileUrl);
        } catch (IOException ae) {
            sLog.error(
                    MessageFormat.format(
                            AeMessages.getString("AeDeploymentFileHandler.ERROR_3"), new Object[] { aFileUrl.getFile() }), ae); //$NON-NLS-1$
            return null;
        }
    }

    /**
     * Handle file removal.
     * 
     * @param aURL
     */
    private void handleRemove(URL aURL) {
        if (AeDeploymentFileInfo.isBprFile(aURL)) {
            sLog.info(AeMessages.getString("AeDeploymentFileHandler.5") + aURL); //$NON-NLS-1$
			undeploy(aURL);
        } 
    }

    // FIXME this is a temp fix until the deployment issue above is resolved. Need to clean up scanner/handler interaction
    public synchronized boolean undeploy(UndeploymentRequest aRequest) {
    	try {
			File file = new File(mScanner.getScanDir(), aRequest.getDeploymentContainerId());
			boolean result = undeploy(file.toURI().toURL());
			if (result)
				file.delete();
			mScanner.getDeployments().remove(file.getName());
			return result;
		} catch (MalformedURLException e) {
			sLog.error(e);
			return false;
		}
    }

    /**
     * Remove the Web Services or BPEL deployment archive via the
     * <code>IAeDeploymentHandler</code>.
     * 
     * @param aFileUrl
     */
    private boolean undeploy(URL aFileUrl) {
        try {
            URL tempUrl = getUnpackedDeploymentStager().getTempURL(aFileUrl);
            AeNewDeploymentInfo info = new AeNewDeploymentInfo();
            info.setURL(aFileUrl);
            info.setTempURL(tempUrl);
            IAeDeploymentHandler handler = getDeploymentHandler();
            handler.undeploy(getDeploymentContainerFactory().createUndeploymentContainer(info));
            getUnpackedDeploymentStager().removeTempDir(aFileUrl);
            return true;
        } catch (Exception ex) {
            sLog.error(MessageFormat.format(AeMessages.getString("AeDeploymentFileHandler.ERROR_6"), //$NON-NLS-1$
                    new Object[] { aFileUrl }), ex);
            return false;
        }
    }

    /**
     * @param scanner
     *            The scanner to set.
     */
    private void setScanner(AeDirectoryScanner scanner) {
        mScanner = scanner;
    }

    /**
     * @return Returns the scanner.
     */
    private AeDirectoryScanner getScanner() {
        return mScanner;
    }

    public long getScanInterval() {
        return mScanInterval;
    }

    public void setScanInterval(long aScanInterval) {
        mScanInterval = aScanInterval;
    }

    public IAeDeploymentContainerFactory getDeploymentContainerFactory() {
        return mDeploymentContainerFactory;
    }

    public void setDeploymentContainerFactory(
            IAeDeploymentContainerFactory aDeploymentContainerFactory) {
        mDeploymentContainerFactory = aDeploymentContainerFactory;
    }
}
