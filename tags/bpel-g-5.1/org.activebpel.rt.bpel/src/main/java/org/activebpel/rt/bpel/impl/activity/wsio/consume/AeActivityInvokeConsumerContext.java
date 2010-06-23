// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/wsio/consume/AeActivityInvokeConsumerContext.java,v 1.3 2007/11/15 02:24:40 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.wsio.consume;

import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.activity.AeActivityInvokeImpl;

/**
 * Implements access to an <code>invoke</code> activity for a message data
 * consumer.
 */
public class AeActivityInvokeConsumerContext implements IAeMessageDataConsumerContext
{
   /** The <code>invoke</code> activity implementation object. */
   private final AeActivityInvokeImpl mInvokeImpl;

   /**
    * Constructs the context for the given <code>invoke</code> activity
    * implementation object.
    *
    * @param aInvokeImpl
    */
   public AeActivityInvokeConsumerContext(AeActivityInvokeImpl aInvokeImpl)
   {
      mInvokeImpl = aInvokeImpl;
   }

   /**
    * Returns the <code>invoke</code> activity definition object.
    */
   protected AeActivityInvokeDef getDef()
   {
      return (AeActivityInvokeDef) getInvokeImpl().getDefinition();
   }

   /**
    * Returns the <code>invoke</code> activity implementation object.
    */
   protected AeActivityInvokeImpl getInvokeImpl()
   {
      return mInvokeImpl;
   }

   /*===========================================================================
    * IAeMessageDataConsumerContext methods
    *===========================================================================
    */

   /**
    * @see org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumerContext#getBpelObject()
    */
   public AeAbstractBpelObject getBpelObject()
   {
      return getInvokeImpl();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumerContext#getVariable()
    */
   public IAeVariable getVariable()
   {
      return getInvokeImpl().getOutputVariable(); 
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumerContext#getMessageConsumerDef()
    */
   public IAeMessageDataConsumerDef getMessageConsumerDef()
   {
      return getDef();
   }
}
