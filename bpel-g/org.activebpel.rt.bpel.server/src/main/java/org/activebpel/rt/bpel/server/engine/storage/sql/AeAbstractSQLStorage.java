// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeAbstractSQLStorage.java,v 1.9 2005/03/17 21:11:57 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;
import org.apache.commons.dbutils.ResultSetHandler;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;

/**
 * Base class for SQL storage objects that extract sql statement from
 * <code>AeSQLConfig</code> object.
 */
abstract public class AeAbstractSQLStorage extends AeSQLObject {
    /**
     * SQL statement repository.
     */
    @Inject
    protected AeSQLConfig mConfig;
    /**
     * SQL key prefix.
     */
    protected String mPrefix;

    /**
     * Returns a SQL statement from the SQL configuration object. This
     * convenience method prepends the name of the statement with
     * "ProcessStorage.".
     *
     * @param aStatementName The name of the statement, such as "InsertProcess".
     * @return The SQL statement found for that name.
     * @throws AeStorageException If the SQL statement is not found.
     */
    protected String getSQLStatement(String aStatementName) throws AeStorageException {
        String key = getPrefix() + aStatementName;
        String sql = getSQLConfig().getSQLStatement(key);

        if (AeUtil.isNullOrEmpty(sql)) {
            throw new AeStorageException(MessageFormat.format(AeMessages.getString("AeAbstractSQLStorage.ERROR_0"), new Object[]{key})); //$NON-NLS-1$
        }
        return sql;
    }

    /**
     * Executes the update sql bound to the name provided and using the params
     * passed in.
     *
     * @param aQueryName
     * @param aParams
     * @throws AeStorageException
     */
    protected int update(String aQueryName, Object... aParams) throws AeStorageException {
        Connection conn = null;
        try {
            conn = getConnection();
            return update(conn, aQueryName, aParams);
        } finally {
            AeCloser.close(conn);
        }
    }

    /**
     * Executes the update with the provided connection. This method will NOT close
     * the connection.
     *
     * @param aConnection It is the caller's responsibility to close the connection.
     * @param aQueryName
     * @param aParams
     * @throws AeStorageException
     */
    protected int update(Connection aConnection, String aQueryName, Object... aParams) throws AeStorageException {
        try {
            String stmt = getSQLStatement(aQueryName);
            return getQueryRunner().update(aConnection, stmt, aParams);
        } catch (SQLException sql) {
            throw new AeStorageException(AeMessages.getString("AeAbstractSQLStorage.ERROR_2") + aQueryName, sql); //$NON-NLS-1$
        }
    }

    /**
     * Executes the query with a connection that gets closed immediately after execution.
     *
     * @param aQueryName
     * @param aParams
     * @param aHandler
     * @throws AeStorageException
     */
    protected <T> T query(String aQueryName, ResultSetHandler<T> aHandler, Object... aParams) throws AeStorageException {
        Connection conn = null;
        try {
            conn = getConnection();
            return query(conn, aQueryName, aHandler, aParams);
        } finally {
            AeCloser.close(conn);
        }
    }

    /**
     * Executes the query with the provided connection, leaving it up to the caller
     * to determine when to close the connection.
     *
     * @param aConn
     * @param aQueryName
     * @param aParams
     * @param aHandler
     * @throws AeStorageException
     */
    protected <T> T query(Connection aConn, String aQueryName, ResultSetHandler<T> aHandler, Object... aParams) throws AeStorageException {
        try {
            String stmt = getSQLStatement(aQueryName);
            return getQueryRunner().query(aConn, stmt, aHandler, aParams);
        } catch (SQLException sql) {
            throw new AeStorageException(AeMessages.getString("AeAbstractSQLStorage.ERROR_2") + aQueryName, sql); //$NON-NLS-1$
        }
    }

    /**
     * Return sql key prefix.
     */
    public String getPrefix() {
        return mPrefix;
    }

    /**
     * @param aPrefix
     */
    public void setPrefix(String aPrefix) {
        mPrefix = aPrefix;
    }

    /**
     * Accessor for sql config.
     */
    public AeSQLConfig getSQLConfig() {
        return mConfig;
    }

    /**
     * @param aConfig
     */
    public void setSQLConfig(AeSQLConfig aConfig) {
        mConfig = aConfig;
    }

    /**
     * @return Returns the config.
     */
    protected AeSQLConfig getConfig() {
        return mConfig;
    }

    /**
     * @param aConfig The config to set.
     */
    protected void setConfig(AeSQLConfig aConfig) {
        mConfig = aConfig;
    }
}
