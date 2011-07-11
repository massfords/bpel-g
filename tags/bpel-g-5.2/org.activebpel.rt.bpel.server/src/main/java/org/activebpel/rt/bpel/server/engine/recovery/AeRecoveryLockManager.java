// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/AeRecoveryLockManager.java,v 1.1 2005/07/12 00:27:00 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.AeManagerAdapter;
import org.activebpel.rt.bpel.impl.IAeLockManager;

/**
 * Implements a lock manager for recovery.
 */
public class AeRecoveryLockManager extends AeManagerAdapter implements IAeLockManager
{
   /** Dummy "do nothing" lock. */
   private final IAeLock mDummyLock = new IAeLock()
   {
      public QName getQName()
      {
         return null;
      }

      public int getLockId()
      {
         return 0;
      }
   };

   /**
    * Constructs a lock manager for recovery.
    */
   public AeRecoveryLockManager()
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeLockManager#acquireLock(javax.xml.namespace.QName, int, int)
    */
   public IAeLock acquireLock(QName aQName, int aLockId, int aTimeout) throws AeBusinessProcessException
   {
      return mDummyLock;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeLockManager#releaseLock(org.activebpel.rt.bpel.impl.IAeLockManager.IAeLock)
    */
   public void releaseLock(IAeLock aLock)
   {
   }
}
