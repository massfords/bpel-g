//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeWSIOActivityImpl.java,v 1.4 2008/02/17 21:37:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity; 

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.IAeMessageValidator;
import org.activebpel.rt.bpel.impl.activity.support.IAeCorrelations;
import org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumer;
import org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducer;

/**
 * Base class for some of the WSIO activities.
 */
public abstract class AeWSIOActivityImpl extends AeActivityImpl implements IAeWSIOActivity
{
   /** request correlations */
   protected IAeCorrelations mRequestCorrelations;
   /** response correlations */
   protected IAeCorrelations mResponseCorrelations;
   /** validates the variable */
   private IAeMessageValidator mMessageValidator;
   /** consumes incoming message data */
   private IAeMessageDataConsumer mMessageDataConsumer;
   /** produces outgoing message data */
   private IAeMessageDataProducer mMessageDataProducer;

   /**
    * Ctor
    * @param aActivityDef
    * @param aParent
    */
   public AeWSIOActivityImpl(AeActivityDef aActivityDef, IAeActivityParent aParent)
   {
      super(aActivityDef, aParent);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeWSIOActivity#setMessageValidator(org.activebpel.rt.bpel.impl.IAeMessageValidator)
    */
   public void setMessageValidator(IAeMessageValidator aValidator)
   {
      mMessageValidator = aValidator;
   }

   /**
    * Getter for the message validator
    */
   protected IAeMessageValidator getMessageValidator()
   {
      return mMessageValidator;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeWSIOActivity#setRequestCorrelations(org.activebpel.rt.bpel.impl.activity.support.IAeCorrelations)
    */
   public void setRequestCorrelations(IAeCorrelations aCorrelations)
   {
      mRequestCorrelations = aCorrelations;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeWSIOActivity#setResponseCorrelations(org.activebpel.rt.bpel.impl.activity.support.IAeCorrelations)
    */
   public void setResponseCorrelations(IAeCorrelations aCorrelations)
   {
      mResponseCorrelations = aCorrelations;
   }
   
   /**
    * Getter for the request correlations
    */
   protected IAeCorrelations getRequestCorrelations()
   {
      return mRequestCorrelations;
   }
   
   /**
    * Getter for the response correlations
    */
   protected IAeCorrelations getResponseCorrelations()
   {
      return mResponseCorrelations;
   }

   /**
    * Setter for the message consumer
    * @param aMessageDataConsumer
    */
   public void setMessageDataConsumer(IAeMessageDataConsumer aMessageDataConsumer)
   {
      mMessageDataConsumer = aMessageDataConsumer;
   }

   /**
    * Getter for the message data consumer
    */
   public IAeMessageDataConsumer getMessageDataConsumer()
   {
      return mMessageDataConsumer;
   }

   /**
    * Setter for the message producer
    * @param aMessageDataProducer
    */
   public void setMessageDataProducer(IAeMessageDataProducer aMessageDataProducer)
   {
      mMessageDataProducer = aMessageDataProducer;
   }

   /**
    * Getter for the message data producer
    */
   public IAeMessageDataProducer getMessageDataProducer()
   {
      return mMessageDataProducer;
   }
}
 