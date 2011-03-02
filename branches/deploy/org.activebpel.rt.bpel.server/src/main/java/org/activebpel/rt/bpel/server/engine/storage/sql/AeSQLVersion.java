// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeSQLVersion.java,v 1.6 2008/02/17 21:38:47 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;

/**
 * Implements database version lookup in the database.
 */
public class AeSQLVersion extends AeSQLObject
{
   /** Singleton instance. */
   private static final AeSQLVersion mInstance = new AeSQLVersion(); 

   /**
    * Constructor.
    */
   protected AeSQLVersion()
   {
   }

   /**
    * Returns singleton instance.
    */
   public static AeSQLVersion getInstance()
   {
      return mInstance;
   }

   /**
    * Returns the version of the databse structure.
    * @return The version of the databsse structure/
    * @throws AeStorageException
    */
   public String getVersion() throws AeStorageException
   {
      try
      {
         Connection connection = getConnection();

         try
         {
            return getVersion(connection);
         }
         finally
         {
            AeCloser.close(connection);
         }
      }
      catch (SQLException e)
      {
         throw new AeStorageException(AeMessages.getString("AeSQLVersion.ERROR_0"), e); //$NON-NLS-1$
      }
   }

   /**
    * Returns current version of the database structure as set in the AeMetaInfo table.
    * @param aConnection The database connection to use.
    * @return String
    * @throws AeStorageException
    * @throws SQLException
    */
   private String getVersion(Connection aConnection) throws AeStorageException, SQLException
   {
      String sql = getDataSource().getSQLConfig().getSQLStatement("MetaInfo.GetVersion"); //$NON-NLS-1$
      if (AeUtil.isNullOrEmpty(sql))
      {
         throw new AeStorageException(AeMessages.getString("AeSQLVersion.ERROR_2")); //$NON-NLS-1$
      }

      String value = (String) getQueryRunner().query(aConnection, sql, AeResultSetHandlers.getStringHandler());
      if (value == null)
      {
         throw new AeStorageException(AeMessages.getString("AeSQLVersion.ERROR_0")); //$NON-NLS-1$
      }

      return value;
   }
}
