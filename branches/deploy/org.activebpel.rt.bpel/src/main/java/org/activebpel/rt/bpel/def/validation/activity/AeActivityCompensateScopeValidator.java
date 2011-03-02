// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivityCompensateScopeValidator.java,v 1.6 2008/03/20 16:01:31 dvilaverde Exp $
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

import org.activebpel.rt.bpel.def.AeScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef;
import org.activebpel.rt.bpel.def.validation.IAeValidationDefs;
import org.activebpel.rt.bpel.def.visitors.AeAmbigousCompensateScopeVisitor;
import org.activebpel.rt.util.AeUtil;

/**
 * Model for validating the compensateScope activity.
 */
public class AeActivityCompensateScopeValidator extends AeActivityCompensateValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeActivityCompensateScopeValidator(AeActivityCompensateScopeDef aDef)
   {
      super(aDef);
   }

   /**
    * Getter for the def.
    */
   protected AeActivityCompensateScopeDef getCompensateScopeDef()
   {
      return (AeActivityCompensateScopeDef) getDefinition();
   }

   /**
    * Validates that the compensateScope activity has a valid 'target' attribute.
    * @see org.activebpel.rt.bpel.def.validation.activity.AeActivityValidator#validate()
    */
   public void validate()
   {
      super.validate();

      if (AeUtil.notNullOrEmpty(getCompensateScopeDef().getTarget()))
      {
         validateScopeTarget(getCompensateScopeDef().getTarget());
      }
      else
      {
         getReporter().reportProblem(BPEL_COMPENSATE_SCOPE_EMPTY_CODE, IAeValidationDefs.ERROR_COMPENSATE_SCOPE_EMPTY, null, getDefinition());
      }
   }
   
   /**
    * Validates that the scope target can be resolved
    * @param aScopeName
    */
   protected void validateScopeTarget(String aScopeName)
   {
      AeScopeDef rootScope = getDef().findRootScopeForCompensation();
      List targetScopes = AeAmbigousCompensateScopeVisitor.findChildScopesByName(rootScope, aScopeName);
      if (targetScopes.size() == 0)
      {
         getReporter().reportProblem( BPEL_SCOPE_NOT_FOUND_CODE, ERROR_SCOPE_NOT_FOUND,
               new String[] { aScopeName },
               getDefinition() );
      }
      else if (targetScopes.size() > 1)
      {
         getReporter().reportProblem( BPEL_TOO_MANY_SCOPES_FOUND_CODE, TOO_MANY_SCOPES_FOUND,
               new String[] { aScopeName },
               getDefinition() );
      }

   }
}
