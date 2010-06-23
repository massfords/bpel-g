// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeDeploymentContainerFactory.java,v 1.1 2005/06/17 21:51:13 PCollins Exp $
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
import java.net.URLClassLoader;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.deploy.bpr.AeBpr;
import org.activebpel.rt.bpel.server.deploy.bpr.AeUnpackedBprContext;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;


/**
 * Create and configure the <code>IAeDeploymentContainers</code>needed for
 * bpr deploys and undeploys.
 */
public class AeDeploymentContainerFactory
{
   /**
    * Create and configure the <code>IAeDeploymentContainer</code>for
    * deployment.
    * @param aInfo
    * @throws AeException
    */
   public static IAeDeploymentContainer createDeploymentContainer( AeNewDeploymentInfo aInfo )
   throws AeException
   {
      ClassLoader current = Thread.currentThread().getContextClassLoader();
      ClassLoader bprResourceClassLoader = URLClassLoader.newInstance( new URL[]{ aInfo.getTempURL() }, current ); 

      String urlString = aInfo.getURL().toString();
      String shortName = urlString.substring( urlString.lastIndexOf('/')+1 );
      AeUnpackedBprContext context = new AeUnpackedBprContext(aInfo.getTempURL(), bprResourceClassLoader, shortName );
      IAeBpr file = AeBpr.createUnpackedBpr( context );
      return new AeDeploymentContainer( context, file, aInfo.getURL() );
   }
   
   /**
    * Create and configure the <code>IAeDeploymentContainer</code>for
    * undeployment.
    * @param aInfo
    * @throws AeException
    */
   public static IAeDeploymentContainer createUndeploymentContainer( 
      AeNewDeploymentInfo aInfo ) throws AeException
   {
      ClassLoader current = Thread.currentThread().getContextClassLoader();
      ClassLoader bprResourceClassLoader = URLClassLoader.newInstance( new URL[]{ aInfo.getTempURL() }, current ); 

      String urlString = aInfo.getURL().toString();
      String shortName = urlString.substring( urlString.lastIndexOf('/')+1 );
      URL idUrl = aInfo.getURL();
      aInfo.setURL( aInfo.getTempURL() );
      AeUnpackedBprContext context = new AeUnpackedBprContext(aInfo.getTempURL(), bprResourceClassLoader, shortName );
      IAeBpr file = AeBpr.createUnpackedBpr( context );
      return new AeDeploymentContainer( context, file, idUrl );
   }
}
