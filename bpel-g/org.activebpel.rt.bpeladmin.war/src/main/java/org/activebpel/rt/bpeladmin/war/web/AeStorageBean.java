// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeStorageBean.java,v 1.17 2006/05/04 17:11:28 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.activebpel.rt.bpel.config.AeDefaultEngineConfiguration;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.server.admin.IAeEngineAdministration;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.storage.AePersistentStoreFactory;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeDataSource;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLStorageProviderFactory;
import org.activebpel.rt.bpeladmin.war.AeEngineManagementFactory;
import org.activebpel.rt.bpeladmin.war.AeMessages;
import org.activebpel.rt.util.AeUtil;

/**
 * Bean for driving display of storage page.
 */
public class AeStorageBean extends AeAbstractAdminBean
{
   /** Flag indicating whether configuration uses persistent storage. */
   protected boolean mPersistent;
   /** Flag indicating persistent configuration is configured. */
   protected boolean mConfigured;
   /** Flag indicating persistent configuration can be connected to. */
   protected boolean mConnected;
   /** Flag indicating persistent configuration can be connected to and is up to date. */
   protected boolean mAvailable;
   /** Database type */
   protected String mDatabaseType;
   /** JNDI Location */
   protected String mJndiLocation;
   /** Username for connecting to the database */
   protected String mUsername;
   /** Password for connecting to database */
   protected String mPassword;
   /** Password confirmation */
   protected String mPasswordConfirm;
   
   /* Some params for setting Tamino config settings. */
   /** The Tamino URL. */
   protected String mTaminoUrl;
   /** The Tamino auth domain. */
   protected String mTaminoDomain;
   /** The Tamino database name. */
   protected String mTaminoDatabaseName;
   /** The Tamino collection name. */
   protected String mTaminoCollection;
   /** The size of the Tamino connection pool. */
   protected String mTaminoPoolsize;

   /**
    * Default constructor.
    */
   public AeStorageBean()
   {
      loadConfigFields();
   }

   /**
    * Load the standard fields from the configuration.
    */
   public void loadConfigFields()
   {
      setPersistent(false);

      // if we have all the info then persistence is on
      AeDefaultEngineConfiguration config = getRawConfig();
      Map storeConfigMap = config.getMapEntry(IAeEngineConfiguration.PERSISTENT_STORE_ENTRY);
      if (!AeUtil.isNullOrEmpty(storeConfigMap))
      {
         // order: PersistentStore/Factory
         Map factoryMap = (Map)storeConfigMap.get(IAeEngineConfiguration.FACTORY_ENTRY);
         if (!AeUtil.isNullOrEmpty(factoryMap))
         {
            Map providerFactoryMap = (Map)factoryMap.get(IAeEngineConfiguration.STORAGE_PROVIDER_FACTORY);
            if (!AeUtil.isNullOrEmpty(providerFactoryMap))
            {
               setPersistent(true);
               // location of db type: PersistentStore/Factory/StorageProviderFactory/
               setDatabaseType((String)providerFactoryMap.get(IAeEngineConfiguration.DATABASE_TYPE_ENTRY));
               Map dsMap = (Map)providerFactoryMap.get(IAeEngineConfiguration.DATASOURCE_ENTRY);
               if(dsMap != null)
               {
                  // location of JNDI, db username and password: PersistentStore/Factory/StorageProviderFactory/DatabaseSource/
                  setJndiLocation((String)dsMap.get(IAeEngineConfiguration.DS_JNDI_NAME_ENTRY));
                  setUsername((String)dsMap.get(IAeEngineConfiguration.DS_USERNAME_ENTRY));
                  setPassword((String)dsMap.get(IAeEngineConfiguration.DS_PASSWORD_ENTRY));
                  setPasswordConfirm(getPassword());
                  setTaminoUrl((String)dsMap.get("URL"));
                  setTaminoDatabaseName((String)dsMap.get("Database"));
                  setTaminoCollection((String)dsMap.get("Collection"));
                  setTaminoDomain((String)dsMap.get("Domain"));
                  setTaminoPoolsize((String)dsMap.get("MaxConnections"));
               } // end dsMap
               if(AeDataSource.MAIN != null /**|| AeTaminoDataSource.MAIN != null**/)
                  setConnected(true);
               if(AeEngineManagementFactory.getBean().isEngineStorageReady())
                  setAvailable(true);
            } // end providerMap
         } // end factoryMap 
      } // end store config map.
   }

