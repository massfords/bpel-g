//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/scanner/AeDeploymentFileInfo.java,v 1.1 2005/06/17 21:51:14 PCollins Exp $
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
import java.net.URL;

/**
 * Container class for file deployment info.
 */
public class AeDeploymentFileInfo {
    // file extension constants
    public static final String BPR_SUFFIX = ".bpr"; //$NON-NLS-1$

    /**
     * The deployment directory.
     */
    private static String mDeployDirectory;
    /**
     * The staging directory.
     */
    private static String mStagingDirectory;
    private static File mInstallDir;

    public static void setInstallDir(File aInstallDir) {
        mInstallDir = aInstallDir;
    }

    /**
     * Setter for the staging directory.
     *
     * @param aStagingDir
     */
    public static void setStagingDirectory(String aStagingDir) {
        mStagingDirectory = aStagingDir;
    }

    /**
     * Getter for the staging directory.
     */
    public static File getStagingDirectory() {
        if (mInstallDir == null)
            return new File(mStagingDirectory);
        else
            return new File(mInstallDir, mStagingDirectory);
    }

    /**
     * Setter for the deployment directory.
     *
     * @param aDeploymentDir
     */
    public static void setDeploymentDirectory(String aDeploymentDir) {
        mDeployDirectory = aDeploymentDir;
    }

    /**
     * Getter for the deployment directory.
     */
    public static File getDeploymentDirectory() {
        if (mInstallDir == null)
            return new File(mDeployDirectory);
        else
            return new File(mInstallDir, mDeployDirectory);
    }

    /**
     * Return true if the URL is point to a BPEL deployment archive file.
     *
     * @param aFileUrl
     */
    public static boolean isBprFile(URL aFileUrl) {
        return aFileUrl.getFile().endsWith(BPR_SUFFIX) || aFileUrl.getFile().endsWith(".zip")
                || aFileUrl.getFile().endsWith(".jar");
    }
}
