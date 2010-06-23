//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel.web/src/org/activebpel/rt/axis/bpel/web/AeWebEngineAdministration.java,v 1.1 2005/02/04 21:46:45 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.web;

import java.io.File;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.engine.AeEngineAdministration;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.logging.AeTeeDeploymentLogger;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;

/**
 * This is an implementation of the ActiveBPEL engine administration interface.  It extends
 * the base implementation in order to override the <code>deployNewBpr</code> method.  This
 * class provides an implementation of that method in order to allow web service deployments
 * to work in ActiveBPEL.
 */
public class AeWebEngineAdministration extends AeEngineAdministration
{
   /**
    * Overrides the base engine admin impl in order to provide an implementation of the 
    * <code>deployNewBpr</code> method.
    * 
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#deployNewBpr(java.io.File, java.lang.String, org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger)
    */
   public void deployNewBpr(File aBprFile, String aBprFilename, IAeDeploymentLogger aLogger) throws AeException
   {
      // Combine the passed-in logger with the engine factory logger using the Tee logger.
      IAeDeploymentLogger logger = AeEngineFactory.getDeploymentLoggerFactory().createLogger();
      IAeDeploymentLogger teeLogger = new AeTeeDeploymentLogger(logger, aLogger);

      AeProcessEngineServlet.getDeploymentHandler().handleDeployment(aBprFile, aBprFilename, teeLogger);
   }

}
