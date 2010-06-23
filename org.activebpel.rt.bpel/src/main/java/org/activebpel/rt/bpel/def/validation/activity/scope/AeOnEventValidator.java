//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/scope/AeOnEventValidator.java,v 1.9 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.scope; 

import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityScopeValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeOnMessageBaseValidator;
import org.activebpel.rt.util.AeUtil;

/**
 * model provides validation for the onEvent model
 */
public class AeOnEventValidator extends AeOnMessageBaseValidator
{
   /** flag to avoid reporting scope error multiple times */
   private boolean mScopeChecked;
   
   /**
    * ctor
    * @param aDef
    */
   public AeOnEventValidator(AeOnEventDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.AeWSIOActivityValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      // checks for required scope
      getChildScope();
      
      // verify that the variable, if provided, is implicit
      AeVariableValidator model = getVariable();
      
      if (model != null && !model.getDef().isImplicit())
      {
         getReporter().reportProblem( BPEL_EVENT_IMPLICIT_VARIABLE_EXPLICITLY_DEFINED_CODE, 
                                 ERROR_IMPLICIT_VARIABLE_EXPLICITLY_DEFINED,
                                 new String[] { getDefinition().getLocationPath(), model.getDef().getName(), model.getDef().getLocationPath() },
                                 getDefinition() );
      }

      // Issue warning if there are no correlations for this event
      if ( !getDef().getCorrelationDefs().hasNext())
      {
         getReporter().reportProblem( BPEL_EVENT_NO_CORR_SET_NO_CREATE_CODE, 
                                    WARNING_NO_CORR_SET_NO_CREATE,
                                    new String[] { AeOnEventDef.TAG_ON_EVENT },
                                    getDef() );
      }
   }
   
   /**
    * Gets the def
    */
   protected AeOnEventDef getDef()
   {
      return (AeOnEventDef) getDefinition();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.AeOnMessageBaseValidator#resolveVariable()
    */
   protected AeVariableValidator resolveVariable()
   {
      if (AeUtil.notNullOrEmpty(getDef().getVariable()))
      {
         AeActivityScopeValidator scope = getChildScope();
         if ( scope != null )
            return getChildScope().getVariableValidator(getDef().getVariable(), AeVariableValidator.VARIABLE_WRITE_WSIO);
      }
      return null;
   }
   
   /**
    * Resolves the variable for an enclosed fromPart
    * @param aVariableName
    */
   public AeVariableValidator resolveFromPartVariable(String aVariableName)
   {
      AeActivityScopeValidator scope = getChildScope();
      if ( scope != null )
         return getChildScope().getVariableValidator(aVariableName, AeVariableValidator.VARIABLE_WRITE_WSIO);
      return null;
   }

   /**
    * Gets the child scope or reports an error if not found
    */
   protected AeActivityScopeValidator getChildScope()
   {
      AeActivityScopeValidator scope = (AeActivityScopeValidator) getChild(AeActivityScopeValidator.class);
      if (scope == null && !mScopeChecked)
      {
         mScopeChecked = true;
         getReporter().reportProblem( BPEL_REQUIRES_SCOPE_CHILD_CODE, 
                                       ERROR_REQUIRES_SCOPE_CHILD,
                                       new String[] { getDefinition().getLocationPath() },
                                       getDefinition() );
      }
      return scope;
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#getCorrelationSetValidator(java.lang.String, boolean)
    */
   protected AeCorrelationSetValidator getCorrelationSetValidator(String aName, boolean aRecordReference)
   {
      AeActivityScopeValidator scope = getChildScope();

      // null scope would have been reported elsewhere
      if (scope != null)
      {
         AeCorrelationSetValidator model = scope.getCorrelationSetModel(aName);
         if (model != null)
         {
            return model;
         }
      }
      return super.getCorrelationSetValidator(aName, aRecordReference);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#getPartnerLinkValidator(java.lang.String, boolean)
    */
   protected AePartnerLinkValidator getPartnerLinkValidator(String aName, boolean aRecordReference)
   {
      AeActivityScopeValidator scope = getChildScope();

      // null scope would have been reported elsewhere
      if (scope != null)
      {
         AePartnerLinkValidator model = scope.getPartnerLinkValidator(aName);
         if (model != null)
         {
            return model;
         }
      }
      return super.getPartnerLinkValidator(aName, aRecordReference);
   }
}
 