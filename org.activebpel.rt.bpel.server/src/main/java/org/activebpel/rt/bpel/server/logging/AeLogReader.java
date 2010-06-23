// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/logging/AeLogReader.java,v 1.3 2005/02/08 15:36:03 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.logging;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.IAeProcessLogger;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLConfig;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLObject;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 * Responsible for reading the log for a given process out of the database. The
 * log gets displayed in the admin console web page as well as the remote debugging
 * console window so we don't want to return the entire log file if it's huge. 
 * Instead, we'll return a configurable head and tail portion of the log.
 */
public class AeLogReader extends AeSQLObject
{
   private static final String SQL_GET_HEAD           = "GetLogHead"; //$NON-NLS-1$
   private static final String SQL_GET_TAIL           = "GetLogTail"; //$NON-NLS-1$
   private static final String SQL_GET_LOG_ENTRIES    = "GetLogEntries"; //$NON-NLS-1$
   private static final String SQL_GET_SMALL_LOG      = "GetSmallLog"; //$NON-NLS-1$

   /** shared instance of the resultset handler used to read small logs */
   private static final ResultSetHandler SMALL_LOG_HANDLER = new AeSmallLogHandler(); 
   
   /** process id we're searching for */
   private Long mProcessId;
   
   /** contains the sql queries needed for this class */
   private AeSQLConfig mSQLConfig;
   
   /** list of log entries for the process */
   private List mLogEntries;
   
   /** total number of lines for entire process log */
   private int mTotalLineCount;
   
   /** max number of lines to include in the head of the log */
   private int mHeadLimit;
   /** max number of lines to include in the tail of the log */
   private int mTailLimit;
      
   /**
    * Ctor
    * @param aProcessId
    */
   public AeLogReader(long aProcessId, AeSQLConfig aConfig)
   {
      mProcessId = new Long(aProcessId);
      mSQLConfig = aConfig;
      
      mHeadLimit = AeEngineFactory.getEngineConfig().getIntegerEntry("Logging.Head", IAeProcessLogger.DEFAULT_HEAD); //$NON-NLS-1$
      mTailLimit = AeEngineFactory.getEngineConfig().getIntegerEntry("Logging.Tail", IAeProcessLogger.DEFAULT_TAIL); //$NON-NLS-1$
   }
   
   /**
    * Reads the log for the process.
    */
   public String readLog() throws AeStorageException, SQLException
   {
      // 1: get the sorted list of log entries
      mLogEntries = (List) getQueryRunner().query(
                  getSQLStatement(SQL_GET_LOG_ENTRIES), 
                  mProcessId, 
                  new AeLogEntryHandler());
                  
      String result = null;
                  
      // 2: see if we can execute a single query to get all of the clob values
      if (getTotalLineCount() <= (mHeadLimit + mTailLimit))
      {
         // execute single query to get everything concat'd together
         result = (String) getQueryRunner().query(
                  getSQLStatement(SQL_GET_SMALL_LOG),
                  mProcessId,
                  SMALL_LOG_HANDLER);
      }
      else
      {
         // 3: get the head
         int headCounterValue = findHeadCounterValue();
         result = getLogSegment(headCounterValue, SQL_GET_HEAD);

         // 4: get the tail
         result = result + IAeProcessLogger.SNIP + getLogSegment(findTailCounterValue(headCounterValue), SQL_GET_TAIL);
      }
      return result;
   }

   /**
    * Returns the segment of the log indicated by the counter value and the
    * sql statement.
    * @param aCounterValue value that identifies the row in the process log table to read
    * @param aSqlStatement
    * @throws SQLException
    * @throws AeStorageException
    */
   private String getLogSegment(int aCounterValue, String aSqlStatement)
      throws SQLException, AeStorageException
   {
      String result;
      Object[] args = { mProcessId, new Integer(aCounterValue) };
      result = (String) getQueryRunner().query(
               getSQLStatement(aSqlStatement),
               args,
               SMALL_LOG_HANDLER);
      return result;
   }
   
   /**
    * Finds the value of the counter from the sorted list that will give us the 
    * number of entries we need for the head of the log.
    */
   protected int findHeadCounterValue()
   {
      return findCounter(mHeadLimit, -1);
   }
   
   /**
    * Finds the value of the counter from the sorted list that will give us the 
    * number of entries we need for the tail of the log.
    */
   protected int findTailCounterValue(int aHeadCounterValue)
   {
      // reverse the list to start of the bottom
      Collections.reverse(mLogEntries);
      int counter = findCounter(mHeadLimit, aHeadCounterValue);
      // put the list back in its proper order
      Collections.reverse(mLogEntries);
      return counter;
   }

