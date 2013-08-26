//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel.web/src/org/activebpel/rt/axis/bpel/web/AeBuildNumber.java,v 1.3 2005/02/01 19:47:46 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.web;


import java.io.IOException;
import java.net.URL;
import java.util.jar.Manifest;

/**
 * Resource file which contains the build number and build date.
 */
public class AeBuildNumber {
    private static String mBuildNumber = ""; //$NON-NLS-1$
    private static String mBuildDate = ""; //$NON-NLS-1$
    private static String mVersionNumber = ""; //$NON-NLS-1$

    static {
        try {
            String cp = AeBuildNumber.class.getResource(AeBuildNumber.class.getSimpleName() + ".class").toString();
            cp = cp.substring(0, cp.indexOf(AeBuildNumber.class.getPackage().getName().replace(".", "/"))) + "META-INF/MANIFEST.MF";
            Manifest mf = new Manifest((new URL(cp)).openStream());

            mBuildNumber = mf.getMainAttributes().getValue("Implementation-Build"); //$NON-NLS-1$
            mBuildDate = mf.getMainAttributes().getValue("Implementation-Date"); //$NON-NLS-1$
            mVersionNumber = mf.getMainAttributes().getValue("Implementation-Version"); //$NON-NLS-1$
        } catch (IOException ioe) {
        }
    }

    /**
     * default constructor for a build resource file
     */
    private AeBuildNumber() {
    }

    /**
     * Obtains the build number for this component.
     *
     * @return a String value representing the build number
     */
    public final static String getBuildNumber() {
        return mBuildNumber;
    }

    /**
     * Obtains the build number for this component.
     *
     * @return a String value representing the build number
     */
    public final static String getVersionNumber() {
        return mVersionNumber;
    }

    /**
     * Obtains the build date for this component.
     *
     * @return a String value representing the build date
     */
    public final static String getBuildDate() {
        return mBuildDate;
    }
}
