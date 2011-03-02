// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/wsio/produce/AeEmptyMessageDataProducer.java,v 1.5 2008/02/17 21:37:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.wsio.produce;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.message.AeEmptyMessage;
import org.activebpel.rt.message.IAeMessageData;

/**
 * Implements a message data producer that produces an empty message.
 */
public class AeEmptyMessageDataProducer extends AeAbstractMessageDataProducer
{
   /**
    * Constructs a message data producer that produces an empty message for the
    * given context
    *
    */
   public AeEmptyMessageDataProducer()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducer#produceMessageData(org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducerContext)
    */
   public IAeMessageData produceMessageData(IAeMessageDataProducerContext aContext) throws AeBusinessProcessException
   {
      return new AeEmptyMessage(aContext.getMessageDataProducerDef().getProducerMessagePartsMap().getMessageType());
   }
}
