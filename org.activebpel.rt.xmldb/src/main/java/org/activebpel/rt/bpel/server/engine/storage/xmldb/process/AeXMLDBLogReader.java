//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/process/AeXMLDBLogReader.java,v 1.1 2007/08/17 00:40:56 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.process;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.IAeProcessLogger;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeAbstractXMLDBStorage;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBConfig;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBListResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.process.handlers.AeSmallLogHandler;
import org.w3c.dom.Element;

/**
 * Responsible for reading the log for a given process out of the XMLDB database. The
 * log gets displayed in the admin console web page as well as the remote debugging
 * console window so we don't want to return the entire log file if it's huge. 
 * Instead, we'll return a configurable head and tail portion of the log.
 */
public class AeXMLDBLogReader extends AeAbstractXMLDBStorage
{
   /** shared instance of the resultset handler used to read small logs */
   private static final IAeXMLDBResponseHandler SMALL_LOG_HANDLER = new AeSmallLogHandler();

   /** process id we're searching for */
   private Long mProcessId;
   /** list of log entries for the process */
   private List mLogEntries;
   /** total number of lines for entire process log */
   private int mTotalLineCount;
   /** max number of lines to include in the head of the log */
   private int mHeadLimit;
   /** max number of lines to include in the tail of the log */
   private int mTailLimit;

   /**
    * Constructs a XMLDB log reader.
    * 
    * @param aProcessId
    * @param aConfig
    */
   public AeXMLDBLogReader(long aProcessId, AeXMLDBConfig aConfig, IAeXMLDBStorageImpl aStorageImpl)
   {
      super(aConfig, AeXMLDBProcessStateStorageProvider.CONFIG_PREFIX, aStorageImpl);

      setProcessId(new Long(aProcessId));
      setHeadLimit(AeEngineFactory.getEngineConfig().getIntegerEntry("Logging.Head", IAeProcessLogger.DEFAULT_HEAD)); //$NON-NLS-1$
      setTailLimit(AeEngineFactory.getEngineConfig().getIntegerEntry("Logging.Tail", IAeProcessLogger.DEFAULT_TAIL)); //$NON-NLS-1$
   }

   /**
    * Reads the log for the process.
    */
   public String readLog() throws AeStorageException
   {
      // 1: get the sorted list of log entries
      Object [] params = { getProcessId() };
      List entries = (List) query(IAeProcessConfigKeys.GET_LOG_ENTRIES, params, new AeLogEntryHandler());
      setLogEntries(entries);

      String result = null;

      // 2: see if we can execute a single query to get all of the clob values
      if (getTotalLineCount() <= (mHeadLimit + mTailLimit))
      {
         result = (String) query(IAeProcessConfigKeys.GET_SMALL_LOG, params, SMALL_LOG_HANDLER);
      }
      else
      {
         // 3: get the head
         long headLogID = findHeadLogID();
         result = getLogSegment(headLogID, IAeProcessConfigKeys.GET_HEAD);

         // 4: get the tail
         long tailLogID = findTailLogID(headLogID);
         result = result + IAeProcessLogger.SNIP + getLogSegment(tailLogID, IAeProcessConfigKeys.GET_TAIL);
      }
      return result;
   }

   /**
    * Returns the segment of the log indicated by the log id value and the
    * sql statement.
    * 
    * @param aLogID value that identifies the row in the process log table to read
    * @param aStatement
    * @throws AeStorageException
    */
   private String getLogSegment(long aLogID, String aStatement) throws AeStorageException
   {
      Object[] params = { getProcessId(), new Long(aLogID) };
      return (String) query(aStatement, params, SMALL_LOG_HANDLER);
   }

   /**
    * Finds the value of the log id from the sorted list that will give us the 
    * number of entries we need for the head of the log.
    */
   protected long findHeadLogID()
   {
      return findLogID(mHeadLimit, -1);
   }
   
