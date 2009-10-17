//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/logging/IAeDeploymentLoggerFactory.java,v 1.2 2004/12/01 21:40:10 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.logging; 

/**
 * Interface for the deployment logging factory. 
 */
public interface IAeDeploymentLoggerFactory
{
   /**
    * Returns a new logger instance that can be used to log deployment information.
    */
   public IAeDeploymentLogger createLogger();
   
   /**
    * Returns all of the log statements written by logger instances.
    */
   public String[] getDeploymentLog();
}
 
