//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/AeDefToValidationVisitor.java,v 1.12 2007/10/12 16:09:48 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation; 

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.AeCatchAllDef;
import org.activebpel.rt.bpel.def.AeCompensationHandlerDef;
import org.activebpel.rt.bpel.def.AeCorrelationSetDef;
import org.activebpel.rt.bpel.def.AeCorrelationSetsDef;
import org.activebpel.rt.bpel.def.AeCorrelationsDef;
import org.activebpel.rt.bpel.def.AeEventHandlersDef;
import org.activebpel.rt.bpel.def.AeExtensionActivityDef;
import org.activebpel.rt.bpel.def.AeExtensionDef;
import org.activebpel.rt.bpel.def.AeExtensionsDef;
import org.activebpel.rt.bpel.def.AeImportDef;
import org.activebpel.rt.bpel.def.AeMessageExchangeDef;
import org.activebpel.rt.bpel.def.AeMessageExchangesDef;
import org.activebpel.rt.bpel.def.AePartnerDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AePartnerLinksDef;
import org.activebpel.rt.bpel.def.AePartnersDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeScopeDef;
import org.activebpel.rt.bpel.def.AeTerminationHandlerDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.AeVariablesDef;
import org.activebpel.rt.bpel.def.activity.AeActivityAssignDef;
import org.activebpel.rt.bpel.def.activity.AeActivityBreakDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityContinueDef;
import org.activebpel.rt.bpel.def.activity.AeActivityEmptyDef;
import org.activebpel.rt.bpel.def.activity.AeActivityExitDef;
import org.activebpel.rt.bpel.def.activity.AeActivityFlowDef;
import org.activebpel.rt.bpel.def.activity.AeActivityForEachDef;
import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef;
import org.activebpel.rt.bpel.def.activity.AeActivityPickDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReplyDef;
import org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef;
import org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef;
import org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef;
import org.activebpel.rt.bpel.def.activity.AeActivityValidateDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWaitDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWhileDef;
import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef;
import org.activebpel.rt.bpel.def.activity.support.AeConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeExtensibleAssignDef;
import org.activebpel.rt.bpel.def.activity.support.AeForDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachCompletionConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachFinalDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachStartDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef;
import org.activebpel.rt.bpel.def.activity.support.AeIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeLinkDef;
import org.activebpel.rt.bpel.def.activity.support.AeLinksDef;
import org.activebpel.rt.bpel.def.activity.support.AeLiteralDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;
import org.activebpel.rt.bpel.def.activity.support.AeQueryDef;
import org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourceDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourcesDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetsDef;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartsDef;
import org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeUntilDef;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityAssignValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityBreakValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityCompensateScopeValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityCompensateValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityContinueValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityEmptyValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityExitValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityFlowValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityForEachValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityInvokeValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityOpaqueValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityPickValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityReceiveValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityRepeatUntilValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityReplyValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityRethrowValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivitySequenceValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivitySuspendValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityValidateValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityWaitValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityWhileValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeOnAlarmValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeOnMessageValidator;
import org.activebpel.rt.bpel.def.validation.activity.assign.AeAssignCopyValidator;
import org.activebpel.rt.bpel.def.validation.activity.assign.AeExtensibleAssignValidator;
import org.activebpel.rt.bpel.def.validation.activity.assign.AeFromValidator;
import org.activebpel.rt.bpel.def.validation.activity.assign.AeLiteralValidator;
import org.activebpel.rt.bpel.def.validation.activity.assign.AeToValidator;
import org.activebpel.rt.bpel.def.validation.activity.decision.AeElseIfValidator;
import org.activebpel.rt.bpel.def.validation.activity.decision.AeIfValidator;
import org.activebpel.rt.bpel.def.validation.activity.links.AeLinkValidator;
import org.activebpel.rt.bpel.def.validation.activity.links.AeLinksValidator;
import org.activebpel.rt.bpel.def.validation.activity.links.AeSourceValidator;
import org.activebpel.rt.bpel.def.validation.activity.links.AeSourcesValidator;
import org.activebpel.rt.bpel.def.validation.activity.links.AeTargetValidator;
import org.activebpel.rt.bpel.def.validation.activity.links.AeTargetsValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeCatchAllValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeCompensationHandlerValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeCorrelationSetValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeCorrelationSetsValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeEventHandlersValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeMessageExchangeValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeMessageExchangesValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeOnEventValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AePartnerLinkValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AePartnerLinksValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeRepeatEveryValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeTerminationHandlerValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeVariablesValidator;
import org.activebpel.rt.bpel.def.validation.activity.wsio.AeCorrelationValidator;
import org.activebpel.rt.bpel.def.validation.activity.wsio.AeCorrelationsValidator;
import org.activebpel.rt.bpel.def.validation.activity.wsio.AeFromPartValidator;
import org.activebpel.rt.bpel.def.validation.activity.wsio.AeFromPartsValidator;
import org.activebpel.rt.bpel.def.validation.activity.wsio.AeToPartValidator;
import org.activebpel.rt.bpel.def.validation.activity.wsio.AeToPartsValidator;
import org.activebpel.rt.bpel.def.validation.expressions.AeConditionValidator;
import org.activebpel.rt.bpel.def.validation.expressions.AeForEachBranchesValidator;
import org.activebpel.rt.bpel.def.validation.expressions.AeForEachCompletionConditionValidator;
import org.activebpel.rt.bpel.def.validation.expressions.AeForEachFinalValidator;
import org.activebpel.rt.bpel.def.validation.expressions.AeForEachStartValidator;
import org.activebpel.rt.bpel.def.validation.expressions.AeForValidator;
import org.activebpel.rt.bpel.def.validation.expressions.AeJoinConditionValidator;
import org.activebpel.rt.bpel.def.validation.expressions.AeTransitionConditionValidator;
import org.activebpel.rt.bpel.def.validation.expressions.AeUntilValidator;
import org.activebpel.rt.bpel.def.validation.extensions.AeExtensionActivityValidator;
import org.activebpel.rt.bpel.def.validation.extensions.AeExtensionValidator;
import org.activebpel.rt.bpel.def.validation.extensions.AeExtensionsValidator;
import org.activebpel.rt.bpel.def.validation.process.AeImportValidator;
import org.activebpel.rt.bpel.def.validation.process.AePartnerValidator;
import org.activebpel.rt.bpel.def.validation.process.AePartnersValidator;
import org.activebpel.rt.bpel.def.validation.process.AeProcessValidator;
import org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor;
import org.activebpel.rt.bpel.def.visitors.AeDefTraverser;
import org.activebpel.rt.bpel.def.visitors.AeTraversalVisitor;
import org.activebpel.rt.bpel.def.visitors.IAeDefTraverser;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeDocumentationDef;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.activebpel.rt.xml.def.AeExtensionElementDef;

