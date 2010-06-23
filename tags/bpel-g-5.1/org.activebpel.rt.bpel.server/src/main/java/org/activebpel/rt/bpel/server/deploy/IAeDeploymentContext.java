// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/IAeDeploymentContext.java,v 1.11 2006/07/18 20:05:32 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.io.InputStream;
import java.net.URL;

/**
 * Wraps information about a deployment and provides access to its
 * resources.
 */
public interface IAeDeploymentContext
{
   /**
    * Accessor for deployment id
    */
   public IAeDeploymentId getDeploymentId();
   
   /**
    * Point to the deployment location.
    */
   public URL getDeploymentLocation();
   
   /**
    * If the deployment is copied to a staging/temp
    * dir, this will return a url pointing to the
    * location, otherwise null.
    */
   public URL getTempDeploymentLocation();
   
   /**
    * Short name of the deployment - the url resource without protocol and path info.
    */
   public String getShortName();
   
   /**
    * Access the named resource.
    * @param aResourceName
    */
   public InputStream getResourceAsStream( String aResourceName );
   
   /**
    * Get the url for the context resource.
    * @param aResourceName
    * @return The url associated with the passed resource.
    */
   public URL getResourceURL( String aResourceName );
   
   /**
    * Return the deployment classloader.  This is the classloader that has
    * access to the resources necessary for bpr deployments.
    */
   public ClassLoader getResourceClassLoader();
}
