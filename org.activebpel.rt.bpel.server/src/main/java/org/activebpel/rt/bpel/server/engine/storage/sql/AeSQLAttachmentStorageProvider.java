// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeSQLAttachmentStorageProvider.java,v 1.8 2008/02/17 21:38:46 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.engine.storage.AeCounter;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.attachment.AeAttachmentItemEntry;
import org.activebpel.rt.bpel.server.engine.storage.attachment.AePairSerializer;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.sql.handlers.AeAttachmentItemResultSetHandler;
import org.activebpel.rt.util.AeBlobInputStream;
import org.activebpel.rt.util.AeCloser;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 * This is a SQL Attachment Storage provider (an implementation of IAeQueueStorageDelegate).
 */
public class AeSQLAttachmentStorageProvider extends AeAbstractSQLStorageProvider implements
      IAeAttachmentStorageProvider
{
   /** The SQL statement prefix for all SQL statements used in this class. */
   public static final String SQLSTATEMENT_PREFIX = "AttachmentStorage."; //$NON-NLS-1$

   /** The SQL statement key for the 'InsertAttachmentGroup lock' statement. */
   protected static final String SQL_ATTACHMENT_GROUP = "InsertAttachmentGroup"; //$NON-NLS-1$

   /** The SQL statement key for the 'AttachProcess' statement. */
   protected static final String SQL_PROCESS_ATTACHMENT_GROUP = "AttachProcess"; //$NON-NLS-1$

   /** The SQL statement key for the 'InsertAttachment' statement. */
   protected static final String SQL_STORE_ATTACHMENT = "InsertAttachment"; //$NON-NLS-1$

   /** The SQL statement key for quering the attachment contents 'QueryAttachmentContents' statement. */
   protected static final String SELECT_ATTACHMENT_CONTENTS = "QueryAttachmentContents"; //$NON-NLS-1$

   /** The SQL statement key for quering the attachment item 'QueryAttachmentHeaders' statement. */
   protected static final String SELECT_ATTACHMENT_HEADERS = "QueryAttachmentHeaders"; //$NON-NLS-1$

   /** The SQL statement key for the 'CleanupAttachments' statement. */
   protected static final String SQL_CLEANUP_ATTACHMENTS = "CleanupAttachments"; //$NON-NLS-1$
   
   /** The SQL statement key for the 'removeAttachment' statement. */
   protected static final String SQL_REMOVE_ATTACHMENT = "RemoveAttachment"; //$NON-NLS-1$

   private AeCounter mCounter;

   /** Result set handler for attachment items. */
   protected static final ResultSetHandler<AeAttachmentItemEntry> sAttachmentItemHandler = new AeAttachmentItemResultSetHandler();

   public AeSQLAttachmentStorageProvider()
   {
      setPrefix(SQLSTATEMENT_PREFIX);
   }

   /**
    * Returns next available attachment group id.
    * @throws AeStorageException
    */
   protected long getNextAttachmentGroupId() throws AeStorageException
   {
      return getCounter().getNextValue();
   }

   /**
    * Returns next available attachment item id.
    * @throws AeStorageException
    */
   protected long getNextAttachmentItemId() throws AeStorageException
   {
      return getCounter().getNextValue();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider#associateProcess(long, long)
    */
   public void associateProcess(long aAttachmentGroupId, long aProcessId) throws AeStorageException
   {
      Connection connection = getTransactionConnection();
      try
      {
         Object[] params = new Object[] {aProcessId, aAttachmentGroupId};
         update(connection, SQL_PROCESS_ATTACHMENT_GROUP, params);
      }
      finally
      {
         AeCloser.close(connection);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider#createAttachmentGroup()
    */
   public long createAttachmentGroup() throws AeStorageException
   {
      long attachmentGroupId = getNextAttachmentGroupId();

      Object[] params = new Object[] {attachmentGroupId};
      update(SQL_ATTACHMENT_GROUP, params);
      return attachmentGroupId;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider#storeAttachment(long, java.io.InputStream, java.util.Map)
    */
   public long storeAttachment(long aAttachmentGroupId, InputStream aInputStream, Map aHeaders) throws AeStorageException
   {
      try
      {
         long attachmentId = getNextAttachmentItemId();
         InputStream content;
         if (aInputStream instanceof AeBlobInputStream)
         {
            content = aInputStream;
         }
         else
         {
            content = new AeBlobInputStream(aInputStream);
         }

         try
         {
            Object[] params = new Object[]
            {
                    aAttachmentGroupId,
                    attachmentId,
               (aHeaders != null) ? AePairSerializer.serialize(aHeaders) : AeQueryRunner.NULL_CLOB,
               content
            };

            update(SQL_STORE_ATTACHMENT, params);
         }
         finally
         {
            AeCloser.close(content); // remove temp file
         }

         return attachmentId;
      }
      catch (AeStorageException e)
      {
         throw e;
      }
      catch (Exception ex)
      {
         throw new AeStorageException(ex);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider#getHeaders(long)
    */
   public Map<String,String> getHeaders(long aAttachmentId) throws AeStorageException
   {
      try
      {
         AeAttachmentItemEntry entry = query(SELECT_ATTACHMENT_HEADERS, sAttachmentItemHandler, aAttachmentId);
         return entry.getHeaders();
      }
      catch (AeStorageException e)
      {
         throw e;
      }
      catch (AeException ex)
      {
         throw new AeStorageException(ex);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider#getContent(long)
    */
   public InputStream getContent(long aAttachmentId) throws AeStorageException
   {
      return query(SELECT_ATTACHMENT_CONTENTS, AeResultSetHandlers.getBlobStreamHandler(), aAttachmentId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider#cleanup()
    */
   public void cleanup() throws AeStorageException
   {
      update(SQL_CLEANUP_ATTACHMENTS);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider#removeAttachment(long)
    */
   public void removeAttachment(long aAttachmentId) throws AeStorageException
   {
      Object[] params = {aAttachmentId};
      update(SQL_REMOVE_ATTACHMENT, params);
   }

   /**
	* @return
	*/
   public AeCounter getCounter() {
      return mCounter;
   }
	
   /**
    * @param aCounter
    */
   public void setCounter(AeCounter aCounter) {
      mCounter = aCounter;
   }
}
