// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/wsio/consume/AeAbstractMessageDataConsumer.java,v 1.3 2008/02/17 21:37:12 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.wsio.consume;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.impl.activity.assign.AeAtomicCopyOperationContext;
import org.activebpel.rt.bpel.impl.activity.wsio.AeAnonymousMessageVariable;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.message.IAeMessageData;

/**
 * The base class for message data consumers that provides access to a delegate
 * {@link org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumerContext}.
 */
public abstract class AeAbstractMessageDataConsumer implements IAeMessageDataConsumer
{
   /**
    * The copy operation context that accesses variables in the BPEL object and
    * that can rollback changes made made to those variables.
    */
   private AeAtomicCopyOperationContext mAtomicCopyOperationContext;

   /**
    * Constructs a message data consumer with access to the given delegate
    * message data consumer context.
    *
    */
   protected AeAbstractMessageDataConsumer()
   {
   }

   /**
    * Returns an anonymous variable that wraps the given message data.
    *
    * @param aMessageData
    */
   protected IAeVariable createAnonymousVariable(IAeMessageData aMessageData, AeMessagePartsMap aMap) throws AeBusinessProcessException
   {
      IAeVariable variable = new AeAnonymousMessageVariable(aMap);
      variable.setMessageData(aMessageData);
      
      return variable;
   }

   /**
    * Returns the copy operation context that accesses variables in the BPEL
    * object and that can rollback changes made to those variables.
    */
   protected AeAtomicCopyOperationContext getAtomicCopyOperationContext(IAeMessageDataConsumerContext aContext)
   {
      if (mAtomicCopyOperationContext == null)
      {
         mAtomicCopyOperationContext = new AeAtomicCopyOperationContext(aContext.getBpelObject());
      }
      
      return mAtomicCopyOperationContext;
   }
}
