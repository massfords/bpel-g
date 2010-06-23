//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/scope/AeWSBPELCatchValidator.java,v 1.7 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.scope; 

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.def.AeCatchDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.bpel.impl.activity.support.AeFault;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * Adds the validation to handle faultVariable resolution rules
 */
public class AeWSBPELCatchValidator extends AeBaseCatchValidator
{
   /** valid catch def patterns for WS-BPEL */
   private static final Set WSBPEL_PATTERNS = new HashSet();

   static 
   {
      // catch w/ name only
      AeCatchSpec spec = new AeCatchSpec();
      spec.setFaultName();
      WSBPEL_PATTERNS.add(spec);
      
      // catch w/ variable + message 
      spec = new AeCatchSpec();
      spec.setFaultVariable();
      spec.setMessageType();
      WSBPEL_PATTERNS.add(spec);
      
      // catch w/ variable + message + name 
      spec = new AeCatchSpec();
      spec.setFaultName();
      spec.setFaultVariable();
      spec.setMessageType();
      WSBPEL_PATTERNS.add(spec);

      // catch w/ variable + element 
      spec = new AeCatchSpec();
      spec.setFaultVariable();
      spec.setElementType();
      WSBPEL_PATTERNS.add(spec);

      // catch w/ variable + element + name 
      spec = new AeCatchSpec();
      spec.setFaultName();
      spec.setFaultVariable();
      spec.setElementType();
      WSBPEL_PATTERNS.add(spec);
   };

   /**
    * Ctor accepts def
    * @param aDef
    */
   public AeWSBPELCatchValidator(AeCatchDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.scope.AeBaseCatchValidator#getPatterns()
    */
   protected Set getPatterns()
   {
      return WSBPEL_PATTERNS;
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#add(org.activebpel.rt.bpel.def.validation.AeBaseValidator)
    */
   public void add(AeBaseValidator aModel)
   {
      if (aModel instanceof AeVariableValidator)
      {
         aModel.setParent(this);
         setVariable((AeVariableValidator) aModel);
      }
      else
      {
         super.add(aModel);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.scope.AeBPWSCatchValidator#getPatternErrorMessage()
    */
   protected String getPatternErrorMessage()
   {
      return ERROR_WSBPEL_CATCH_PATTERN;
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.scope.AeBPWSCatchValidator#validate()
    */
   public void validate()
   {
      super.validate();
      if (getVariable() != null)
      {
         getVariable().validate();
      }
      //Validate illegal catch when exitOnStandardFault is set to yes
      QName faultName = getDef().getFaultName();
      if(faultName != null) 
      {
         IAeFault aFault =  new AeFault(faultName, (IAeMessageData) null);
         if ( getFaultFactory().isStandardFaultForExit(aFault)
               && AeDefUtil.isExitOnStandardFaultEnabled(getDefinition()) )
         {
            getReporter().reportProblem(WSBPEL_ILLEGAL_CATCH_FOR_EXIT_ON_STD_FAULT_CODE, 
                                       ERROR_ILLEGAL_CATCH_FOR_EXIT_ON_STD_FAULT, null, getDef());
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#getVariableValidator(java.lang.String, java.lang.String, boolean, int, org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   protected AeVariableValidator getVariableValidator(String aName, String aFieldName,
         boolean aRecordReference, int aMode, AeBaseXmlDef aDef)
   {
      if (AeUtil.compareObjects(aName, getDef().getFaultVariable()))
      {
         getVariable().addVariableUsage(aMode);
         return (AeVariableValidator) getVariable();
      }
      else
      {
         return super.getVariableValidator(aName, aFieldName, aRecordReference, aMode, aDef);
      }
   }
}
 