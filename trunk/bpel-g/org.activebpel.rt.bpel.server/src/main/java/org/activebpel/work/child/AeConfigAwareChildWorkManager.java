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

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import org.activebpel.rt.bpel.AePreferences;

/**
 * Extends {@link AeChildWorkManager} to update maximum work count from
 * configuration whenever configuration changes.
 */
public class AeConfigAwareChildWorkManager extends AeChildWorkManager implements PreferenceChangeListener {

    public void init() {
        AePreferences.childWorkManagers().addPreferenceChangeListener(this);
        setMaxWorkCount(AePreferences.getAlarmMaxCount());
    }

    @Override
    public void preferenceChange(PreferenceChangeEvent aEvt) {
        setMaxWorkCount(AePreferences.getAlarmMaxCount());
    }
}
