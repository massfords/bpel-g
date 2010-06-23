// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeAbstractDeploymentContext.java,v 1.5 2005/06/17 21:51:13 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.net.URL;

/**
 * Base class for implementing a deployment context.  
 * The URL serves as the unique identifier for each deployment.
 */
abstract public class AeAbstractDeploymentContext implements IAeDeploymentContext
{
   /** deployment location */
   private URL mLocation;
   /** deployment id */
   private IAeDeploymentId mDeploymentId;   

   /**
    * Constructor.
    * @param aURL the deployment url
    */
   public AeAbstractDeploymentContext( URL aURL )
   {
      mLocation = aURL;
      mDeploymentId = new AeDeploymentId( aURL );
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getDeploymentId()
    */
   public IAeDeploymentId getDeploymentId()
   {
      return mDeploymentId;
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return mDeploymentId.hashCode();
   }

   /**
    * Returns - deploymentId
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      return mDeploymentId.getId();
   }

   /**
    * Determines equality based ONLY on the deploymentId
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aObj)
   {
      if( aObj != null && aObj instanceof IAeDeploymentContext )
      {
         IAeDeploymentContext other = (IAeDeploymentContext)aObj;
         return getDeploymentId().equals( other.getDeploymentId() );
      }
      return false;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getDeploymentLocation()
    */
   public URL getDeploymentLocation()
   {
      return mLocation;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getShortName()
    */
   public String getShortName()
   {
      String urlString = getDeploymentLocation().toString();
      return urlString.substring( urlString.lastIndexOf('/')+1 );
   }

}
