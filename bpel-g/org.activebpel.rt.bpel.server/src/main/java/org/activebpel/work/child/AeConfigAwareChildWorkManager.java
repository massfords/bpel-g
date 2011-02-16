// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/child/AeConfigAwareChildWorkManager.java,v 1.1 2007/06/20 19:40:06 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work.child;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.config.IAeConfigChangeListener;
import org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

/**
 * Extends {@link AeChildWorkManager} to update maximum work count from
 * configuration whenever configuration changes.
 */
public class AeConfigAwareChildWorkManager extends AeChildWorkManager implements
        IAeConfigChangeListener {
    /** Path to max. work count entry in engine configuration. */
    private String mConfigMaxWorkCountPath;

    /**
     * Returns the configuration value for the maximum number of work items to
     * schedule from this work manager to its parent.
     * 
     * @param aConfig
     * @return a <code>Number</code> representing the configuration value or
     *         <code>null</code> if the configuration value cannot be found or
     *         parsed
     */
    protected Number getConfigMaxWorkCount(IAeUpdatableEngineConfig aConfig) {
        Number result = null;

        // Get the config entry.
        Object entry = aConfig.getEntryByPath(getConfigMaxWorkCountPath());

        if (entry instanceof String) {
            try {
                result = Integer.valueOf((String) entry);
            } catch (Exception e) {
                AeException.logError(e);
            }
        }

        return result;
    }

    /**
     * @return path to entry in engine configuration
     */
    public String getConfigMaxWorkCountPath() {
        return mConfigMaxWorkCountPath;
    }

    public void setConfigMaxWorkCountPath(String aPath) {
        mConfigMaxWorkCountPath = aPath;
    }

    public void init() {
        IAeUpdatableEngineConfig config = AeEngineFactory.getEngineConfig().getUpdatableEngineConfig();
        config.addConfigChangeListener(this);
        updateConfig(config);
    }

    /**
     * @see org.activebpel.rt.bpel.config.IAeConfigChangeListener#updateConfig(org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig)
     */
    public void updateConfig(IAeUpdatableEngineConfig aConfig) {
        Number maxWorkCount = getConfigMaxWorkCount(aConfig);
        if (maxWorkCount != null) {
            // Set the new maximum work count.
            setMaxWorkCount(maxWorkCount.intValue());
        }
    }
}
