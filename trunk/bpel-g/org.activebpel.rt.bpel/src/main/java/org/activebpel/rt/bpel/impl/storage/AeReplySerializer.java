// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/storage/AeReplySerializer.java,v 1.3 2006/06/05 20:40:22 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.storage;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.impl.IAeImplStateNames;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.bpel.impl.queue.AeReply;

/**
 * Serializes a reply to an instance of {@link
 * org.activebpel.rt.bpel.impl.fastdom.AeFastElement} or {@link
 * org.activebpel.rt.bpel.impl.fastdom.AeFastDocument}.
 */
public class AeReplySerializer implements IAeImplStateNames
{
   /** The reply to serialize. */
   private AeReply mReply;

   /** The resulting serialization. */
   private AeFastElement mReplyElement;

   /**
    * Serializes the specified reply to an instance of {@link
    * org.activebpel.rt.bpel.impl.fastdom.AeFastElement}.
    *
    * @param aReply
    */
   protected AeFastElement createReplyElement(AeReply aReply) throws AeBusinessProcessException
   {
      AeFastElement replyElement = new AeFastElement(STATE_REPLY);
      replyElement.setAttribute(STATE_PID      , String.valueOf(aReply.getProcessId()));
      replyElement.setAttribute(STATE_REPLY_ID , String.valueOf(aReply.getReplyId()) );
      return replyElement;
   }

   /**
    * Returns the reply to serialize.
    */
   protected AeReply getReply()
   {
      return mReply;
   }

   /**
    * Returns an instance of {@link
    * org.activebpel.rt.bpel.impl.fastdom.AeFastDocument} representing the
    * reply.
    */
   public AeFastDocument getReplyDocument() throws AeBusinessProcessException
   {
      return new AeFastDocument(getReplyElement());
   }

   /**
    * Returns an instance of {@link
    * org.activebpel.rt.bpel.impl.fastdom.AeFastElement} representing the reply.
    */
   public AeFastElement getReplyElement() throws AeBusinessProcessException
   {
      if (mReplyElement == null)
      {
         if (getReply() == null)
         {
            throw new IllegalStateException(AeMessages.getString("AeReplySerializer.ERROR_0")); //$NON-NLS-1$
         }

         mReplyElement = createReplyElement(getReply());
      }

      return mReplyElement;
   }

   /**
    * Resets all output variables.
    */
   protected void reset()
   {
      mReplyElement = null;
   }

   /**
    * Sets the reply to serialize.
    *
    * @param aReply
    */
   public void setReply(AeReply aReply)
   {
      reset();

      mReply = aReply;
   }
}
