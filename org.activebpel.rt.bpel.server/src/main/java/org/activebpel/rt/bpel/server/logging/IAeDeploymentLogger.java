//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/logging/IAeDeploymentLogger.java,v 1.3 2004/12/10 15:59:24 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.logging; 

import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;

/**
 * Interface for reporting errors, warnings, and progress information during
 * deployments. An instance will be used for the deployment of a single BPR file
 * which may contain multiple .pdd files.
 */
public interface IAeDeploymentLogger extends IAeBaseErrorReporter
{
   /**
    * Sets the name of the container that we're deploying. This is typically the
    * name of the BPR file that was uploaded.
    * @param aContainerName
    */
   public void setContainerName(String aContainerName);
   
   /**
    * Sets the name of the pdd currently being deployed.  This method is called each time the engine
    * begins deploying a new deployment unit (PDD). 
    * @param aPddName
    */
   public void setPddName(String aPddName);

   /**
    * Called when the deployment is done. 
    */
   public void close();
   
   /**
    * Resets the warning and error flags. 
    */
   public void resetWarningAndErrorFlags();
   
   /**
    * Adds an info message to the log
    * @param aMessage
    */
   public void addInfo(String aMessage);
   
   /**
    * This method is called when the processing of a PDD has finished (either successfully or
    * not).
    * 
    * @param aBool true if the PDD was actually deployed, false if it was not (for whatever reason)
    */
   public void processDeploymentFinished(boolean aBool);
}
