package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

public interface AeResultSetHandler<T> extends ResultSetHandler {

    /**
     * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
     */
    public T handle(ResultSet aResultSet) throws SQLException;
    
}
