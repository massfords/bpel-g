// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/IAeRecoveredItemsSet.java,v 1.5 2007/11/15 21:06:53 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery;

import java.util.List;

import org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem;

/**
 * Defines the interface for a set of alarm and queue manager items generated
 * during recoverys.
 */
public interface IAeRecoveredItemsSet
{
   /**
    * Adds the given recovered item to the set and verifies that the set did
    * not already contain a matching item.
    */
   // fixme The exception here only applies to message receivers.
   // It seems possible that we could have multiple instances of a one-way invoke recovered if it were in a loop.
   public void addRecoveredItem(IAeRecoveredItem aRecoveredItem) throws AeRecoveryConflictingRequestException;

   /**
    * Returns recovered items as a list.
    */
   public List getRecoveredItems();

   /**
    * Removes and returns the recovered item that matches the given item;
    * returns <code>null</code> if there is no matching item.
    * 
    * @param aItem
    */
   public IAeRecoveredItem removeRecoveredItem(IAeRecoveredItem aItem);
}
