// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/IAeProcessLogger.java,v 1.4 2005/02/08 15:36:02 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine;

import java.io.Reader;

import org.activebpel.rt.bpel.IAeProcessListener;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.server.AeMessages;


/**
 * Interface for class that logs process events for the engine. 
 */
public interface IAeProcessLogger extends IAeProcessListener
{
   /** default number of lines to read for the head of the log */
   public static final int DEFAULT_HEAD = 100;
   /** default number of lines to read for the tail of the log */
   public static final int DEFAULT_TAIL = 500;

   /** Used as the delimiter between the head and tail of the log */
   public static final String SNIP = 
      AeMessages.getString("IAeProcessLogger.0"); //$NON-NLS-1$

   /**
    * Gets an abbreviated form of the log which is suitable for display in the 
    * remote debug console or in the admin console. The String returned will contain
    * a configurable amount of the head and tail of the log. The system default is
    * approximately the top 100 lines and the bottom 500 lines, but this will vary
    * with how the logger is implemented. 
    * @param aProcessId
    */
   public String getAbbreviatedLog(long aProcessId) throws Exception;
   
   /**
    * Returns the entire log as a reader. The size of the log is unknown but it 
    * should be assumed that it is not safe to read the entire contents into
    * memory.
    * @param aProcessId
    * @throws Exception
    */
   public Reader getFullLog(long aProcessId) throws Exception;
   
   /**
    * Setter for the engine.
    * @param aEngine
    */
   public void setEngine(IAeBusinessProcessEngineInternal aEngine);
}
