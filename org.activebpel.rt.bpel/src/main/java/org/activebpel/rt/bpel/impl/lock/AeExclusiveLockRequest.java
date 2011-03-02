// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/lock/AeExclusiveLockRequest.java,v 1.2 2004/07/08 13:10:00 ewittmann Exp $
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
 * Extension of the base lock request that will only acquire the lock if it's
 * the only lock holder present.
 */
class AeExclusiveLockRequest extends AeLockRequest
{
   /**
    * Creates a lock request with all of its required data.
    * @param aVariablesToLock - The set of variable paths that we want to lock
    * @param aOwner - The path of the object that will be the owner of the lock
    * @param aCallback - The callback that gets used if we can't fulfill the request immediately
    */
   public AeExclusiveLockRequest(AeVariableLocker aVariableLocker, Set aVariablesToLock, String aOwner, IAeVariableLockCallback aCallback)
   {
      super(aVariableLocker, aVariablesToLock, aOwner, aCallback);
   }

   /**
    * Returns true if all of the requested variables can be locked immediately
    * and exclusively
    */
   protected boolean canLock()
   {
      boolean immediatelyAvailable = true;
      for (Iterator iter = mVariablesToLock.iterator(); iter.hasNext() && immediatelyAvailable; )
      {
         String path = (String) iter.next();
         if (mVariableLocker.isLocked(path))
         {
            immediatelyAvailable = false;
         }
      }
      return immediatelyAvailable;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.lock.AeLockRequest#isExclusiveRequest()
    */
   public boolean isExclusiveRequest()
   {
      return true;
   }
}