   /**
    * Walks the list from the top, returning the value of the counter when it 
    * gets the specified number of lines or greater.
    * @param aLimit - max number of lines we want
    * @param aStopValue - we'll stop searching when we come across the counter
    *                     with this value.
    */
   protected int findCounter(int aLimit, int aStopValue)
   {
      int lines = 0;
      int counter = 0;

      for (Iterator iter = mLogEntries.iterator(); iter.hasNext() && lines < aLimit;)
      {
         AeLogEntry entry = (AeLogEntry) iter.next();
         counter = entry.getCounter();
         if (counter == aStopValue)
            break;
         lines += entry.getLines();
      }
      return counter;
   }
   
   /**
    * A ResultSetHandler that's responsible for walking the entire result set
    * and returning a String that represents the concatenation of all of the clobs.
    * As its name implies, this is designed for reading a small number of clobs since
    * it reads the entire contents of the clobs into memory.
    */
   protected static class AeSmallLogHandler implements ResultSetHandler
   {
      /**
       * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
       */
      public Object handle(ResultSet rs) throws SQLException
      {
         Reader reader = null;
         StringBuffer sb = new StringBuffer();
         try
         {
            synchronized(sb)
            {
               char[] cbuf = null;
               int read;
               // walk all rows in the result set
               while(rs.next())
               {
                  Clob clob = rs.getClob(1);
                  sb.ensureCapacity((int) (sb.length() + clob.length()));
                  cbuf = sizeArray(cbuf, (int)clob.length());
                  reader = clob.getCharacterStream();
                  // read all char data into string buffer
                  while ((read = reader.read(cbuf)) != -1)
                  {
                     sb.append(cbuf, 0, read);
                  }
                  reader.close();
               }
               return sb.toString();
            }
         }
         catch(IOException e)
         {
            AeCloser.close(reader);
            throw new SQLException(e.getMessage());
         }
      }

      /**
       * Resizes the array passed in so it's at least the length of the 
       * clob that we're trying to read. 
       * @param aArray - can be null
       * @param aClobSize
       */
      private char[] sizeArray(char[] aArray, int aClobSize)
      {
         if (aArray == null || aArray.length < aClobSize)
            return new char[Math.max(aClobSize, 1024*4)];
         return aArray;
      }
   }
   
   /**
    * Converts the ResultSet into a <code>java.util.List</code> of AeLogEntry objects
    * sorted by their counter value. We're doing the sorting here to get around an
    * apparent performance issue with mysql where it was generating OutOfMemoryErrors
    * when trying to sort a small ResultSet. 
    */
   protected class AeLogEntryHandler implements ResultSetHandler
   {
      /**
       * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
       */
      public Object handle(ResultSet rs) throws SQLException
      {
         List list = new ArrayList();
         
         while(rs.next())
         {
            int counter = rs.getInt(1);
            int lines = rs.getInt(2);

            setTotalLineCount(getTotalLineCount() + lines);

            list.add(new AeLogEntry(counter, lines));
         }
         
         Collections.sort(list);
         return list;
      }
   }
   
   
   /**
    * Container class for a clob row. Includes the number of lines in the clob
    * and the value of the counter field for the row. 
    */
   protected static class AeLogEntry implements Comparable
   {
      /** number of lines in the clob */
      private int mLines;
      /** value of the counter for the row */
      private int mCounter;
      
      /**
       * Ctor
       * @param aLines
       * @param aCounter
       */
      public AeLogEntry(int aCounter, int aLines)
      {
         mLines = aLines;
         mCounter = aCounter;
      }
      
      /**
       * Getter for the counter
       */
      public int getCounter()
      {
         return mCounter;
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
         return getCounter() - other.getCounter();
      }

   }
   /**
    * Getter for the sql config
    */
   protected AeSQLConfig getSQLConfig()
   {
      return mSQLConfig;
   }

   /**
    * Returns a SQL statement from the SQL configuration object. This
    * convenience method prepends the name of the statement with
    * "LogReader.".
    *
    * @param aStatementName The name of the statement, such as "InsertProcess".
    * @return The SQL statement found for that name.
    * @throws AeStorageException If the SQL statement is not found.
    */
   protected String getSQLStatement(String aStatementName) throws AeStorageException
   {
      String key = "LogReader." + aStatementName; //$NON-NLS-1$
      String sql = getSQLConfig().getSQLStatement(key);

      if (AeUtil.isNullOrEmpty(sql))
      {
         throw new AeStorageException(MessageFormat.format(AeMessages.getString("AeLogReader.ERROR_0"), //$NON-NLS-1$
                                                           new Object[] {key}));
      }

      return sql;
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

}
