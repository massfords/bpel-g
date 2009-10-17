//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeTaskManagerBean.java,v 1.1 2008/01/18 22:46:41 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig;
import org.activebpel.rt.bpeladmin.war.AeMessages;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.schema.AeSchemaDuration;

/**
 * Provides support for configuring task manager properties
 */
public class AeTaskManagerBean extends AeAbstractAdminBean
{
   private static final String CUSTOM_MANAGERS_TASK_MANAGER_FINALIZATION_DURATION =
            "CustomManagers/BPEL4PeopleManager/FinalizationDuration"; //$NON-NLS-1$

   private static final String CACHE_SIZE =
      "CustomManagers/BPEL4PeopleManager/TaskClientProperties/cache-size"; //$NON-NLS-1$

   private static final String CACHE_DURATION =
      "CustomManagers/BPEL4PeopleManager/TaskClientProperties/cache-duration-mins"; //$NON-NLS-1$

   /** duration to keep the in the inbox once it's reached a final state */
   private int mFinalizationDuration = 1;
   /** Duration (ttl) of inbox cache, in minutes. */
   private int mCacheDuration;
   /** Cache size . */
   private int mCacheSize;

   /**
    * Ctor inits the properties
    */
   public AeTaskManagerBean()
   {
      IAeUpdatableEngineConfig config = (IAeUpdatableEngineConfig) getAdmin().getEngineConfig().getUpdatableEngineConfig();

      setCacheSize( AeUtil.parseInt((String)config.getEntryByPath(CACHE_SIZE), 500) );
      setCacheDuration( AeUtil.parseInt((String)config.getEntryByPath(CACHE_DURATION), 10) );

      String finalizationDuration = (String) config.getEntryByPath(CUSTOM_MANAGERS_TASK_MANAGER_FINALIZATION_DURATION);
      if (AeUtil.notNullOrEmpty(finalizationDuration))
      {
         try
         {
            AeSchemaDuration duration = new AeSchemaDuration(finalizationDuration);
            setFinalizationDuration(Math.max(1, duration.getDays()));
         }
         catch (Exception e)
         {
         }
      }
   }

   /**
    * Indicates the update button has been pressed and all changes will be persisted
    * to the config.
    * @param aValue
    */
   public void setUpdate( String aValue )
   {
      validate();

      if (!isErrorDetail())
      {
         saveChanges();
      }
   }

   /**
    * validates that the client url is set and that the finalization duration
    * is non-negative
    */
   protected void validate()
   {
      if (getFinalizationDuration() < 1)
      {
         addError("finalizationDuration", String.valueOf(getFinalizationDuration()), AeMessages.getString("AeTaskManagerBean.FinalizationDuration")); //$NON-NLS-1$ //$NON-NLS-2$
      }

      if (getCacheSize() < 0)
      {
         addError("cacheSize", String.valueOf(getCacheSize()), AeMessages.getString("AeTaskManagerBean.CacheSize")); //$NON-NLS-1$ //$NON-NLS-2$
      }

      if (getCacheDuration() < 1)
      {
         addError("cacheDuration", String.valueOf(getCacheDuration()), AeMessages.getString("AeTaskManagerBean.CacheDuration")); //$NON-NLS-1$ //$NON-NLS-2$
      }

   }

   /**
    * Convenience method to get the updatable config.
    */
   protected IAeUpdatableEngineConfig getUpdatableConfig()
   {
      return getAdmin().getEngineConfig().getUpdatableEngineConfig();
   }

   /**
    * Saves the current config settings to the database.
    */
   protected void saveChanges()
   {
      String duration = "P" + getFinalizationDuration() + "D"; //$NON-NLS-1$ //$NON-NLS-2$

      IAeUpdatableEngineConfig config = getUpdatableConfig();
      config.addEntryByPath(CUSTOM_MANAGERS_TASK_MANAGER_FINALIZATION_DURATION, duration);
      config.addEntryByPath( CACHE_SIZE, String.valueOf( getCacheSize()) );
      config.addEntryByPath( CACHE_DURATION, String.valueOf( getCacheDuration()) );
      config.update();
   }

   /**
    * @return the finalizationDuration
    */
   public int getFinalizationDuration()
   {
      return mFinalizationDuration;
   }

   /**
    * @param aFinalizationDuration the finalizationDuration to set
    */
   public void setFinalizationDuration(int aFinalizationDuration)
   {
      mFinalizationDuration = aFinalizationDuration;
   }

   /**
    * @return cache duration in minutes.
    */
   public int getCacheDuration()
   {
      return mCacheDuration;
   }

   /**
    * Sets the cache duration.
    * @param aCacheDuration duration, in minutes.
    */
   public void setCacheDuration(int aCacheDuration)
   {
      mCacheDuration = aCacheDuration;
   }

   /**
    * @return Cache size.
    */
   public int getCacheSize()
   {
      return mCacheSize;
   }

   /**
    * Sets the cache size.
    * @param aCacheSize
    */
   public void setCacheSize(int aCacheSize)
   {
      mCacheSize = aCacheSize;
   }

}
