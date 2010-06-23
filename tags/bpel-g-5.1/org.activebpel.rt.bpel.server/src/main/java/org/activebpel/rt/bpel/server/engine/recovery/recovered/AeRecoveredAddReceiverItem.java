// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/recovered/AeRecoveredAddReceiverItem.java,v 1.3 2007/11/15 21:06:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.recovered;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.rt.bpel.server.AeMessages;

/**
 * Implements a recovered item to add a message receiver.
 */
public class AeRecoveredAddReceiverItem extends AeRecoveredLocationIdItem
{
   /** The message receiver. */
   private final AeMessageReceiver mMessageReceiver;

   /**
    * Constructs a recovered item to add a message receiver.
    */
   public AeRecoveredAddReceiverItem(AeMessageReceiver aMessageReceiver)
   {
      super(aMessageReceiver.getProcessId(), aMessageReceiver.getMessageReceiverPathId());

      mMessageReceiver = aMessageReceiver;
   }

   /**
    * Constructs a recovered item to match another add message receiver item by
    * location id.
    */
   public AeRecoveredAddReceiverItem(int aLocationId)
   {
      super(0, aLocationId);

      mMessageReceiver = null;
   }

   /**
    * Returns the message receiver.
    */
   public AeMessageReceiver getMessageReceiver()
   {
      return mMessageReceiver;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem#queueItem(org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal)
    */
   public void queueItem(IAeBusinessProcessEngineInternal aTargetEngine) throws AeBusinessProcessException
   {
      if (getMessageReceiver() == null)
      {
         throw new IllegalStateException(AeMessages.getString("AeRecoveredAddReceiverItem.ERROR_0")); //$NON-NLS-1$
      }

      aTargetEngine.getQueueManager().addMessageReceiver(getMessageReceiver());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem#isRemoval()
    */
   public boolean isRemoval()
   {
      return false;
   }
}
