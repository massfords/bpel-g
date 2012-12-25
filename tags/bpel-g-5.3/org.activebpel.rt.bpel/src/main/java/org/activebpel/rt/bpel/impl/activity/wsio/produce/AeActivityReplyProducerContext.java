// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/wsio/produce/AeActivityReplyProducerContext.java,v 1.4 2007/11/15 02:24:40 mford Exp $
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
import org.activebpel.rt.bpel.def.activity.AeActivityReplyDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.activity.AeActivityReplyImpl;

/**
 * Implements access to an <code>reply</code> activity for a message data
 * producer.
 */
public class AeActivityReplyProducerContext implements IAeMessageDataProducerContext
{
   /** The <code>reply</code> activity implementation object. */
   private final AeActivityReplyImpl mReplyImpl;

   /**
    * Constructs the context for the given <code>reply</code> activity
    * implementation object.
    *
    * @param aReplyImpl
    */
   public AeActivityReplyProducerContext(AeActivityReplyImpl aReplyImpl)
   {
      mReplyImpl = aReplyImpl;
   }

   /**
    * Returns the <code>reply</code> activity definition object.
    */
   protected AeActivityReplyDef getDef()
   {
      return (AeActivityReplyDef) getReplyImpl().getDefinition();
   }

   /**
    * Returns the <code>reply</code> activity implementation object.
    */
   protected AeActivityReplyImpl getReplyImpl()
   {
      return mReplyImpl;
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
      return getReplyImpl();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducerContext#getVariable()
    */
   public IAeVariable getVariable()
   {
      return getReplyImpl().findVariable(getDef().getVariable());
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducerContext#getMessageDataProducerDef()
    */
   public IAeMessageDataProducerDef getMessageDataProducerDef()
   {
      return getDef();
   }
}
