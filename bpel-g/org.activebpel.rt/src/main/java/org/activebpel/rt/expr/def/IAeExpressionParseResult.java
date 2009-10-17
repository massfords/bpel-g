//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/def/IAeExpressionParseResult.java,v 1.2 2008/02/17 21:09:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.expr.def;

import java.util.List;
import java.util.Set;

/**
 * This interface defines what an expression parse result is.  An expression parse result is the
 * result of parsing an expression (of course).
 */
public interface IAeExpressionParseResult
{
   /**
    * Gets the expression that was parsed to create this result.
    */
   public String getExpression();

   /**
    * Returns true if there were errors during parse.
    */
   public boolean hasErrors();

   /**
    * Gets a list of errors found during parsing.
    */
   public List getParseErrors();

   /**
    * Gets a list of all the function calls made in the expression.  The return value is a set of
    * AeScriptFuncDef objects.
    */
   public Set getFunctions();

   /**
    * Gets a list of all the expression variable (not necessarily BPEL variable) references.  Note that this
    * should be a set of 'external' variable references.  This means that references to variables declared in
    * the expression itself (xquery, javascript, etc) should not be returned.
    * @return Set of {@link AeScriptVarDef}
    */
   public Set getVariableReferences();

   /**
    * Gets a list of all the variables used in the expression.  This method returns a set of String
    * objects (the names of the variables).
    */
   public Set getVarNames();

   /**
    * Gets a list of all the bpws:getVariableData functions in the expression. 
    * @return List of {@link AeScriptFuncDef}
    */
   public List getVarDataFunctionList();

   /**
    * Gets a list of all the bpws:getVariableProperty functions in the expression. 
    * @return List of {@link AeScriptFuncDef}
    */
   public List getVarPropertyFunctionList();
   
   /**
    * Gets a list of all of the abx: attachment functions in the expression.
    * @return List of {@link AeScriptFuncDef}
    */
   public List getAttachmentFunctionList();

   /**
    * Gets a list of all the abx:getMyRoleProperty functions in the expression. 
    * @return List of {@link AeScriptFuncDef}
    */
   public List getMyRolePropertyFunctionList();

   /**
    * Gets a list of all the bpws:getLinkStatus functions in the expression.
    * @return List of {@link AeScriptFuncDef}
    */
   public List getLinkStatusFunctionList();

   /**
    * Gets a list of all the bpel:doXslTransform functions in the expression.  
    * @return List of {@link AeScriptFuncDef}
    */
   public List getDoXslTransformFunctionList();

   /**
    * Gets a list of all the variable data (AeVariableData) objects.
    */
   public List getVarDataList();

   /**
    * Gets a list of all the variable property (AeVariableProperty) objects.
    */
   public List getVarPropertyList();
   
   /**
    * Gets a list of all the variable data (AeVariableData) objects which 
    * reference attachments.
    */
   public List getVarAttachmentList();
   
   /**
    * Gets the list of all stylesheet URIs found in the expression.  Stylesheet URIs are found
    * as the first param of the bpel:doXslTransform function.
    */
   public List getStylesheetURIList();
}
