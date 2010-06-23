// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/wsio/produce/AeToPartsMessageDataProducer.java,v 1.9 2007/11/15 02:24:40 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.wsio.produce;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartDef;
import org.activebpel.rt.bpel.impl.activity.assign.AeVirtualCopyOperation;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation;
import org.activebpel.rt.bpel.impl.activity.assign.IAeFrom;
import org.activebpel.rt.bpel.impl.activity.assign.IAeTo;
import org.activebpel.rt.bpel.impl.activity.assign.from.AeFromVariableElement;
import org.activebpel.rt.bpel.impl.activity.assign.from.AeFromVariableType;
import org.activebpel.rt.bpel.impl.activity.assign.to.AeToVariableMessagePart;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.message.IAeMessageData;

/**
 * Implements a message data producer that copies data according to a series of
 * <code>toPart</code> definitions.
 */
public class AeToPartsMessageDataProducer extends AeAbstractMessageDataProducer
{
   /** The list of copy operations. */
   private List mCopyOperations;

   /**
    * Constructs a <code>toParts</code> message data producer for the given
    * context.
    */
   public AeToPartsMessageDataProducer()
   {
      super();
   }

   /**
    * Returns an {@link IAeFrom} instance that copies from the BPEL variable
    * specified by the given <code>toPart</code> definition.
    *
    * @param aToPartDef
    * @param aContext
    * @throws AeBusinessProcessException
    */
   protected IAeFrom createCopyFrom(AeToPartDef aToPartDef, IAeMessageDataProducerContext aContext) throws AeBusinessProcessException
   {
      String variableName = aToPartDef.getFromVariable();

      IAeVariable variable = aContext.getBpelObject().findVariable(variableName);
      IAeFrom from = null;
      
      // By the time we get here, we know that the variable is either an element
      // variable or a schema type variable.
      if (variable.isElement())
      {
         from = new AeFromVariableElement(variableName);
      }
      else
      {
         from = new AeFromVariableType(variableName);
      }

      return from;
   }

   /**
    * Returns an {@link IAeTo} instance that copies to a part in the anonymous
    * variable that will wrap the outgoing message data.
    *
    * @param aToPartDef
    */
   protected IAeTo createCopyTo(AeMessagePartsMap aMap, AeToPartDef aToPartDef) throws AeBusinessProcessException
   {
      IAeVariable variable = getAnonymousVariable(aMap);
      String partName = aToPartDef.getPart();
      
      return new AeToVariableMessagePart(variable, partName);
   }

   /**
    * Returns a list of copy operations that copy from the
    * <code>fromVariable</code> variables defined by the <code>toPart</code>
    * definitions to an anonymous variable that will wrap the outgoing message
    * data.
    */
   protected List getCopyOperations(IAeMessageDataProducerContext aContext) throws AeBusinessProcessException
   {
      if (mCopyOperations == null)
      {
         mCopyOperations = new LinkedList();

         IAeMessageDataProducerDef def = aContext.getMessageDataProducerDef();
         for (Iterator i = def.getToPartsDef().getToPartDefs(); i.hasNext(); )
         {
            AeToPartDef toPartDef = (AeToPartDef) i.next();
            IAeFrom from = createCopyFrom(toPartDef, aContext);
            IAeTo to = createCopyTo(def.getProducerMessagePartsMap(), toPartDef);

            AeVirtualCopyOperation copyOperation = AeVirtualCopyOperation.createFromPartToPartOperation();
            copyOperation.setContext(getCopyOperationContext(aContext));
            copyOperation.setFrom(from);
            copyOperation.setTo(to);

            mCopyOperations.add(copyOperation);
         }
      }

      return mCopyOperations;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducer#produceMessageData(org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducerContext)
    */
   public IAeMessageData produceMessageData(IAeMessageDataProducerContext aContext) throws AeBusinessProcessException
   {
      IAeMessageDataProducerDef def = aContext.getMessageDataProducerDef();
      AeMessagePartsMap map = def.getProducerMessagePartsMap();
      
      // Initialize the anonymous variable to wrap a new message data.
      IAeMessageData messageData = createMessageData(map);
      getAnonymousVariable(map).setMessageData(messageData);
      
      // initialize all of the outgoing message parts
      for (Iterator iter = map.getPartNames(); iter.hasNext();)
      {
         String partName = (String) iter.next();
         getAnonymousVariable(map).initializeForAssign(partName);
      }

      // Copy to the message data parts.
      for (Iterator i = getCopyOperations(aContext).iterator(); i.hasNext(); )
      {
         IAeCopyOperation copyOperation = (IAeCopyOperation) i.next();
         copyOperation.execute();
      }

      return messageData;
   }
}
