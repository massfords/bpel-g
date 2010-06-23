//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/reply/AeQueueManagerReplyReceiver.java,v 1.3 2006/06/08 19:30:55 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.reply;

import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.IAeQueueManager;
import org.activebpel.rt.bpel.impl.queue.AeReply;
import org.activebpel.rt.message.IAeMessageData;

/**
 * A reply receiver that delegates to a matching non-durable reply receiver
 * maintained by the queue manager. 
 *
 */
public class AeQueueManagerReplyReceiver implements IAeReplyReceiver
{
   /** Reply receiver prototype. */;
   private IAeReplyReceiver mDelegate;
   
   public AeQueueManagerReplyReceiver(IAeQueueManager aQueueManager, AeReply aReplyPrototype)
   {      
      // find the waiting reply from the queue manager. if a waiting reply is not found,
      // then use a missing reply receiver.
      AeReply waitingReply  = aQueueManager.removeReply(aReplyPrototype);
      if (waitingReply != null)
      {
         setDelegate( waitingReply.getReplyReceiver() );  
      }      
      else
      {
         setDelegate ( new AeMissingReplyReceiver(aReplyPrototype.getReplyId()) );
      }
   }
   
   /**
    * @param aDelegate The delegate to set.
    */
   protected void setDelegate(IAeReplyReceiver aDelegate)
   {
      mDelegate = aDelegate;
   }
   
   /**
    * @return Returns the delegate.
    */
   public IAeReplyReceiver getDelegate()
   {
      return mDelegate;
   }


   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#onMessage(org.activebpel.rt.message.IAeMessageData, java.util.Map)
    */
   public void onMessage(IAeMessageData aMessage, Map aProcessProperties) throws AeBusinessProcessException
   {
      getDelegate().onMessage(aMessage, aProcessProperties);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#onFault(org.activebpel.rt.bpel.IAeFault, java.util.Map)
    */
   public void onFault(IAeFault aFault, Map aProcessProperties) throws AeBusinessProcessException
   {
      getDelegate().onFault(aFault, aProcessProperties);
   }

   /** 
    * Overrides method to return null. 
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#getDurableReplyInfo()
    */
   public IAeDurableReplyInfo getDurableReplyInfo()
   {
      return null;
   }

}
