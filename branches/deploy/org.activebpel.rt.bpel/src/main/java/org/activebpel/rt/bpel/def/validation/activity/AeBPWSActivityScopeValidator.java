//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeBPWSActivityScopeValidator.java,v 1.4 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.validation.IAeValidationDefs;
import org.activebpel.rt.bpel.def.visitors.AeChildScopeByNameVisitor;

/**
 * Validator for a BPWS scope 
 */
public class AeBPWSActivityScopeValidator extends AeActivityScopeValidator
{

   /**
    * Ctor
    * @param aDef
    */
   public AeBPWSActivityScopeValidator(AeActivityScopeDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.AeActivityScopeValidator#validateIsolatedScope()
    */
   protected void validateIsolatedScope()
   {
      // MUST not contain any child scopes
      AeNoNestedScopes visitor = new AeNoNestedScopes(getDef());
      getDef().accept(visitor);
      if (visitor.isFound())
      {
         getReporter().reportProblem(BPWS_SERIALIZABLE_LEAF_CODE, IAeValidationDefs.WARNING_BPWS_SERIALIZABLE_LEAF, null, getDefinition());
      }
   }

   /**
    * Visits the def and reports if there is a nested scope
    */
   protected static class AeNoNestedScopes extends AeChildScopeByNameVisitor
   {
      /** scoep that we start visiting from */
      private AeActivityScopeDef mRoot;
      
      /**
       * Ctor
       * @param aDef
       */
      protected AeNoNestedScopes(AeActivityScopeDef aDef)
      {
         super(null);
         mRoot = aDef;
      }
      
      /**
       * @see org.activebpel.rt.bpel.def.visitors.AeChildScopeByNameVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
       */
      public void visit(AeActivityScopeDef aDef)
      {
         if (aDef == mRoot)
         {
            traverse(aDef);
         }
         else
         {
            setScopeDef(aDef);
         }
      }
   }
}
 