// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/javascript/AeWSBPELJavaScriptExpressionAnalyzer.java,v 1.6 2008/01/25 21:28:25 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.def.javascript;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.expr.AeExpressionLanguageUtil;
import org.activebpel.rt.expr.def.AeScriptFuncDef;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext;
import org.activebpel.rt.expr.def.IAeExpressionParser;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ScriptOrFnNode;
import org.mozilla.javascript.Token;

/**
 * A concrete implementation of an expression analyzer for JavaScript (BPEL 2.0).  This class helps the 
 * Designer perform analysis and manipulation of expressions written in JavaScript.
 */
public class AeWSBPELJavaScriptExpressionAnalyzer extends AeAbstractJavaScriptExpressionAnalyzer
{
   /**
    * Default c'tor.
    */
   public AeWSBPELJavaScriptExpressionAnalyzer()
   {
      super();
   }

   /**
    * Overrides method to supply a javascript impl for the expression parser.
    * 
    * @see org.activebpel.rt.expr.def.AeAbstractExpressionAnalyzer#createExpressionParser(org.activebpel.rt.expr.def.IAeExpressionParserContext)
    */
   protected IAeExpressionParser createExpressionParser(IAeExpressionParserContext aContext)
   {
      return new AeWSBPELJavaScriptExpressionParser(aContext);
   }
   
   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#parseExpressionToSpec(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String)
    */
   public AeExpressionToSpecDetails parseExpressionToSpec(IAeExpressionAnalyzerContext aContext, String aExpression)
   {
      // TODO (EPW)  Note: we would need access to the variable types in order to figure out if the two 
      // argument variant of getVarData is (varName, partName) or (varName, query).  However, since 
      // there is a workaround for this (don't use the expression form of the to-spec), I am leaving
      // this as-is for now (since Javascript is the only currently supported language affected).
      try
      {
         Context ctx = Context.enter();
         ctx.setGeneratingDebug(true);
         ctx.setGeneratingSource(true);
         CompilerEnvirons compilerEnv = new CompilerEnvirons();
         compilerEnv.initFromContext(ctx);
         ErrorReporter compilationErrorReporter = compilerEnv.getErrorReporter();

         Parser p = new Parser(compilerEnv, compilationErrorReporter);
         ScriptOrFnNode tree = p.parse(aExpression, "<java>", 0); //$NON-NLS-1$
         return processExpressionToSpec(aContext, tree);
      }
      catch (Exception e)
      {
         AeException.logError(e);
      }
      finally
      {
         Context.exit();
      }
      return null;
   }
   
   /**
    * Process the javascript tree that resulted from parsing a Javascript expression.  The JavaScript
    * expression must be of the following form:  bpel.getVariableData('varName'[, 'partName'][, 'query'])
    * 
    * @param aContext
    * @param aTree
    */
   protected AeExpressionToSpecDetails processExpressionToSpec(IAeExpressionAnalyzerContext aContext, ScriptOrFnNode aTree)
   {
      // The expected tree if the expression is valid will look like:
      //  SCRIPT
      //    |-->  EXPR_RESULT
      //             |-->  CALL
      if (aTree.getType() == Token.SCRIPT)
      {
         Node node = aTree.getFirstChild();
         if (node != null && node.getType() == Token.EXPR_RESULT)
         {
            node = node.getFirstChild();
            if (node != null && node.getType() == Token.CALL)
            {
               AeScriptFuncDef funcDef = AeJavaScriptParseUtil.extractFunction(aContext.getNamespaceContext(), node);
               AeJavaScriptParseUtil.extractArgsIntoFunction(node, funcDef);
               if (isGetVariableDataFunction(funcDef))
               {
                  Object varName = funcDef.getArgument(0);
                  Object partName = funcDef.getArgument(1);
                  Object query = funcDef.getArgument(2);
                  
                  // Params must be either Strings or null - if they are anything else, then the
                  // expression is not a valid to-spec expression.
                  if ((varName instanceof String) && (partName == null || partName instanceof String)
                        && (query == null || query instanceof String))
                  {
                     return new AeExpressionToSpecDetails((String) varName, (String) partName,
                           IAeBPELConstants.WSBPEL_EXPR_LANGUAGE_URI, (String) query);
                  }
               }
            }
         }
      }
      return null;
   }
   
   /**
    * Returns true if the function is a getVariableDataFunction.
    * 
    * @param aFunction
    */
   protected boolean isGetVariableDataFunction(AeScriptFuncDef aFunction)
   {
      return AeWSBPELJavaScriptParseResult.GET_VARIABLE_DATA_FUNC_NAME.equals(aFunction.getQName())
            || AeExpressionLanguageUtil.isVarDataFunction(aFunction);
   }
}
