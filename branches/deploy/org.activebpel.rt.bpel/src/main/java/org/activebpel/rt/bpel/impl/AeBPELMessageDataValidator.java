// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeBPELMessageDataValidator.java,v 1.10 2008/02/29 04:09:36 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl; 

import java.util.List;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.IAeMonitorListener;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef;
import org.activebpel.rt.bpel.impl.activity.IAeImplAdapter;
import org.activebpel.rt.message.AeMessageDataValidator;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;

/**
 * Subclass of AeMessageDataValidator that validates the message iff the engine config has message
 * validation enabled. Any faults during validation are rethrown as the standard bpel validation fault. 
 */
public class AeBPELMessageDataValidator implements IAeMessageValidator, IAeImplAdapter
{
   /** if true, the validator allows for empty parts or extra parts */
   private boolean mRelaxedValidation;
   
   /**
    * Ctor
    * @param aRelaxedValidationFlag - controls whether we'll fault if there are parts missing or extra parts
    */
   public AeBPELMessageDataValidator(boolean aRelaxedValidationFlag)
   {
      setRelaxedValidation(aRelaxedValidationFlag);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeMessageValidator#validateInbound(org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal, org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef, org.activebpel.rt.message.IAeMessageData, java.util.List)
    */
   public void validateInbound(IAeBusinessProcessInternal aProcess,
         IAeMessageDataConsumerDef aDef, IAeMessageData aMessageData,
         List aPolicies) throws AeBpelException
   {
      validate(aProcess, aDef.getConsumerOperation(), aMessageData, aDef.getConsumerMessagePartsMap(), aPolicies, false);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeMessageValidator#validateOutbound(org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal, org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef, org.activebpel.rt.message.IAeMessageData, java.util.List)
    */
   public void validateOutbound(IAeBusinessProcessInternal aProcess,
         IAeMessageDataProducerDef aDef, IAeMessageData aMessageData,
         List aPolicies) throws AeBpelException
   {
      validate(aProcess, aDef.getProducerOperation(), aMessageData, aDef.getProducerMessagePartsMap(), aPolicies, true);
   }

   /**
    * Performs the validation on the message
    * @param aProcess
    * @param aOperation
    * @param aMessageData
    * @param aMessagePartsMap
    * @param aPolicies
    * @param aIsSend
    * @throws AeBpelException
    */
   private void validate(IAeBusinessProcessInternal aProcess, String aOperation, IAeMessageData aMessageData,
         AeMessagePartsMap aMessagePartsMap, List aPolicies, boolean aIsSend) throws AeBpelException
   {
      boolean validationDefault = aProcess.getEngine().getEngineConfiguration().validateServiceMessages();
      if (! AeValidationPolicy.isValidateEnabled(aPolicies, aOperation, aIsSend, validationDefault))
         return;
      long start = System.currentTimeMillis();

      Exception cause = null;
      String errorMessage = null;
      try
      {
         AeBPELExtendedWSDLDef def = AeWSDLDefHelper.getWSDLDefinitionForMsg(aProcess.getProcessPlan(), aMessageData.getMessageType());
         AeMessageDataValidator messageValidator = new AeMessageDataValidator(isRelaxedValidation(), aMessagePartsMap);
         errorMessage = messageValidator.validate(aMessageData, def, aProcess.getEngine().getTypeMapping());
      }
      catch(AeException e)
      {
         cause = e;
      }
      finally
      {
         // Send monitor event indicating time to validate message
         long loadTime = System.currentTimeMillis() - start;
         aProcess.getEngine().fireMonitorEvent(IAeMonitorListener.MONITOR_MESSAGE_VALIDATION_TIME, loadTime);
      }
      if (errorMessage != null)
      {
         String msg = AeMessages.format("AeBPELMessageDataValidator.InvalidMessage", new Object[] {aMessageData.getMessageType(), errorMessage}); //$NON-NLS-1$

         IAeFaultFactory factory = AeFaultFactory.getFactory(aProcess.getBPELNamespace());
         throw new AeBpelException(msg, factory.getInvalidVariables(msg), cause);    
      }
   }

   /**
    * @return Returns the relaxedValidation.
    */
   public boolean isRelaxedValidation()
   {
      return mRelaxedValidation;
   }

   /**
    * @param aRelaxedValidation The relaxedValidation to set.
    */
   public void setRelaxedValidation(boolean aRelaxedValidation)
   {
      mRelaxedValidation = aRelaxedValidation;
   }
}