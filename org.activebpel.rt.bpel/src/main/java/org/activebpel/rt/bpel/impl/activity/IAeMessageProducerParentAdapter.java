//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/IAeMessageProducerParentAdapter.java,v 1.1 2007/11/15 02:24:40 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity; 

import org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef;
import org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducer;

/**
 * Interface for an activity that is the parent of a message producer. Examples
 * of this are the invoke and reply activities. In both cases, the activity 
 * has an object that producers a message. This message may be produced by using
 * the value in a variable or executing some toParts.
 */
public interface IAeMessageProducerParentAdapter extends IAeImplAdapter
{
   /**
    * Setter for the message data producer.
    * @param aMessageDataProducer
    */
   public void setMessageDataProducer(IAeMessageDataProducer aMessageDataProducer);
   
   /**
    * Getter for the producer
    */
   public IAeMessageDataProducer getMessageDataProducer();
   
   /**
    * Gets the def
    */
   public IAeMessageDataProducerDef getMessageDataProducerDef();
}
 