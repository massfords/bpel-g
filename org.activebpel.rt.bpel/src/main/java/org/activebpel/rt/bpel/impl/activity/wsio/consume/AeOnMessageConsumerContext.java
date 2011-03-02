// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/wsio/consume/AeOnMessageConsumerContext.java,v 1.4 2007/11/15 02:24:40 mford Exp $
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
import org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.activity.support.AeOnMessage;

/**
 * Implements access to an <code>onMessage</code> implementation object for a
 * message data consumer.
 */
public class AeOnMessageConsumerContext implements IAeMessageDataConsumerContext
{
   /** The <code>onMessage</code> implementation object. */
   private final AeOnMessage mOnMessage;

   /**
    * Constructs the context for the given <code>onMessage</code> implementation
    * object.
    *
    * @param aOnMessage
    */
   public AeOnMessageConsumerContext(AeOnMessage aOnMessage)
   {
      mOnMessage = aOnMessage;
   }

   /**
    * Returns the <code>onMessage</code> definition object.
    */
   protected AeOnMessageDef getDef()
   {
      return (AeOnMessageDef) getOnMessage().getDefinition();
   }

   /**
    * Returns the <code>onMessage</code> implementation object.
    */
   protected AeOnMessage getOnMessage()
   {
      return mOnMessage;
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
      return getOnMessage();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumerContext#getVariable()
    */
   public IAeVariable getVariable()
   {
      return getOnMessage().findVariable(getDef().getVariable()); 
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumerContext#getMessageConsumerDef()
    */
   public IAeMessageDataConsumerDef getMessageConsumerDef()
   {
      return getDef();
   }
}
