//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/handlers/AeJournalEntriesLocationIdsResultSetHandler.java,v 1.1 2005/11/16 16:48:11 EWittmann Exp $
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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;


/**
 * Implements a {@link org.apache.commons.dbutils.ResultSetHandler} that
 * converts a {@link java.sql.ResultSet} to a map from journal entry ids to
 * location ids.
 */
public class AeJournalEntriesLocationIdsResultSetHandler implements ResultSetHandler<Map<Long, Integer>> {
    /**
     * Default constructor that is visible to classes derived from
     * <code>AeSQLProcessStateStorage</code>.
     */
    public AeJournalEntriesLocationIdsResultSetHandler() {
    }

    /**
     * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
     */
    public Map<Long, Integer> handle(ResultSet rs) throws SQLException {
        Map<Long, Integer> result = new HashMap<>();

        while (rs.next()) {
            long journalId = rs.getLong(1);

            if (rs.wasNull()) {
                continue;
            }

            int locationId = rs.getInt(2);

            if (rs.wasNull()) {
                continue;
            }

            result.put(journalId, locationId);
        }

        return result;
    }
}