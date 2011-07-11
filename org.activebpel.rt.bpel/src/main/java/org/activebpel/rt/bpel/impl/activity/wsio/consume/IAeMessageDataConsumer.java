// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/wsio/consume/IAeMessageDataConsumer.java,v 1.2 2007/11/15 02:24:40 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.wsio.consume;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.message.IAeMessageData;

/**
 * Defines interface for consuming incoming message data.
 */
public interface IAeMessageDataConsumer
{
   /**
    * Consumes the given incoming message data.
    *
    * @param aMessageData
    * @param aContext
    * @throws AeBusinessProcessException
    */
   public void consumeMessageData(IAeMessageData aMessageData, IAeMessageDataConsumerContext aContext) throws AeBusinessProcessException;
}
