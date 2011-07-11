// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/provider/AeIdentityFileConfig.java,v 1.2 2007/06/14 14:59:45 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.provider;

import java.util.Map;

import org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig;
import org.activebpel.rt.config.AeConfigurationUtil;
import org.activebpel.rt.identity.AeIdentityConfig;
import org.activebpel.rt.util.AeUtil;

/**
 * Provides extension to base config for file type identity services configurations.
 */
public class AeIdentityFileConfig extends AeIdentityConfig
{
   /** Config key name to the file location path of the identity file.  */
   public static final String FILE_NAME_KEY = "filename"; //$NON-NLS-1$
   
   /** Config value for ldif file provider.  */
   public static final String LDIF_PROVIDER = "org.activebpel.rt.identity.provider.ldap.AeLdifIdentitySearchProvider"; //$NON-NLS-1$
   
   /** Config value for tomcat file provider.  */
   public static final String TOMCAT_PROVIDER = "org.activebpel.rt.identity.provider.tomcat.AeTomcatUsersIdentityProvider"; //$NON-NLS-1$
   
   /**
    * Gets the config from the engine config
    * @param aEngineConfig
    */
   public static AeIdentityFileConfig getFileConfigFromConfig(IAeUpdatableEngineConfig aEngineConfig)
   {
      Map identityProviderMap = getProviderMapEntryCopy(aEngineConfig);
      AeIdentityFileConfig providerConfig = new AeIdentityFileConfig(identityProviderMap);
      boolean enabled = AeUtil.toBoolean( (String) getIdentityServiceManagerMapEntry(aEngineConfig).get("enabled") ); //$NON-NLS-1$
      providerConfig.setEnabled(enabled);
      return providerConfig;
   }

   /**
    * @param aProviderConfig
    */
   protected AeIdentityFileConfig(Map aProviderConfig)
   {
      super(aProviderConfig);
   }
   
   /**
    * Overrides method to check that the filename is set and returns false if it isn't. 
    * @see org.activebpel.rt.identity.AeIdentityConfig#isConfigured()
    */
   public boolean isConfigured()
   {
      return AeUtil.notNullOrEmpty(getFileName());
   }

   /**
    * Returns true if the ldif provider is the configuration setting.
    */
   public boolean isLdif()
   {
      return LDIF_PROVIDER.equals(getMap().get(AeConfigurationUtil.CLASS_ENTRY));
   }

   /**
    * Returns true if the tomcat provider is the configuration setting.
    */
   public boolean isTomcat()
   {
      return TOMCAT_PROVIDER.equals(getMap().get(AeConfigurationUtil.CLASS_ENTRY));
   }

   /**
    * @return the fileName
    */
   public String getFileName()
   {
      return AeUtil.getSafeString( (String) getMap().get(FILE_NAME_KEY) ).trim();
   }

   /**
    * @param aFileName the fileName to set
    */
   public void setFileName(String aFileName)
   {
      getMap().put(FILE_NAME_KEY, AeUtil.getSafeString(aFileName).trim() );
   }
}
