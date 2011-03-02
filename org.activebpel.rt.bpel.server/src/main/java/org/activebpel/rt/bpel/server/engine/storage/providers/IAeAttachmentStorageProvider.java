// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/providers/IAeAttachmentStorageProvider.java,v 1.4 2007/07/26 21:10:19 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.providers;

import java.io.InputStream;
import java.util.Map;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;

/**
 * An attachment queue storage delegate. This interface defines methods that the delegating attachment storage will call in
 * order to store/read date in the underlying database.
 */
public interface IAeAttachmentStorageProvider extends IAeStorageProvider
{
   /**
    * Stores an attachment item (headers and content) in the database and returns the stored attachment id.
    *
    * @param aAttachmentGroupId
    * @param aInputStream
    * @param aHeaders
    * @return attachment item token number
    * @throws AeStorageException
    */
   public long storeAttachment(long aAttachmentGroupId, InputStream aInputStream, Map aHeaders) throws AeStorageException;
   
   /**
    * Loads an attachment's headers from the database.
    * 
    * @param aAttachmentId attachment id
    * @return Map of header name/value pairs
    * @throws AeStorageException
    */
   public Map getHeaders(long aAttachmentId) throws AeStorageException;
   
   /**
    * Returns a new attachment group id.
    */
   public long createAttachmentGroup() throws AeStorageException;
   
   /**
    * Associates an existing attachment group with a process in the database.
    * @param aAttachmentGroupId the attachment group id to be associated with the process
    * @param aProcessId the active process instance id
    */
   public void associateProcess(long aAttachmentGroupId, long aProcessId) throws AeStorageException;
   
   /**
    * Retrieves the binary content of an attachment.
    * 
    * @param aAttachmentId the attachment id
    * @return the binary stream
    * @throws AeStorageException
    */
   public InputStream getContent(long aAttachmentId) throws AeStorageException;
   
   /**
    * Cleanup unassociated attachments.
    * 
    * @throws AeStorageException
    */
   public void cleanup() throws AeStorageException;
   
   /**
    * Removes an attachment.
    * 
    * @param aAttachmentId the attachment id
    * @throws AeStorageException
    */
   public void removeAttachment(long aAttachmentId) throws AeStorageException;
}
