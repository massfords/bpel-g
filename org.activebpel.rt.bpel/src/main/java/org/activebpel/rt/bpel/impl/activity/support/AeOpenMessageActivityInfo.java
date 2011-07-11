// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeOpenMessageActivityInfo.java,v 1.7 2008/02/17 21:37:09 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support;

import org.activebpel.rt.bpel.impl.AePartnerLinkOpImplKey;
import org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver;
import org.activebpel.rt.util.AeUtil;

/**
 * Contains information about an inbound open message activities that have been consumed
 * by a receiver such as the BPEL Receive or OnMessage, but not responded to.
 */
public class AeOpenMessageActivityInfo
{
   /** Partner link op key */
   private AePartnerLinkOpImplKey mPartnerLinkOpImplKey;
   /** Message exchange if applicable. */
   private String mMessageExchangePath;
   /** The durable reply receiver associated with a two-way inbound message. */
   private IAeReplyReceiver mDurableReplyReceiver;
   /** The replyId which is used to look up the queue manager for the matching AeReply. */
   private long mReplyId;
   /** Cached hash code. */
   private int mHashCode;
   /** <code>true</code> if and only if this open message activity has been marked orphaned. */
   private boolean mIsOrphaned = false;
   /** Location path for the receive activity associated with the two-way exchange */
   private String mReceiverPath;
   
   /**
    * Default contructor.
    * @param aPartnerLinkKey partnerlink op key for the inbound message.
    * @param aMessageExchangePath receive reply message exchange path.
    * @param aReplyId reply id.
    */
   public AeOpenMessageActivityInfo(AePartnerLinkOpImplKey aPartnerLinkKey, String aMessageExchangePath, long aReplyId)
   {      
      this(aPartnerLinkKey, aMessageExchangePath, aReplyId, null);
   }   

   /**
    * Default contructor.
    * @param aPartnerLinkKey partnerlink op impl key for the inbound message.
    * @param aMessageExchangePath receive reply message exchange path.
    * @param aReplyId reply id.
    * @param aReplyReceiver
    */
   public AeOpenMessageActivityInfo(AePartnerLinkOpImplKey aPartnerLinkKey, String aMessageExchangePath,
         long aReplyId, IAeReplyReceiver aReplyReceiver)
   {           
      setPartnerLinkOpImplKey(aPartnerLinkKey);
      setMessageExchangePath(aMessageExchangePath);
      setDurableReplyReceiver(aReplyReceiver);
      setReplyId(aReplyId);
   }
   
   /**
    * @return Returns the replyId.
    */
   public long getReplyId()
   {
      return mReplyId;
   }
   
   /**
    * @param aReplyId The replyId to set.
    */
   public void setReplyId(long aReplyId)
   {
      mReplyId = aReplyId;
   }
   
   /**
    * @return Returns the messageExchange.
    */
   public String getMessageExchangePath()
   {
      return mMessageExchangePath;
   }

   /**
    * @param aMessageExchange The messageExchange to set.
    */
   public void setMessageExchangePath(String aMessageExchange)
   {
      mMessageExchangePath = aMessageExchange;
      updateHashCode();
   }

   /**
    * Returns the durable reply receiver if available or <code>null</code> 
    * otherwise (for one-way or non-durable receives).
    * @return Returns the durable reply receiver.
    */
   public IAeReplyReceiver getDurableReplyReceiver()
   {
      return mDurableReplyReceiver;
   }

   /**
    * Sets the durable reply receiver associated with a two-way inbound receive.
    * @param aReplyReceiver
    */
   public void setDurableReplyReceiver(IAeReplyReceiver aReplyReceiver)
   {
      mDurableReplyReceiver = aReplyReceiver;
   }
   
   /** 
    * @return true if this message activity has a durable reply receiver.
    */
   public boolean hasDurableReply()
   {
      return getDurableReplyReceiver() != null && getDurableReplyReceiver().getDurableReplyInfo() != null;
   }

   /** 
    * Overrides method to compare the partnerLink, operation and messageExchange.
    * This method doe not use the replyId for comparison. 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aObject)
   {
      boolean rVal = false;
      if (aObject instanceof AeOpenMessageActivityInfo)
      {
         AeOpenMessageActivityInfo other = (AeOpenMessageActivityInfo) aObject;
         rVal = AeUtil.compareObjects( getMessageExchangePath(), other.getMessageExchangePath() )
             && AeUtil.compareObjects( getPartnerLinkOpImplKey(), other.getPartnerLinkOpImplKey() );
      }
      return rVal;
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return mHashCode;
   }

   /**
    * Generates hash code from partnerLink, operation, and messageExchange.
    */
   protected void updateHashCode()
   {
      int h1 = (getMessageExchangePath()  == null) ? 0 : getMessageExchangePath() .hashCode();
      int h2 = (getPartnerLinkOpImplKey() == null) ? 0 : getPartnerLinkOpImplKey().hashCode();
      mHashCode = h1 + h2;
   }

   /**
    * @return Returns the partnerLinkOpImplKey.
    */
   public AePartnerLinkOpImplKey getPartnerLinkOpImplKey()
   {
      return mPartnerLinkOpImplKey;
   }

   /**
    * @param aPartnerLinkOpImplKey The partnerLinkOpImplKey to set.
    */
   public void setPartnerLinkOpImplKey(AePartnerLinkOpImplKey aPartnerLinkOpImplKey)
   {
      mPartnerLinkOpImplKey = aPartnerLinkOpImplKey;
      updateHashCode();
   }

   /**
    * Convenience method returns partner link location path.
    */
   public String getPartnerLinkLocationPath()
   {
      AePartnerLinkOpImplKey partnerLinkKey = getPartnerLinkOpImplKey();
      return (partnerLinkKey == null) ? null : partnerLinkKey.getPartnerLinkLocationPath();
   }

   /**
    * Sets whether this open message activity has been marked orphaned.
    */
   public void setOrphaned(boolean isOrphaned)
   {
      mIsOrphaned = isOrphaned;
   }

   /**
    * Returns <code>true</code> if and only if this open message activity has
    * been marked orphaned.
    */
   public boolean isOrphaned()
   {
      return mIsOrphaned;
   }

   /**
    * @return Location path for the receive activity associated with the two-way exchange
    */
   public String getReceiverPath()
   {
      return mReceiverPath;
   }

   /**
    * @param aReceiverPath Location path for the receive activity associated with the two-way exchange
    */
   public void setReceiverPath(String aReceiverPath)
   {
      mReceiverPath = aReceiverPath;
   }
}
