package org.activebpel.rt.bpel.def.validation;

import org.activebpel.rt.bpel.def.AeCatchDef;
import org.activebpel.rt.bpel.def.AeCompensationHandlerDef;
import org.activebpel.rt.bpel.def.AeExtensionActivityDef;
import org.activebpel.rt.bpel.def.AeFaultHandlersDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeTerminationHandlerDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.activity.AeActivityIfDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityThrowDef;
import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityIfValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeWSBPELActivityScopeValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeWSBPELActivityThrowValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeWSBPELOnAlarmValidator;
import org.activebpel.rt.bpel.def.validation.activity.assign.AeWSBPELToValidator;
import org.activebpel.rt.bpel.def.validation.activity.decision.AeElseValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeWSBPELCatchValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeWSBPELCompensationHandlerValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeWSBPELFaultHandlersValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeWSBPELPartnerLinkValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeWSBPELTerminationHandlerValidator;
import org.activebpel.rt.bpel.def.validation.extensions.AeWSBPELChildExtensionActivityValidator;
import org.activebpel.rt.bpel.def.validation.extensions.AeWSBPELExtensionActivityValidator;
import org.activebpel.rt.bpel.def.validation.extensions.AeWSBPELExtensionAttributeValidator;
import org.activebpel.rt.bpel.def.validation.extensions.AeWSBPELExtensionElementValidator;
import org.activebpel.rt.bpel.def.validation.process.AeWSBPELProcessValidator;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.activebpel.rt.xml.def.AeExtensionElementDef;

/**
 * WSBPEL validation visitor
 */
public class AeWSBPELDefToValidationVisitor extends AeDefToValidationVisitor
{
   /**
    * Ctor
    * 
    * @param aContext
    */
   public AeWSBPELDefToValidationVisitor(IAeValidationContext aContext)
   {
      super(aContext);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeDefToValidationVisitor#initMap()
    */
   protected void initMap()
   {
      super.initMap();
      getDefToValidatorMap().put(AeProcessDef.class, AeWSBPELProcessValidator.class);
      getDefToValidatorMap().put(AeCatchDef.class, AeWSBPELCatchValidator.class);
      getDefToValidatorMap().put(AeActivityThrowDef.class, AeWSBPELActivityThrowValidator.class);
      getDefToValidatorMap().put(AeToDef.class, AeWSBPELToValidator.class);
      getDefToValidatorMap().put(AeOnAlarmDef.class, AeWSBPELOnAlarmValidator.class);
      getDefToValidatorMap().put(AeExtensionActivityDef.class, AeWSBPELExtensionActivityValidator.class);
      getDefToValidatorMap().put(AeCompensationHandlerDef.class, AeWSBPELCompensationHandlerValidator.class);
      getDefToValidatorMap().put(AeTerminationHandlerDef.class, AeWSBPELTerminationHandlerValidator.class);
      getDefToValidatorMap().put(AeActivityScopeDef.class, AeWSBPELActivityScopeValidator.class);
      getDefToValidatorMap().put(AeFaultHandlersDef.class, AeWSBPELFaultHandlersValidator.class);
      getDefToValidatorMap().put(AeExtensionAttributeDef.class, AeWSBPELExtensionAttributeValidator.class);
      getDefToValidatorMap().put(AeExtensionElementDef.class, AeWSBPELExtensionElementValidator.class);
      getDefToValidatorMap().put(AeChildExtensionActivityDef.class, AeWSBPELChildExtensionActivityValidator.class);
      getDefToValidatorMap().put(AePartnerLinkDef.class, AeWSBPELPartnerLinkValidator.class);
      getDefToValidatorMap().put(AeVariableDef.class, AeWSBPELVariableValidator.class);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityIfDef)
    */
   public void visit(AeActivityIfDef aDef)
   {
      AeActivityIfValidator ifModel = new AeActivityIfValidator(aDef);
      ifModel.setMissingConditionError(IAeValidationDefs.IF_MISSING_CONDITION);
      traverse(aDef, ifModel);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseDef)
    */
   public void visit(AeElseDef aDef)
   {
      AeElseValidator elseModel = new AeElseValidator(aDef);
      elseModel.setTagName(AeElseDef.TAG_ELSE);
      traverse(aDef, elseModel);
   }
}
