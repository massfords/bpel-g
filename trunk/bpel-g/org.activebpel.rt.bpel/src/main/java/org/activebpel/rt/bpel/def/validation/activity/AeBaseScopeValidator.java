//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeBaseScopeValidator.java,v 1.5 2008/02/27 20:48:36 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeCorrelationSetValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeCorrelationSetsValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeFaultHandlersValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AePartnerLinkValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AePartnerLinksValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeVariablesValidator;
import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * Base class for scope validation, whether the root scope (process) or nested scope 
 */
public class AeBaseScopeValidator extends AeActivityValidator
{
   /** variables validator */
   private AeVariablesValidator mVariablesValidator;
   /** partnerLinks validator */
   private AePartnerLinksValidator mPartnerLinksValidator;
   /** correlationSets validator */
   private AeCorrelationSetsValidator mCorrelationSetsValidator;

   /**
    * Ctor for subclass
    * @param aDef
    */
   protected AeBaseScopeValidator(AeBaseDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      // validate the variables and plinks after the children so we can record references to them
      // since we use these references to drive the validation of variables and plinks.
      if (getVariablesValidator() != null)
         getVariablesValidator().validate();
      if (getPartnerLinksValidator() != null)
         getPartnerLinksValidator().validate();
      if (getCorrelationSetsValidator() != null)
         getCorrelationSetsValidator().validate();
   }

   /**
    * The variables, partnerlinks, and correlationsets are stored in member variables as opposed to the 
    * children list in order to provide tighter control over the order of validation. We want to validate
    * these resources last to given any enclosed resources a chance to validate first and record their
    * usage of these resources. This also allows us to easily detect resources that are never referenced.
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#add(org.activebpel.rt.bpel.def.validation.AeBaseValidator)
    */
   public void add(AeBaseValidator aValidator)
   {
      if (aValidator instanceof AeVariablesValidator)
         setVariablesValidator((AeVariablesValidator) aValidator);
      else if (aValidator instanceof AePartnerLinksValidator)
         setPartnerLinksValidator((AePartnerLinksValidator) aValidator);
      else if (aValidator instanceof AeCorrelationSetsValidator)
         setCorrelationSetsValidator((AeCorrelationSetsValidator) aValidator);
      else
      {
         super.add(aValidator);
      }
   }

   /**
    * Getter for the correlationSets validator
    */
   public AeCorrelationSetsValidator getCorrelationSetsValidator()
   {
      return mCorrelationSetsValidator;
   }

   /**
    * Setter for the correlationSets validator
    * @param aValidator
    */
   protected void setCorrelationSetsValidator(AeCorrelationSetsValidator aValidator)
   {
      mCorrelationSetsValidator = aValidator;
      mCorrelationSetsValidator.setParent(this);
   }

   /**
    * Pass through to the variables validator if not null, otherwise returns null
    * @param aVarName
    * @param aMode variable usage mode
    */
   public AeVariableValidator getVariableValidator(String aVarName, int aMode)
   {
      if (getVariablesValidator() != null)
      {
         return getVariablesValidator().getVariableValidator(aVarName, aMode);
      }
      return null;
   }

   /**
    * Pass through to the correlationSetModel if not null, otherwise returns null
    * @param aName
    */
   public AeCorrelationSetValidator getCorrelationSetModel(String aName)
   {
      if (getCorrelationSetsValidator() != null)
      {
         return getCorrelationSetsValidator().getCorrelationSetModel(aName);
      }
      return null;
   }

   /**
    * Pass through to the partnerLinkValidator if not null, otherwise null
    * @param aName
    */
   public AePartnerLinkValidator getPartnerLinkValidator(String aName)
   {
      if (getPartnerLinksValidator() != null)
      {
         return getPartnerLinksValidator().getPartnerLinkModel(aName);
      }
      return null;
   }

   /**
    * Getter for the variables validator
    */
   public AeVariablesValidator getVariablesValidator()
   {
      return mVariablesValidator;
   }

   /**
    * Setter for the variables validator
    * @param aVariablesValidator
    */
   protected void setVariablesValidator(AeVariablesValidator aVariablesValidator)
   {
      mVariablesValidator = aVariablesValidator;
      mVariablesValidator.setParent(this);
   }

   /**
    * Getter for the partnerLinks validator
    */
   public AePartnerLinksValidator getPartnerLinksValidator()
   {
      return mPartnerLinksValidator;
   }

   /**
    * Setter for the partnerLinks validator
    * @param aValidator
    */
   protected void setPartnerLinksValidator(AePartnerLinksValidator aValidator)
   {
      mPartnerLinksValidator = aValidator;
      mPartnerLinksValidator.setParent(this);
   }

   /**
    * Getter for the faultHandlers validator
    */
   public AeFaultHandlersValidator getFaultHandlersModel()
   {
      return (AeFaultHandlersValidator) getChild(AeFaultHandlersValidator.class);
   }

   /**
    * Checks locally to see if the partner link is present
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#getPartnerLinkValidator(java.lang.String, boolean)
    */
   protected AePartnerLinkValidator getPartnerLinkValidator(String aName, boolean aRecordReference)
   {
      AePartnerLinkValidator validator = null;
      if (getPartnerLinksValidator() != null)
      {
         validator = getPartnerLinksValidator().getPartnerLinkModel(aName);
      }
      
      if (validator == null)
         validator = super.getPartnerLinkValidator(aName, aRecordReference);
      return validator;
   }

   /**
    * Checks locally to see if the variable is present.
    * 
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#getVariableValidator(java.lang.String, java.lang.String, boolean, int, org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   protected AeVariableValidator getVariableValidator(String aName, String aFieldName,
         boolean aRecordReference, int aMode, AeBaseXmlDef aDef)
   {
      AeVariableValidator validator = null;
      if (getVariablesValidator() != null)
      {
         validator = getVariablesValidator().getVariableValidator(aName, aMode);
      }

      if (validator == null)
         validator = super.getVariableValidator(aName, aFieldName, aRecordReference, aMode, aDef);
      return validator;
   }

   /**
    * Checks locally to see if the correlationSet is present
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#getCorrelationSetValidator(java.lang.String, boolean)
    */
   public AeCorrelationSetValidator getCorrelationSetValidator(String aName, boolean aRecordReference)
   {
      AeCorrelationSetValidator csModel = null;
      if (getCorrelationSetsValidator() != null)
      {
         csModel = getCorrelationSetsValidator().getCorrelationSetModel(aName);
      }
      
      if (csModel == null)
      {
         csModel = super.getCorrelationSetValidator(aName, aRecordReference);
      }
      return csModel;
   }

}
 