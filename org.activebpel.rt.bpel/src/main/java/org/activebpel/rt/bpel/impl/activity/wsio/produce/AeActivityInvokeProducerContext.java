// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/wsio/produce/AeActivityInvokeProducerContext.java,v 1.4 2007/11/15 02:24:40 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.wsio.produce;

import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.activity.AeActivityInvokeImpl;

/**
 * Implements access to an <code>invoke</code> activity for a message data
 * producer.
 */
public class AeActivityInvokeProducerContext implements IAeMessageDataProducerContext
{
   /** The <code>invoke</code> activity implementation object. */
   private final AeActivityInvokeImpl mInvokeImpl;

   /**
    * Constructs the context for the given <code>invoke</code> activity
    * implementation object.
    *
    * @param aInvokeImpl
    */
   public AeActivityInvokeProducerContext(AeActivityInvokeImpl aInvokeImpl)
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
    * IAeMessageDataProducerContext methods
    *===========================================================================
    */

   /**
    * @see org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducerContext#getBpelObject()
    */
   public AeAbstractBpelObject getBpelObject()
   {
      return getInvokeImpl();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducerContext#getVariable()
    */
   public IAeVariable getVariable()
   {
      return getInvokeImpl().findVariable(getDef().getInputVariable());
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducerContext#getMessageDataProducerDef()
    */
   public IAeMessageDataProducerDef getMessageDataProducerDef()
   {
      return getDef();
   }
}
