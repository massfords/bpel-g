// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/attachment/AeStreamAttachmentItem.java,v 1.1 2007/05/24 00:55:15 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.attachment;

import java.io.InputStream;
import java.util.Map;

import org.activebpel.rt.attachment.IAeAttachmentItem;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeBusinessProcess;

/**
 * Implements an attachment item that has not yet been stored by the attachment
 * manager.
 */
public class AeStreamAttachmentItem implements IAeAttachmentItem
{
   /** <code>InputStream</code> to the attachment's content. */
   private final InputStream mContent;
   
   /** <code>Map</code> of attachment's MIME headers. */
   private final Map mHeaders;

   /**
    * Default constructor.
    */
   public AeStreamAttachmentItem(InputStream aContent, Map aHeaders)
   {
      mContent = aContent;
      mHeaders = aHeaders;
   }

   /**
    * @see org.activebpel.rt.attachment.IAeAttachmentItem#getAttachmentId()
    */
   public long getAttachmentId()
   {
      throw new IllegalStateException(AeMessages.getString("AeStreamAttachmentItem.ERROR_AttachmentId")); //$NON-NLS-1$
   }

   /**
    * Return the <code>InputStream</code> to the attachment's content.
    */
   public InputStream getContent()
   {
      return mContent;
   }

   /**
    * @see org.activebpel.rt.attachment.IAeAttachmentItem#getHeader(java.lang.String)
    */
   public String getHeader(String aHeaderName)
   {
      return (String) getHeaders().get(aHeaderName);
   }

   /**
    * @see org.activebpel.rt.attachment.IAeAttachmentItem#getHeaders()
    */
   public Map getHeaders()
   {
      return mHeaders;
   }

   /**
    * @see org.activebpel.rt.attachment.IAeAttachmentItem#getProcessId()
    */
   public long getProcessId()
   {
      // Not associated with a process.
      return IAeBusinessProcess.NULL_PROCESS_ID;
   }
}
