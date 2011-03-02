//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/AeIdentityConfig.java,v 1.4 2008/02/17 21:54:48 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity;

import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig;
import org.activebpel.rt.config.AeConfigurationUtil;
import org.activebpel.rt.identity.search.AeIdentitySearch;
import org.activebpel.rt.identity.search.IAeIdentitySearch;
import org.activebpel.rt.util.AeUtil;

/**
 * Convinience wrapper around the engine identity configuration.
 */
public class AeIdentityConfig
{
   /** Configuration entry key name for Identity Service Manager root node (search service). */
   public static String IDENTITY_SERVICE_MANAGER_ENTRY = "IdentityServiceManager"; //$NON-NLS-1$

   /** Configuration entry key name for Identity Service search implementation. */
   public static String IDENTITY_SEARCH_ENTRY = "IdentitySearch"; //$NON-NLS-1$

   /** Configuration entry key name for search Identity Service provider configuration. */
   public static String IDENTITY_PROVIDER_ENTRY = "Provider"; //$NON-NLS-1$

   /** Configuration entry key name for search Identity Service provider enablement. */
   public static String IDENTITY_PROVIDER_ENABLED = "enabled"; //$NON-NLS-1$

   /** Parent entry containing identity service's provider config entry settings. */
   private Map mMap;
   
   /** Service manager enabled state. */
   private boolean mEnabled;

   /**
    * Gets a copy of the identity service config from the engine config
    * @param aEngineConfig
    */
   public static AeIdentityConfig getFromConfig(IAeUpdatableEngineConfig aEngineConfig)
   {
      Map identityProviderMap = getProviderMapEntryCopy(aEngineConfig);
      AeIdentityConfig providerConfig = new AeIdentityConfig(identityProviderMap);
      boolean enabled = AeUtil.toBoolean( (String) getIdentityServiceManagerMapEntry(aEngineConfig).get(IDENTITY_PROVIDER_ENABLED) );
      providerConfig.setEnabled(enabled);
      return providerConfig;
   }

   /**
    * Sets the config on the engine config
    * @param aConfig
    * @param aEngineConfig
    */
   public static void setOnConfig(AeIdentityConfig aConfig, IAeUpdatableEngineConfig aEngineConfig)
   {
      Map identityProviderMap = getProviderMapEntry(aEngineConfig);
      identityProviderMap.clear();
      identityProviderMap.putAll(aConfig.getMap());
      getIdentityServiceManagerMapEntry(aEngineConfig).put(IDENTITY_PROVIDER_ENABLED, String.valueOf(aConfig.isEnabled()) ) ;
   }

   /**
    * Tests the current configuration. Throws if the settings are incorrect.
    * @param aConfig
    * @param aPrincipalName username to test the current configuration. 
    */
   public static void testConfiguration(AeIdentityConfig aConfig, String aPrincipalName) throws AeException
   {
      try
      {
         Map identitySearchMap = new HashMap();
         identitySearchMap.put("Class", AeIdentitySearch.class.getName()); //$NON-NLS-1$
         identitySearchMap.put(IDENTITY_PROVIDER_ENTRY, aConfig.getMap());
         IAeIdentitySearch searcher = AeIdentityFactory.createIdentitySearch(identitySearchMap);
         // excercise (1) find User by Principal; (2) Find Groups where User DN is member.
         IAeIdentityRole roles[] = searcher.findRolesByPrincipal(aPrincipalName);
         if (roles.length == 0)
         {
            throw new AeException(AeMessages.format("AeIdentityConfig.CONFIGTEST_GROUPS_NOT_FOUND", aPrincipalName));  //$NON-NLS-1$
         }
         if (AeUtil.isNullOrEmpty(roles[0].getName()))
         {
            String args[] = {String.valueOf(roles.length), aPrincipalName};
            throw new AeException(AeMessages.format("AeIdentityConfig.CONFIGTEST_GROUPNAME_NOT_FOUND", args));  //$NON-NLS-1$
         }
         // excercise (1) Find Group by group name (2) Get group's member/unique member attr (3) Look up user given user DN.
         IAeIdentity ids[] = searcher.findIdentitiesByRole(roles[0].getName());
         if (ids.length == 0)
         {
            String args[] = {aPrincipalName, roles[0].getName()};
            throw new AeException(AeMessages.format("AeIdentityConfig.CONFIGTEST_USER_NOT_FOUND", args));  //$NON-NLS-1$
         }
      }
      catch(AeIdentityException ie)
      {
         // pass the detailed message for identity service related (e.g. LDAP codes) errors.
         throw new AeException(ie.getDetailedMessage());
      }
      catch(AeException aex)
      {
         throw aex;
      }
      catch(Throwable t)
      {
         throw new AeException(t.getMessage());
      }      
   }
   
   /**
    * Constructs the config withe the passed config map.
    * @param aProviderConfig
    */
   protected AeIdentityConfig(Map aProviderConfig)
   {
      setMap(aProviderConfig);
   }

   /**
    * @return the config
    */
   public Map getMap()
   {
      return mMap;
   }

   /**
    * Returns true, derived configs can override this method.
    * @return true if configured
    */
   public boolean isConfigured()
   {
      return true;
   }

   /**
    * @return true if enabled.
    */
   public boolean isEnabled()
   {
      return mEnabled;
   }

   /**
    * Sets the enabled flag.
    * @param aEnabled
    */
   public void setEnabled(boolean aEnabled)
   {
      mEnabled = aEnabled; 
   }

   /**
    * @param aConfig the config to set
    */
   public void setMap(Map aConfig)
   {
      mMap = aConfig;
   }

   /**
    * Returns map containing identity service manager settings.
    */
   protected static Map getIdentityServiceManagerMapEntry(IAeUpdatableEngineConfig aEngineConfig)
   {
      // Map path: //CustomManagers/IdentityServiceManager
      Map customManagersMap = aEngineConfig.getMapEntry(IAeUpdatableEngineConfig.CUSTOM_MANAGERS_ENTRY, true);
      Map identityServiceMap = aEngineConfig.getMapEntry(IDENTITY_SERVICE_MANAGER_ENTRY, true, customManagersMap);
      return identityServiceMap;
   }

   /**
    * Returns a deep copy of  the config entry map containing provider settings.
    * @return copy of the ldap provider setting.
    */
   protected static Map getProviderMapEntryCopy(IAeUpdatableEngineConfig aEngineConfig)
   {
      Map identityProviderMap = getProviderMapEntry(aEngineConfig);
      Map tmp = new HashMap();
      AeConfigurationUtil.copyEntries(identityProviderMap, tmp);
      return tmp;
   }   
   
   /**
    * Returns map containing provider settings.
    */
   protected static Map getProviderMapEntry(IAeUpdatableEngineConfig aEngineConfig)
   {
      // Map path: //CustomManagers/IdentityServiceManager/IdentitySearch/Provider
      Map identityServiceMap = getIdentityServiceManagerMapEntry(aEngineConfig);
      Map identitySearchMap = aEngineConfig.getMapEntry(IDENTITY_SEARCH_ENTRY, true, identityServiceMap);
      Map identityProviderMap = aEngineConfig.getMapEntry(IDENTITY_PROVIDER_ENTRY, true, identitySearchMap);
      return identityProviderMap;
   }
   
   /**
    * Sets the current provider.
    */
   public void setProvider(String aProviderClass)
   {
      getMap().put(AeConfigurationUtil.CLASS_ENTRY, aProviderClass);
   }
}