/**
 * Visits the def to create validators
 */
public abstract class AeDefToValidationVisitor extends AeAbstractDefVisitor
{
   protected static final Object NULL = new Object();
   /** root validator */
   private AeProcessValidator mProcessValidator;
   /** stack used for parenting */
   private Stack mStack = new Stack();
   /** context for the validation */
   private IAeValidationContext mContext;
   /** maps defs classes to their validators */
   private Map mDefToValidatorMap = new HashMap();
   
   /**
    * Ctor accepts the validation context
    * @param aContext
    */
   protected AeDefToValidationVisitor(IAeValidationContext aContext)
   {
      mContext = aContext;
      setTraversalVisitor(new AeTraversalVisitor(createTraverser(), this));
      initMap();
   }
   
   /**
    * Creates a traverser for the def
    */
   protected IAeDefTraverser createTraverser()
   {
      return new AeDefTraverser();
   }
   
   /**
    * Creates map of def class to validator class
    */
   protected void initMap()
   {
      Map map = getDefToValidatorMap();
      map.put(AeProcessDef.class,                   AeProcessValidator.class);
      map.put(AeActivityAssignDef.class,            AeActivityAssignValidator.class);
      map.put(AeActivityCompensateDef.class,        AeActivityCompensateValidator.class);
      map.put(AeActivityCompensateScopeDef.class,   AeActivityCompensateScopeValidator.class);
      map.put(AeActivityEmptyDef.class,             AeActivityEmptyValidator.class);
      map.put(AeActivityFlowDef.class,              AeActivityFlowValidator.class);
      map.put(AeActivityInvokeDef.class,            AeActivityInvokeValidator.class);
      map.put(AeActivityPickDef.class,              AeActivityPickValidator.class);
      map.put(AeActivityReceiveDef.class,           AeActivityReceiveValidator.class);
      map.put(AeActivityReplyDef.class,             AeActivityReplyValidator.class);
      map.put(AeActivitySuspendDef.class,           AeActivitySuspendValidator.class);
      map.put(AeActivityContinueDef.class,          AeActivityContinueValidator.class);
      map.put(AeActivityBreakDef.class,             AeActivityBreakValidator.class);
      map.put(AeCorrelationSetDef.class,            AeCorrelationSetValidator.class);
      map.put(AeCatchAllDef.class,                  AeCatchAllValidator.class);
      map.put(AeVariableDef.class,                  AeVariableValidator.class);
      map.put(AeVariablesDef.class,                 AeVariablesValidator.class);
      map.put(AeEventHandlersDef.class,             AeEventHandlersValidator.class);
      map.put(AeCompensationHandlerDef.class,       AeCompensationHandlerValidator.class);
      map.put(AeCorrelationSetsDef.class,           AeCorrelationSetsValidator.class);
      map.put(AeOnMessageDef.class,                 AeOnMessageValidator.class);
      map.put(AeOnEventDef.class,                   AeOnEventValidator.class);
      map.put(AeOnAlarmDef.class,                   AeOnAlarmValidator.class);
      map.put(AeActivitySequenceDef.class,          AeActivitySequenceValidator.class);
      map.put(AeActivityExitDef.class,              AeActivityExitValidator.class);
      map.put(AeActivityWaitDef.class,              AeActivityWaitValidator.class);
      map.put(AeActivityWhileDef.class,             AeActivityWhileValidator.class);
      map.put(AeActivityRepeatUntilDef.class,       AeActivityRepeatUntilValidator.class);
      map.put(AeActivityForEachDef.class,           AeActivityForEachValidator.class);
      map.put(AeForEachCompletionConditionDef.class, AeForEachCompletionConditionValidator.class);
      map.put(AeForEachStartDef.class,              AeForEachStartValidator.class);
      map.put(AeForEachFinalDef.class,              AeForEachFinalValidator.class);
      map.put(AeForEachBranchesDef.class,           AeForEachBranchesValidator.class);
      map.put(AePartnerDef.class,                   AePartnerValidator.class);
      map.put(AeMessageExchangesDef.class,          AeMessageExchangesValidator.class);
      map.put(AeMessageExchangeDef.class,           AeMessageExchangeValidator.class);
      map.put(AeAssignCopyDef.class,                AeAssignCopyValidator.class);
      map.put(AeCorrelationDef.class,               AeCorrelationValidator.class);
      map.put(AeLinkDef.class,                      AeLinkValidator.class);
      map.put(AeSourceDef.class,                    AeSourceValidator.class);
      map.put(AeTargetDef.class,                    AeTargetValidator.class);
      map.put(AePartnerLinksDef.class,              AePartnerLinksValidator.class);
      map.put(AePartnersDef.class,                  AePartnersValidator.class);
      map.put(AePartnerLinkDef.class,               AePartnerLinkValidator.class);
      map.put(AeLinksDef.class,                     AeLinksValidator.class);
      map.put(AeCorrelationsDef.class,              AeCorrelationsValidator.class);
      map.put(AeFromDef.class,                      AeFromValidator.class);
      map.put(AeToDef.class,                        AeToValidator.class);
      map.put(AeImportDef.class,                    AeImportValidator.class);
      map.put(AeDocumentationDef.class,             NULL);
      map.put(AeActivityValidateDef.class,          AeActivityValidateValidator.class);
      map.put(AeExtensibleAssignDef.class,          AeExtensibleAssignValidator.class);
      map.put(AeExtensionsDef.class,                AeExtensionsValidator.class);
      map.put(AeExtensionDef.class,                 AeExtensionValidator.class);
      map.put(AeFromPartsDef.class,                 AeFromPartsValidator.class);
      map.put(AeToPartsDef.class,                   AeToPartsValidator.class);
      map.put(AeFromPartDef.class,                  AeFromPartValidator.class);
      map.put(AeToPartDef.class,                    AeToPartValidator.class);
      map.put(AeSourcesDef.class,                   AeSourcesValidator.class);
      map.put(AeTargetsDef.class,                   AeTargetsValidator.class);
      map.put(AeTransitionConditionDef.class,       AeTransitionConditionValidator.class);
      map.put(AeJoinConditionDef.class,             AeJoinConditionValidator.class);
      map.put(AeForDef.class,                       AeForValidator.class);
      map.put(AeUntilDef.class,                     AeUntilValidator.class);
      map.put(AeExtensionActivityDef.class,         AeExtensionActivityValidator.class);
      map.put(AeChildExtensionActivityDef.class,    NULL);
      map.put(AeIfDef.class,                        AeIfValidator.class);
      map.put(AeElseIfDef.class,                    AeElseIfValidator.class);
      map.put(AeConditionDef.class,                 AeConditionValidator.class);
      map.put(AeActivityRethrowDef.class,           AeActivityRethrowValidator.class);
      map.put(AeRepeatEveryDef.class,               AeRepeatEveryValidator.class);
      map.put(AeTerminationHandlerDef.class,        AeTerminationHandlerValidator.class);
      map.put(AeLiteralDef.class,                   AeLiteralValidator.class);
      map.put(AeScopeDef.class,                     NULL);
      map.put(AeQueryDef.class,                     NULL);
      map.put(AeActivityOpaqueDef.class,            AeActivityOpaqueValidator.class);
      map.put(AeExtensionAttributeDef.class,        NULL);
      map.put(AeExtensionElementDef.class,          NULL);
   }
   
