// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeDeploymentId.java,v 1.3 2004/10/05 23:00:39 PCollins Exp $
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
 *  Uniquely identifies a deployment.  This impl uses the url 
 *  of the BPR archive as the id.
 */
public class AeDeploymentId implements IAeDeploymentId
{
   /** url string */
   private String mId;
   
   /**
    * Constructor.
    * @param aURL deployment url
    */
   public AeDeploymentId( URL aURL )
   {
      mId = aURL.toExternalForm();   
   }
   
   /**
    * Constructor.
    * @param aId
    */
   public AeDeploymentId( String aId )
   {
      mId = aId;
   }
   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentId#getId()
    */
   public String getId()
   {
      return mId;
   }
   
   
   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aObj)
   {
      if( aObj != null && aObj instanceof IAeDeploymentId )
      {
         IAeDeploymentId other = (IAeDeploymentId)aObj;
         return getId().equals( other.getId() );
      }
      return false;
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return getId().hashCode();
   }

}
