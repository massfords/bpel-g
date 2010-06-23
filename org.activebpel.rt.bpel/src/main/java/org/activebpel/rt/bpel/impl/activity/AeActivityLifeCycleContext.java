//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityLifeCycleContext.java,v 1.12 2008/03/11 03:05:23 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity;

import java.util.Date;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef;
import org.activebpel.rt.bpel.function.IAeFunctionFactory;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumer;
import org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumerContext;
import org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducer;
import org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducerContext;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.xml.schema.AeSchemaDuration;
import org.activebpel.wsio.invoke.IAeTransmission;
import org.activebpel.wsio.receive.AeMessageContext;

/**
 * This class provides state change support and impl object that can be 
 * used by extension activity adapter
 */
public class AeActivityLifeCycleContext implements IAeActivityLifeCycleContext
{
   /** Child Extension Activity Impl object */
   private AeActivityChildExtensionActivityImpl mChildExtensionActivityImpl;

   /**
    * Ctor
    * @param aImpl
    */
   public AeActivityLifeCycleContext(AeActivityChildExtensionActivityImpl aImpl)
   {
      setChildExtensionActivityImpl(aImpl);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleContext#complete()
    */
   public void complete() throws AeBusinessProcessException
   {
      AeActivityChildExtensionActivityImpl impl = getChildExtensionActivityImpl();
      if (impl.isTerminating())
         impl.terminationComplete();
      else
         impl.objectCompleted();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleContext#completedWithFault(org.activebpel.rt.bpel.IAeFault)
    */
   public void completedWithFault(IAeFault aFault) throws AeBusinessProcessException
   {
      AeActivityChildExtensionActivityImpl impl = (AeActivityChildExtensionActivityImpl) getChildExtensionActivityImpl();
      impl.objectCompletedWithFault(aFault);
   }


   /**
    * Getter for the impl
    */
   protected AeActivityChildExtensionActivityImpl getChildExtensionActivityImpl()
   {
      return mChildExtensionActivityImpl;
   }

   /**
	* Setter for ChildExtensionActivityImpl
    * @param aChildExtensionActivityImpl the childExtensionActivityImpl to set
    */
   protected void setChildExtensionActivityImpl(AeActivityChildExtensionActivityImpl aChildExtensionActivityImpl)
   {
      mChildExtensionActivityImpl = aChildExtensionActivityImpl;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleContext#queueReceiveData(org.activebpel.rt.message.IAeMessageData, org.activebpel.wsio.receive.AeMessageContext, boolean)
    */
   public void queueReceiveData(IAeMessageData aMessageData, AeMessageContext aMessageContext, boolean aOneway) throws AeBusinessProcessException
   {
      AeExtensionActivityUtil.queueReceiveData(getBpelObject(), aMessageData, aMessageContext, aOneway, getTransmission().getTransmissionId());
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleContext#execute(org.activebpel.rt.bpel.def.IAeExpressionDef, java.lang.Object, org.activebpel.rt.bpel.function.IAeFunctionFactory)
    */
   public Object execute(IAeExpressionDef aDef, Object aContext, IAeFunctionFactory aFunctionFactory) throws AeBusinessProcessException
   {
      return getChildExtensionActivityImpl().executeExpression(aDef, aContext, aFunctionFactory);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleContext#executeDurationExpression(org.activebpel.rt.bpel.def.IAeExpressionDef, java.lang.Object, org.activebpel.rt.bpel.function.IAeFunctionFactory)
    */
   public AeSchemaDuration executeDurationExpression(IAeExpressionDef aDef, Object aContext, IAeFunctionFactory aFactory) throws AeBusinessProcessException
   {
      return getChildExtensionActivityImpl().executeDurationExpression(aDef, aContext, aFactory);
   }
   
   public Date executeDeadlineExpression(IAeExpressionDef aDef, Object aContext, IAeFunctionFactory aFactory) throws AeBusinessProcessException
   {
      return getChildExtensionActivityImpl().executeDeadlineExpression(aDef, aContext, aFactory);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleContext#getContextFunctionFactory()
    */
   public IAeFunctionFactory getContextFunctionFactory()
   {
      return getChildExtensionActivityImpl();
   }


   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleContext#findVariable(java.lang.String)
    */
   public IAeVariable findVariable(String aVariable)
   {
      return getChildExtensionActivityImpl().findVariable(aVariable);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleContext#consumeMessage(org.activebpel.rt.bpel.impl.activity.IAeMessageConsumerParentAdapter, org.activebpel.rt.message.IAeMessageData)
    */
   public void consumeMessage(final IAeMessageConsumerParentAdapter aConsumerParent,
         IAeMessageData aMessageData) throws AeBusinessProcessException
   {
      IAeMessageDataConsumer consumer = aConsumerParent.getMessageDataConsumer();
      
      IAeMessageDataConsumerContext context = new IAeMessageDataConsumerContext()
      {

         /**
          * @see org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumerContext#getBpelObject()
          */
         public AeAbstractBpelObject getBpelObject()
         {
            return getChildExtensionActivityImpl();
         }

         /**
          * @see org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumerContext#getMessageConsumerDef()
          */
         public IAeMessageDataConsumerDef getMessageConsumerDef()
         {
            return aConsumerParent.getMessageDataConsumerDef();
         }

         /**
          * @see org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumerContext#getVariable()
          */
         public IAeVariable getVariable()
         {
            return findVariable(getMessageConsumerDef().getMessageDataConsumerVariable().getName());
         }
      };
      
      consumer.consumeMessageData(aMessageData, context);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleContext#produceMessage(org.activebpel.rt.bpel.impl.activity.IAeMessageProducerParentAdapter)
    */
   public IAeMessageData produceMessage(final IAeMessageProducerParentAdapter aProducer)
         throws AeBusinessProcessException
   {
      IAeMessageDataProducer producer = aProducer.getMessageDataProducer();
      IAeMessageDataProducerContext context = new IAeMessageDataProducerContext()
      {

         /**
          * @see org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducerContext#getBpelObject()
          */
         public AeAbstractBpelObject getBpelObject()
         {
            return getChildExtensionActivityImpl();
         }

         /**
          * @see org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducerContext#getMessageDataProducerDef()
          */
         public IAeMessageDataProducerDef getMessageDataProducerDef()
         {
            return aProducer.getMessageDataProducerDef();
         }

         /**
          * @see org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducerContext#getVariable()
          */
         public IAeVariable getVariable()
         {
            return findVariable(getMessageDataProducerDef().getMessageDataProducerVariable().getName());
         }
      };
      return producer.produceMessageData(context);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleContext#getProcess()
    */
   public IAeBusinessProcessInternal getProcess()
   {
      return mChildExtensionActivityImpl.getProcess();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleContext#getBpelObject()
    */
   public IAeBpelObject getBpelObject()
   {
      return mChildExtensionActivityImpl;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleContext#getTransmission()
    */
   public IAeTransmission getTransmission()
   {
      return mChildExtensionActivityImpl;
   }
}
