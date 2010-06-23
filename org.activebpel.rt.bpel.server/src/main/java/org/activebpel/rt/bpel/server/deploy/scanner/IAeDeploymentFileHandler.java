//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/scanner/IAeDeploymentFileHandler.java,v 1.1 2005/06/17 21:51:14 PCollins Exp $
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

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;


/**
 * Interface for interacting with file system deployments.
 */
public interface IAeDeploymentFileHandler
{
   
   /**
    * Start scanning the deployment directory to look for changes.
    */
   public void startScanning();
   
   /**
    * Handle any initial deployments that need to be processed before
    * the engine can be started.
    */
   public void handleInitialDeployments();

   /**
    * Handle the deployment of a single BPR/WSR.
    * 
    * @param aFile
    * @param aBprName
    * @param aLogger
    * @throws AeException
    */
   public void handleDeployment(File aFile, String aBprName, IAeDeploymentLogger aLogger)  throws AeException;

   /**
    * Stop scanning and release any resources.
    */
   public void stopScanning();
   
}
