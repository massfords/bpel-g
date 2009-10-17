//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeListingResultSetHandler.java,v 1.4 2008/03/21 15:12:29 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql; 

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.activebpel.rt.bpel.impl.list.IAeListingFilter;
import org.activebpel.rt.bpel.server.AeMessages;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 * Provides a template method for reading the results from a filter type listing
 * query.
 * <br/>
 * Note: This class is not intended to be thread safe. 
 */
abstract public class AeListingResultSetHandler implements ResultSetHandler
{
   /** The row number where to start fetching results. */
   private int mListStart;

   /** The maximum number of results to return. */
   private int mMaxReturn;

   /** The <code>ResultSet</code> to process. */
   private ResultSet mResultSet;

   /** Whether the <code>ResultSet</code> has more rows. */
   private boolean mHasNext = true;

   /** The number of rows in the <code>ResultSet</code>. */
   private int mRowCount = 0;

   /** Max number of rows to scan to determine the upper limit */
   private static final int REPORT_SCAN_LIMIT = 500;

   /** indicates whether the results returned were truncated or not.  */
   private boolean mTruncated;
   
   /**
    * Constructor.
    *
    * @param aFilter
    */
   public AeListingResultSetHandler(IAeListingFilter aFilter)
   {
      setFilter(aFilter);
   }

   /**
    * Returns the <code>ResultSet</code>.
    */
   protected ResultSet getResultSet()
   {
      return mResultSet;
   }

   /**
    * Returns the number of rows.
    */
   protected int getRowCount()
   {
      return mRowCount;
   }

   /**
    * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
    */
   public synchronized Object handle(ResultSet aResultSet) throws SQLException
   {
      // Note: This class is not intended to be thread safe. We need to
      // to do some checks in case some client code uses a shared instance.
      
      if (getResultSet() != null)
      {
         // as per KR - double check - previous resultSet usage should have completed.
         throw new IllegalStateException(AeMessages.getString("AeListingResultSetHandler.ILLEGAL_STATE")); //$NON-NLS-1$);
      }
      
      try
      {
         List results = new ArrayList();      
         // Set ResultSet for next().
         setResultSet(aResultSet);
         setRowCount(0);
         skipRows();
         readRows(aResultSet, results);
         determineRowCount();         
         return convertToType(results);
      }
      finally
      {
         // always reset the result set.
         setResultSet(null);
      }
   }

   /**
    * Attempts to determine the total number of rows in the ResultSet, reading
    * up to a maximum of the <code>REPORT_SCAN_LIMIT</code>. In the event that
    * there are more rows then the limit then we'll indicate that the search
    * for the end was aborted and use KR's PlusAPI in the web console.
    */
   protected void determineRowCount() throws SQLException
   {
      setTruncated(false);
      for (int n = REPORT_SCAN_LIMIT; next() && !isTruncated(); )
      {
         --n;
         setTruncated((n == 0));
      }

      // If we didn't find the end of the rows, then round down the
      // approximate row count to a nice round number.
      int rows = getRowCount();
      if (isTruncated())
      {
         setRowCount((rows / REPORT_SCAN_LIMIT) * REPORT_SCAN_LIMIT);
      }
   }

   /**
    * Walks the result set reading each row and adding the object to its list.
    * This method will keep reading until the max number of rows has been read
    * or until the result set is exhausted.
    * @param aResultSet
    * @param results
    * @throws SQLException
    */
   protected void readRows(ResultSet aResultSet, List results) throws SQLException
   {
      // Iterate through rows until we have nMaxReturn rows.
      while ((results.size() < mMaxReturn) && next())
      {
         // Add result if one was returned, if null handler impl may have filtered it out
         // In which case, we must decrement the row count so it will match the result set
         Object result = readRow(aResultSet);
         if (result != null)
            results.add(result);
         else
            setRowCount(getRowCount() - 1);
      }
   }

   /**
    * Skips the required number of rows based on the filter's starting point.
    * @throws SQLException
    */
   protected void skipRows() throws SQLException
   {
      // Skip the first mListStart rows.
      while ((getRowCount() < mListStart) && next())
      {
      }
   }

   /**
    * Reads the type from the result set.
    * @param aResultSet
    * @throws SQLException
    */
   abstract protected Object readRow(ResultSet aResultSet) throws SQLException;

   /**
    * Converts the list of results to the specific type needed.
    * @param aResults
    */
   abstract protected Object convertToType(List aResults);

   /**
    * Wraps <code>mResultSet.next()</code> to count the total number of rows
    * and to avoid calling it more times than necessary.
    */
   protected boolean next() throws SQLException
   {
      if (mHasNext)
      {
         if (getResultSet().next())
         {
            setRowCount(getRowCount() + 1);
         }
         else
         {
            mHasNext = false;
         }
      }

      return mHasNext;
   }

   /**
    * Sets the filter to use.
    */
   protected void setFilter(IAeListingFilter aFilter)
   {
      if (aFilter == null)
      {
         mListStart = 0;
         mMaxReturn = Integer.MAX_VALUE;
      }
      else
      {
         mListStart = aFilter.getListStart();
         mMaxReturn = aFilter.getMaxReturn();

         // aFilter.getMaxReturn() is 0 for no limit.
         if (mMaxReturn == 0)
         {
            mMaxReturn = Integer.MAX_VALUE;
         }
      }
   }

   /**
    * Sets the <code>ResultSet</code> to handle.
    */
   protected void setResultSet(ResultSet aResultSet)
   {
      mResultSet = aResultSet;
      mHasNext = (aResultSet != null);      
   }

   /**
    * Sets the number of rows.
    */
   protected void setRowCount(int aRowCount)
   {
      mRowCount = aRowCount;
   }

   /**
    * @param aborted The aborted to set.
    */
   protected void setTruncated(boolean aborted)
   {
      mTruncated = aborted;
   }

   /**
    * @return Returns the aborted.
    */
   protected boolean isTruncated()
   {
      return mTruncated;
   }

}
 
