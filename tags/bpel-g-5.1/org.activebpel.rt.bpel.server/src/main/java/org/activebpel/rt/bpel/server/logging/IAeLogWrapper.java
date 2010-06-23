// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/logging/IAeLogWrapper.java,v 1.5 2005/02/08 15:36:03 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.logging;


/**
 *  Wrapper around app server logging.
 */
public interface IAeLogWrapper
{
   /**
    * Debug messages.
    * @param aMessage
    */
   public void logDebug( String aMessage );

   /**
    * Info messages.
    * @param aMessage
    */
   public void logInfo( String aMessage );
   
   /**
    * Error messages.
    * @param aMessage
    * @param aProblem
    */
   public void logError( String aMessage, Throwable aProblem );
}
