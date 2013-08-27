// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeUnpackedDeploymentStager.java,v 1.9 2006/02/24 16:37:30 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.util.AeFileUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Responsible for deploying the bprs to the expanded working dir.
 */
public class AeUnpackedDeploymentStager {
    // message constants
    private static final String CANNOT_WRITE_ERROR = "AeUnpackedDeploymentStager.ERROR_2"; //$NON-NLS-1$
    private static final String DELETING_OLD_DEPLOYMENTS_MSG = "AeUnpackedDeploymentStager.MSG_1"; //$NON-NLS-1$
    private static final String UNABLE_TO_DELETE_RESOURCE = "AeUnpackedDeploymentStager.MSG_3"; //$NON-NLS-1$

    /**
     * Temp deployment managet instance.
     */
    private static AeUnpackedDeploymentStager sInstance;
    /**
     * Working dir.
     */
    private final File mWorkingDir;
    /**
     * Maps url to deployment dir.
     */
    private final Map<URL, AeTempResource> mTempResources;
    /**
     * Logging object.
     */
    private static final Log sLog = LogFactory.getLog(AeUnpackedDeploymentStager.class);

    /**
     * Static initialization of <code>AeUnpackedDeploymentStager</code> instance.
     *
     * @param aWorkingDir
     */
    public static void init(File aWorkingDir) {
        sInstance = new AeUnpackedDeploymentStager(aWorkingDir);
    }

    /**
     * Accessor for AeUnpackedDeploymentStager.
     */
    public static AeUnpackedDeploymentStager getInstance() {
        return sInstance;
    }

    /**
     * Constructor.  On start up, any existing (old) deployments
     * are removed.
     *
     * @param aWorkingDir
     */
    protected AeUnpackedDeploymentStager(File aWorkingDir) {
        mWorkingDir = aWorkingDir;
        deleteOldDeployments();
        mWorkingDir.mkdirs();
        mTempResources = new HashMap<>();
    }

    /**
     * Delete any old deployment directories.
     */
    protected void deleteOldDeployments() {
        if (getWorkingDir().isDirectory()) {
            sLog.debug(AeMessages.format(DELETING_OLD_DEPLOYMENTS_MSG, new Object[]{getWorkingDir().getPath()}));
            AeFileUtil.recursivelyDelete(getWorkingDir());
        }
    }

    /**
     * Copies/expands the contents of the bpr file to the working dir.
     *
     * @param aDeployment
     * @throws IOException
     */
    public URL deploy(URL aDeployment) throws IOException {
        File originalFile = new File(aDeployment.getFile().replace('/', File.separatorChar));
        URL unpackedLocation = unpack(originalFile);
        addMapping(aDeployment, unpackedLocation);
        return unpackedLocation;
    }

    protected URL unpack(File aOriginalFile)
            throws IOException {
        String unpackedDirName = generateTempFileName(aOriginalFile.getName()).replace('.', '_');
        File unpackedDir = new File(getWorkingDir(), unpackedDirName);
        AeFileUtil.unpack(aOriginalFile, unpackedDir);
        return unpackedDir.toURI().toURL();
    }

    /**
     * Given the deployment URL, find the matching temp URL and
     * delete the associated directory that a deployment was unpacked to.
     *
     * @param aDeployment
     */
    public void removeTempDir(URL aDeployment) {
        AeTempResource tempResource = getTempResource(aDeployment);
        if (tempResource != null) {
            tempResource.deleteTempDir();
            getTempResourcesMap().remove(aDeployment);
        }
    }

    /**
     * Given the deployment URL returns the temp deployment URL.
     *
     * @param aDeployment
     */
    public URL getTempURL(URL aDeployment) {
        URL tempUrl = null;
        AeTempResource tempResource = getTempResource(aDeployment);
        if (tempResource != null) {
            tempUrl = tempResource.getTempDirUrl();
        }
        return tempUrl;
    }

    /**
     * Getter for the <code>AeTempResource</code> mapped to the deployment url.
     *
     * @param aDeployment
     */
    protected AeTempResource getTempResource(URL aDeployment) {
        AeTempResource tempResource = getTempResourcesMap().get(aDeployment);
        return tempResource;
    }

    /**
     * @return Return the temp resources map.
     */
    protected Map<URL, AeTempResource> getTempResourcesMap() {
        return mTempResources;
    }

    /**
     * Maps a deployment URL to its temp file location.
     *
     * @param aDeploymentURL
     * @param aTempURL
     */
    protected void addMapping(URL aDeploymentURL, URL aTempURL) {
        AeTempResource tempResource = new AeTempResource(aTempURL);
        mTempResources.put(aDeploymentURL, tempResource);
    }

    /**
     * Generate a temp file name.
     *
     * @param aOriginalName
     */
    protected String generateTempFileName(String aOriginalName) {
        return "ae_temp_" + aOriginalName; //$NON-NLS-1$
    }

    /**
     * Utility method for copying files.
     * Delegates to <code>AeFileUtil.copyFile</code>.
     *
     * @param aSource      The soure file.
     * @param aDestination The target file.
     * @throws IOException
     */
    public void copyFile(URL aSource, File aDestination) throws IOException {
        if (!getWorkingDir().canWrite()) {
            throw new IOException(AeMessages.format(CANNOT_WRITE_ERROR, new Object[]{getWorkingDir()}));
        }

        try (InputStream in = aSource.openStream();
             OutputStream out = new FileOutputStream(aDestination)) {
            AeFileUtil.copy(in, out);
        }
    }

    /**
     * Accessor for the working directory.
     */
    protected File getWorkingDir() {
        return mWorkingDir;
    }

    /**
     * Convenience class that wraps the temporary resources created during
     * a deployment.
     */
    class AeTempResource {
        /**
         * the root dir where the deployment file has been unpacked to
         */
        private final String mTempDir;

        /**
         * Constructor.
         *
         * @param aUnpackedDir
         */
        public AeTempResource(URL aUnpackedDir) {
            mTempDir = aUnpackedDir.toExternalForm();
        }

        /**
         * @return Return the url of the root dir where the deployment file has been unpacked.
         */
        public URL getTempDirUrl() {
            try {
                return new URL(mTempDir);
            } catch (MalformedURLException me) {
                return null;
            }
        }

        /**
         * Delete the temp dir and all of its contents.
         */
        public void deleteTempDir() {
            File tempDir = new File(getTempDirUrl().getFile());
            AeFileUtil.recursivelyDelete(tempDir);
            if (tempDir.isDirectory()) {
                sLog.info(AeMessages.format(UNABLE_TO_DELETE_RESOURCE, new Object[]{mTempDir}));
            }
        }
    }
}
