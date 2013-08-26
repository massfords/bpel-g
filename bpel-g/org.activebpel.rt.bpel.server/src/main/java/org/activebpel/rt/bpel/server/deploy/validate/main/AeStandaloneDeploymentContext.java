// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/main/AeStandaloneDeploymentContext.java,v 1.5 2006/07/18 20:05:34 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate.main;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentId;

/**
 * Simple impl of IAeDeploymentContext for running the stand alone
 * pre-deployment validators.  Most of these methods are no-ops as
 * the validators are primarily concerned with getResourceAsStream
 * (which is wrapped inside the AeBprFile).
 */
public class AeStandaloneDeploymentContext implements IAeDeploymentContext {
    /**
     * URL class loader
     */
    private URLClassLoader mContext;

    /**
     * Constructor.
     *
     * @param aBprFile The file to be validated.
     * @throws MalformedURLException
     */
    public AeStandaloneDeploymentContext(File aBprFile)
            throws MalformedURLException {
        URL url = aBprFile.toURI().toURL();
        mContext = new URLClassLoader(new URL[]{url});
    }

    /**
     * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getDeploymentId()
     */
    public IAeDeploymentId getDeploymentId() {
        return null;
    }

    /**
     * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getDeploymentLocation()
     */
    public URL getDeploymentLocation() {
        return mContext.getURLs()[0];
    }

    /**
     * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getResourceAsStream(java.lang.String)
     */
    public InputStream getResourceAsStream(String aResourceName) {
        return mContext.getResourceAsStream(aResourceName);
    }

    /**
     * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getResourceURL(java.lang.String)
     */
    public URL getResourceURL(String aResourceName) {
        return mContext.getResource(aResourceName);
    }

    /**
     * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getShortName()
     */
    public String getShortName() {
        return null;
    }

    /**
     * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getTempDeploymentLocation()
     */
    public URL getTempDeploymentLocation() {
        return null;
    }

    /**
     * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getResourceClassLoader()
     */
    public ClassLoader getResourceClassLoader() {
        return mContext;
    }
}
