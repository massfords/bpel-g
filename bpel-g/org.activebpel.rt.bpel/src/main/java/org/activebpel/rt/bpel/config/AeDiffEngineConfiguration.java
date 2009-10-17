// $Header$
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.config.AeConfigurationUtil;

/**
 * This class implements an engine configuration that saves/loads a list of engine config differences rather
 * than an entire engine config document. It is loaded first with a base config file, then a set of
 * differences are applied to that base to get the final config. When saving the config, only those settings
 * that are different from the base are saved.
 */
public abstract class AeDiffEngineConfiguration extends AeDefaultEngineConfiguration
{
   /** Copy of the original config entries. */
   protected Map mOriginalParams;

   /**
    * Constructor.
    */
   public AeDiffEngineConfiguration()
   {
      super();
   }

   /**
    * This method adds a map of "diff" config settings to the engine config.  The map will be
    * from engine config path (pseudo-xpath) to config value.
    * 
    * @param aDiffSettings
    */
   public void addConfigSettingsFromDiff(Map aDiffSettings)
   {
      if (!aDiffSettings.isEmpty())
      {
         syncInternalEntries(aDiffSettings);
         resetFunctionContexts();
         notifyListeners();
      }
   }

   /**
    * Add any customized entries to the map.
    * 
    * @param aConfigDiff
    */
   protected void syncInternalEntries(Map aConfigDiff)
   {
      for (Iterator iter = aConfigDiff.entrySet().iterator(); iter.hasNext(); )
      {
         Map.Entry entry = (Map.Entry) iter.next();
         String pathToSetting = (String) entry.getKey();
         String customSetting = (String) entry.getValue();
         addEntryByPath(pathToSetting, customSetting);
      }
   }

   /**
    * Clear current function contexts from the container and then reload them from the map.
    */
   protected void resetFunctionContexts()
   {
      clearFunctionContexts();
      Map functionContexts = getMapEntry(FUNCTION_CONTEXTS_ENTRY);
      if (functionContexts != null)
      {
         processFunctionContexts(functionContexts);
      }
   }

   /**
    * Clears the function context container if not null 
    */
   protected void clearFunctionContexts()
   {
      if (getFunctionContextContainer() != null)
         getFunctionContextContainer().clearCustomFunctions();
   }

   /**
    * @see org.activebpel.rt.bpel.config.AeDefaultEngineConfiguration#update()
    */
   public void update()
   {
      try
      {
         Map customSettings = extractConfigDif();
         updateConfigData(customSettings);
      }
      catch (AeException se)
      {
         se.logError();
      }
      super.update();
   }

   /**
    * Extract the diff.
    */
   protected Map extractConfigDif()
   {
      Map source = getOriginalParams();
      Map target = getEntries();
      return AeConfigDifUtil.compare(source, target);
   }

   /**
    * @see org.activebpel.rt.config.AeConfiguration#setEntries(java.util.Map)
    */
   protected void setEntries(Map aEntries)
   {
      super.setEntries(aEntries);

      // keep a clean copy of the original entries
      if (getOriginalParams() == null)
      {
         mOriginalParams = new HashMap();
         copy(getEntries(), mOriginalParams);
      }
   }

   /**
    * Utility copy method. Recurses into the source map if the value itself is a <code>Map</code>
    * 
    * @param aSource
    * @param aTarget
    */
   protected void copy(Map aSource, Map aTarget)
   {
      AeConfigurationUtil.copyEntries(aSource, aTarget);
   }

   /**
    * Accessor for original params. These remain "untouched" and are used as a reference to determine what has
    * changed during updates.
    */
   protected Map getOriginalParams()
   {
      return mOriginalParams;
   }

   /**
    * Subclasses must implement this method when
    * 
    * @param aCustomSettings
    * @throws AeException
    */
   protected abstract void updateConfigData(Map aCustomSettings) throws AeException;
}
