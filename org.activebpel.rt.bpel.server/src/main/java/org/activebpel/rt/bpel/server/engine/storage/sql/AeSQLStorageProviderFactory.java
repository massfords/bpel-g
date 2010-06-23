// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeSQLStorageProviderFactory.java,v 1.8 2007/08/17 00:23:09 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.storage.AeAbstractStorageProviderFactory;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageConfig;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeTransmissionTrackerStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeURNStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.sql.upgrade.AeSQLUpgrader;
import org.activebpel.rt.util.AeUtil;

/**
 * This factory instantiates SQL versions of the queue, and process state
 * storage objects.
 */
public class AeSQLStorageProviderFactory extends AeAbstractStorageProviderFactory
{
   /** The default database type if one is not specified in the config. */
   protected static final String DEFAULT_DATABASE_TYPE = "mysql"; //$NON-NLS-1$

   /** The SQL Config object. */
   protected AeSQLConfig mSQLConfig;

   /**
    * Constructs the SQL storage provider factory.
    * 
    * @param aConfig
    */
   public AeSQLStorageProviderFactory(Map aConfig) throws AeException
   {
      super(aConfig);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.AeAbstractStorageProviderFactory#setConfiguration(java.util.Map)
    */
   public void setConfiguration(Map aConfig) throws AeException
   {
      super.setConfiguration(aConfig);
      
      String type = (String) aConfig.get(IAeEngineConfiguration.DATABASE_TYPE_ENTRY);
      if (AeUtil.isNullOrEmpty(type))
      {
         type = DEFAULT_DATABASE_TYPE;
      }
      Map constantOverrides = (Map) aConfig.get(IAeEngineConfiguration.SQL_CONSTANTS);
      if (constantOverrides == null)
      {
         constantOverrides = Collections.EMPTY_MAP;
      }

      AeSQLConfig sqlConfig = createSQLConfig(type, constantOverrides);
      setSQLConfig(sqlConfig);

      Map dataSourceConfig = (Map) aConfig.get(IAeEngineConfiguration.DATASOURCE_ENTRY);
      if (AeUtil.isNullOrEmpty(dataSourceConfig))
      {
         throw new AeException(AeMessages.getString("AeSQLStoreFactory.ERROR_2")); //$NON-NLS-1$
      }
      AeDataSource.MAIN = createDataSource(dataSourceConfig, getSQLConfig());

      // validate the database schema type (mysql, sqlserver, etc...)
      validateDatabaseType(AeDataSource.MAIN, type);

      // validate the database connection and schema (table structure) version
      String version = (String) aConfig.get(IAeEngineConfiguration.PERSISTENT_VERSION_ENTRY);
      validateVersion(AeDataSource.MAIN, version);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageProviderFactory#createQueueStorageProvider()
    */
   public IAeQueueStorageProvider createQueueStorageProvider()
   {
      return new AeSQLQueueStorageProvider(getSQLConfig());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageProviderFactory#createProcessStateStorageProvider()
    */
   public IAeProcessStateStorageProvider createProcessStateStorageProvider()
   {
      return new AeSQLProcessStateStorageProvider(getSQLConfig());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageProviderFactory#createCoordinationStorageProvider()
    */
   public IAeCoordinationStorageProvider createCoordinationStorageProvider()
   {
      return new AeSQLCoordinationStorageProvider(getSQLConfig());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageProviderFactory#createURNStorageProvider()
    */
   public IAeURNStorageProvider createURNStorageProvider()
   {
      return new AeSQLURNStorageProvider(getSQLConfig());
   }
   
   /**  
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageProviderFactory#createTransmissionTrackerStorageProvider()
    */
   public IAeTransmissionTrackerStorageProvider createTransmissionTrackerStorageProvider()
   {
      return new AeSQLTransmissionTrackerStorageProvider(getSQLConfig());
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageProviderFactory#createAttachmentStorageProvider()
    */
   public IAeAttachmentStorageProvider createAttachmentStorageProvider()
   {
      return new AeSQLAttachmentStorageProvider(getSQLConfig());
   }

   /**
    * Initializes the SQL store.  Checks for required upgrades to the schema and performs
    * each upgrade in sequence.
    * 
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory#init()
    */
   public void init() throws AeException
   {
      AeSQLUpgrader upgrader = new AeSQLUpgrader(getSQLConfig());
      upgrader.upgrade();
   }
   
   /**
    * Validates the database is configured correctly for ActiveBPEL handling.
    * @param aSource The datasource to check.
    * @param aVersion the version the database tables should be configured with.
    */
   private void validateVersion(AeDataSource aSource, String aVersion) throws AeStorageException
   {
      String version = AeSQLVersion.getInstance().getVersion();
      if(! AeUtil.compareObjects(version, aVersion))
         throw new AeStorageException(MessageFormat.format(AeMessages.getString("AeSQLStoreFactory.ERROR_0"), //$NON-NLS-1$
                                                           new Object[] {version, aVersion}));
   }

   /**
    * Validates the database is configured for the correct database type.
    *
    * @param aSource The datasource to check.
    * @param aType the type the database tables should be configured with.
    * @throws AeStorageException
    */
   private void validateDatabaseType(AeDataSource aSource, String aType) throws AeStorageException
   {
      String type = AeSQLDatabaseType.getInstance().getDatabaseType();
      if (!AeUtil.compareObjects(type, aType))
      {
         throw new AeStorageException(MessageFormat.format(AeMessages.getString("AeSQLStoreFactory.ERROR_1"), //$NON-NLS-1$
                                                           new Object[] {type, aType}));
      }
   }

   /**
    * Creates the SQL config object that the storage objects will use.
    *
    * @param aType The type of database that is configured.
    * @param aOverrideMap map of name value pairs to override the inlined constants
    * @return The SQL config object.
    */
   protected AeSQLConfig createSQLConfig(String aType, Map aOverrideMap)
   {
      return new AeSQLConfig(aType, aOverrideMap);
   }

   /**
    * Creates a data source from the engine configuration.
    *
    * @param aConfig The engine configuration map.
    * @param aSQLConfig The SQL configuration.
    * @return The data source to use.
    */
   protected AeDataSource createDataSource(Map aConfig, AeSQLConfig aSQLConfig) throws AeException
   {
      String className = (String) aConfig.get(IAeEngineConfiguration.CLASS_ENTRY);
      if (AeUtil.isNullOrEmpty(className))
      {
         throw new AeException(AeMessages.getString("AeSQLStoreFactory.ERROR_8")); //$NON-NLS-1$
      }
      try
      {
         Class clazz = Class.forName(className);
         Constructor constructor = clazz.getConstructor( new Class [] { Map.class, AeSQLConfig.class } );
         return (AeDataSource) constructor.newInstance(new Object[] { aConfig, aSQLConfig });
      }
      catch (Exception e)
      {
         if (e instanceof AeException)
         {
            throw (AeException) e;
         }
         else
         {
            throw new AeException(AeMessages.getString("AeSQLStoreFactory.ERROR_9"), e); //$NON-NLS-1$
         }
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.AeAbstractStorageProviderFactory#getProviderCtorArg()
    */
   protected AeStorageConfig getProviderCtorArg()
   {
      return getSQLConfig();
   }
   
   /**
    * Sets the sql configuration.
    */
   protected void setSQLConfig(AeSQLConfig sQLConfig)
   {
      mSQLConfig = sQLConfig;
   }

   /**
    * Returns the sql configuration.
    */
   protected AeSQLConfig getSQLConfig()
   {
      return mSQLConfig;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageProviderFactory#getDBConfig()
    */
   public AeStorageConfig getDBConfig()
   {
      return getSQLConfig();
   }
}
