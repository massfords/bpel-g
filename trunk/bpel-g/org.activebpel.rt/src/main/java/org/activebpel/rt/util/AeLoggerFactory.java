//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeLoggerFactory.java,v 1.3 2007/11/27 02:47:28 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory class for creating a logger
 */
public class AeLoggerFactory
{
   /**
    * Creates a logger with an initial level of warning. Adds a default console
    * handler to the logger if the logger does not already have a handler.
    *
    * @param aName logger name
    * @return logger
    */
   public static Logger createLogger(String aName)
   {
      Logger logger = Logger.getLogger(aName);
      // fixme (MF) logger shouldn't set level
      logger.setLevel(Level.WARNING);      

      synchronized (logger)
      {
         if (!hasHandler(logger))
         {
            logger.addHandler(new AeConsoleLogHandler());
         }
      }

      return logger;
   }

   /**
    * Returns <code>true</code> if and only if the given logger has at least one
    * handler.
    */
   protected static boolean hasHandler(Logger aLogger)
   {
      if (aLogger.getHandlers().length > 0)
      {
         return true;
      }

      // If the logger is sending its output to its parent, then check the
      // parent for handlers.
      if (aLogger.getUseParentHandlers())
      {
         Logger parent = aLogger.getParent();
         
         if ((parent != null) && (parent != aLogger))
         {
            return hasHandler(parent);
         }
      }

      // Couldn't find a handler.
      return false;
   }
}