   /**
    * Getter for the process validator
    */
   protected AeProcessValidator getProcessValidator()
   {
      return mProcessValidator;
   }
   
   /**
    * Setter for the process validator
    * @param aValidator
    */
   protected void setProcessValidator(AeProcessValidator aValidator)
   {
      mProcessValidator = aValidator;
   }
   
   /**
    * Getter for the current parent 
    */
   protected AeBaseValidator getParent()
   {
      return (AeBaseValidator) mStack.peek();
   }
   
   /**
    * Getter for the def to validator map
    */
   protected Map getDefToValidatorMap()
   {
      return mDefToValidatorMap;
   }
   
   /**
    * Gets the validator class to create for the given def
    * @param aDef
    */
   protected Class getValidatorClass(AeBaseXmlDef aDef)
   {
      Object clazz = getDefToValidatorMap().get(aDef.getClass());
      if (clazz == NULL)
      {
         // there is no validation validator for this def, we'll return null which skips it
         return null;
      }
      else if (clazz == null)
      {
         // we didn't have a mapping for this def, this is a configuation error
         AeException.logError(new Exception(), "Error in validation configuration"); //$NON-NLS-1$
      }
      return (Class) clazz;
   }
   
   /**
    * Creates the validator given the validator class and the arg for its ctor
    * @param aValidatorClass
    * @param aDef
    */
   protected AeBaseValidator createValidator(Class aValidatorClass, AeBaseXmlDef aDef)
   {
      try
      {
         Constructor c = aValidatorClass.getConstructor(new Class[]{aDef.getClass()});
         return (AeBaseValidator) c.newInstance(new Object[]{aDef});
      }
      catch(Throwable t)
      {
         AeException.logError(t);
         return null;
      }
   }

