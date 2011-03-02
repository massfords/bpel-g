// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/AeAbstractXMLDBStorageProvider.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.xmldb;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageProvider;

/**
 * Base class for all XMLDB storage providers.
 */
public abstract class AeAbstractXMLDBStorageProvider extends AeAbstractXMLDBStorage implements IAeStorageProvider
{
   /** Debug flag. */
   private static final boolean DEBUG = false;

   /**
    * Constructs a XMLDB Storage Delegate.
    *
    * @param aConfig
    * @param aPrefix
    * @param aStorageImpl
    */
   public AeAbstractXMLDBStorageProvider(AeXMLDBConfig aConfig, String aPrefix, IAeXMLDBStorageImpl aStorageImpl)
   {
      super(aConfig, aPrefix, aStorageImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageProvider#getDBConnection()
    */
   public IAeStorageConnection getDBConnection() throws AeStorageException
   {
      return new AeXMLDBStorageConnection(getNewConnection());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageProvider#getCommitControlDBConnection()
    */
   public IAeStorageConnection getCommitControlDBConnection() throws AeStorageException
   {
      // Note: IAeStorageConnection impl should not be wrapped around a TransactionManager connection.
      // (Always use a new connection).
      IAeXMLDBConnection connection = getNewConnection(false);
      if (DEBUG)
      {
         return new AeDebugXMLDBStorageConnection(connection);
      }
      else
      {
         return new AeXMLDBStorageConnection(connection);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageProvider#getTxCommitControlDBConnection()
    */
   public IAeStorageConnection getTxCommitControlDBConnection() throws AeStorageException
   {
      IAeXMLDBConnection connection = getTransactionManagerConnection(true);
      if (DEBUG)
      {
         return new AeDebugXMLDBStorageConnection(connection);
      }
      else
      {
         return new AeXMLDBStorageConnection(connection);
      }
   }

   /**
    * Convenience method to return the XMLDB connection from the abstract IAeDBConnection.
    *
    * @param aConnection
    */
   protected IAeXMLDBConnection getXMLDBConnection(IAeStorageConnection aConnection)
   {
      return ((AeXMLDBStorageConnection) aConnection).getConnection();
   }

   /**
    * Convenience method to return a Transaction manager XMLDB connection.
    * If the current thread is not participating in an active transaction, then
    * a normal (with autocommit) connection is returned.
    *
    * @throws AeStorageException
    */
   protected IAeXMLDBConnection getXMLDBTransactionManagerConnection() throws AeStorageException
   {
      return getTransactionManagerConnection(false);
   }
}
