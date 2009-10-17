// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/config/AeConfiguration.java,v 1.5 2007/08/07 00:55:47 mford Exp $
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.config;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeUtil;

/**
 * A base class for configuration classes (such as the ActiveBPEL Engine
 * Configuration class).
 */
public abstract class AeConfiguration
{
   /** Map of entries in the configuration. */
   private Map mEntries = new HashMap();

   // /////////////////////////////////////////////////////
   // Main config accessor methods, with variant support
   // /////////////////////////////////////////////////////

   /**
    * Get a string entry in the config with the passed name. Returning null if
    * none available.
    */
   public String getEntry(String aName)
   {
      return getEntry(aName, null);
   }

   /**
    * Get a string entry in the config with the passed name. Returning the
    * passed default if entry is unavailable.
    * 
    * @param aName
    * @param aDefault
    */
   public String getEntry(String aName, String aDefault)
   {
      return (String) getEntryInternal(getEntries(), aName, String.class, aDefault);
   }

   /**
    * Get a boolean entry in the config with the passed name. Returning the
    * passed default if entry is unavailable.
    * 
    * @param aName
    * @param aDefault
    */
   public boolean getBooleanEntry(String aName, boolean aDefault)
   {
      String trueFalseFlag = getEntry(aName, Boolean.toString(aDefault));
      return AeUtil.toBoolean(trueFalseFlag);
   }

   /**
    * Get a boolean entry from the map with the passed name. Returning the
    * passed default if entry is unavailable.
    * 
    * @param aMap
    * @param aName
    * @param aDefault
    */
   protected boolean getBooleanEntryInternal(Map aMap, String aName, boolean aDefault)
   {
      String defaultFlag = Boolean.toString(aDefault);
      String trueFalseFlag = (String) getEntryInternal(aMap, aName, String.class, defaultFlag);
      return AeUtil.toBoolean(trueFalseFlag);
   }

   /**
    * Get an integer entry in the config with the passed name. Returning the
    * passed default if entry is unavailable.
    * 
    * @param aName
    * @param aDefault
    */
   public int getIntegerEntry(String aName, int aDefault)
   {
      return getIntegerEntryInternal(getEntries(), aName, aDefault);
   }

   /**
    * Internal utility method for accessing config entries with expected int
    * values.
    * 
    * @param aMap
    * @param aKey
    * @param aDefaultValue
    */
   protected int getIntegerEntryInternal(Map aMap, String aKey, int aDefaultValue)
   {
      int retVal = aDefaultValue;
      try
      {
         String intValue = (String) getEntryInternal(aMap, aKey, String.class, String.valueOf(aDefaultValue));
         retVal = Integer.parseInt(intValue);
      }
      catch (NumberFormatException nfe)
      {
         AeException.logError(nfe, nfe.getMessage());
      }
      return retVal;
   }

   /**
    * Get a mapped entry in the config with the passed name. Returns null if no
    * map entry found or entry is not a map.
    * 
    * @param aName
    */
   public Map getMapEntry(String aName)
   {
      return getMapEntry(aName, false);
   }

   /**
    * Retrieve the map entry from the config.
    * 
    * @param aName The config entry name.
    * @param aCreateFlag If true and the named map is null, it will be created
    *           and added to the config map.
    */
   public Map getMapEntry(String aName, boolean aCreateFlag)
   {
      return getMapEntry(aName, aCreateFlag, getEntries());
   }
   
   /**
    * Retrieve the map entry from the config.
    * 
    * @param aName The config entry name.
    * @param aCreateFlag If true and the named map is null, it will be created
    *           and added to the config map.
    * aConfigEntry Map to containing entries.
    */   
   public Map getMapEntry(String aName, boolean aCreateFlag, Map aConfigEntry)
   {
      Map map = (Map) getEntryInternal(aConfigEntry, aName, Map.class, null);
      if (map == null && aCreateFlag)
      {
         map = new HashMap();
         aConfigEntry.put(aName, map);
      }
      return map;
   }

   /**
    * Internal utility method for accessing config entries.
    * 
    * @param aMap
    * @param aKey
    * @param aType
    * @param aDefault
    */
   protected Object getEntryInternal(Map aMap, String aKey, Class aType, Object aDefault)
   {
      Object retVal = aDefault;

      if (aMap != null)
      {
         Object entry = aMap.get(aKey);
         if (entry != null && aType.isAssignableFrom(entry.getClass()))
         {
            retVal = entry;
         }
      }
      return retVal;
   }

   // /////////////////////////////////////////////////////
   // Main config mutator methods, with variant support
   // /////////////////////////////////////////////////////

   /**
    * Descend into the config maps for the given path to set the value.
    * 
    * @param aPath The path to the map. The path format will be something like foo/bar/debug where foo and bar
    *           are key names for map and debug is the key that will be added to the bar map (with the setting
    *           as its value).
    * @param aSetting The value of the custom param.
    */
   public void addEntryByPath(String aPath, String aSetting)
   {
      AeConfigSetting setting = new AeConfigSetting(aPath, aSetting);
      Map map = setting.findMapByPath(getEntries(), true);
      map.put(setting.getKey(), setting.getValue());
   }
   
