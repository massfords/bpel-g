// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/lock/AeLockerDeserializer.java,v 1.4 2005/02/08 15:33:12 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.lock;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeMessages;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Implements deserialization for a variable locker.
 */
public class AeLockerDeserializer implements IAeLockerSerializationNames
{
   /** The variable locker to deserialize. */
   private final AeVariableLocker mVariableLocker;

   /** The callback to reconstruct lock requests for. */
   private final IAeVariableLockCallback mCallback;

   /**
    * Constructor.
    *
    * @param aVariableLocker The variable locker to deserialize.
    * @param aCallback The callback to reconstruct lock requests for.
    */
   public AeLockerDeserializer(AeVariableLocker aVariableLocker, IAeVariableLockCallback aCallback)
   {
      mVariableLocker = aVariableLocker;
      mCallback = aCallback;
   }

   /**
    * Deserializes the variable locker.
    *
    * @param aNode the serialized locker data
    */
   public synchronized void deserialize(Node aNode) throws AeException
   {
      // Deserialize locks.
      List locks = selectNodes(aNode, ".//" + TAG_LOCKS + "/" + TAG_LOCK); //$NON-NLS-1$ //$NON-NLS-2$

      mVariableLocker.clearLocks();

      for (Iterator i = locks.iterator(); i.hasNext(); )
      {
         Element lock = (Element) i.next();

         deserializeLock(lock);
      }

      // Deserialize requests.
      List requests = selectNodes(aNode, ".//" + TAG_REQUESTS + "/" + TAG_REQUEST); //$NON-NLS-1$ //$NON-NLS-2$

      mVariableLocker.clearRequests();

      for (Iterator i = requests.iterator(); i.hasNext(); )
      {
         Element request = (Element) i.next();

         deserializeLockRequest(request);
      }
   }

   /**
    * Deserializes a lock.
    *
    * @param aLock The lock element containing the serialized lock data.
    * @throws AeException
    */
   private void deserializeLock(Element aLock) throws AeException
   {
      String variablePath = aLock.getAttribute(ATTR_VARIABLEPATH);
      boolean exclusive = "true".equals(aLock.getAttribute(ATTR_EXCLUSIVE)); //$NON-NLS-1$
      List owners = selectNodes(aLock, TAG_OWNER);

      for (Iterator i = owners.iterator(); i.hasNext(); )
      {
         Element owner = (Element) i.next();
         String ownerPath = owner.getAttribute(ATTR_OWNERPATH);

         mVariableLocker.addLockHolder(variablePath, ownerPath, exclusive);
      }
   }

   /**
    * Deserializes a lock request.
    *
    * @param aRequest The element containing the serialized lock request.
    * @throws AeException
    */
   private void deserializeLockRequest(Element aRequest) throws AeException
   {
      String ownerPath = aRequest.getAttribute(ATTR_OWNERPATH);
      boolean exclusive = "true".equals(aRequest.getAttribute(ATTR_EXCLUSIVE)); //$NON-NLS-1$

      List variables = selectNodes(aRequest, TAG_VARIABLE);
      Set variablePaths = new HashSet();

      for (Iterator i = variables.iterator(); i.hasNext(); )
      {
         Element variable = (Element) i.next();
         String variablePath = variable.getAttribute(ATTR_VARIABLEPATH);
         variablePaths.add(variablePath);
      }

      AeLockRequest lockRequest;

      if (exclusive)
      {
         lockRequest = new AeExclusiveLockRequest(mVariableLocker, variablePaths, ownerPath, mCallback);
      }
      else
      {
         lockRequest = new AeSharedLockRequest(mVariableLocker, variablePaths, ownerPath, mCallback);
      }

      mVariableLocker.addLockRequest(ownerPath, lockRequest);
   }

   /**
    * Select nodes by XPath.
    *
    * @param aNode The node to select from.
    * @param aPath The XPath expression.
    * @return List The list of matching nodes.
    * @throws AeException
    */
   private static List selectNodes(Node aNode, String aPath) throws AeException
   {
      try
      {
         XPath xpath = new DOMXPath(aPath);
         return xpath.selectNodes(aNode);
      }
      catch (JaxenException e)
      {
         throw new AeException(AeMessages.getString("AeLockerDeserializer.ERROR_6") + aPath, e); //$NON-NLS-1$
      }
   }
}
