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
public class AeDeploymentFileInfo
{
   // file extension constants
   public static final String BPR_SUFFIX = ".bpr"; //$NON-NLS-1$
   public static final String WSR_SUFFIX = ".wsr"; //$NON-NLS-1$

   /** Engine config file name. */
   private static String mConfigFileName;
   /** The deployment directory. */
   private static String mDeployDirectory;
   /** The staging directory. */
   private static String mStagingDirectory;
   
   /**
    * Accessor for engine config file name.
    */
   public static String getConfigFileName()
   {
      return mConfigFileName;
   }
   
   /**
    * Setter for the engine config file name.
    * @param aConfigFileName
    */
   public static void setConfigFileName( String aConfigFileName )
   {
      mConfigFileName = aConfigFileName;
   }
   
   /**
    * Setter for the staging directory.
    * @param aStagingDir
    */
   public static void setStagingDirectory( String aStagingDir )
   {
      mStagingDirectory = aStagingDir;
   }
   
   /**
    * Getter for the staging directory.
    */
   public static String getStagingDirectory()
   {
      return mStagingDirectory;
   }

   /**
    * Setter for the deployment directory.
    * @param aDeploymentDir
    */
   public static void setDeploymentDirectory( String aDeploymentDir )
   {
      mDeployDirectory = aDeploymentDir;
   }
   
   /**
    * Getter for the deployment directory.
    */
   public static String getDeploymentDirectory()
   {
      return mDeployDirectory;
   }
   
   /**
    * Accessor for the engine config file.
    */
   public static File getEngineConfigFile()
   {
      File deploymentDir = new File( getDeploymentDirectory() );
      return new File( deploymentDir, getConfigFileName() );
   }
   
   /**
    * Return true if the URL is point to the engine config file.
    * @param aFileUrl
    */
   public static boolean isEngineConfig( URL aFileUrl )
   {
      return aFileUrl.getFile().endsWith( getConfigFileName() );
   }

   /**
    * Return true if the URL is point to a web services file.
    * @param aFileUrl
    */
   public static boolean isWsrFile( URL aFileUrl )
   {
      return aFileUrl.getFile().endsWith( WSR_SUFFIX );
   }
   
   /**
    * Return true if the URL is point to a BPEL deployment archive file.
    * @param aFileUrl
    */
   public static boolean isBprFile( URL aFileUrl )
   {
      return aFileUrl.getFile().endsWith( BPR_SUFFIX );
   }
}
