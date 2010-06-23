// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/queue/AeReply.java,v 1.9 2008/01/02 20:44:08 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.queue;

import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver;
import org.activebpel.rt.message.IAeMessageData;

/**
 * An in memory queue object for replies.
 */
public class AeReply
{
   /** The invoke that's waiting for a reply */
   private IAeReplyReceiver mReplyReceiver;

   /** Process id is needed to differentiate between queued objects */
   private long mProcessId;
   
   /** Reply id assigned to this reply. */
   private long mReplyId;
   
   /** Data sent as the reply message*/
   private IAeMessageData mMessageData;
   
   /** Fault data sent as the reply */
   private IAeFault mFault;
   
   /** Location path of the receive activity associated with this reply */
   private String mReceiverPath;
   
   /**
    * Create reply queue object given partner link name, port type, operation,
    * message exchange path and the open message activity's reply receiver. 
    * @param aProcessId
    * @param aReplyId reply id 
    */   
   public AeReply(long aProcessId, long aReplyId)
   {
      this(aProcessId, aReplyId, null); 
   }    
   /**
    * Create reply queue object given partner link name, port type, operation,
    * message exchange path and the open message activity's reply receiver. 
    * @param aProcessId
    * @param aReplyId reply id 
    * @param aReplyReceiver 
    */   
   public AeReply(long aProcessId, long aReplyId, IAeReplyReceiver aReplyReceiver)
   { 
      setProcessId(aProcessId);
      setReplyReceiver( aReplyReceiver );
      setReplyId (aReplyId );      
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
    * Getter for the response receiver.
    */
   public IAeReplyReceiver getReplyReceiver()
   {
      return mReplyReceiver; 
   }

   /**
    * Setter for the reply receiver.
    * @param aReplyReceiver
    */
   public void setReplyReceiver(IAeReplyReceiver aReplyReceiver)
   {
      mReplyReceiver = aReplyReceiver;
   }
   
   /**
    * Sets the process id.
    * @param processId
    */
   public void setProcessId(long processId)
   {
      mProcessId = processId;
   }

   /**
    * Returns the process id.
    */
   public long getProcessId()
   {
      return mProcessId;
   }
   
   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aObject)
   {
      if (aObject instanceof AeReply)
      {
         AeReply other = (AeReply) aObject;
         return getReplyId() == other.getReplyId();
      }
      return false;
   }

   /**
    * Overrides to return the hashcode of the replyId, which is unique.
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return new Long(getReplyId()).hashCode();
   }
   
   /**
    * @return the messageData
    */
   public IAeMessageData getMessageData()
   {
      return mMessageData;
   }
   /**
    * @param aMessageData the messageData to set
    */
   public void setMessageData(IAeMessageData aMessageData)
   {
      mMessageData = aMessageData;
   }
   /**
    * @return the fault
    */
   public IAeFault getFault()
   {
      return mFault;
   }
   /**
    * @param aFault the fault to set
    */
   public void setFault(IAeFault aFault)
   {
      mFault = aFault;
   }
   /**
    * @return the receiverPath
    */
   public String getReceiverPath()
   {
      return mReceiverPath;
   }
   /**
    * @param aReceiverPath the receiverPath to set
    */
   public void setReceiverPath(String aReceiverPath)
   {
      mReceiverPath = aReceiverPath;
   }
}
