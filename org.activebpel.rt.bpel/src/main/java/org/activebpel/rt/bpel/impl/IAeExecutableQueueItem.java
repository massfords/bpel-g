// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeExecutableQueueItem.java,v 1.3 2005/01/28 20:22:10 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;

/**
 * Interface for items that can be executed by <code>AeExecutableQueue</code>.
 */
public interface IAeExecutableQueueItem
{
   /**
    * All implementations provide an execute method, which performs actual work.
    */
   public void execute() throws AeBusinessProcessException;

   /**
    * Returns the item's location path.
    */
   public String getLocationPath();

   /**
    * Returns the current state
    */
   public AeBpelState getState();

   /**
    * Indicates that the object has completed with a fault. Sets the objects state
    * to faulted and propagates the fault through the process.
    * @param aFault
    * @throws AeBusinessProcessException
    */
   public void objectCompletedWithFault(IAeFault aFault) throws AeBusinessProcessException;

   /**
    * Setter for the state.
    * @param aState
    */
   public void setState(AeBpelState aState) throws AeBusinessProcessException;
}
