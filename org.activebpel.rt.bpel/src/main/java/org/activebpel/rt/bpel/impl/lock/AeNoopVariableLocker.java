// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/lock/AeNoopVariableLocker.java,v 1.1 2004/09/02 16:59:07 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002, 2003, 2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.lock;

import java.util.Set;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

/**
 * Noop variable locker, does nothing and is used when no serializable scopes 
 * are present.
 */
public class AeNoopVariableLocker implements IAeVariableLocker
{
   /**
    * @see org.activebpel.rt.bpel.impl.lock.IAeVariableLocker#addExclusiveLock(java.util.Set, java.lang.String, org.activebpel.rt.bpel.impl.lock.IAeVariableLockCallback)
    */
   public boolean addExclusiveLock(Set aSetOfVariablePaths, String aOwnerXPath, IAeVariableLockCallback aCallback)
   {
      return true;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.lock.IAeVariableLocker#addSharedLock(java.util.Set, java.lang.String, org.activebpel.rt.bpel.impl.lock.IAeVariableLockCallback)
    */
   public boolean addSharedLock(Set aSetOfVariablePaths, String aOwnerXPath, IAeVariableLockCallback aCallback)
   {
      return true;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.lock.IAeVariableLocker#releaseLocks(java.lang.String)
    */
   public void releaseLocks(String aOwner) throws AeBusinessProcessException
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.lock.IAeVariableLocker#getLockerData(org.activebpel.rt.bpel.impl.lock.IAeVariableLockCallback)
    */
   public DocumentFragment getLockerData(IAeVariableLockCallback aProcess) throws AeBusinessProcessException
   {
      return null;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.lock.IAeVariableLocker#setLockerData(org.w3c.dom.Node, org.activebpel.rt.bpel.impl.lock.IAeVariableLockCallback)
    */
   public void setLockerData(Node aNode, IAeVariableLockCallback aProcess) throws AeBusinessProcessException
   {
   }

}
