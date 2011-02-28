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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.AeDelegatingDeploymentHandler;
import org.activebpel.rt.bpel.server.deploy.AeNewDeploymentInfo;
import org.activebpel.rt.bpel.server.deploy.AeUnpackedDeploymentStager;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainerFactory;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentHandler;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.config.AeFileBasedEngineConfig;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLoggerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bpelg.services.deploy.types.UndeploymentRequest;

/**
 * Active bprl impl of the <code>IAeDeploymentFileHandler</code>.
 */
public class AeDeploymentFileHandler implements IAeDeploymentFileHandler, IAeScannerListener {
    private Log sLog = LogFactory.getLog(AeDeploymentFileHandler.class);
    private IAeDeploymentContainerFactory mDeploymentContainerFactory;
    /** The directory scanner. */
    protected AeDirectoryScanner mScanner;
    /** The scan interval for the scanner. */
    protected long mScanInterval;

    /**
     * Constructor.
     * 
     * @param aLog
     */
    public AeDeploymentFileHandler() {
        AeUnpackedDeploymentStager.init(AeDeploymentFileInfo.getStagingDirectory());
    }

    /**
     * Return <code>FilenameFilter</code> for deployment files.
     */
    protected FilenameFilter getDeploymentFileFilter() {
        return new FilenameFilter() {
            public boolean accept(File aDir, String aFileName) {
                return aFileName.endsWith(AeDeploymentFileInfo.BPR_SUFFIX)
                        || aFileName.endsWith(".zip")
                        || aFileName.endsWith(AeDeploymentFileInfo.getConfigFileName());
            }
        };
    }

    /**
     * Returns the unpacked deployment stager instance to use.
     */
    protected AeUnpackedDeploymentStager getUnpackedDeploymentStager() {
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
    protected void createScanner() {
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
    public void handleAdd(URL aURL, IAeDeploymentLogger aLogger) {
        if (AeDeploymentFileInfo.isEngineConfig(aURL)) {
            handleAddConfig(aURL);
        } else {
            URL tempURL = unpackDeployment(aURL);

            if (tempURL != null && AeDeploymentFileInfo.isBprFile(aURL)) {
                handleAddBpr(aURL, aLogger);
            }
        }
    }

    /**
     * Handle a new bpr archive file.
     * 
     * @param aBprUrl
     */
    protected void handleAddBpr(URL aBprUrl, IAeDeploymentLogger aLogger) {
        handleAddInternal(aBprUrl, aLogger);
    }

    /**
     * Handle a new engine config file.
     * 
     * @param aFileURL
     */
    protected void handleAddConfig(URL aFileURL) {
        ((AeFileBasedEngineConfig) AeEngineFactory.getEngineConfig()).updateBecauseFileChanged();
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
    public void handleRemove(URL aURL) {
        if (!AeDeploymentFileInfo.isEngineConfig(aURL)) {
            if (AeDeploymentFileInfo.isBprFile(aURL)) {
                sLog.info(AeMessages.getString("AeDeploymentFileHandler.5") + aURL); //$NON-NLS-1$
				undeploy(aURL);
            }
        }
    }

    public boolean undeploy(UndeploymentRequest aRequest) {
    	try {
			return undeploy(new File(mScanner.getScanDir(), aRequest.getDeploymentContainerId()).toURI().toURL());
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
    public boolean undeploy(URL aFileUrl) {
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
    protected void setScanner(AeDirectoryScanner scanner) {
        mScanner = scanner;
    }

    /**
     * @return Returns the scanner.
     */
    protected AeDirectoryScanner getScanner() {
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
