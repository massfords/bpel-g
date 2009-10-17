// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeAbstractDeploymentHandler.java,v 1.7 2005/02/01 19:56:30 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;
import org.activebpel.rt.bpel.server.logging.IAeLogWrapper;

/**
 *  Base class for AeDeploymentHandler impls.
 */
abstract public class AeAbstractDeploymentHandler implements IAeDeploymentHandler
{
   /** Platform specific logging. */
   private IAeLogWrapper mLogger = IAeLogWrapper.NULL_LOG;
   /** logger that we're currently writing to */
   protected IAeDeploymentLogger mDeploymentLogger;
   /** name of the container currently being deployed */
   private String mContainerName;
   
   /**
    * Setter for the container name being deployed 
    * @param aContainerName
    */
   protected void setContainerName(String aContainerName)
   {
      mContainerName = aContainerName;
   }
   
   /**
    * Constructor.
    * @param aLogger IAeLogWrapper impl. May be null.
    */
   protected AeAbstractDeploymentHandler( IAeLogWrapper aLogger )
   {
      if( aLogger != null )
      {
         mLogger = aLogger;
      }
   }
   
   /**
    * Log debug messages.
    * @param aMessge
    */
   protected void logDebug( String aMessge )
   {
      mLogger.logDebug( formatMessage( aMessge ) );
   }

   /**
    * Log info messages.
    * @param aMessage
    */
   protected void logInfo( String aMessage )
   {
      mLogger.logInfo( formatMessage( aMessage ) );
   }

   /**
    * Log error messages.
    * @param aMessage
    */
   protected void logError( String aMessage )
   {
      logError(aMessage, null);
   }
   
   /**
    * Logs the error w/ the stacktrace.
    * @param aMessage
    * @param aError
    */
   protected void logError(String aMessage, Throwable aError)
   {
      String message = formatMessage(aMessage);
      mLogger.logError(message, aError);
      AeException.logError(aError, aMessage);
   }
   
   /**
    * Utility method for log output.
    * @param aMessage
    */
   protected String formatMessage( String aMessage )
   {
      StringBuffer sb = new StringBuffer();
      sb.append( '[' );
      sb.append( mContainerName );
      sb.append( "] "); //$NON-NLS-1$
      sb.append( aMessage );
      return sb.toString();      
   }
   
   /**
    * @return Returns the deploymentLogger.
    */
   protected IAeDeploymentLogger getDeploymentLogger()
   {
      return mDeploymentLogger;
   }
   
   /**
    * @param aDeploymentLogger The deploymentLogger to set.
    */
   protected void setDeploymentLogger(IAeDeploymentLogger aDeploymentLogger)
   {
      mDeploymentLogger = aDeploymentLogger;
   }
}
