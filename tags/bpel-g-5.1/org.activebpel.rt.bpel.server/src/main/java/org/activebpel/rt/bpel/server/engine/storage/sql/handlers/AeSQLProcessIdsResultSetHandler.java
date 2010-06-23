// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/handlers/AeSQLProcessIdsResultSetHandler.java,v 1.2 2007/02/02 14:32:19 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeListingResultSetHandler;

/**
 * Helper class to convert a <code>ResultSet</code> to a long[].
 */
public class AeSQLProcessIdsResultSetHandler extends AeListingResultSetHandler
{
   /**
    * Constructor.
    *
    * @param aFilter
    */
   public AeSQLProcessIdsResultSetHandler(AeProcessFilter aFilter)
   {
      super(aFilter);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeListingResultSetHandler#readRow(java.sql.ResultSet)
    */
   protected Object readRow(ResultSet aResultSet) throws SQLException
   {
      return new Long(aResultSet.getLong(1));
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeListingResultSetHandler#convertToType(java.util.List)
    */
   protected Object convertToType(List aResults)
   {
      // aResults is already sorted, because the SELECT includes ORDER BY ProcessId.
      int count = aResults.size();
      long[] result = new long[count];
      Iterator iter = aResults.iterator();
      
      for (int i = 0; (i < count) && iter.hasNext(); ++i)
      {
         result[i] = ((Number) iter.next()).longValue();
      }

      return result;
   }
}
