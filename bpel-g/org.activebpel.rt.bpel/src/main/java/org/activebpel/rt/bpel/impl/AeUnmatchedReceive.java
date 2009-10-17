//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeUnmatchedReceive.java,v 1.1 2006/05/24 23:07:00 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;

/**
 * Umatched Receive collections object - which is a tuple for holding
 * the unmatched <code>AeInboundReceive</code> and its assoicated <code>IAeMessageAcknowledgeCallback</code>.
 */
public class AeUnmatchedReceive
{
   /** The unmatched inbound receive. */ 
   private AeInboundReceive mInboundReceive;
   /** Durable message acknowlege callback. */
   private IAeMessageAcknowledgeCallback mAckCallback;
   
   /**
    * Default ctor.
    * @param aInboundReceive
    * @param aAckCallback
    */
   public AeUnmatchedReceive(AeInboundReceive aInboundReceive, IAeMessageAcknowledgeCallback aAckCallback)
   {
      mInboundReceive = aInboundReceive;
      mAckCallback = aAckCallback;
   }
   
   /** 
    * @return Returns the unmatched inbound receive.
    */
   public AeInboundReceive getInboundReceive()
   {
      return mInboundReceive;
   }
   
   /** 
    * @return Returns the unmatched receive's associated durable callback.
    */
   public IAeMessageAcknowledgeCallback getAckCallback()
   {
      return mAckCallback;
   }
   
   /**
    * Returns the  queue id assigned to the inbound receive..
    */
   public String getQueueId()
   {
      return getInboundReceive().getQueueId();
   }   
   
   /**
    * Sets the inbound receive's queue id.
    * @param aQueueId
    */
   public void setQueueId(String aQueueId)
   {
      getInboundReceive().setQueueId(aQueueId);
   }
   
   /** 
    * Overrides method to compare the inbound receives. 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object other)
   {
      return AeUtil.compareObjects( getInboundReceive(), other);
   }
}
