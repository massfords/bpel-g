//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/IAeRecoveryAwareManager.java,v 1.1 2008/03/11 03:09:29 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery; 

import java.util.List;

import org.activebpel.rt.bpel.impl.activity.IAeImplAdapter;

/**
 * Interface for managers that provide special behavior during recovery. During 
 * recovery of a process, the processes will run in a special recovery engine.
 * This version of the engine will walk the custom managers map and check with
 * each manager to see if it implements this interface. If it does, that manager
 * will be replaced with its recovery version returned from this interface's
 * method. 
 */
public interface IAeRecoveryAwareManager extends IAeImplAdapter
{
   /**
    * Setter for the items that have been recovered for the process
    * @param aRecoveredItemsSet
    */
   public void setRecoveredItemsSet(IAeRecoveredItemsSet aRecoveredItemsSet);

   /**
    * Setter for the journal entries that contain transmission ids. These are used
    * to restore the transmission id on activities when the go to requeue.
    * @param aInvokeTransmittedEntries
    */
   // fixme (MF-recovery) replace these two setters with an interface to provide the whole context for recovery
   public void setInvokeTransmittedEntries(List aInvokeTransmittedEntries);
}
 