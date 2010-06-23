// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/xquery/AeXQueryStaticVariableResolver.java,v 1.1 2006/09/07 15:11:45 EWittmann Exp $
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.def.xquery;

import net.sf.saxon.expr.VariableDeclaration;
import net.sf.saxon.query.GlobalVariableDefinition;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.variables.VariableResolver;

/**
 * An implementation of an XQuery variable resolver.  This implementation resolves all variables to 
 * simple 'no-op' variables that are used only for analysis (not execution).
 */
public class AeXQueryStaticVariableResolver implements VariableResolver
{
   /**
    * Default c'tor.
    */
   public AeXQueryStaticVariableResolver()
   {
   }

   /**
    * @see net.sf.saxon.variables.VariableResolver#hasVariable(int, java.lang.String, java.lang.String)
    */
   public boolean hasVariable(int aNameCode, String aUri, String aLocal)
   {
      return true;
   }

   /**
    * @see net.sf.saxon.variables.VariableResolver#resolve(int, java.lang.String, java.lang.String)
    */
   public VariableDeclaration resolve(int aNameCode, String aUri, String aLocal)
   {
      return createGlobalVariableDef(aNameCode, aUri, aLocal);
   }

   /**
    * Creates a global variable definition for the given namecode + local name.
    * 
    * @param aNameCode
    * @param aVariableName
    */
   private GlobalVariableDefinition createGlobalVariableDef(int aNameCode, String aUri, String aVariableName)
   {
      GlobalVariableDefinition globalVarDef = new GlobalVariableDefinition();
      globalVarDef.setNameCode(aNameCode);
      globalVarDef.setIsParameter(true);
      globalVarDef.setVariableName(aVariableName);
      globalVarDef.setRequiredType(SequenceType.SINGLE_ITEM);

      globalVarDef.setValueExpression(new AeNoOpVariable(aUri, aVariableName));

      return globalVarDef;
   }
}
