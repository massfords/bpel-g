//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeSQLDatabaseType.java,v 1.4 2008/02/17 21:38:46 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;

/**
 * Implements a database type lookup from the database.  The database has been configured with
 * a value for DatabaseType in the meta info table.  This row will contain the type of the 
 * database, for example:  mysql, db2, sqlserver, or oracle.  The application checks this 
 * value against the value found in the engine configuration as an extra configuration check.
 */
public class AeSQLDatabaseType extends AeSQLObject
{
   /** Singleton instance. */
   private static final AeSQLDatabaseType mInstance = new AeSQLDatabaseType(); 

   /**
    * Constructor.
    */
   protected AeSQLDatabaseType()
   {
   }

   /**
    * Returns singleton instance.
    */
   public static AeSQLDatabaseType getInstance()
   {
      return mInstance;
   }

   /**
    * Returns the type of database.
    * 
    * @return The type of the database.
    * @throws AeStorageException
    */
   public String getDatabaseType() throws AeStorageException
   {
      try
      {
         Connection connection = getConnection();

         try
         {
            return getDatabaseType(connection);
         }
         finally
         {
            AeCloser.close(connection);
         }
      }
      catch (SQLException e)
      {
         throw new AeStorageException(AeMessages.getString("AeSQLDatabaseType.ERROR_0"), e); //$NON-NLS-1$
      }
   }

   /**
    * Returns the database type as configured in the meta info table.
    * 
    * @param aConnection The database connection to use.
    * @return The configured database type.
    * @throws AeStorageException
    * @throws SQLException
    */
   private String getDatabaseType(Connection aConnection) throws AeStorageException, SQLException
   {
      String sql = getDataSource().getSQLConfig().getSQLStatement("MetaInfo.GetDatabaseType"); //$NON-NLS-1$
      if (AeUtil.isNullOrEmpty(sql))
      {
         throw new AeStorageException(AeMessages.getString("AeSQLDatabaseType.ERROR_2")); //$NON-NLS-1$
      }

      String value = (String) getQueryRunner().query(aConnection, sql, AeResultSetHandlers.getStringHandler());
      if (value == null)
      {
         throw new AeStorageException(AeMessages.getString("AeSQLDatabaseType.ERROR_3")); //$NON-NLS-1$
      }

      return value;
   }

}
