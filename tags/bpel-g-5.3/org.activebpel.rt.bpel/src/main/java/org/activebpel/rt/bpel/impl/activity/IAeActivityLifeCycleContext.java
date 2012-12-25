//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/IAeActivityLifeCycleContext.java,v 1.9 2008/03/11 03:05:22 mford Exp $
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
import org.activebpel.rt.bpel.function.IAeFunctionFactory;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.xml.schema.AeSchemaDuration;
import org.activebpel.wsio.invoke.IAeTransmission;
import org.activebpel.wsio.receive.AeMessageContext;

/**
 * This interface provides a context for the extension activity adapter
 */
public interface IAeActivityLifeCycleContext
{
   /**
    * Set the state of extension activity to complete
    * @throws AeBusinessProcessException
    */
   public void complete() throws AeBusinessProcessException;

   /**
    * Sets the state of extension activity to completed with fault
    * @param aFault
    * @throws AeBusinessProcessException
    */
   public void completedWithFault(IAeFault aFault) throws AeBusinessProcessException;
   

   /**
    * Calls queueReceiveData() on engine
    * @param aMessageData
    * @param aMessageContext
    * @param aOneway
    * @throws AeBusinessProcessException
    */
   public void queueReceiveData(IAeMessageData aMessageData, AeMessageContext aMessageContext, boolean aOneway) throws AeBusinessProcessException;
   
   /**
    * Executes the expression and returns the result 
    * @param aDef
    * @param aContext
    * @param aFactory
    * @throws AeBusinessProcessException
    */
   public Object execute(IAeExpressionDef aDef, Object aContext, IAeFunctionFactory aFactory) throws AeBusinessProcessException;

   /**
    * executes duration expression 
    * @param aDef
    * @param aContext
    * @param aFactory
    */
   public AeSchemaDuration executeDurationExpression(IAeExpressionDef aDef, Object aContext, IAeFunctionFactory aFactory) throws AeBusinessProcessException;
   
   /**
    * executes deadline expression 
    * @param aDef
    * @param aContext
    * @param aFactory
    * @throws AeBusinessProcessException
    */
   public Date executeDeadlineExpression(IAeExpressionDef aDef, Object aContext, IAeFunctionFactory aFactory) throws AeBusinessProcessException;

   /**
    * Returns the system function factory 
    */
   public IAeFunctionFactory getContextFunctionFactory();
   
   /**
    * Finds the variable within the given name, walking up parent hierarchy until
    * resolved
    * @param aVariable
    */
   public IAeVariable findVariable(String aVariable);
   
   /**
    * Produces the message data given the parent of the message producer.
    * @param aProducer
    * @throws AeBusinessProcessException
    */
   public IAeMessageData produceMessage(IAeMessageProducerParentAdapter aProducer) throws AeBusinessProcessException;
   
   /**
    * Consumes the message data given the parent of the message consumer
    * @param aConsumer
    * @param aMessageData
    * @throws AeBusinessProcessException
    */
   public void consumeMessage(IAeMessageConsumerParentAdapter aConsumer, IAeMessageData aMessageData) throws AeBusinessProcessException;
   
   /**
    * Delegates the call to parent of Impl where this context is created  
    */
   public IAeBusinessProcessInternal getProcess();
   
   /**
    * @return bpel object on which this context is present 
    */
   public IAeBpelObject getBpelObject();
   
   /**
    * Returns the transmission associated with this context. Maintains an id that
    * is used to track a transmission or other behavior that an extension 
    * activity doesn't want to repeat in the event of a crash and recovery.
    */
   public IAeTransmission getTransmission();
}
