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

import org.activebpel.rt.bpel.server.AeMessages;

/**
 *  Wrapper around app server logging.
 */
public interface IAeLogWrapper
{
   /**
    * Simple console logging. 
    */
   public static final IAeLogWrapper CONSOLE_LOG = new IAeLogWrapper()
   {
      /**
       * Debug messages.
       * @param aMessage
       */
      public void logDebug( String aMessage )
      {
         System.out.println( AeMessages.getString("IAeLogWrapper.0") + aMessage ); //$NON-NLS-1$
      }

      /**
       * Info messages.
       * @param aMessage
       */
      public void logInfo( String aMessage )
      {
         System.out.println( AeMessages.getString("IAeLogWrapper.1") + aMessage ); //$NON-NLS-1$
      }
   
      /**
       * Error messages.
       * @param aMessage
       * @param aProblem
       */
      public void logError( String aMessage, Throwable aProblem )
      {
         System.out.println( AeMessages.getString("IAeLogWrapper.2") + aMessage ); //$NON-NLS-1$
         if( aProblem != null )
         {
            aProblem.printStackTrace();
      	}
      }
   };
   
   /** No-op IAeLogWrapper impl. */
   public static IAeLogWrapper NULL_LOG = new IAeLogWrapper()
   {
      /**
       * @see org.activebpel.rt.bpel.server.logging.IAeLogWrapper#logDebug(java.lang.String)
       */
      public void logDebug(String aMessage)
      {
      }

      /**
       * @see org.activebpel.rt.bpel.server.logging.IAeLogWrapper#logError(java.lang.String, java.lang.Throwable)
       */
      public void logError(String aMessage, Throwable aProblem)
      {
      }

      /**
       * @see org.activebpel.rt.bpel.server.logging.IAeLogWrapper#logInfo(java.lang.String)
       */
      public void logInfo(String aMessage)
      {
      }
   };

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
