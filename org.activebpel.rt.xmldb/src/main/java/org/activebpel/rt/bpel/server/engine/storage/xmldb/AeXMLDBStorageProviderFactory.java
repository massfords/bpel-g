//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/AeXMLDBStorageProviderFactory.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.server.engine.storage.AeAbstractStorageProviderFactory;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageConfig;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeTransmissionTrackerStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeURNStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.attachments.AeXMLDBAttachmentStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.coord.AeXMLDBCoordinationStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.log.AeXMLDBPerformanceLogger;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.process.AeXMLDBProcessStateStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.queue.AeXMLDBQueueStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.transreceive.AeXMLDBTransmissionTrackerStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.urn.AeXMLDBURNStorageProvider;
import org.activebpel.rt.config.AeConfigurationUtil;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xmldb.AeMessages;

/**
 * A storage factory that creates XMLDB versions of the store objects.
 */
public abstract class AeXMLDBStorageProviderFactory extends AeAbstractStorageProviderFactory
{
   /** The XMLDB config. */
   private AeXMLDBConfig mXMLDBConfig;
   /** The storage impl that all storage providers can use. */
   private IAeXMLDBStorageImpl mStorageImpl;

   /**
    * Default constructor.
    */
   public AeXMLDBStorageProviderFactory(Map aConfig) throws AeException
   {
      super(aConfig);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.AeAbstractStorageProviderFactory#setConfiguration(java.util.Map)
    */
   protected void setConfiguration(Map aConfig) throws AeException
   {
      super.setConfiguration(aConfig);

      String logDir = (String) aConfig.get("PerformanceLogDirectory"); //$NON-NLS-1$
      AeXMLDBPerformanceLogger.init(logDir);

      Map constantOverrides = (Map) aConfig.get(IAeEngineConfiguration.SQL_CONSTANTS);
      if (constantOverrides == null)
      {
         constantOverrides = Collections.EMPTY_MAP;
      }

      AeXMLDBConfig xmldbConfig = createXMLDBConfig(constantOverrides);
      setXMLDBConfig(xmldbConfig);

      Map dsConfig = (Map) aConfig.get(IAeEngineConfiguration.DATASOURCE_ENTRY);
      IAeXMLDBDataSource dataSource = createXMLDBDataSource(dsConfig);
      
      setStorageImpl(createStorageImpl(dataSource));

      validateDBVersion(aConfig);
      
      setDataSource(dataSource);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.AeAbstractStorageProviderFactory#createCustomStorageProvider(java.util.Map)
    */
   protected IAeStorageProvider createCustomStorageProvider(Map aCustomProviderConfig) throws AeException
   {
      Object [] args = new Object[] { getProviderCtorArg(), getStorageImpl() };
      Class [] classes = new Class[] { AeStorageConfig.class, IAeXMLDBStorageImpl.class };
      
      return (IAeStorageProvider) AeConfigurationUtil.createConfigSpecificClass(aCustomProviderConfig,
            args, classes);
   }
   
   /**
    * Creates the storage impl.
    * 
    * @param aDataSource
    */
   protected abstract IAeXMLDBStorageImpl createStorageImpl(IAeXMLDBDataSource aDataSource);

   /**
    * Validates that the DB is the correct version.
    * 
    * @param aConfig
    * @throws AeException
    */
   protected void validateDBVersion(Map aConfig) throws AeException
   {
      try
      {
         AeXMLDBValidator validator = createDBValidator(aConfig);
         validator.validateDBVersion();
      }
      catch (AeException ae)
      {
         throw ae;
      }
      catch (Exception error)
      {
         // Catch this - will happen if the Exist JARs are missing.
         throw new AeException(error);
      }
   }

   /**
    * Creates the DB validator to use.
    * 
    * @param aConfig
    */
   protected AeXMLDBValidator createDBValidator(Map aConfig)
   {
      return new AeXMLDBValidator(getXMLDBConfig(), aConfig, getStorageImpl());
   }

   /**
    * Save the data source for later use - useful for subclasses.
    * 
    * @param aDataSource
    */
   protected abstract void setDataSource(IAeXMLDBDataSource aDataSource);

   /**
    * Creates the XMLDB config object that the storage objects will use.
    *
    * @param aOverrideMap map of name value pairs to override the inlined constants
    * @return The XMLDB config object.
    */
   protected AeXMLDBConfig createXMLDBConfig(Map aOverrideMap)
   {
      return new AeXMLDBConfig(aOverrideMap);
   }

   /**
    * Creates a XMLDB data source from the engine configuration.
    *
    * @param aConfig The engine configuration map.
    * @return The data source to use.
    */
   protected IAeXMLDBDataSource createXMLDBDataSource(Map aConfig) throws AeException
   {
      String className = (String) aConfig.get(IAeEngineConfiguration.CLASS_ENTRY);
      if (AeUtil.isNullOrEmpty(className))
      {
         throw new AeException(AeMessages.getString("AeXMLDBStoreFactory.NOT_CLASS_FOR_XMLDB_DATASOURCE_ERROR")); //$NON-NLS-1$
      }
      try
      {
         Class clazz = Class.forName(className);
         Constructor constructor = clazz.getConstructor( new Class [] { Map.class } );
         return (IAeXMLDBDataSource) constructor.newInstance(new Object[] { aConfig });
      }
      catch (Exception e)
      {
         if (e instanceof AeException)
         {
            throw (AeException) e;
         }
         else
         {
            throw new AeException(AeMessages.getString("AeXMLDBStoreFactory.FAILED_TO_CREATE_XMLDB_DATASOURCE_ERROR"), e); //$NON-NLS-1$
         }
      }
   }

   /**
    * @return Returns the xmldbConfig.
    */
   protected AeXMLDBConfig getXMLDBConfig()
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
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageProviderFactory#init()
    */
   public void init() throws AeException
   {
      // Generally used to do upgrades
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageProviderFactory#getDBConfig()
    */
   public AeStorageConfig getDBConfig()
   {
      return getXMLDBConfig();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageProviderFactory#createQueueStorageProvider()
    */
   public IAeQueueStorageProvider createQueueStorageProvider()
   {
      return new AeXMLDBQueueStorageProvider(getXMLDBConfig(), getStorageImpl());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageProviderFactory#createProcessStateStorageProvider()
    */
   public IAeProcessStateStorageProvider createProcessStateStorageProvider()
   {
      return new AeXMLDBProcessStateStorageProvider(getXMLDBConfig(), getStorageImpl());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageProviderFactory#createCoordinationStorageProvider()
    */
   public IAeCoordinationStorageProvider createCoordinationStorageProvider()
   {
      return new AeXMLDBCoordinationStorageProvider(getXMLDBConfig(), getStorageImpl());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageProviderFactory#createURNStorageProvider()
    */
   public IAeURNStorageProvider createURNStorageProvider()
   {
      return new AeXMLDBURNStorageProvider(getXMLDBConfig(), getStorageImpl());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageProviderFactory#createTransmissionTrackerStorageProvider()
    */
   public IAeTransmissionTrackerStorageProvider createTransmissionTrackerStorageProvider()
   {
      return new AeXMLDBTransmissionTrackerStorageProvider(getXMLDBConfig(), getStorageImpl());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageProviderFactory#createAttachmentStorageProvider()
    */
   public IAeAttachmentStorageProvider createAttachmentStorageProvider()
   {
      return new AeXMLDBAttachmentStorageProvider(getXMLDBConfig(), getStorageImpl());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.AeAbstractStorageProviderFactory#getProviderCtorArg()
    */
   protected AeStorageConfig getProviderCtorArg()
   {
      return getXMLDBConfig();
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
