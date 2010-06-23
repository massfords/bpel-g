// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/lock/AeLockerSerializer.java,v 1.6 2005/02/01 19:53:00 twinkler Exp $
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

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

/**
 * Implements variable locker serialization.
 */
class AeLockerSerializer implements IAeLockerSerializationNames
{
   /** The variable locker to serialize. */
   private final AeVariableLocker mVariableLocker;

   /** The callback to serialize lock requests for. */
   private final IAeVariableLockCallback mCallback;

   /**
    * Constructor.
    *
    * @param aVariableLocker The variable locker to serialize.
    * @param aCallback The callback to serialize lock requests for.
    */
   public AeLockerSerializer(AeVariableLocker aVariableLocker, IAeVariableLockCallback aCallback)
   {
      mVariableLocker = aVariableLocker;
      mCallback = aCallback;
   }

   /**
    * Serializes the variable locker.
    *
    * @return DocumentFragment the serialization for the variable locker
    * @throws AeException
    */
   public DocumentFragment serialize() throws AeException
   {
      Document doc = AeXmlUtil.newDocument();
      DocumentFragment result = doc.createDocumentFragment();

      // Serialize locks.
      Element locks = doc.createElement(TAG_LOCKS);
      result.appendChild(locks);

      for (Iterator i = mVariableLocker.getLockedPaths().iterator(); i.hasNext(); )
      {
         String variablePath = (String) i.next();

         serializeLock(variablePath, locks);
      }

      // Serialize lock requests.
      Element requests = doc.createElement(TAG_REQUESTS);
      result.appendChild(requests);

      for (Iterator i = mVariableLocker.getLockRequests().iterator(); i.hasNext(); )
      {
         AeLockRequest lockRequest = (AeLockRequest) i.next();

         // AeVariableLocker#setLockerData can reconstruct lock requests for
         // only one callback, so serialize requests only for one callback.
         if (lockRequest.getCallback() == mCallback)
         {
            serializeLockRequest(lockRequest, requests);
         }
      }

      return result;
   }

   /**
    * Serializes a lock.
    *
    * @param aVariablePath The variable path of the lock to serialize.
    * @param aLocks The parent element for locks.
    */
   private void serializeLock(String aVariablePath, Element aLocks)
   {
      AeLockHolder lockHolder = (AeLockHolder) mVariableLocker.getLockHolder(aVariablePath);

      Document doc = aLocks.getOwnerDocument();
      Element lock = doc.createElement(TAG_LOCK);
      aLocks.appendChild(lock);

      lock.setAttribute(ATTR_VARIABLEPATH, aVariablePath);
      lock.setAttribute(ATTR_EXCLUSIVE   , "" + lockHolder.isExclusive()); //$NON-NLS-1$

      for (Iterator i = lockHolder.getOwners().iterator(); i.hasNext(); )
      {
         Element owner = doc.createElement(TAG_OWNER);
         lock.appendChild(owner);

         String ownerPath = (String) i.next();
         owner.setAttribute(ATTR_OWNERPATH, ownerPath);
      }
   }

   /**
    * Serializes a lock request.
    *
    * @param aLockRequest The lock request to serialize.
    * @param aRequests The parent element for requests.
    */
   private void serializeLockRequest(AeLockRequest aLockRequest, Element aRequests)
   {
      Document doc = aRequests.getOwnerDocument();
      Element request = doc.createElement(TAG_REQUEST);
      aRequests.appendChild(request);

      request.setAttribute(ATTR_OWNERPATH, aLockRequest.getOwner());
      request.setAttribute(ATTR_EXCLUSIVE, "" + aLockRequest.isExclusiveRequest()); //$NON-NLS-1$

      for (Iterator i = aLockRequest.getVariablesToLock().iterator(); i.hasNext(); )
      {
         Element variable = doc.createElement(TAG_VARIABLE);
         request.appendChild(variable);

         String variablePath = (String) i.next();
         variable.setAttribute(ATTR_VARIABLEPATH, variablePath);
      }
   }
}
