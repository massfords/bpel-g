// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/lock/AeSharedLockRequest.java,v 1.2 2004/07/08 13:10:00 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.lock;

import java.util.Iterator;
import java.util.Set;

/**
 * A request for a shared lock on a variable. As the name implies, multiple
 * owners are permitted to share a lock.
 */
class AeSharedLockRequest extends AeLockRequest
{
   /**
    * Creates a lock request with all of its required data.
    * @param aVariablesToLock - The set of variable paths that we want to lock
    * @param aOwner - The path of the object that will be the owner of the lock
    * @param aCallback - The callback that gets used if we can't fulfill the request immediately
    */
   public AeSharedLockRequest(AeVariableLocker aVariableLocker, Set aVariablesToLock, String aOwner, IAeVariableLockCallback aCallback)
   {
      super(aVariableLocker, aVariablesToLock, aOwner, aCallback);
   }

   /**
    * Walks all of the variable paths that we're trying to add a lock to and
    * returns true if they're all able to be locked. Fails on the first one
    * that cannot be locked.
    * @see org.activebpel.rt.bpel.impl.lock.AeLockRequest#canLock()
    */
   protected boolean canLock()
   {
      boolean immediatelyAvailable = true;

      for (Iterator iter = mVariablesToLock.iterator(); iter.hasNext() && immediatelyAvailable; )
      {
         String variablePath = (String) iter.next();
         AeLockHolder lockHolder = mVariableLocker.getLockHolder(variablePath);
         immediatelyAvailable = canAdd(lockHolder);
      }
      return immediatelyAvailable;
   }

   /** conv method that checks to see if we can add to the lock */
   private boolean canAdd(AeLockHolder aLockHolder)
   {
      return aLockHolder == null || aLockHolder.canAdd();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.lock.AeLockRequest#isExclusiveRequest()
    */
   public boolean isExclusiveRequest()
   {
      return false;
   }
}
