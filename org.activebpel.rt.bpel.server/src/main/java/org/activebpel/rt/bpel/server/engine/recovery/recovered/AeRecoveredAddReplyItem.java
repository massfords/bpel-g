//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/recovered/AeRecoveredAddReplyItem.java,v 1.5 2007/11/15 21:06:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.recovered;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.rt.bpel.impl.queue.AeReply;
import org.activebpel.rt.util.AeUtil;

/**
 * Implements a recovered item to add a reply.
 */
public class AeRecoveredAddReplyItem extends AeRecoveredReplyItem
{
   /** The previously queued message receiver that goes with the reply. */
   private final AeMessageReceiver mMessageReceiver;

   /**
    * Constructs a recovered item to add a reply.
    */
   public AeRecoveredAddReplyItem(AeReply aReply, AeMessageReceiver aMessageReceiver)
   {
      super(aReply);

      mMessageReceiver = aMessageReceiver;
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aOther)
   {
      return (aOther instanceof AeRecoveredAddReplyItem)
         && AeUtil.compareObjects(((AeRecoveredAddReplyItem) aOther).getReply(), getReply());
   }

   /**
    * Returns the previously queued message receiver that goes with the reply.
    */
   protected AeMessageReceiver getMessageReceiver()
   {
      return mMessageReceiver;
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return getReply().hashCode();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem#queueItem(org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal)
    */
   public void queueItem(IAeBusinessProcessEngineInternal aTargetEngine) throws AeBusinessProcessException
   {
      aTargetEngine.getQueueManager().addNonDurableReply(getReply(), getMessageReceiver());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem#isRemoval()
    */
   public boolean isRemoval()
   {
      return false;
   }
}
