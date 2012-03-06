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

import java.text.MessageFormat;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageConfig;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.IAeStorageProviderFactory;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeTransmissionTrackerStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeURNStorageProvider;
import org.activebpel.rt.util.AeUtil;

import javax.inject.Inject;

/**
 * This factory instantiates SQL versions of the queue, and process state
 * storage objects.
 */
public class AeSQLStorageProviderFactory implements IAeStorageProviderFactory
{
   /** The default database type if one is not specified in the config. */
   protected static final String DEFAULT_DATABASE_TYPE = "h2"; //$NON-NLS-1$
   
   private AeDataSource mDataSource;

   /** The SQL Config object. */
   @Inject
   protected AeSQLConfig mSQLConfig;
   
   private String mVersion;
   private IAeQueueStorageProvider mQueueStorageProvider;
   private IAeProcessStateStorageProvider mProcessStateStorageProvider;
   private IAeCoordinationStorageProvider mCoordinationStorageProvider;
   private IAeURNStorageProvider mURNStorageProvider;
   private IAeTransmissionTrackerStorageProvider mTransmissionTrackerStorageProvider;
   private IAeAttachmentStorageProvider mAttachmentStorageProvider;
   
   public IAeQueueStorageProvider createQueueStorageProvider()
   {
      return mQueueStorageProvider;
   }

   public IAeProcessStateStorageProvider createProcessStateStorageProvider()
   {
      return mProcessStateStorageProvider;
   }

   public IAeCoordinationStorageProvider createCoordinationStorageProvider()
   {
      return mCoordinationStorageProvider;
   }

   public IAeURNStorageProvider createURNStorageProvider()
   {
      return mURNStorageProvider;
   }
   
   public IAeTransmissionTrackerStorageProvider createTransmissionTrackerStorageProvider()
   {
      return mTransmissionTrackerStorageProvider;
   }
   
   public IAeAttachmentStorageProvider createAttachmentStorageProvider()
   {
      return mAttachmentStorageProvider;
   }

   /**
    * Initializes the SQL store.  Checks for required upgrades to the schema and performs
    * each upgrade in sequence.
    * 
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory#init()
    */
   public void init() throws AeException
   {
       // validate the database schema type (mysql, sqlserver, etc...)
       validateDatabaseType();

       // validate the database connection and schema (table structure) version
       validateVersion();
   }
   
   /**
    * Validates the database is configured for the correct database type.
    *
    * @param aSource The datasource to check.
    * @param aType the type the database tables should be configured with.
    * @throws AeStorageException
    */
   private void validateDatabaseType() throws AeStorageException
   {
      String type = AeSQLDatabaseType.getInstance().getDatabaseType();
      if (!AeUtil.compareObjects(type, getSQLConfig().getDatabaseType()))
      {
         throw new AeStorageException(MessageFormat.format(AeMessages.getString("AeSQLStoreFactory.ERROR_1"), //$NON-NLS-1$
                                                           new Object[] {type, getSQLConfig().getDatabaseType()}));
      }
   }

   /**
    * Validates the database is configured correctly for ActiveBPEL handling.
    */
   private void validateVersion() throws AeStorageException
   {
      String version = AeSQLVersion.getInstance().getVersion();
      if(! AeUtil.compareObjects(version, getVersion()))
         throw new AeStorageException(MessageFormat.format(AeMessages.getString("AeSQLStoreFactory.ERROR_0"), //$NON-NLS-1$
                                                           new Object[] {version, getVersion()}));
   }


   /**
    * Sets the sql configuration.
    */
   public void setSQLConfig(AeSQLConfig sQLConfig)
   {
      mSQLConfig = sQLConfig;
   }

   /**
    * Returns the sql configuration.
    */
   public AeSQLConfig getSQLConfig()
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

    public String getVersion() {
        return mVersion;
    }
    
    public void setVersion(String aVersion) {
        mVersion = aVersion;
    }

    public void setQueueStorageProvider(IAeQueueStorageProvider aQueueStorageProvider) {
        mQueueStorageProvider = aQueueStorageProvider;
    }

    public void setProcessStateStorageProvider(
            IAeProcessStateStorageProvider aProcessStateStorageProvider) {
        mProcessStateStorageProvider = aProcessStateStorageProvider;
    }

    public void setCoordinationStorageProvider(
            IAeCoordinationStorageProvider aCoordinationStorageProvider) {
        mCoordinationStorageProvider = aCoordinationStorageProvider;
    }

    public void setURNStorageProvider(IAeURNStorageProvider aURNStorageProvider) {
        mURNStorageProvider = aURNStorageProvider;
    }

    public void setTransmissionTrackerStorageProvider(
            IAeTransmissionTrackerStorageProvider aTransmissionTrackerStorageProvider) {
        mTransmissionTrackerStorageProvider = aTransmissionTrackerStorageProvider;
    }

    public void setAttachmentStorageProvider(IAeAttachmentStorageProvider aAttachmentStorageProvider) {
        mAttachmentStorageProvider = aAttachmentStorageProvider;
    }

    public AeDataSource getDataSource() {
        return mDataSource;
    }

    public void setDataSource(AeDataSource aDataSource) {
        mDataSource = aDataSource;
    }
}
