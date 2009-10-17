// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/AePersistedMessageReceiver.java,v 1.3 2006/09/22 19:56:15 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage;

import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AePartnerLinkOpKey;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;

/**
 * This class is used by the SQL version of the QueueStorage to conveniently
 * return AeMessageReceiver objects with a database ID attached.  This database
 * ID is often used to delete the receive object from the database.
 */
public class AePersistedMessageReceiver extends AeMessageReceiver
{
   /** The queued receive id from the database. */
   protected int mQueuedReceiveId;

   /**
    * Constructs a SQL message receiver.
    * @param aQueuedReceiveId The database ID.
    * @param aProcessId The process id.
    * @param aProcessName Qualified name of the process
    * @param aPartnerLinkOpKey The partner link op key.
    * @param aPortType The port type.
    * @param aCorrelation The correlation set.
    * @param aMessageReceiverPathId The message receiver path id.
    * @param aGroupId The group id of the message receiver.
    * @param aConcurrent True if message receiver supports concurrent messages
    */
   public AePersistedMessageReceiver(int aQueuedReceiveId, long aProcessId, QName aProcessName,
         AePartnerLinkOpKey aPartnerLinkOpKey, QName aPortType, Map aCorrelation,
         int aMessageReceiverPathId, int aGroupId, boolean aConcurrent)
   {
      super(aProcessId, aProcessName, aPartnerLinkOpKey, aPortType, aCorrelation,
            aMessageReceiverPathId, aGroupId, aConcurrent);
      mQueuedReceiveId = aQueuedReceiveId;
   }

   /**
    * Returns the queued receive database id.
    */
   public int getQueuedReceiveId()
   {
      return mQueuedReceiveId;
   }
}
