// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/IAeRecoveryProcessManager.java,v 1.2 2007/11/15 21:06:53 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery;

import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.IAeProcessManager;

/**
 * Extends {@link org.activebpel.rt.bpel.impl.IAeProcessManager} to define the
 * interface for a process manager used by an instance of {@link
 * org.activebpel.rt.bpel.server.engine.recovery.AeRecoveryEngine} during
 * recovery.
 */
public interface IAeRecoveryProcessManager extends IAeProcessManager
{
   /**
    * Sets the process that is being recovered.
    */
   public void setRecoveryProcess(IAeBusinessProcess aRecoveryProcess);

   /**
    * Returns true if the process manager processed a create instance inbound receive
    */
   public boolean wasCreateInstance();

   /**
    * Clears the create instance flag from the process manager. Called in between
    * the replaying of the journal items to determine if one was the create instance
    * message. 
    */
   public void clearCreateInstance();
}
