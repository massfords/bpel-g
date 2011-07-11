//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeConsoleLogHandler.java,v 1.1 2005/11/15 17:09:38 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Logging handler that will write messages to the system console. If the
 * message is at Warning level, it will be surrounded by asterisks.
 */
public class AeConsoleLogHandler extends Handler
{

   public AeConsoleLogHandler()
   {
      super();
   }
   
   /**
    * Write out the message to the system console.
    * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
    */
   public void publish(LogRecord aLogRecord)
   {
    if (aLogRecord.getLevel() == Level.WARNING)
       System.out.println("*******************************"); //$NON-NLS-1$
    
    System.out.println(aLogRecord.getMessage());
    
    if (aLogRecord.getLevel() == Level.WARNING)
       System.out.println("*******************************"); //$NON-NLS-1$    
   }
   
   /**
    * Nothing to do
    * @see java.util.logging.Handler#close()
    */
   public void close()
   {
   }
   
   /**
    * Nothing to do
    * @see java.util.logging.Handler#flush()
    */
   public void flush()
   {
   }
   
}
