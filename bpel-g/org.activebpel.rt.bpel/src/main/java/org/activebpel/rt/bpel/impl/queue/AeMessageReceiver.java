// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/queue/AeMessageReceiver.java,v 1.16 2006/09/22 19:52:39 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.queue;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AePartnerLinkOpKey;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.util.AeUtil;

/**
 * Models a single bpel activity that has executed and is waiting to receive
 * an inbound message.
 */
public class AeMessageReceiver extends AeCorrelatedReceive
{
   /** next unique hashcode value */
   private static int sNextHashCode = 1;

   /** Message receiver (location path id) of message queue entry */
   private int mMessageReceiverPathId;
   /** Process id is needed to differentiate between queued objects */
   private long mProcessId;
   /** The group id of this message receiver. */
   private int mGroupId;
   /** hashCode for the message receiver */
   private int mHashCode;
   /** If the matching inbound receive has been journalled, this will be the ID of that journal entry. */
   private long mJournalId = IAeProcessManager.NULL_JOURNAL_ID;
   /** port type for the receiver */
   private QName mPortType;
   /** flag for receivers that allow concurrent messages (ws-bpel 2.0 onEvent) */
   private boolean mConcurrent;

   /**
    * Constructor for the receiver, accepts all of the data necessary to init the object.
    * @param aProcessId id of the parent process for the message receiver
    * @param aProcessName qualified name of the parent process
    * @param aPartnerLinkOpKey the partner link:operation we're expecting to be called on
    * @param aPortType name of the port type
    * @param aCorrelation correlated properties, can be empty
    * @param aGroupId The group id of the message receiver.
    */
   public AeMessageReceiver(long aProcessId, QName aProcessName,
         AePartnerLinkOpKey aPartnerLinkOpKey, QName aPortType,
         Map aCorrelation, int aMessageReceiverPathId, int aGroupId, boolean aConcurrent)
   {
      super(aPartnerLinkOpKey, aProcessName, aCorrelation);
      setMessageReceiverPathId(aMessageReceiverPathId);
      setProcessId(aProcessId);
      setGroupId(aGroupId);
      mHashCode = getNextHashCode();
      mPortType = aPortType;
      setConcurrent(aConcurrent);
   }

   /**
    * This unique hashcode value is used as part of the sorting of message receivers
    * within the in-memory queue manager. When resolving inbound receives to
    * message receivers, it is important to compare the inbound receives against
    * the message receivers in descending order of correlation properties.
    *
    * The hash is used to differentiate message receivers with the same number of
    * correlated properties.
    */
   protected static synchronized int getNextHashCode()
   {
      return sNextHashCode++;
   }

   /**
    * Getter for the message receiver path id
    */
   public int getMessageReceiverPathId()
   {
      return mMessageReceiverPathId;
   }

   /**
    * Setter for the message receiver path id
    * @param aInt
    */
   public void setMessageReceiverPathId(int aInt)
   {
      mMessageReceiverPathId = aInt;
   }

   /**
    * Returns true if the partnerlink, port type, and operation match
    * @param aCorrelatedReceive
    */
   public boolean matches(AeCorrelatedReceive aCorrelatedReceive)
   {
      return AeUtil.compareObjects(getPartnerLinkOperationKey(), aCorrelatedReceive.getPartnerLinkOperationKey()) &&
            AeUtil.compareObjects(getProcessName(), aCorrelatedReceive.getProcessName());
   }

   /**
    * Returns true if the message receiver correlates to the inbound receive.
    * @param aInboundReceive
    */
   public boolean correlatesTo(AeInboundReceive aInboundReceive)
   {
      if (isCorrelated() && matches(aInboundReceive))
      {
         return correlatedPropertiesMatch(aInboundReceive);
      }
      return false;
   }

   /**
    * Returns true if the correlated properties match. This accounts for the possibility
    * that the inbound receive data may have more properties correlated than the
    * queued object. This is possible if there are multiple receives in a process
    * for the same operation but which use different correlation sets. The inbound
    * correlation map will consist of all of the available properties for the given
    * message type but the individual receive would only have been queued with
    * the specified correlation sets.
    * @param aInboundReceive
    */
   protected boolean correlatedPropertiesMatch(AeInboundReceive aInboundReceive)
   {
      for (Iterator iter = getCorrelation().entrySet().iterator(); iter.hasNext();)
      {
         Map.Entry entry = (Entry) iter.next();
         Object otherValue = aInboundReceive.getCorrelation().get(entry.getKey());
         if (otherValue == null || !entry.getValue().equals(otherValue))
         {
            return false;
         }
      }

      return true;
   }

   /**
    * Setter for the process id
    * @param processId
    */
   public void setProcessId(long processId)
   {
      mProcessId = processId;
   }

   /**
    * Getter for the process id
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
      if (aObject instanceof AeMessageReceiver)
      {
         AeMessageReceiver other = (AeMessageReceiver) aObject;
         return getProcessId() == other.getProcessId()
            && getMessageReceiverPathId() == other.getMessageReceiverPathId();
      }
      return false;
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return mHashCode;
   }

   /**
    * @return Returns the group id.
    */
   public int getGroupId()
   {
      return mGroupId;
   }

   /**
    * @param aGroupId The group id to set.
    */
   protected void setGroupId(int aGroupId)
   {
      mGroupId = aGroupId;
   }

   /**
    * @return Returns the journalId.
    */
   public long getJournalId()
   {
      return mJournalId;
   }

   /**
    * @param aJournalId The journalId to set.
    */
   public void setJournalId(long aJournalId)
   {
      mJournalId = aJournalId;
   }
   
   /**
    * Getter for the port type
    */
   public QName getPortType()
   {
      return mPortType;
   }

   /**
    * @return Returns the concurrent.
    */
   public boolean isConcurrent()
   {
      return mConcurrent;
   }

   /**
    * @param aConcurrent The concurrent to set.
    */
   public void setConcurrent(boolean aConcurrent)
   {
      mConcurrent = aConcurrent;
   }
}
