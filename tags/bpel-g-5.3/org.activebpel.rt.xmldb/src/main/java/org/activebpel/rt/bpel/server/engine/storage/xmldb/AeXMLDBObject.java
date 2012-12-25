//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/AeXMLDBObject.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb;

import java.text.MessageFormat;

import org.activebpel.rt.bpel.server.engine.storage.xmldb.tx.IAeXMLDBTransaction;
import org.activebpel.rt.bpel.server.engine.transaction.AeTransactionException;
import org.activebpel.rt.bpel.server.engine.transaction.AeTransactionManager;
import org.activebpel.rt.bpel.server.engine.transaction.IAeTransaction;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xmldb.AeMessages;

/**
 * A base object for all classes that need to look up XMLDB statements from the xmldb config object.
 * This class also provides access to the data source.
 */
public abstract class AeXMLDBObject
{
   /** The XMLDB config. */
   private AeXMLDBConfig mXMLDBConfig;
   /** The config prefix configured for this object (optional). */
   private String mConfigPrefix;
   /** A xmldb storage impl to delegate db-specific operations to. */
   private IAeXMLDBStorageImpl mStorageImpl;

   /**
    * Constructs the xmldb object with the given config and config prefix.
    *
    * @param aConfig
    * @param aPrefix
    */
   public AeXMLDBObject(AeXMLDBConfig aConfig, String aPrefix, IAeXMLDBStorageImpl aStorageImpl)
   {
      setXMLDBConfig(aConfig);
      setConfigPrefix(aPrefix);
      setStorageImpl(aStorageImpl);
   }

   /**
    * Retrieves an xquery statement from the xmldb config object at the given key.
    *
    * @param aKey
    */
   protected String getXQueryStatement(String aKey) throws AeXMLDBException
   {
      String key = resolveXQueryKey(aKey);
      setCurrentStatementName(key);
      String statement = getXMLDBConfig().getXQueryStatement(key);
      if (AeUtil.isNullOrEmpty(statement))
      {
         throw new AeXMLDBException(AeMessages.getString("AeXMLDBObject.FAILED_TO_FIND_XQUERY_STATEMENT_ERROR") + aKey); //$NON-NLS-1$
      }
      else
      {
         return statement;
      }
   }

   /**
    * Gets the XMLDB DataSource.
    */
   protected IAeXMLDBDataSource getXMLDBDataSource()
   {
      return getStorageImpl().getXMLDBDataSource();
   }

   /**
    * Gets a new XMLDB connection (auto-commit set to 'true').
    */
   protected IAeXMLDBConnection getNewConnection() throws AeXMLDBException
   {
      return getNewConnection(true);
   }

   /**
    * Gets a new XMLDB connection.
    *
    * @param aAutoCommit if false, caller must commit the transaction
    * @throws AeXMLDBException
    */
   protected IAeXMLDBConnection getNewConnection(boolean aAutoCommit) throws AeXMLDBException
   {
      return getStorageImpl().getXMLDBDataSource().getNewConnection(aAutoCommit);
   }

   /**
    * Returns a connection from the transaction manager if the current thread is already participating
    * in a transaction. Otherwise returns a either normal connection or commit control connection.
    * @param aCommitControlOnFallback if true, returns a commit control connection if the thread is not part of a active transaction.
    * @throws AeXMLDBException
    */
   protected IAeXMLDBConnection getTransactionManagerConnection(boolean aCommitControlOnFallback) throws AeXMLDBException
   {
      IAeTransaction transaction = getTransaction();
      // If the current thread has started a transaction in the transaction
      // manager, then return the transaction's database connection.
      if (transaction.isActive())
      {
         if (!(transaction instanceof IAeXMLDBTransaction))
         {
            throw new AeXMLDBException(AeMessages.getString("AeXMLDBObject.ERROR_GETTING_TRANSACTION")); //$NON-NLS-1$
         }
         return ((IAeXMLDBTransaction) transaction).getConnection();
      }
      // return either a normal or commit control connection.
      return getNewConnection(! aCommitControlOnFallback );
   }

   /**
    * Convenience method to return transaction for current thread, translating
    * <code>AeTransactionException</code> to <code>AeXMLDBException</code>.
    */
   protected IAeTransaction getTransaction() throws AeXMLDBException
   {
      try
      {
         return AeTransactionManager.getInstance().getTransaction();
      }
      catch (AeTransactionException e)
      {
         String message = AeMessages.getString("AeXMLDBObject.ERROR_GETTING_TRANSACTION"); //$NON-NLS-1$
         if (e.getLocalizedMessage() != null)
         {
            message += ": " + e.getLocalizedMessage(); //$NON-NLS-1$
         }
         throw new AeXMLDBException(message);
      }
   }

   /**
    * Resolves the given key to a real xmldb config key.  If there is a prefix set on this object,
    * then it is prepended to the given key.  If no prefix is set, the key is left unchanged.
    *
    * @param aXQueryKey
    */
   protected String resolveXQueryKey(String aXQueryKey)
   {
      if (AeUtil.isNullOrEmpty(getConfigPrefix()))
      {
         return aXQueryKey;
      }
      else
      {
         return getConfigPrefix() + "." + aXQueryKey; //$NON-NLS-1$
      }
   }

   /**
    * Formats a given statement (which presumably has variables in it) with the values in the aParams
    * array.
    *
    * @param aStatementPattern
    * @param aParams
    */
   protected String formatStatement(String aStatementPattern, Object [] aParams) throws AeXMLDBException
   {
      try
      {
         return MessageFormat.format(aStatementPattern, aParams);
      }
      catch (Exception e)
      {
         throw new AeXMLDBException(AeMessages.getString("AeXMLDBObject.ERROR_FORMATTING_STATEMENT") + aStatementPattern); //$NON-NLS-1$
      }
   }

   /**
    * @return Returns the xmldbConfig.
    */
   public AeXMLDBConfig getXMLDBConfig()
   {
      return mXMLDBConfig;
   }

   /**
    * @param aXMLDBConfig The xmldbConfig to set.
    */
   protected void setXMLDBConfig(AeXMLDBConfig aXMLDBConfig)
   {
      mXMLDBConfig = aXMLDBConfig;
   }

   /**
    * @return Returns the configPrefix.
    */
   protected String getConfigPrefix()
   {
      return mConfigPrefix;
   }

   /**
    * @param aConfigPrefix The configPrefix to set.
    */
   protected void setConfigPrefix(String aConfigPrefix)
   {
      mConfigPrefix = aConfigPrefix;
   }

   /** A threadlocal variable to store the name of the current statement (for logging). */
   private static ThreadLocal sCurrentStatementName = new ThreadLocal()
   {
      protected Object initialValue()
      {
         return "Unknown"; //$NON-NLS-1$
      }
   };

   /**
    * Gets access to the thread local current statement name.
    */
   public static String getCurrentStatementName()
   {
      String rval = (String) sCurrentStatementName.get();
      sCurrentStatementName.set("Unknown"); //$NON-NLS-1$
      return rval;
   }

   /**
    * Sets the thread's current statment name.
    *
    * @param aStatementName
    */
   protected static void setCurrentStatementName(String aStatementName)
   {
      sCurrentStatementName.set(aStatementName);
   }

   /**
    * @return Returns the storageImpl.
    */
   protected IAeXMLDBStorageImpl getStorageImpl()
   {
      return mStorageImpl;
   }

   /**
    * @param aStorageImpl the storageImpl to set
    */
   protected void setStorageImpl(IAeXMLDBStorageImpl aStorageImpl)
   {
      mStorageImpl = aStorageImpl;
   }
}
