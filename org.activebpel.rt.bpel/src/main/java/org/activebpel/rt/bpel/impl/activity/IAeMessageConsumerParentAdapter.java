//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/IAeMessageConsumerParentAdapter.java,v 1.1 2007/11/15 02:24:40 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity; 

import org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef;
import org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumer;

/**
 * Parent interface for an activity that consumes messages. Examples of this
 * are the standard IMA (inbound message activities) as well as request-response
 * invokes. Other examples may be extension activities that want to receive 
 * messages or responses from the outside world.  
 */
public interface IAeMessageConsumerParentAdapter extends IAeImplAdapter
{
   /**
    * Setter for the message data consumer.
    * @param aMessageDataConsumer
    */
   public void setMessageDataConsumer(IAeMessageDataConsumer aMessageDataConsumer);
   
   /**
    * Getter for the message consumer
    */
   public IAeMessageDataConsumer getMessageDataConsumer();
   
   /**
    * Gets the def
    */
   public IAeMessageDataConsumerDef getMessageDataConsumerDef();

}
 