   /**
    * Indicates that all updates have taken place if the
    * the given value is set to true.
    * @param aValue Flag to signal ok to do engine config updates.
    */
   public void setFinished( boolean aValue )
   {
      // clear any local status detail
      setStatusDetail(null);
      setConnected(false);
      setAvailable(false);

      // only allow update if the engine is not running.  The ui will
      // prevent this but just in case
      if(isEngineRunning())
      {
         setStatusDetail(AeMessages.getString("AeStorageBean.0")); //$NON-NLS-1$
         return;
      }

      // check that the passwords match
      if(! AeUtil.compareObjects(getPassword(), getPasswordConfirm()))
      {
         setStatusDetail(AeMessages.getString("AeStorageBean.1")); //$NON-NLS-1$
         return;
      }

      // if we have all the info then persistence is on
      AeDefaultEngineConfiguration config = getRawConfig();
      Map storeConfigMap = config.getMapEntry(IAeEngineConfiguration.PERSISTENT_STORE_ENTRY);
      if (!AeUtil.isNullOrEmpty(storeConfigMap))
      {
         // order: PersistentStore/Factory
         Map factoryMap = (Map)storeConfigMap.get(IAeEngineConfiguration.FACTORY_ENTRY);         
         if (!AeUtil.isNullOrEmpty(factoryMap))
         {
            Map providerFactoryMap = (Map)factoryMap.get(IAeEngineConfiguration.STORAGE_PROVIDER_FACTORY);
            // location of db type: PersistentStore/Factory/StorageProviderFactory/
            if (!AeUtil.isNullOrEmpty(providerFactoryMap))
            {
               providerFactoryMap.put(IAeEngineConfiguration.DATABASE_TYPE_ENTRY, getDatabaseType());
               Map dsMap = (Map)providerFactoryMap.get(IAeEngineConfiguration.DATASOURCE_ENTRY);
               if(dsMap != null)
               {
                  if (!AeUtil.isNullOrEmpty(getJndiLocation()))
                  dsMap.put(IAeEngineConfiguration.DS_JNDI_NAME_ENTRY, getJndiLocation());
                  if(!AeUtil.isNullOrEmpty(getUsername()))
                  {
                     dsMap.put("Domain", getTaminoDomain());
                     dsMap.put(IAeEngineConfiguration.DS_USERNAME_ENTRY, getUsername());
                     dsMap.put(IAeEngineConfiguration.DS_PASSWORD_ENTRY, getPassword());
                  }
                  else
                  {
                     dsMap.remove("Domain");
                     dsMap.remove(IAeEngineConfiguration.DS_USERNAME_ENTRY);
                     dsMap.remove(IAeEngineConfiguration.DS_PASSWORD_ENTRY);
                  }
                  if (!AeUtil.isNullOrEmpty(getTaminoUrl()))
                     dsMap.put("URL", getTaminoUrl());
                  if (!AeUtil.isNullOrEmpty(getTaminoDatabaseName()))
                     dsMap.put("Database", getTaminoDatabaseName());
                  if (!AeUtil.isNullOrEmpty(getTaminoCollection()))
                     dsMap.put("Collection", getTaminoCollection());
                  if (!AeUtil.isNullOrEmpty(getTaminoPoolsize()))
                     dsMap.put("MaxConnections", getTaminoPoolsize());
               } // end dsMap
            } // end providerMap

            Map entries = config.getEntries();
            Properties props = new Properties();
            props.putAll(entries);
            StringWriter sw = new StringWriter();
            try {
                props.store(sw, "raw dump");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            getAdmin().setRawConfig(sw.toString());
            
//            // write the new configuration out
//            if( getAdmin().getEngineConfig() instanceof AeFileBasedEngineConfig )
//            {
//               ((AeFileBasedEngineConfig)getAdmin().getEngineConfig()).update();
//               // tell users they must restart
//               setStatusDetail(AeMessages.getString("AeStorageBean.2")); //$NON-NLS-1$
//            }
//            else
//            {
               setStatusDetail( AeMessages.getString("AeStorageBean.3") ); //$NON-NLS-1$
//            }
         } // end factory map
      } // end config map
   }

   /**
    * Reinitialize the persistence entries.
    * Note: not called yet, this is a placeholder.
    * todo This won't work since we have the wrong context, BpelAdmin not
    * active-bpel in the future we may want to consolidate the wars or cache
    * the context so we can accomplish this.
    *
    * todo If this is ever used, someone should review the Tamino impl to make sure that works too... 
    */
   protected void updatePersistence()
   {
      try
      {
         // reinit the storage
         AeEngineManagementFactory.getBean().initializeStorage();
      }
      catch (AeStorageException ex)
      {
         AeStorageException.logWarning(""); //$NON-NLS-1$
         ex.logError();
         AeStorageException.logWarning(""); //$NON-NLS-1$
      }

      // check if we can connect and have a db which is ready for use

      // note this use of main is a little more knowledge then we should have here
      // todo remove check for datasource main and add engine factory method instead
      if (AeDataSource.MAIN != null)
         setConnected(true);

      if (AeEngineFactory.isPersistentStoreReadyForUse())
         setAvailable(true);
   }

   /**
    * Returns true if the database can be connected and has an up to date schema.
    */
   public boolean isAvailable()
   {
      return mAvailable;
   }

   /**
    * Returns true if credentials (username/pass) exist in the engine config.
    */
   public boolean isCredentials()
   {
      // Credentials always exist in Tamino mode - or when a username HAS been specified.
      return isTamino() || AeUtil.notNullOrEmpty(getUsername());
   }

   /**
    * Returns true if Tamino is the configured DB.
    */
   public boolean isTamino()
   {
      return "tamino".equals(getDatabaseType()); //$NON-NLS-1$
   }

   /**
    * Returns <code>true</code> if and only if the engine is using a JNDI data
    * source.
    */
   public boolean isJndiDataSource()
   {
      // Currently, we are using JNDI whenever we are not using Tamino.
      return !isTamino();
   }

   /**
    * Returns true if the configuration file has information for storage.
    */
   public boolean isConfigured()
   {
      return mConfigured;
   }

   /**
    * Returns true if the database can be connected to.
    */
   public boolean isConnected()
   {
      return mConnected;
   }

   /**
    * Returns true if the configuration includes persistence.
    */
   public boolean isPersistent()
   {
      return mPersistent;
   }

   /**
    * Set to true if the configuration includes persistence.
    */
   protected void setPersistent(boolean aB)
   {
      mPersistent = aB;
   }

   /**
    * Sets flag indicating database and schema are ready for use.
    */
   protected void setAvailable(boolean aB)
   {
      mAvailable = aB;
   }

   /**
    * Sets flag indicating that the configuration contains persistence information.
    */
   protected void setConfigured(boolean aB)
   {
      mConfigured = aB;
   }

   /**
    * Sets flag indicating the database can be connected to.
    */
   public void setConnected(boolean aB)
   {
      mConnected = aB;
   }

   /**
    * Get the database type of storage.
    */
   public String getDatabaseType()
   {
      return mDatabaseType;
   }

   /**
    * Set the database type of storage.
    */
   public void setDatabaseType(String aString)
   {
      mDatabaseType = aString;
   }

   /**
    * Get the optional database password.
    */
   public String getPassword()
   {
      return AeUtil.getSafeString(mPassword);
   }

   /**
    * Set the optional database password.
    */
   public void setPassword(String aString)
   {
      mPassword = aString;
   }

   /**
    * Get the optional database password confirmation.
    */
   public String getPasswordConfirm()
   {
      return mPasswordConfirm;
   }

   /**
    * Set the optional database password confirmation.
    */
   public void setPasswordConfirm(String aString)
   {
      mPasswordConfirm = aString;
   }

   /**
    * Get the optional user name for connecting to database.
    */
   public String getUsername()
   {
      return AeUtil.getSafeString(mUsername);
   }

   /**
    * Set the optional user name for connecting to database.
    */
   public void setUsername(String aString)
   {
      mUsername = aString;
   }

   /**
    * Returns true if the configuration uses the standard dbms storage.
    */
   public boolean isStandardDbms()
   {
      try
      {
         return AePersistentStoreFactory.getInstance() instanceof AeSQLStorageProviderFactory;
      }
      catch (AeStorageException e)
      {
         return false;
      }
   }

   /**
    * Returns the standard dbms storage factory or null if error.
    */
   protected AeSQLStorageProviderFactory getStorageFactory()
   {
      try
      {
         return (AeSQLStorageProviderFactory) AePersistentStoreFactory.getInstance();
      }
      catch (Throwable e)
      {
         // shouldn't happen since we only call after checking for standard dbms.
         return null;
      }
   }

   /**
    * Returns the configured JNDI location or null if not avaiable.
    */
   public String getJndiLocation()
   {
      return mJndiLocation;
   }

   /**
    * Sets the configured JNDI location.
    */
   public void setJndiLocation(String aString)
   {
      mJndiLocation = aString;
   }

   /**
    * Error message if the persistence is not available (ready for use).
    */
   public String getErrorMessage()
   {
      if(getStatusDetail() != null)
         return getStatusDetail();
      return AeEngineManagementFactory.getBean().getStorageError();
   }

   /**
    * Returns boolean true if engine is running.
    */
   public boolean isEngineRunning()
   {
      return getAdmin().getEngineState() == IAeEngineAdministration.RUNNING;
   }
   
   /**
    * @return Returns the taminoCollection.
    */
   public String getTaminoCollection()
   {
      return AeUtil.getSafeString(mTaminoCollection);
   }

   /**
    * @param aTaminoCollection The taminoCollection to set.
    */
   public void setTaminoCollection(String aTaminoCollection)
   {
      mTaminoCollection = AeUtil.getSafeString(aTaminoCollection);
   }

   /**
    * @return Returns the taminoDatabaseName.
    */
   public String getTaminoDatabaseName()
   {
      return AeUtil.getSafeString(mTaminoDatabaseName);
   }

   /**
    * @param aTaminoDatabaseName The taminoDatabaseName to set.
    */
   public void setTaminoDatabaseName(String aTaminoDatabaseName)
   {
      mTaminoDatabaseName = aTaminoDatabaseName;
   }

   /**
    * @return Returns the taminoDomain.
    */
   public String getTaminoDomain()
   {
      return AeUtil.getSafeString(mTaminoDomain);
   }

   /**
    * @param aTaminoDomain The taminoDomain to set.
    */
   public void setTaminoDomain(String aTaminoDomain)
   {
      mTaminoDomain = AeUtil.getSafeString(aTaminoDomain);
   }

   /**
    * @return Returns the taminoUrl.
    */
   public String getTaminoUrl()
   {
      return AeUtil.getSafeString(mTaminoUrl);
   }

   /**
    * @param aTaminoUrl The taminoUrl to set.
    */
   public void setTaminoUrl(String aTaminoUrl)
   {
      mTaminoUrl = AeUtil.getSafeString(aTaminoUrl);
   }
   
   /**
    * @return Returns the taminoPoolsize.
    */
   public String getTaminoPoolsize()
   {
      return mTaminoPoolsize;
   }
   
   /**
    * @param aTaminoPoolsize The taminoPoolsize to set.
    */
   public void setTaminoPoolsize(String aTaminoPoolsize)
   {
      mTaminoPoolsize = aTaminoPoolsize;
   }
}
