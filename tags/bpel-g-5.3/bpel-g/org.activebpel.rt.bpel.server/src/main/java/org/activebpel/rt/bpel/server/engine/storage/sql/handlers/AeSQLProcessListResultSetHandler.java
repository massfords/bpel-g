//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/handlers/AeSQLProcessListResultSetHandler.java,v 1.1 2005/11/16 16:48:11 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.activebpel.rt.bpel.server.engine.storage.sql.AeListingResultSetHandler;

import bpelg.services.processes.types.ProcessFilterType;
import bpelg.services.processes.types.ProcessInstanceDetail;
import bpelg.services.processes.types.ProcessList;


/**
 * Helper class to convert a <code>ResultSet</code> to an
 * <code>AeProcessListResult</code>.
 */
public class AeSQLProcessListResultSetHandler extends AeListingResultSetHandler<ProcessList, ProcessInstanceDetail>
{
   /**
    * Constructor.
    *
    * @param aFilter
    */
   public AeSQLProcessListResultSetHandler(ProcessFilterType aFilter)
   {
      super(aFilter.getMaxReturn(), aFilter.getListStart());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeListingResultSetHandler#readRow(java.sql.ResultSet)
    */
   protected ProcessInstanceDetail readRow(ResultSet aResultSet) throws SQLException
   {
      return AeSQLProcessInstanceResultSetHandler.createProcessInstanceDetail(aResultSet);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeListingResultSetHandler#convertToType(java.util.List)
    */
   protected ProcessList convertToType(List<ProcessInstanceDetail> aResults)
   {
      return new ProcessList().withTotalRowCount(getRowCount()).withProcessInstanceDetail(aResults).withComplete(!isTruncated());
   }
}