//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/def/AeNoOpExpressionAnalyzer.java,v 1.1 2008/01/25 20:41:50 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.expr.def;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Implements a no-op version of the expression analyzer.  This analyzer is typically used during 
 * development of other pieces of functionality needed for supporting a new expression language.
 */
public class AeNoOpExpressionAnalyzer implements IAeExpressionAnalyzer
{
   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#getNamespaces(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String)
    */
   public Set getNamespaces(IAeExpressionAnalyzerContext aContext, String aExpression)
   {
      return Collections.EMPTY_SET;
   }
   
   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#getVarDataList(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String)
    */
   public List getVarDataList(IAeExpressionAnalyzerContext aContext, String aExpression)
   {
      return Collections.EMPTY_LIST;
   }
   
   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#getVariables(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String)
    */
   public Set getVariables(IAeExpressionAnalyzerContext aContext, String aExpression)
   {
      return Collections.EMPTY_SET;
   }
   
   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#getVarPropertyList(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String)
    */
   public List getVarPropertyList(IAeExpressionAnalyzerContext aContext, String aExpression)
   {
      return Collections.EMPTY_LIST;
   }
   
   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#getStylesheetURIs(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String)
    */
   public Set getStylesheetURIs(IAeExpressionAnalyzerContext aContext, String aExpression)
   {
      return Collections.EMPTY_SET;
   }
   
   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#renameNamespacePrefix(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String, java.lang.String, java.lang.String)
    */
   public String renameNamespacePrefix(IAeExpressionAnalyzerContext aContext, String aExpression, String aOldPrefix,
         String aNewPrefix)
   {
      return null;
   }
   
   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#renameVariable(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String, java.lang.String, java.lang.String)
    */
   public String renameVariable(IAeExpressionAnalyzerContext aContext, String aExpression, String aOldVarName,
         String aNewVarName)
   {
      return null;
   }
   
   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#parseExpressionToSpec(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String)
    */
   public AeExpressionToSpecDetails parseExpressionToSpec(IAeExpressionAnalyzerContext aContext, String aExpression)
   {
      return null;
   }
}
