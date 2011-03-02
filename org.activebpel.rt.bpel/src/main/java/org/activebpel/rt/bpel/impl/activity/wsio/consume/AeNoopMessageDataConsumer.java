// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/wsio/consume/AeNoopMessageDataConsumer.java,v 1.2 2007/11/15 02:24:40 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.wsio.consume;

import org.activebpel.rt.message.IAeMessageData;

/**
 * Implements a message data consumer that does nothing.
 */
public class AeNoopMessageDataConsumer implements IAeMessageDataConsumer
{
   /**
    * Constructs a noop message data consumer for the given BPEL implementation
    * object.
    */
   public AeNoopMessageDataConsumer()
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumer#consumeMessageData(org.activebpel.rt.message.IAeMessageData, org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumerContext)
    */
   public void consumeMessageData(IAeMessageData aMessageData, IAeMessageDataConsumerContext aContext)
   {
      // Do nothing.
   }
}
