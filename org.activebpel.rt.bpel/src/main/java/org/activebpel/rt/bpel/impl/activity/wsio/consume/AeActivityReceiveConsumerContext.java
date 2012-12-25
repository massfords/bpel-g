// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/wsio/consume/AeActivityReceiveConsumerContext.java,v 1.4 2007/11/15 02:24:40 mford Exp $
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
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.activity.AeActivityReceiveImpl;

/**
 * Implements access to a <code>receive</code> activity for a message data
 * consumer.
 */
public class AeActivityReceiveConsumerContext implements IAeMessageDataConsumerContext
{
   /** The <code>receive</code> activity implementation object. */
   private final AeActivityReceiveImpl mReceiveImpl;

   /**
    * Constructs the context for the given <code>receive</code> activity
    * implementation object.
    *
    * @param aReceiveImpl
    */
   public AeActivityReceiveConsumerContext(AeActivityReceiveImpl aReceiveImpl)
   {
      mReceiveImpl = aReceiveImpl;
   }

   /**
    * Returns the <code>receive</code> activity definition object.
    */
   protected AeActivityReceiveDef getDef()
   {
      return (AeActivityReceiveDef) getReceiveImpl().getDefinition();
   }

   /**
    * Returns the <code>receive</code> activity implementation object.
    */
   protected AeActivityReceiveImpl getReceiveImpl()
   {
      return mReceiveImpl;
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
      return getReceiveImpl();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumerContext#getVariable()
    */
   public IAeVariable getVariable()
   {
      return getReceiveImpl().findVariable(getDef().getVariable()); 
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumerContext#getMessageConsumerDef()
    */
   public IAeMessageDataConsumerDef getMessageConsumerDef()
   {
      return getDef();
   }
}
