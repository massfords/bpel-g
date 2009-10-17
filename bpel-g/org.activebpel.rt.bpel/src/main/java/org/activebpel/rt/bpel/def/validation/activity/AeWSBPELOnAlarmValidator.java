// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeWSBPELOnAlarmValidator.java,v 1.5 2008/03/20 16:01:31 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation.activity;

import java.util.List;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.AeEventHandlersDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeRepeatEveryValidator;
import org.activebpel.rt.bpel.def.validation.expressions.AeForValidator;
import org.activebpel.rt.bpel.def.validation.expressions.AeUntilValidator;
import org.activebpel.rt.bpel.def.validation.expressions.IAeExpressionModelValidator;

/**
 * Extends the base class in order to provide some slightly altered validation logic for
 * WS-BPEL 2.0 compatible OnAlarms.
 */
public class AeWSBPELOnAlarmValidator extends AeOnAlarmValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeWSBPELOnAlarmValidator(AeOnAlarmDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Check for required Scope child if this onAlarm is defined within an Event Handler.
    * 
    * @see org.activebpel.rt.bpel.def.validation.activity.AeOnAlarmValidator#validate()
    */
   public void validate()
   {
      if ( getDef().getParent() instanceof AeEventHandlersDef )
      {
         // onAlarm activities within an event handler requires a child Scope.         
         AeActivityScopeValidator scope = (AeActivityScopeValidator) getChild(AeActivityScopeValidator.class);
         if ( scope == null )
         {
            getReporter().reportProblem( WSBPEL_REQUIRES_SCOPE_CHILD_CODE, ERROR_REQUIRES_SCOPE_CHILD, new String[] { getDefinition().getLocationPath() },
                                    getDefinition() );
         }
      }
      
      super.validate();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.AeOnAlarmValidator#validateAlarmChildren()
    */
   protected void validateAlarmChildren()
   {
      List list = getChildren(IAeExpressionModelValidator.class);
      // Two children, the first child must be a for or an until, the 2nd child must be a repeatEvery
      if (list.size() == 2)
      {
         IAeExpressionModelValidator child1 = (IAeExpressionModelValidator) list.get(0);
         IAeExpressionModelValidator child2 = (IAeExpressionModelValidator) list.get(1);
         if (!(child1 instanceof AeForValidator) && !(child1 instanceof AeUntilValidator))
         {
            getReporter().reportProblem(WSBPEL_INVALID_ALARM_CHILD_CODE, AeMessages.getString("AeWSBPEL20OnAlarmModel.InvalidChild1Error"), null, //$NON-NLS-1$
                  getDefinition());
         }
         else
         {
            validateAlarmChild(child1);
         }
         
         if (!(child2 instanceof AeRepeatEveryValidator))
         {
            getReporter().reportProblem(WSBPEL_INVALID_ALARM_CHILD_CODE, AeMessages.getString("AeWSBPEL20OnAlarmModel.InvalidChild2Error"), null, //$NON-NLS-1$
                  getDefinition());
         }
         else
         {
            validateAlarmChild(child2);
         }
      }
      else
      {
         super.validateAlarmChildren();
      }
   }
}
