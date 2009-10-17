// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/recovered/AeRecoveredSendReplyItem.java,v 1.3 2007/11/15 21:06:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.recovered;

import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.queue.AeReply;
import org.activebpel.rt.message.IAeMessageData;

/**
 * Implements a recovered item to send a reply.
 */
public class AeRecoveredSendReplyItem extends AeRecoveredReplyItem
{
   /** The message data to send. */
   private final IAeMessageData mMessageData;

   /** The fault to send. */
   private final IAeFault mFault;

   /** The associated process properties. */
   private final Map mProcessProperties;

   /**
    * Constructs a recovered item to send a reply.
    */
   public AeRecoveredSendReplyItem(AeReply aReply, IAeMessageData aMessageData, IAeFault aFault, Map aProcessProperties)
   {
      super(aReply);

      mMessageData = aMessageData;
      mFault = aFault;
      mProcessProperties = aProcessProperties;
   }

   /**
    * Returns the fault to send.
    */
   protected IAeFault getFault()
   {
      return mFault;
   }

   /**
    * Returns the message data to send.
    */
   protected IAeMessageData getMessageData()
   {
      return mMessageData;
   }

   /**
    * Returns the associated process properties.
    */
   protected Map getProcessProperties()
   {
      return mProcessProperties;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem#queueItem(org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal)
    */
   public void queueItem(IAeBusinessProcessEngineInternal aTargetEngine) throws AeBusinessProcessException
   {
      aTargetEngine.getQueueManager().sendReply(getReply(), getMessageData(), getFault(), getProcessProperties());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem#isRemoval()
    */
   public boolean isRemoval()
   {
      return false;
   }
}
