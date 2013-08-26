// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeSQLConfig.java,v 1.20 2006/02/10 21:51:13 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageConfig;
import org.activebpel.rt.util.AeUtil;

import javax.inject.Singleton;
import java.util.LinkedList;
import java.util.List;

/**
 * This class encapsulates the SQL statements used by the Active BPEL
 * persistence layer. This class uses a DOM parser to first parse a "common" xml
 * file that contains the SQL statements. It then parses a second xml file that
 * is specific to the database being used. The values in the specific xml file
 * will override the values in the common file.
 */
@Singleton
public class AeSQLConfig extends AeStorageConfig {
    private static final String SQL_ELEM_NAME = "sql"; //$NON-NLS-1$
    private static final String SQL_STATEMENT_ELEM_NAME = "sql-statement"; //$NON-NLS-1$

    /**
     * The specific type of database being used. For example, "mysql".
     */
    protected String mType;

    public AeSQLConfig() {
        setStatementRootName(SQL_STATEMENT_ELEM_NAME);
        setStatementValueName(SQL_ELEM_NAME);
    }

    /**
     * Returns the database type.
     */
    public String getDatabaseType() {
        return mType;
    }

    public void setDatabaseType(String aType) {
        mType = aType;
    }

    /**
     * Gets a SQL statement given a key (the name of the statement as configured
     * in the file). This is a convenience method that simply delegates to the
     * base class's <code>getStatement</code> method.
     *
     * @param aKey A key that references a SQL statement in the config file.
     * @return A SQL statement.
     */
    public String getSQLStatement(String aKey) {
        return getStatement(aKey);
    }

    /**
     * Gets a SQL statement fragment that limits the query to the specified
     * number of rows or an empty string if the db doesn't support the
     * operation.
     *
     * @param aLimitValue
     */
    public String getLimitStatement(int aLimitValue) {
        String stmt = getSQLStatement("Generic.Limit"); //$NON-NLS-1$
        if (!AeUtil.isNullOrEmpty(stmt)) {
            return " " + stmt.replaceFirst("\\?", "" + aLimitValue); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * @see org.activebpel.rt.bpel.server.engine.storage.AeStorageConfig#getStatementConfigFilenames()
     */
    protected List<AeFilenameClassTuple> getStatementConfigFilenames() {
        String commonFileName = "common-sql.xml"; //$NON-NLS-1$
        String specificFileName = mType + "-sql.xml"; //$NON-NLS-1$

        List<AeFilenameClassTuple> list = new LinkedList<>();
        list.add(new AeFilenameClassTuple(commonFileName, AeSQLConfig.class));
        list.add(new AeFilenameClassTuple(specificFileName, AeSQLConfig.class));

        return list;
    }
}
