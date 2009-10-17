// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/attachment/AeAttachmentItemEntry.java,v 1.3 2007/10/31 19:49:38 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.attachment;

import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.w3c.dom.Document;

/**
 * Entry containing attachment group id, attachment item id, and serialized
 * attachment headers <code>Document</code> 
 */
public class AeAttachmentItemEntry
{
   /** Attachment group id. */
   private long mAttachmentGroupId;
   
   /** Attachment id. */
   private long mAttachmentId;

   /** Serialized attachment headers <code>Document</code>. */
   private Document mHeadersDocument;

   /** Deserialized attachment headers. */
   private Map mHeaders;

   /**
    * Constructs attachment item entry from specified values.
    *
    * @param aAttachmentGroupId
    * @param aAttachmentId
    * @param aHeadersDocument
    */
   public AeAttachmentItemEntry(long aAttachmentGroupId, long aAttachmentId, Document aHeadersDocument)
   {
      mAttachmentGroupId  = aAttachmentGroupId;
      mAttachmentId = aAttachmentId;
      mHeadersDocument = aHeadersDocument;
   }
   
   /**
    * @return attachment group id.
    */
   public long getAttachmentGroupId()
   {
      return mAttachmentGroupId;
   }

   /**
    * @return attachment item id.
    */
   public long getAttachmentId()
   {
      return mAttachmentId;
   }

   /**
    * @return attachment headers.
    */
   public Map getHeaders() throws AeBusinessProcessException
   {
      if ((mHeaders == null) && (getHeadersDocument() != null))
      {
         mHeaders = AePairDeserializer.deserialize(getHeadersDocument());
      }

      return mHeaders;
   }

   /**
    * @return serialized attachment headers <code>Document</code>.
    */
   public Document getHeadersDocument()
   {
      return mHeadersDocument;
   }
}
