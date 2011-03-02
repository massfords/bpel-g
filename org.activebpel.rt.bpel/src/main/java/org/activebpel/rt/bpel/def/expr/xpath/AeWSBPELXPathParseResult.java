// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/expr/xpath/AeWSBPELXPathParseResult.java,v 1.3 2008/01/25 21:01:19 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.expr.xpath;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.activebpel.rt.bpel.def.util.AeVariableData;
import org.activebpel.rt.bpel.xpath.ast.AeXPathAST;
import org.activebpel.rt.expr.def.AeScriptVarDef;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;
import org.activebpel.rt.util.AeUtil;

/**
 * A concrete implementation of a parse result for the xpath language (for BPEL 2.0).
 */
public class AeWSBPELXPathParseResult extends AeAbstractXPathParseResult
{
   /**
    * Creates the xpath parse result.
    * 
    * @param aExpression
    * @param aXPathAST
    * @param aErrors
    * @param aParserContext
    */
   public AeWSBPELXPathParseResult(String aExpression, AeXPathAST aXPathAST, List aErrors, IAeExpressionParserContext aParserContext)
   {
      super(aExpression, aXPathAST, aErrors, aParserContext);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.expr.AeAbstractExpressionParseResult#getVarDataList()
    */
   public List getVarDataList()
   {
      List varData = super.getVarDataList();
      varData.addAll(getVarDataFromXPathVariables());
      return varData;
   }

   /**
    * Gets a list of AeVariableData objects built from the 
    */
   protected Collection getVarDataFromXPathVariables()
   {
      List list = new LinkedList();
      for (Iterator iter = getVariableReferences().iterator(); iter.hasNext(); )
      {
         AeScriptVarDef varDef = (AeScriptVarDef) iter.next();
         // BPEL 2.0 variables are referenced using an unqualified XPath 1.0 variable reference.
         if (AeUtil.isNullOrEmpty(varDef.getNamespace()))
         {
            AeXPathVariableReference varRef = new AeXPathVariableReference(varDef.getName());
            list.add(new AeVariableData(varRef.getVariableName(), varRef.getPartName(), varDef.getQuery()));
         }
      }
      return list;
   }
}
