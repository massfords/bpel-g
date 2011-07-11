/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.xmldb.attachments;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.attachment.AePairDeserializer;
import org.activebpel.rt.bpel.server.engine.storage.attachment.AePairSerializer;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeAbstractXMLDBStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBConfig;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBResponseHandler;
import org.activebpel.rt.util.AeCloser;
import org.w3c.dom.Document;

/**
 * XMLDB implementation of an attachment storage provider.
 */
public class AeXMLDBAttachmentStorageProvider extends AeAbstractXMLDBStorageProvider implements
      IAeAttachmentStorageProvider
{
   /** The prefix into the xmldb config that this storage object uses. */
   protected static final String CONFIG_PREFIX = "AttachmentStorage"; //$NON-NLS-1$

   /**
    * Constructs a XMLDB attachment storage provider with the given XMLDB config.
    *
    * @param aConfig
    */
   public AeXMLDBAttachmentStorageProvider(AeXMLDBConfig aConfig, IAeXMLDBStorageImpl aStorageImpl)
   {
      super(aConfig, CONFIG_PREFIX, aStorageImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider#associateProcess(long, long)
    */
   public void associateProcess(long aAttachmentGroupId, long aProcessId) throws AeStorageException
   {
      IAeXMLDBConnection connection = getTransactionManagerConnection(false);
      try
      {
         Object[] params = new Object[] { new Long(aAttachmentGroupId), new Long(aProcessId) };
         updateDocuments(IAeAttachmentConfigKeys.ATTACH_PROCESS, params, connection);
      }
      finally
      {
         connection.close();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider#cleanup()
    */
   public void cleanup() throws AeStorageException
   {
      deleteDocuments(IAeAttachmentConfigKeys.CLEANUP_ATTACHMENTS);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider#createAttachmentGroup()
    */
   public long createAttachmentGroup() throws AeStorageException
   {
      LinkedHashMap params = new LinkedHashMap();
      params.put(IAeAttachmentElements.PROCESS_ID, NULL_INTEGER);
      return insertDocument(IAeAttachmentConfigKeys.INSERT_ATTACHMENT_GROUP, params);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider#getContent(long)
    */
   public InputStream getContent(long aAttachmentId) throws AeStorageException
   {
      Object[] params = { new Long(aAttachmentId) };
      Long attachmentContentId = (Long) query(IAeAttachmentConfigKeys.QUERY_ATTACHMENT_CONTENT_ID, params, AeXMLDBResponseHandler.LONG_RESPONSE_HANDLER);
      if (attachmentContentId != null)
         return retrieveAttachmentContent(attachmentContentId.longValue());
      return null;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider#getHeaders(long)
    */
   public Map getHeaders(long aAttachmentId) throws AeStorageException
   {
      try
      {
         Object[] params = { new Long(aAttachmentId) };
         Document headersDoc = (Document) query(IAeAttachmentConfigKeys.QUERY_ATTACHMENT_HEADERS, params,
               AeXMLDBResponseHandler.DOCUMENT_RESPONSE_HANDLER);
         if (headersDoc != null)
            return AePairDeserializer.deserialize(headersDoc);

         return null;
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
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider#storeAttachment(long, java.io.InputStream, java.util.Map)
    */
   public long storeAttachment(long aAttachmentGroupId, InputStream aInputStream, Map aHeaders) throws AeStorageException
   {
      IAeXMLDBConnection connection = getNewConnection(false);

      try
      {
         long attachmentContentId = storeAttachmentContent(aInputStream, connection);

         Object headers = (aHeaders != null) ? (Object) AePairSerializer.serialize(aHeaders) : NULL_DOCUMENT;

         LinkedHashMap params = new LinkedHashMap();
         params.put(IAeAttachmentElements.ATTACHMENT_GROUP_ID, new Long(aAttachmentGroupId));
         params.put(IAeAttachmentElements.ATTACHMENT_HEADER, headers);
         params.put(IAeAttachmentElements.ATTACHMENT_CONTENT_ID, new Long(attachmentContentId));
         long attachmentId = insertDocument(IAeAttachmentConfigKeys.INSERT_ATTACHMENT, params, connection);
         connection.commit();
         return attachmentId;
      }
      catch (AeStorageException se)
      {
         connection.rollback();
         throw se;
      }
      catch (Throwable t)
      {
         connection.rollback();
         throw new AeStorageException(t);
      }
      finally
      {
         connection.close();
      }
   }

   /**
    * Stores the given attachment content as a non-XML object in the XMLDB
    * database and returns the resulting object's ID.
    * 
    * @param aInputStream
    * @param aConnection
    */
   private long storeAttachmentContent(InputStream aInputStream, IAeXMLDBConnection aConnection)
         throws AeStorageException
   {
      try
      {
         return insertNonXMLDocument(aInputStream, aConnection);
      }
      finally
      {
         AeCloser.close(aInputStream);
      }
   }

   /**
    * Retrieves the attachment content with the given non-XML XMLDB object 
    * id.
    * 
    * @param aAttachmentContentId
    */
   private InputStream retrieveAttachmentContent(long aAttachmentContentId) throws AeStorageException
   {
      return retrieveNonXMLDocument(aAttachmentContentId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider#removeAttachment(long)
    */
   public void removeAttachment(long aAttachmentId) throws AeStorageException
   {
      deleteNonXMLDocument(aAttachmentId);
   }
}
