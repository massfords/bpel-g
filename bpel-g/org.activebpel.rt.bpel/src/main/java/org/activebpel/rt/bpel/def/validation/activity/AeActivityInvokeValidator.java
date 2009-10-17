//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivityInvokeValidator.java,v 1.7 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.util.AeUtil;

/**
 * model provides validation for the invoke activity
 */
public class AeActivityInvokeValidator extends AeWSIOActivityValidator
{
   /** variable used for the invoke's request data */
   private AeVariableValidator mInputVariable;
   /** variable used for the invoke's response data */
   private AeVariableValidator mOutputVariable;

   /**
    * ctor
    * @param aDef
    */
   public AeActivityInvokeValidator(AeActivityInvokeDef aDef)
   {
      super(aDef);
   }
   
   /**
    * validates:
    * 1. input variable exists
    * 2. output variable, if defined, exists
    * @see org.activebpel.rt.bpel.def.validation.activity.AeWSIOActivityValidator#validate()
    */
   public void validate()
   {
      mInputVariable = getVariableValidator(getDef().getInputVariable(), null, true, AeVariableValidator.VARIABLE_READ_WSIO);
      mOutputVariable = getVariableValidator(getDef().getOutputVariable(), null, true, AeVariableValidator.VARIABLE_WRITE_WSIO);
      
      super.validate();

      AeMessagePartsMap producerMap = getProducerMessagePartsMap();
      if (producerMap == null)
      {
         // if the invoke is missing its parts map, then there are a two things that 
         // could be wrong
         // 1. bpws 1.1 and missing input variable or output variable, both of which are used
         //    to identify the operation being invoked
         // 2. problem in resolving plink, port type, or operation
         //
         // 
         // This checks for case 1, an empty producer strategy means invalid mix of input/output/fromPart/toPart
         if (AeUtil.isNullOrEmpty(getDef().getMessageDataProducerStrategy()))
         {
            // use this call to report the error (missing input or output variable)
            validateMessageProducerStrategy(producerMap, mInputVariable);
         }
         else
         {
            // if the port type is null then we'll report an error on the plink validator
            // otherwise, report an error that we couldn't find an operation with the given input and output data
            if (findPortType() != null)
            {
               QName inputType = mInputVariable != null ? mInputVariable.getType() : EMPTY_QNAME;
               QName outputType = mOutputVariable != null ? mOutputVariable.getType() : EMPTY_QNAME;
               getReporter().reportProblem( BPEL_OPERATION_INOUT_NOT_FOUND_CODE, 
                     ERROR_OPERATION_INOUT_NOT_FOUND,
                     new String[] { getOperation(),
                                    inputType == null ? "" : getNSPrefix( inputType.getNamespaceURI()), //$NON-NLS-1$
                                    inputType == null ? "" : inputType.getLocalPart(), //$NON-NLS-1$
                                    outputType == null ? "" : getNSPrefix( outputType.getNamespaceURI()), //$NON-NLS-1$
                                    outputType == null ? "" : outputType.getLocalPart() //$NON-NLS-1$
                                    },
                     getDefinition());
            }
         }
      }
      else
      {
         validateMessageProducerStrategy(producerMap, mInputVariable);
         
         AeMessagePartsMap consumerMap = getConsumerMessagePartsMap();
         if (consumerMap != null)
         {
            validateMessageConsumerStrategy(consumerMap, mOutputVariable);
         }
      }
   }
   
   /**
    * Getter for the input variable
    */
   public AeVariableValidator getInputVariableModel()
   {
      return mInputVariable;
   }
   
   /**
    * Getter for the output variable
    */
   public AeVariableValidator getOutputVariableModel()
   {
      return mOutputVariable;
   }

   /**
    * Getter for the def
    */
   protected AeActivityInvokeDef getDef()
   {
      return (AeActivityInvokeDef) getDefinition();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAePartnerLinkOperationUser#isMyRole()
    */
   public boolean isMyRole()
   {
      return false;
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAePartnerLinkOperationUser#isPartnerRole()
    */
   public boolean isPartnerRole()
   {
      return true;
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAePartnerLinkOperationUser#getOperation()
    */
   public String getOperation()
   {
      return getDef().getOperation();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAePartnerLinkOperationUser#getPortType()
    */
   public QName getPortType()
   {
      return getDef().getPortType();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.IAeCorrelationUser#isPatternRequired()
    */
   public boolean isPatternRequired()
   {
      return true;
   }
}
 