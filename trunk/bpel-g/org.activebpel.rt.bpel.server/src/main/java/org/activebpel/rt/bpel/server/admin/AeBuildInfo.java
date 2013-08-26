// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/AeBuildInfo.java,v 1.8 2005/02/01 19:56:36 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin;

import java.beans.ConstructorProperties;
import java.lang.reflect.Method;
import java.util.List;

import org.activebpel.rt.AeException;

/**
 * Simple struct that stores the build number and info.
 */
public class AeBuildInfo {
    private String mBuildVersion;
    /**
     * The build number for the project
     */
    private String mBuildNumber;
    /**
     * The build date for the project
     */
    private String mBuildDate;
    /**
     * The project name
     */
    private String mProjectName;

    /**
     * C'tor.
     */
    public AeBuildInfo() {
    }

    /**
     * C'tor.
     *
     * @param aProjectName
     * @param aBuildNumber
     * @param aBuildDate
     * @param aBuildVersion
     */
    @ConstructorProperties({"projectName", "buildNumber", "buildDate", "buildVersion"})
    public AeBuildInfo(String aProjectName, String aBuildNumber, String aBuildDate, String aBuildVersion) {
        setProjectName(aProjectName);
        setBuildNumber(aBuildNumber);
        setBuildDate(aBuildDate);
        setBuildVersion(aBuildVersion);
    }

    /**
     * Gets the build date as a string
     */
    public String getBuildDate() {
        return mBuildDate;
    }

    /**
     * Gets the build number
     */
    public String getBuildNumber() {
        return mBuildNumber;
    }

    /**
     * Gets the build version
     */
    public String getBuildVersion() {
        return mBuildVersion;
    }

    /**
     * Sets the build date
     *
     * @param aString
     */
    public void setBuildDate(String aString) {
        mBuildDate = aString;
    }

    /**
     * Sets the build number
     *
     * @param aString
     */
    public void setBuildNumber(String aString) {
        mBuildNumber = aString;
    }

    /**
     * Sets the build version
     *
     * @param aString
     */
    public void setBuildVersion(String aString) {
        mBuildVersion = aString;
    }

    /**
     * Gets the project name
     */
    public String getProjectName() {
        return mProjectName;
    }

    /**
     * Sets the project name
     *
     * @param aString
     */
    public void setProjectName(String aString) {
        mProjectName = aString;
    }

    /**
     * Creates a BuildInfo object and adds it to the list.  The build number to use is
     * gotten from supplied classname.  The display name to use for the build info object
     * is also passed in.  This method fails silently without adding anything to the list
     * if something goes wrong (typically a class not found exception).
     *
     * @param aList
     * @param aBuildNumberClassname
     * @param aDisplayName
     */
    public static void createBuildInfoFor(List<AeBuildInfo> aList, String aBuildNumberClassname, String aDisplayName) {
        try {
            aList.add(createBuildInfoFor(aBuildNumberClassname, aDisplayName));
        } catch (Exception e) {
        }
    }

    /**
     * Returns a build info object for the given build number class and display name.
     *
     * @param aBuildNumberClassname
     * @param aDisplayName
     * @throws AeException
     */
    public static AeBuildInfo createBuildInfoFor(String aBuildNumberClassname, String aDisplayName) throws AeException {
        try {
            Class clazz = Class.forName(aBuildNumberClassname);
            return createBuildInfoFor(clazz, aDisplayName);
        } catch (Exception e) {
            throw new AeException(e);
        }
    }

    /**
     * Returns a build info object for the given build number class and display name.
     *
     * @param aBuildNumberClass
     * @param aDisplayName
     * @throws AeException
     */
    public static AeBuildInfo createBuildInfoFor(Class aBuildNumberClass, String aDisplayName) throws AeException {
        try {
            String buildNumber = invoke(aBuildNumberClass, "getBuildNumber");  //$NON-NLS-1$
            String buildDate = invoke(aBuildNumberClass, "getBuildDate");  //$NON-NLS-1$
            String buildVersion = invoke(aBuildNumberClass, "getVersionNumber");  //$NON-NLS-1$
            return new AeBuildInfo(aDisplayName, buildNumber, buildDate, buildVersion);
        } catch (Exception e) {
            throw new AeException(e);
        }
    }

    /**
     * Helper method to hide some of the ugliness in using reflection to hit the
     * axis build number info.
     *
     * @param aClass
     * @param aMethod
     */
    protected static String invoke(Class<?> aClass, String aMethod) throws Exception {
        Method m = aClass.getMethod(aMethod, (Class[]) null);
        return (String) m.invoke(null, (Object[]) null);
    }
}
