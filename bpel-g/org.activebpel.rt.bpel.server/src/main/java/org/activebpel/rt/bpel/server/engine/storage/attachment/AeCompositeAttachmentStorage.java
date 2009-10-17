// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/attachment/AeCompositeAttachmentStorage.java,v 1.3 2007/07/26 21:10:19 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.attachment;

import java.io.InputStream;
import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.deploy.AeProcessDeploymentFactory;
import org.activebpel.rt.bpel.server.deploy.AeProcessPersistenceType;

/**
 * Implements a composite attachment storage that chooses between a local
 * storage implementation or a remote storage implementation depending on
 * whether a process is persistent or not.
 */
public class AeCompositeAttachmentStorage implements IAeAttachmentStorage
{
   /** The remote attachment storage implementation. */
   private final IAeAttachmentStorage mRemoteStorage;
   
   /** The local attachment storage implementation. */
   private final IAeAttachmentStorage mLocalStorage;
   
   /**
    * Constructs a composite attachment storage with the given local attachment
    * storage implementation and remote attachment storage implementation.
    * @param aRemoteStorage
    * @param aLocalStorage
    */
   public AeCompositeAttachmentStorage(IAeAttachmentStorage aRemoteStorage, IAeAttachmentStorage aLocalStorage)
   {
      mRemoteStorage = aRemoteStorage;
      mLocalStorage = aLocalStorage;
   }

   /**
    * Returns the remote attachment storage implementation.
    */
   protected IAeAttachmentStorage getRemoteStorage()
   {
      return mRemoteStorage;
   }

   /**
    * Returns the local attachment storage implementation.
    */
   protected IAeAttachmentStorage getLocalStorage()
   {
      return mLocalStorage;
   }

   /**
    * Returns <code>true</code> if and only if the given process plan is for a
    * persistent process.
    *
    * @param aPlan
    */
   protected boolean isPersistent(IAeProcessPlan aPlan)
   {
      boolean persistent = true;

      if (aPlan != null)
      {
         IAeProcessDeployment deployment = AeProcessDeploymentFactory.getDeploymentForPlan(aPlan);
         
         persistent = deployment.getPersistenceType() != AeProcessPersistenceType.NONE;
      }
      
      return persistent;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#associateProcess(long, long)
    */
   public void associateProcess(long aGroupId, long aProcessId) throws AeBusinessProcessException
   {
      if (aGroupId > 0)
      {
         getRemoteStorage().associateProcess(aGroupId, aProcessId);
      }
      else
      {
         getLocalStorage().associateProcess(-aGroupId, aProcessId);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#cleanup()
    */
   public void cleanup() throws AeBusinessProcessException
   {
      try
      {
         getRemoteStorage().cleanup();
      }
      finally
      {
         getLocalStorage().cleanup();
      }
   }

   /**
    * Overrides method to create the attachment group remotely or locally
    * depending on whether the process is persistent or not. Returns a negative
    * group id if the attachment group is stored locally.
    * 
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#createAttachmentGroup(org.activebpel.rt.bpel.impl.IAeProcessPlan)
    */
   public long createAttachmentGroup(IAeProcessPlan aPlan) throws AeBusinessProcessException
   {
      long groupId;
      
      if (isPersistent(aPlan))
      {
         groupId = getRemoteStorage().createAttachmentGroup(aPlan);
      }
      else
      {
         groupId = getLocalStorage().createAttachmentGroup(aPlan);

         // Mark local attachment groups with negative ids.
         groupId = -groupId;
      }

      return groupId;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#getContent(long)
    */
   public InputStream getContent(long aAttachmentId) throws AeBusinessProcessException
   {
      InputStream content;
      
      if (aAttachmentId > 0)
      {
         content = getRemoteStorage().getContent(aAttachmentId);
      }
      else
      {
         content = getLocalStorage().getContent(-aAttachmentId);
      }

      return content;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#getHeaders(long)
    */
   public Map getHeaders(long aAttachmentId) throws AeBusinessProcessException
   {
      Map headers;
      
      if (aAttachmentId > 0)
      {
         headers = getRemoteStorage().getHeaders(aAttachmentId);
      }
      else
      {
         headers = getLocalStorage().getHeaders(-aAttachmentId);
      }

      return headers;
   }

   /**
    * Overrides method to store the given attachment remotely or locally
    * depending on whether the process is persistent or not. Returns a negative
    * attachment id if the attachment is stored locally.
    * 
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#storeAttachment(long, java.io.InputStream, java.util.Map)
    */
   public long storeAttachment(long aGroupId, InputStream aInputStream, Map aHeaders) throws AeBusinessProcessException
   {
      long attachmentId;
      
      if (aGroupId > 0)
      {
         attachmentId = getRemoteStorage().storeAttachment(aGroupId, aInputStream, aHeaders);
      }
      else
      {
         attachmentId = getLocalStorage().storeAttachment(-aGroupId, aInputStream, aHeaders);

         // Mark local attachment with negative ids.
         attachmentId = -attachmentId;
      }

      return attachmentId;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#removeAttachment(long)
    */
   public void removeAttachment(long aAttachmentId) throws AeBusinessProcessException
   {
      if (aAttachmentId > 0)
      {
         getRemoteStorage().removeAttachment(aAttachmentId);
      }
      else
      {
         getLocalStorage().removeAttachment(-aAttachmentId);
      }
   }
}