   /**
    * Finds the value of the log id from the sorted list that will give us the 
    * number of entries we need for the tail of the log.
    */
   protected long findTailLogID(long aHeadLogID)
   {
      // reverse the list to start of the bottom
      Collections.reverse(mLogEntries);
      long logID = findLogID(mHeadLimit, aHeadLogID);
      // put the list back in its proper order
      Collections.reverse(mLogEntries);
      return logID;
   }

   /**
    * Walks the list from the top, returning the value of the log id when it 
    * gets the specified number of lines or greater.
    * @param aLimit - max number of lines we want
    * @param aStopValue - we'll stop searching when we come across the log id
    *                     with this value.
    */
   protected long findLogID(int aLimit, long aStopValue)
   {
      int lines = 0;
      long logID = 0;

      for (Iterator iter = mLogEntries.iterator(); iter.hasNext() && lines < aLimit;)
      {
         AeLogEntry entry = (AeLogEntry) iter.next();
         logID = entry.getLogID();
         if (logID == aStopValue)
            break;
         lines += entry.getLines();
      }
      return logID;
   }
   
   /**
    * Converts the ResultSet into a <code>java.util.List</code> of AeLogEntry objects.
    */
   protected class AeLogEntryHandler extends AeXMLDBListResponseHandler
   {
      /**
       * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBListResponseHandler#handleElement(org.w3c.dom.Element)
       */
      protected Object handleElement(Element aElement)
      {
         int logID = getIntFromElement(aElement, IAeProcessElements.LOG_ID).intValue();
         int lines = getIntFromElement(aElement, IAeProcessElements.LINE_COUNT).intValue();

         setTotalLineCount(getTotalLineCount() + lines);

         return new AeLogEntry(logID, lines);
      }
   }

   /**
    * Container class for a clob row. Includes the number of lines in the clob
    * and the value of the log id field for the row. 
    */
   protected static class AeLogEntry implements Comparable
   {
      /** number of lines in the clob */
      private int mLines;
      /** value of the log id for the row */
      private long mLogID;

      /**
       * Ctor
       * @param aLines
       * @param aLogID
       */
      public AeLogEntry(long aLogID, int aLines)
      {
         mLines = aLines;
         mLogID = aLogID;
      }

      /**
       * Getter for the log id
       */
      public long getLogID()
      {
         return mLogID;
      }

      /**
       * Geter for the number of lines
       */
      public int getLines()
      {
         return mLines;
      }

      /**
       * @see java.lang.Comparable#compareTo(java.lang.Object)
       */
      public int compareTo(Object o)
      {
         AeLogEntry other = (AeLogEntry) o;
         return (int) (getLogID() - other.getLogID());
      }

   }

   /**
    * Getter for the total number of lines in process log
    */
   public int getTotalLineCount()
   {
      return mTotalLineCount;
   }

   /**
    * Setter for the total number of lines in process log
    * @param aI
    */
   public void setTotalLineCount(int aI)
   {
      mTotalLineCount = aI;
   }

   /**
    * @return Returns the headLimit.
    */
   protected int getHeadLimit()
   {
      return mHeadLimit;
   }
   
   /**
    * @param aHeadLimit The headLimit to set.
    */
   protected void setHeadLimit(int aHeadLimit)
   {
      mHeadLimit = aHeadLimit;
   }
   
   /**
    * @return Returns the logEntries.
    */
   protected List getLogEntries()
   {
      return mLogEntries;
   }
   
   /**
    * @param aLogEntries The logEntries to set.
    */
   protected void setLogEntries(List aLogEntries)
   {
      mLogEntries = aLogEntries;
   }
   
   /**
    * @return Returns the processId.
    */
   protected Long getProcessId()
   {
      return mProcessId;
   }
   
   /**
    * @param aProcessId The processId to set.
    */
   protected void setProcessId(Long aProcessId)
   {
      mProcessId = aProcessId;
   }
   
   /**
    * @return Returns the tailLimit.
    */
   protected int getTailLimit()
   {
      return mTailLimit;
   }
   
   /**
    * @param aTailLimit The tailLimit to set.
    */
   protected void setTailLimit(int aTailLimit)
   {
      mTailLimit = aTailLimit;
   }
}
