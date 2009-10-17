// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/xquery/AeAbstractXQueryExpressionParser.java,v 1.2 2008/01/25 21:28:25 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.def.xquery;

import net.sf.saxon.Configuration;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.XQueryExpression;

import org.activebpel.rt.AeException;
import org.activebpel.rt.expr.def.AeAbstractExpressionParser;
import org.activebpel.rt.expr.def.IAeExpressionParseResult;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;

/**
 * An abstract implementation of an expression parser for XQuery.  This impl uses the Saxon
 * library to implement XQuery support.
 */
public abstract class AeAbstractXQueryExpressionParser extends AeAbstractExpressionParser
{
   /**
    * Constructs an xquery parser given the context.
    * 
    * @param aParserContext
    */
   public AeAbstractXQueryExpressionParser(IAeExpressionParserContext aParserContext)
   {
      super(aParserContext);
   }

   /**
    * Uses the Saxon parser to parse the expression into a Saxon parse tree.  This parse tree is then
    * walked (by the AeXQueryParseResult) whenever specific information is needed.  This method simply
    * creates all of the objects required by Saxon for parsing.
    * 
    * @see org.activebpel.rt.expr.def.IAeExpressionParser#parse(java.lang.String)
    */
   public IAeExpressionParseResult parse(String aExpression) throws AeException
   {
      try
      {
         Configuration config = new Configuration();
         config.setExtensionBinder(new AeXQueryStaticFunctionLibrary(getParserContext().getNamespaceContext()));
         StaticQueryContext staticContext = new StaticQueryContext(config);
         staticContext.setExternalNamespaceResolver(new AeXQueryNamespaceResolver(getParserContext().getNamespaceContext()));
         staticContext.setVariableResolver(new AeXQueryStaticVariableResolver());
         XQueryExpression exp = staticContext.compileQuery(aExpression);

         return createParseResult(aExpression, config, exp);
      }
      catch (Exception e)
      {
         throw new AeException(e);
      }
   }

   /**
    * Create the parse result.
    * 
    * @param aExpression
    * @param aConfig
    * @param aXQueryExpression
    */
   protected abstract IAeExpressionParseResult createParseResult(String aExpression, Configuration aConfig,
         XQueryExpression aXQueryExpression);
}
