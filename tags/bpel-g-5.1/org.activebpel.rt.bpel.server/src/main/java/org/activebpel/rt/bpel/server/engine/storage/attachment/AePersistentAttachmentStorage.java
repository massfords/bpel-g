// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/attachment/AePersistentAttachmentStorage.java,v 1.3 2007/07/26 21:10:19 jbik Exp $
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
import org.activebpel.rt.bpel.server.engine.storage.AeAbstractStorage;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageProvider;

/**
 * Generic attachment storage.
 */
public class AePersistentAttachmentStorage extends AeAbstractStorage implements IAeAttachmentStorage
{
   /**
    * Constructor
    * @param aProvider
    */
   public AePersistentAttachmentStorage(IAeStorageProvider aProvider)
   {
      super(aProvider);
   }

   /**
    * Convenience method to return the provider cast to a IAeAttachmentStorageProvider.
    */
   protected IAeAttachmentStorageProvider getAttachmentStorageProvider()
   {
      return (IAeAttachmentStorageProvider)getProvider();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#associateProcess(long, long)
    */
   public void associateProcess(long aAttachmentGroupId, long aProcessId) throws AeStorageException
   {
      getAttachmentStorageProvider().associateProcess(aAttachmentGroupId, aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#createAttachmentGroup(org.activebpel.rt.bpel.impl.IAeProcessPlan)
    */
   public long createAttachmentGroup(IAeProcessPlan aPlan) throws AeStorageException
   {
      return getAttachmentStorageProvider().createAttachmentGroup();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#storeAttachment(long, java.io.InputStream, java.util.Map)
    */
   public long storeAttachment(long aAttachmentGroupId, InputStream aInputStream, Map aHeaders) throws AeStorageException
   {
      return getAttachmentStorageProvider().storeAttachment(aAttachmentGroupId, aInputStream, aHeaders);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#getHeaders(long)
    */
   public Map getHeaders(long aAttachmentId) throws AeStorageException
   {
      return getAttachmentStorageProvider().getHeaders(aAttachmentId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#getContent(long)
    */
   public InputStream getContent(long aAttachmentId) throws AeStorageException
   {
      return getAttachmentStorageProvider().getContent(aAttachmentId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#cleanup()
    */
   public void cleanup() throws AeStorageException
   {
      getAttachmentStorageProvider().cleanup();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#removeAttachment(long)
    */
   public void removeAttachment(long aAttachmentId) throws AeBusinessProcessException
   {
      getAttachmentStorageProvider().removeAttachment(aAttachmentId);
      
   }
}
