// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/wsio/produce/IAeMessageDataProducer.java,v 1.3 2007/11/15 02:24:40 mford Exp $
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
import org.activebpel.rt.message.IAeMessageData;

/**
 * Defines interface for producing outgoing message data.
 */
public interface IAeMessageDataProducer
{
   /**
    * Produces outgoing message data. The data produced must not be modifiable
    * by any of the other activities within the process. As such, it should be
    * a clone of any data that was used to construct the message data. 
    * 
    * @param aContext
    * @return IAeMessageData 
    */
   public IAeMessageData produceMessageData(IAeMessageDataProducerContext aContext) throws AeBusinessProcessException;
}
