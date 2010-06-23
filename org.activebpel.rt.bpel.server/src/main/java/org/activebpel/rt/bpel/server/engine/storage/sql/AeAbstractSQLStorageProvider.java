// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeAbstractSQLStorageProvider.java,v 1.7 2007/05/11 17:55:32 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.sql.Connection;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageProvider;

/**
 * An abstract storage provider implementation for SQL.
 */
public abstract class AeAbstractSQLStorageProvider extends AeAbstractSQLStorage implements IAeStorageProvider
{
   /**
    * Constructs a SQL storage delegate with the given storage prefix and config.
    * 
    * @param aPrefix
    * @param aConfig
    */
   public AeAbstractSQLStorageProvider(String aPrefix, AeSQLConfig aConfig)
   {
      super(aPrefix, aConfig);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageProvider#getDBConnection()
    */
   public IAeStorageConnection getDBConnection() throws AeStorageException
   {
      return new AeSQLStorageConnection(getNewConnection());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageProvider#getCommitControlDBConnection()
    */
   public IAeStorageConnection getCommitControlDBConnection() throws AeStorageException
   {
      // Note: AeSQLStorageConnection is NOT wrapped around a TransactionManager connection
      // (always returns a new commit control connection
      return new AeSQLStorageConnection( getCommitControlConnection() );
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageProvider#getTxCommitControlDBConnection()
    */
   public IAeStorageConnection getTxCommitControlDBConnection() throws AeStorageException
   {
      // Fall back to a commit control connection if no active TX.
      return new AeSQLStorageConnection(getTransactionConnection(true, false));
   }
   
   /**
    * Convenience method to get the SQL connection from the IAeDBConnection.
    * 
    * @param aConnection
    */
   protected Connection getSQLConnection(IAeStorageConnection aConnection)
   {
      return ((AeSQLStorageConnection) aConnection).getConnection();
   }
}
