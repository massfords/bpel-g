// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/AeRecoveredItemsSet.java,v 1.5 2007/11/15 21:06:53 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem;

/**
 * Implements a set of recovered items representing alarm and queue manager
 * requests. Extends {@link java.util.LinkedHashMap} to preserve order of
 * insertion for {@link #getRecoveredItems()}.
 */
public class AeRecoveredItemsSet extends LinkedHashMap implements IAeRecoveredItemsSet
{
   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.IAeRecoveredItemsSet#addRecoveredItem(org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem)
    */
   public void addRecoveredItem(IAeRecoveredItem aRecoveredItem) throws AeRecoveryConflictingRequestException
   {
      if (containsKey(aRecoveredItem))
      {
         throw new AeRecoveryConflictingRequestException();
      }

      put(aRecoveredItem, aRecoveredItem);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.IAeRecoveredItemsSet#getRecoveredItems()
    */
   public List getRecoveredItems()
   {
      return new LinkedList(this.values());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.IAeRecoveredItemsSet#removeRecoveredItem(org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem)
    */
   public IAeRecoveredItem removeRecoveredItem(IAeRecoveredItem aItem)
   {
      return (IAeRecoveredItem) remove(aItem);
   }
}
