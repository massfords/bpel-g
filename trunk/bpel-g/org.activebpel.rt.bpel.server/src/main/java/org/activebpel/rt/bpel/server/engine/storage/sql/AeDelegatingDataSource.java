//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeDelegatingDataSource.java,v 1.1 2007/08/13 17:57:07 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * A delegating DataSource
 */
public class AeDelegatingDataSource implements DataSource {
    /**
     * our delegate
     */
    private DataSource mDelegate;

    /**
     * Ctor accepts delegate
     *
     * @param aDelegate
     */
    public AeDelegatingDataSource(DataSource aDelegate) {
        setDelegate(aDelegate);
    }

    /**
     * @see javax.sql.DataSource#getConnection()
     */
    public Connection getConnection() throws SQLException {
        return getDelegate().getConnection();
    }

    /**
     * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
     */
    public Connection getConnection(String aUsername, String aPassword)
            throws SQLException {
        return getDelegate().getConnection(aUsername, aPassword);
    }

    /**
     * @see javax.sql.DataSource#getLogWriter()
     */
    public PrintWriter getLogWriter() throws SQLException {
        return getDelegate().getLogWriter();
    }

    /**
     * @see javax.sql.DataSource#getLoginTimeout()
     */
    public int getLoginTimeout() throws SQLException {
        return getDelegate().getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return getDelegate().getParentLogger();
    }

    /**
     * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter(PrintWriter aOut) throws SQLException {
        getDelegate().setLogWriter(aOut);
    }

    /**
     * @see javax.sql.DataSource#setLoginTimeout(int)
     */
    public void setLoginTimeout(int aSeconds) throws SQLException {
        getDelegate().setLoginTimeout(aSeconds);
    }

    /**
     * @return the delegate
     */
    protected DataSource getDelegate() {
        return mDelegate;
    }

    /**
     * @param aDelegate the delegate to set
     */
    protected void setDelegate(DataSource aDelegate) {
        mDelegate = aDelegate;
    }

    public boolean isWrapperFor(Class<?> aIface) throws SQLException {
        return getDelegate().isWrapperFor(aIface);
    }

    public <T> T unwrap(Class<T> aIface) throws SQLException {
        return getDelegate().unwrap(aIface);
    }
}
 