   /**
    * Returns the entry given a path
    * @param aPath
    */
   public Object getEntryByPath(String aPath)
   {
      AeConfigSetting setting = new AeConfigSetting(aPath, null);
      Map map = setting.findMapByPath(getEntries(), false);
      if (map != null)
      {
         return map.get(setting.getKey());
      }
      return null;
   }
   
   /**
    * Gets the entry at the given path as an integer, returning the default value
    * if the entry didn't exist or produced a NumberFormatException during the 
    * parse.
    * @param aPath
    * @param aDefault
    */
   public int getIntEntryByPath(String aPath, int aDefault)
   {
      Object entry = getEntryByPath(aPath);
      int value = aDefault;
      if (entry != null)
      {
         try
         {
            value = Integer.parseInt(entry.toString());
         }
         catch(NumberFormatException e)
         {
            // eat exception
         }
      }
      return value;
   }
   
   /**
    * Removes an entry in the config using the given key object.
    * 
    * @param aKey the key object
    */
   public void removeEntry(Object aKey)
   {
      getEntries().remove(aKey);
   }

   /**
    * Set an entry in the config with a specific value.
    * 
    * @param aName
    * @param aValue
    */
   public void setEntry(String aName, String aValue)
   {
      getEntries().put(aName, aValue);
   }

   /**
    * Set an entry in the config with a specific value.
    * 
    * @param aName
    * @param aBool
    */
   public void setBooleanEntry(String aName, boolean aBool)
   {
      getEntries().put(aName, Boolean.toString(aBool));
   }

   /**
    * Set an entry in the config with a specific value.
    * 
    * @param aName
    * @param aInt
    */
   public void setIntegerEntry(String aName, int aInt)
   {
      getEntries().put(aName, Integer.toString(aInt));
   }

   /**
    * Set an entry in the config with a map of values.
    * 
    * @param aName
    * @param aMap
    */
   public void setEntry(String aName, Map aMap)
   {
      getEntries().put(aName, aMap);
   }

   /**
    * Writes the configuration file to the passed Writer.
    * 
    * @param aWriter
    * @throws AeException If an error occurs wsaving the config to the stream.
    */
   public void save(Writer aWriter) throws AeException
   {
      AeConfigurationUtil.saveConfig(aWriter, getEntries());
   }


   /**
    * Setter for top level entries map.
    * 
    * @param aEntries
    */
   protected void setEntries(Map aEntries)
   {
      mEntries = aEntries;
   }

   /**
    * Accessor for entries map.
    */
   protected Map getEntries()
   {
      return mEntries;
   }

   /**
    * Utility class for wrapping the config path and value.
    */
   protected static class AeConfigSetting
   {
      /** The ordered list of map keys. */
      protected List mMapKeys;
      /** The setting name. */
      protected String mName;
      /** The setting value. */
      protected String mValue;
      
      /**
       * Constructor.
       * @param aPath
       * @param aValue
       */
      public AeConfigSetting( String aPath, String aValue )
      {
         initKeys( aPath );
         mValue = aValue;
      }
      
      /**
       * Locates the nested map by its path
       * @param aEntries - root map to begin search
       * @param aAddMaps - flag to create nested maps if not found
       * @return Map or null if not found
       */
      public Map findMapByPath(Map aEntries, boolean aAddMaps)
      {
         Map parentMap = aEntries;
         Map currentMap = parentMap;
         for (Iterator iter = getMapKeyIterator(); currentMap != null && iter.hasNext();)
         {
            String mapKey = (String) iter.next();
            currentMap = (Map) parentMap.get(mapKey);
            if (currentMap == null && aAddMaps)
            {
               currentMap = new HashMap();
               parentMap.put(mapKey, currentMap);
            }
            parentMap = currentMap;
         }
         
         return currentMap;
      }
      
      /**
       * Parse the path.  A given path of foo/bar/debug
       * results in a list with the [foo,bar] entries
       * and the key property is set to debug.
       * @param aPath
       */
      protected void initKeys( String aPath )
      {
         StringTokenizer st = new StringTokenizer( aPath, "/" ); //$NON-NLS-1$
         int count = st.countTokens();
         int currentIndex = 0;
         ArrayList mapKeys = new ArrayList();
         while( st.hasMoreTokens() )
         {
            ++currentIndex;
            String pathElement = st.nextToken();
            if( currentIndex < count )
            {
               mapKeys.add( pathElement );
            }
            else
            {
               mName = pathElement;
            }
         }
         mMapKeys = mapKeys;
      }
      
      /**
       * Accessor for iterator over the map key list.
       */
      public Iterator getMapKeyIterator()
      {
         return mMapKeys.iterator();
      }
      
      /**
       * Key for the custom setting.
       */
      public String getKey()
      {
         return mName;
      }
      
      /**
       * Value of the custom setting.
       */
      public String getValue()
      {
         return mValue;
      }
   }
}
