// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/wsio/produce/AeAbstractMessageDataProducer.java,v 1.3 2008/02/17 21:37:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.wsio.produce;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessageDataFactory;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.impl.activity.assign.AeCopyOperationContext;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext;
import org.activebpel.rt.bpel.impl.activity.wsio.AeAnonymousMessageVariable;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.message.IAeMessageData;

/**
 * The base class for message data producers that provides access to a delegate
 * {@link org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducerContext}.
 */
public abstract class AeAbstractMessageDataProducer implements IAeMessageDataProducer
{
   /** An anonymous variable for wrapping outgoing message data. */
   private IAeVariable mAnonymousVariable;

   /** The copy operation context that accesses variables in the BPEL object. */
   private IAeCopyOperationContext mCopyOperationContext;

   /**
    * Constructs a message data producer with access to the given delegate
    * message data producer context.
    *
    */
   protected AeAbstractMessageDataProducer()
   {
   }

   /**
    * Returns a new instance of the outgoing message data.
    * @param aMap
    * @throws AeBusinessProcessException
    */
   protected IAeMessageData createMessageData(AeMessagePartsMap aMap) throws AeBusinessProcessException
   {
      // We won't know the outgoing message type if we are replying with a fault
      // that was not defined as part of the WSDL operation. 
      // TODO (MF) When static analysis is updated, then remove this check and throw.
      if (aMap == null)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeAbstractMessageDataProducer.ERROR_MissingMessagePartsMap")); //$NON-NLS-1$
      }

      return AeMessageDataFactory.instance().createMessageData(aMap.getMessageType());
   }

   /**
    * Returns an anonymous variable for wrapping outgoing message data.
    */
   protected IAeVariable getAnonymousVariable(AeMessagePartsMap aMap) throws AeBusinessProcessException
   {
      if (mAnonymousVariable == null)
      {
         mAnonymousVariable = new AeAnonymousMessageVariable(aMap);
      }
      
      return mAnonymousVariable;
   }

   /**
    * Returns the copy operation context that accesses variables in the BPEL
    * object.
    */
   protected IAeCopyOperationContext getCopyOperationContext(IAeMessageDataProducerContext aContext) throws AeBusinessProcessException
   {
      if (mCopyOperationContext == null)
      {
         mCopyOperationContext = new AeCopyOperationContext(aContext.getBpelObject());
      }
   
      return mCopyOperationContext;
   }
}
