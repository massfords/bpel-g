// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/wsio/produce/AeVariableMessageDataProducer.java,v 1.8 2008/02/17 21:37:08 mford Exp $
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
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.message.IAeMessageData;

/**
 * Implements a message data producer that copies data from a variable.
 */
public class AeVariableMessageDataProducer extends AeAbstractMessageDataProducer
{
   /**
    * Constructs a variable message data producer for the given
    * context activity.
    *
    */
   public AeVariableMessageDataProducer()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducer#produceMessageData(org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducerContext)
    */
   public IAeMessageData produceMessageData(IAeMessageDataProducerContext aContext) throws AeBusinessProcessException
   {
      IAeVariable variable = aContext.getVariable();
      IAeMessageData result = null;

      // By the time we get here, we know that this is either a message type
      // variable or an element variable.
      if (variable.isMessageType())
      {
         result = (IAeMessageData) variable.getMessageData();
      }
      else
      {
         AeMessagePartsMap partsMap = aContext.getMessageDataProducerDef().getProducerMessagePartsMap();
         String partName = (String) partsMap.getPartNames().next();
         result = createMessageData(partsMap);
         result.setData(partName, variable.getElementData().getOwnerDocument());
         result.setAttachmentContainer(variable.getAttachmentData());
      }

      return (IAeMessageData) result.clone();
   }
}
