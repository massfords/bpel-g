//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/handlers/AeCoordinationDetailListResultSetHandler.java,v 1.3 2006/02/13 22:31:00 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.activebpel.rt.bpel.coord.AeCoordinationDetail;
import org.activebpel.rt.bpel.server.engine.storage.sql.IAeCoordinationColumns;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 * Implements a <code>ResultSetHandler</code> that converts the next row of
 * a <code>ResultSet</code> to an <code>AeCoordinationDetail</code>.
 * <br/>
 */

public class AeCoordinationDetailListResultSetHandler implements ResultSetHandler<List<AeCoordinationDetail>> {

    /**
     * Default ctor
     */
    public AeCoordinationDetailListResultSetHandler() {
    }

    /**
     * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
     */
    public List<AeCoordinationDetail> handle(ResultSet aResultSet) throws SQLException {
        List<AeCoordinationDetail> results = new ArrayList<>();
        // Iterate through rows
        while (aResultSet.next()) {
            results.add(readRow(aResultSet));
        }
        return results;
    }


    /**
     * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeListingResultSetHandler#readRow(java.sql.ResultSet)
     */
    protected AeCoordinationDetail readRow(ResultSet aResultSet) throws SQLException {
        String coordId = aResultSet.getString(IAeCoordinationColumns.COORDINATION_ID);
        String state = aResultSet.getString(IAeCoordinationColumns.STATE);
        long processId = aResultSet.getLong(IAeCoordinationColumns.PROCESS_ID);
        String locationPath = aResultSet.getString(IAeCoordinationColumns.LOCATION_PATH);
        return new AeCoordinationDetail(processId, coordId, state, locationPath);
    }
}
