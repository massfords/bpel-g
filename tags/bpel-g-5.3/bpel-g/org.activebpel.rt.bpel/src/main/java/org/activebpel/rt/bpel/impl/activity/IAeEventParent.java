// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/IAeEventParent.java,v 1.3 2004/07/08 13:09:59 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.activity.support.AeOnAlarm;
import org.activebpel.rt.bpel.impl.activity.support.AeOnMessage;

/**
 * Interface for <code>pick</code>s and <code>eventHandlers</code>. Provides methods
 * for adding messages and alarms into the parent.
 */
public interface IAeEventParent extends IAeBpelObject
{
   /**
    * Adds the alarm to the <code>pick</code> or <code>eventHandlers</code>
    * @param aAlarm
    */
   public void addAlarm(AeOnAlarm aAlarm);

   /**
    * Adds the message to the <code>pick</code> or <code>eventHandlers</code>
    * @param aMessage
    */
   public void addMessage(AeOnMessage aMessage);
   
   /**
    * Callback from a child when it becomes active.
    * @param aChild the child becoming active.
    */
   public void childActive(IAeBpelObject aChild) throws AeBusinessProcessException;
}