   /**
    * Creates the process validator for the given process def.
    * 
    * @param aProcessDef
    */
   protected AeProcessValidator createProcessValidator(AeProcessDef aProcessDef)
   {
      try
      {
         Class validatorClass = getValidatorClass(aProcessDef);
         Class [] classes = new Class[] { IAeValidationContext.class, AeProcessDef.class };
         Object [] args = new Object[] { mContext, aProcessDef };
         Constructor c = validatorClass.getConstructor(classes);
         return (AeProcessValidator) c.newInstance(args);
      }
      catch (Exception e)
      {
         AeException.logError(e);
         return null;
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#traverse(org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   protected void traverse(AeBaseXmlDef aDef)
   {
      Class validatorClass = getValidatorClass(aDef);
      if (validatorClass == null)
      {
         super.traverse(aDef);
      }
      else
      {
         AeBaseValidator validator = createValidator(validatorClass, aDef);
         traverse(aDef, validator);
      }
   }

   /**
    * Generic traverse method handles the push, traverse, and pop
    * @param aDef
    * @param aImpl
    */
   protected void traverse(AeBaseXmlDef aDef, AeBaseValidator aImpl)
   {
      if (!mStack.isEmpty())
      {
         getParent().add(aImpl);
      }
      mStack.push(aImpl);
      aDef.accept(getTraversalVisitor());
      mStack.pop();
   }

   /**
    * Creates process validator and sets the root
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      setProcessValidator(createProcessValidator(aDef));
      traverse(aDef, getProcessValidator());
      
      getProcessValidator().validate();
   }
}
 