//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/def/AeAbstractExpressionParser.java,v 1.1 2008/01/25 20:41:50 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.expr.def;

/**
 * An abstract expression parser that language impls can extend.
 */
public abstract class AeAbstractExpressionParser implements IAeExpressionParser
{
   /** The context to use for parsing. */
   private IAeExpressionParserContext mParserContext;

   /**
    * Constructor.
    * 
    * @param aParserContext
    */
   public AeAbstractExpressionParser(IAeExpressionParserContext aParserContext)
   {
      setParserContext(aParserContext);
   }


   /**
    * @return Returns the parserContext.
    */
   protected IAeExpressionParserContext getParserContext()
   {
      return mParserContext;
   }

   /**
    * @param aParserContext The parserContext to set.
    */
   protected void setParserContext(IAeExpressionParserContext aParserContext)
   {
      mParserContext = aParserContext;
   }
}
