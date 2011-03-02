// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/javascript/AeAbstractJavaScriptExpressionParser.java,v 1.3 2008/01/25 21:28:25 dvilaverde Exp $
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
import org.activebpel.rt.expr.def.AeAbstractExpressionParser;
import org.activebpel.rt.expr.def.IAeExpressionParseResult;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ScriptOrFnNode;

/**
 * Base class for JavaScript implementations of an Expression Parser.
 */
public abstract class AeAbstractJavaScriptExpressionParser extends AeAbstractExpressionParser
{
   /**
    * Constructs a javascript parser given the context.
    * 
    * @param aParserContext
    */
   public AeAbstractJavaScriptExpressionParser(IAeExpressionParserContext aParserContext)
   {
      super(aParserContext);
   }

   /**
    * This implementation uses Rhino to parse the expression into a parse tree.  The resulting parse
    * tree is then walked by the AeJavaScriptParseResult object.
    * 
    * @see org.activebpel.rt.expr.def.IAeExpressionParser#parse(java.lang.String)
    */
   public IAeExpressionParseResult parse(String aExpression) throws AeException
   {
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
         return createParseResult(aExpression, tree);
      }
      catch (Exception e)
      {
         throw new AeException(e);
      }
      finally
      {
         Context.exit();
      }
   }

   /**
    * Creates the JavaScript parse result object using the given information.
    * 
    * @param aExpression
    * @param aTree
    */
   protected abstract IAeExpressionParseResult createParseResult(String aExpression, ScriptOrFnNode aTree);
}
