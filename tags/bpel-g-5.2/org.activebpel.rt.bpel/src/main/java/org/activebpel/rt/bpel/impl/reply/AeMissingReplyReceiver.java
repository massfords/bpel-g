//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/reply/AeMissingReplyReceiver.java,v 1.3 2006/06/08 19:30:55 PJayanetti Exp $
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
import org.activebpel.rt.message.IAeMessageData;

/**
 * Implements the receiver for the reply to an inbound receive when the
 * system went down before the reply happened.
 */
public class AeMissingReplyReceiver implements IAeReplyReceiver
{
   /**
    * Reply id of the associated inbound receive.
    */
   private long mInboundReceiveReplyId;

   /**
    * Contructs a missing reply receiver given reply id.
    * @param aInboundReceiveReplyId
    */
   public AeMissingReplyReceiver(long aInboundReceiveReplyId)
   {
      mInboundReceiveReplyId = aInboundReceiveReplyId;
   }
   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#onFault(org.activebpel.rt.bpel.IAeFault, java.util.Map)
    */
   public void onFault(IAeFault aFault, Map aProcessProperties) throws AeBusinessProcessException
   {
      // Throw an exception, because we no longer have the original receiver
      // for the reply to the inbound receive.
      throw new AeMissingReplyReceiverException(mInboundReceiveReplyId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#onMessage(org.activebpel.rt.message.IAeMessageData, java.util.Map)
    */
   public void onMessage(IAeMessageData aMessage, Map aProcessProperties) throws AeBusinessProcessException
   {
      // Throw an exception, because we no longer have the original receiver
      // for the reply to the inbound receive.
      throw new AeMissingReplyReceiverException(mInboundReceiveReplyId);
   }
   
   /** 
    * Overrides method to return <code>null</code> 
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#getDurableReplyInfo()
    */
   public IAeDurableReplyInfo getDurableReplyInfo()
   {
      return null;
   }
      
}


