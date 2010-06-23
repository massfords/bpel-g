// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/xquery/AeWSBPELXQueryParseResult.java,v 1.3 2008/01/25 21:28:25 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.def.xquery;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.saxon.Configuration;
import net.sf.saxon.expr.Expression;

import org.activebpel.rt.bpel.def.expr.xpath.AeXPathVariableReference;
import org.activebpel.rt.bpel.def.util.AeVariableData;
import org.activebpel.rt.expr.def.AeScriptVarDef;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;
import org.activebpel.rt.util.AeUtil;

/**
 * A WS-BPEL version of a XQuery parse result.
 */
public class AeWSBPELXQueryParseResult extends AeAbstractXQueryParseResult
{
   /**
    * Constructs a WS-BPEL XQuery Parse Result.
    * 
    * @param aExpression
    * @param aXQueryExpression
    * @param aConfiguration
    * @param aParserContext
    */
   public AeWSBPELXQueryParseResult(String aExpression, Expression aXQueryExpression, Configuration aConfiguration,
         IAeExpressionParserContext aParserContext)
   {
      super(aExpression, aXQueryExpression, aConfiguration, aParserContext);
   }

   /**
    * @see org.activebpel.rt.bpel.def.expr.AeAbstractExpressionParseResult#getVarDataList()
    */
   public List getVarDataList()
   {
      List varData = super.getVarDataList();
      varData.addAll(getVarDataFromXQueryVariables());
      return varData;
   }

   /**
    * Gets a list of AeVariableData objects built from the 
    */
   protected Collection getVarDataFromXQueryVariables()
   {
      List list = new LinkedList();
      for (Iterator iter = getVariableReferences().iterator(); iter.hasNext(); )
      {
         AeScriptVarDef varDef = (AeScriptVarDef) iter.next();
         // BPEL 2.0 variables are referenced using an unqualified XPath 1.0 variable reference.
         if (AeUtil.isNullOrEmpty(varDef.getNamespace()))
         {
            AeXPathVariableReference varRef = new AeXPathVariableReference(varDef.getName());
            // Note that, at the moment, the query portion of the var def will be null for any
            // $varName syntax based XQuery var reference.
            list.add(new AeVariableData(varRef.getVariableName(), varRef.getPartName(), varDef.getQuery()));
         }
      }
      return list;
   }
}
