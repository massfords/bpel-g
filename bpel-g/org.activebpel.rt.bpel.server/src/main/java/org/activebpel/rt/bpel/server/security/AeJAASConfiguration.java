//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/security/AeJAASConfiguration.java,v 1.1 2007/02/13 15:26:59 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.security;

import java.util.HashMap;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

/**
 * Extension of the JAAS configuration that allows us to add an additional login
 * module to the existing configuration.
 */
public class AeJAASConfiguration extends Configuration {
    /**
     * Name of entry for login module entry.
     */
    public static final String LOGIN_MODULE_ENTRY = "LoginModule"; //$NON-NLS-1$

    private String mAppName;
    private final Configuration mDelegateConfig;
    private AppConfigurationEntry[] mEntry;
    private String mLoginModule;

    /**
     * Constructor that takes the engine configuration
     *
     * @param aConfig
     */
    public AeJAASConfiguration() {
        // Get the current config as the delegate
        mDelegateConfig = Configuration.getConfiguration();
    }

    public void init() {
        mEntry = createEntries();
    }

    /**
     * Retreives the config for the application. Will check the original
     * configuration first. If not found, will return the configuration from the
     * bpel engine.
     *
     * @see javax.security.auth.login.Configuration#getAppConfigurationEntry(java.lang.String)
     */
    public AppConfigurationEntry[] getAppConfigurationEntry(
            String aConfigAppName) {
        AppConfigurationEntry[] entries = null;
        try {
            // try from the original config first
            entries = getDelegate().getAppConfigurationEntry(aConfigAppName);
            if (entries == null) {
                // Return engine login module.
                entries = getEntriesFromEngineConfig(aConfigAppName);
            }
        } catch (IllegalArgumentException e) {
            // Weblogic throws a runtime exception if the
            // config is not found, rather than simply returning null
            // so we'll return the entries based the engine configuration
            if (getEntriesFromEngineConfig(aConfigAppName) != null) {
                return getEntriesFromEngineConfig(aConfigAppName);
            } else {
                throw e;
            }
        }
        return entries;
    }

    /**
     * @see javax.security.auth.login.Configuration#refresh()
     */
    public void refresh() {
        getDelegate().refresh();
    }

    /**
     * Creates configuration entries from settings in the engine config
     */
    protected AppConfigurationEntry[] createEntries() {
        if (getLoginModule() != null) {
            AppConfigurationEntry entry = new AppConfigurationEntry(
                    getLoginModule(),
                    AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                    new HashMap<String, Object>());
            return new AppConfigurationEntry[]{entry};
        } else {
            return null;
        }
    }

    /**
     * @return the original configuration
     */
    protected Configuration getDelegate() {
        return mDelegateConfig;
    }

    /**
     * @return the entries
     */
    protected AppConfigurationEntry[] getEntriesFromEngineConfig(String aAppName) {
        if (mAppName.equals(aAppName)) {
            return mEntry;
        } else {
            return null;
        }
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String aAppName) {
        mAppName = aAppName;
    }

    public String getLoginModule() {
        return mLoginModule;
    }

    public void setLoginModule(String aLoginModule) {
        mLoginModule = aLoginModule;
    }

}
