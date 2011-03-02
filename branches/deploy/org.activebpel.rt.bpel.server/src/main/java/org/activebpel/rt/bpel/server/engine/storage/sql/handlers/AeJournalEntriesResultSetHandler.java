//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/handlers/AeJournalEntriesResultSetHandler.java,v 1.3 2006/02/10 21:51:13 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql.handlers;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeJournalEntryFactory;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntryFactory;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeDbUtils;
import org.apache.commons.dbutils.ResultSetHandler;
import org.w3c.dom.Document;


/**
 * Implements a {@link org.apache.commons.dbutils.ResultSetHandler} that
 * converts a {@link java.sql.ResultSet} to a list of {@link
 * org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry}
 * instances.
 */
public class AeJournalEntriesResultSetHandler implements ResultSetHandler
{
   /**
    * Default constructor that is visible to classes derived from
    * <code>AeSQLProcessStateStorage</code>.
    */
   public AeJournalEntriesResultSetHandler()
   {
      super();
   }

   /**
    * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
    */
   public Object handle(ResultSet rs) throws SQLException
   {
      IAeJournalEntryFactory factory = AeJournalEntryFactory.getInstance();
      List result = new LinkedList();

      while (rs.next())
      {
         long journalId = rs.getLong(1);
         int entryType = rs.getInt(2);
         int locationId = rs.getInt(3);
         Clob clob = rs.getClob(4);

         if (rs.wasNull())
         {
            clob = null;
         }

         Document document = (clob == null) ? null : AeDbUtils.getDocument(clob);
         IAeJournalEntry item; 

         try
         {
            item = factory.newJournalEntry(entryType, locationId, journalId, document); 
         }
         catch (AeException e)
         {
            e.logError();
            continue;
         }

         result.add(item);
      }

      return result;
   }
}
