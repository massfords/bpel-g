// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeMessageValidator.java,v 1.5 2008/02/29 04:09:36 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl; 

import java.util.List;

import org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef;
import org.activebpel.rt.message.IAeMessageData;

/**
 * interface for message validation encapsulates whether or not empty parts are allowed
 */
public interface IAeMessageValidator
{
   /**
    * Validates the message data being transmitted by the engine
    * @param aProcess reference to process used in validaton
    * @param aDef def that defines the operation and metadata for the message
    * @param aMessageData the data for the message
    * @param aPolicies a List of policies on the endpoint
    * @throws AeBpelException
    */
   public void validateOutbound(IAeBusinessProcessInternal aProcess, IAeMessageDataProducerDef aDef, IAeMessageData aMessageData, List aPolicies) throws AeBpelException;


   /**
    * Validates the message data being received by the engine
    * @param aProcess reference to process used in validaton
    * @param aDef def that defines the operation and metadata for the message
    * @param aMessageData the data for the message
    * @param aPolicies a List of policies on the endpoint
    * @throws AeBpelException
    */
   public void validateInbound(IAeBusinessProcessInternal aProcess, IAeMessageDataConsumerDef aDef, IAeMessageData aMessageData, List aPolicies) throws AeBpelException;
}
 