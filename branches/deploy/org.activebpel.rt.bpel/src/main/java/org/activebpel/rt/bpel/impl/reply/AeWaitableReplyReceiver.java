// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/reply/AeWaitableReplyReceiver.java,v 1.4 2007/06/10 19:07:57 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.reply;

import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.message.IAeMessageData;

/**
 * Class used to handle the reply in a request response operation.
 * This class's onFault and onMessage method notifies any waiting
 * threads by calling notifyAll.
 */
public class AeWaitableReplyReceiver extends AeReplyReceiverBase
{
   /**
    * Notifies any waiting threads on when this callback is invoked.
    * @param aFault fault
    * @param aProcessProperties business process properties.
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#onFault(org.activebpel.rt.bpel.IAeFault, java.util.Map)
    */
   public synchronized void onFault(IAeFault aFault, Map aProcessProperties) throws AeBusinessProcessException
   {
      if (aFault == null)
         throw new AeBusinessProcessException(AeMessages.getString("AeWaitableReplyReceiver.ERROR_0")); //$NON-NLS-1$

      setFault(aFault);
      setBusinessProcessProperties(aProcessProperties);
      notifyAll();
   }

   /**
    * Notifies any waiting threads on when this callback is invoked.
    * @param aMessageData message data.
    * @param aProcessProperties business process properties.
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#onMessage(org.activebpel.rt.message.IAeMessageData, java.util.Map)
    */
   public synchronized void onMessage(IAeMessageData aMessageData, Map aProcessProperties) throws AeBusinessProcessException
   {
      if (aMessageData == null)
         throw new AeBusinessProcessException(AeMessages.getString("AeWaitableReplyReceiver.ERROR_1")); //$NON-NLS-1$

      setMessageData(aMessageData);
      setBusinessProcessProperties(aProcessProperties);
      notifyAll();
   }

   /**
    * This object is considered waitable if it its callback methods for receiving
    * the message data, fault, or void signal haven't been invoked. This method
    * should be used to determine if the object is in a waitable state before
    * waiting on it. Failing to call this method (or testing the object's state
    * through the aforementioned calls) risks waiting forever for a callback that
    * will never be issued.
    */
   public synchronized boolean isWaitable()
   {
      return getMessageData() == null && getFault() == null;
   }
}