//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/def/AeAbstractExpressionAnalyzer.java,v 1.2 2008/04/01 21:14:21 JPerrotto Exp $
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeUtil;

/**
 * This is a base class for any concrete, language specific implementation of an expression analyzer. Any
 * class that extends this must implement the <code>createExpressionParser</code> abstract method, which
 * will be used to in the implementation of the <code>getVarDataList</code>, <code>getVarPropertyList</code>, 
 * <code>renameVariable</code>, and <code>getVariables</code> methods. The other methods declared in the 
 * expression analyzer interface must, of course, also be implemented by extending classes.
 */
public abstract class AeAbstractExpressionAnalyzer implements IAeExpressionAnalyzer
{
   /**
    * Simple constructor.
    */
   public AeAbstractExpressionAnalyzer()
   {
   }

   /**
    * Creates an expression parser.
    * 
    * @param aContext
    */
   protected abstract IAeExpressionParser createExpressionParser(IAeExpressionParserContext aContext);

   /**
    * Creates an expression parser context from the analyzer context.
    * 
    * @param aContext
    */
   protected IAeExpressionParserContext createParserContext(IAeExpressionAnalyzerContext aContext)
   {
      return new AeExpressionParserContext(aContext.getNamespaceContext());
   }

   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#getVarDataList(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String)
    */
   public List getVarDataList(IAeExpressionAnalyzerContext aContext, String aExpression)
   {
      if (AeUtil.isNullOrEmpty(aExpression))
         return Collections.EMPTY_LIST;

      try
      {
         IAeExpressionParserContext parserCtx = createParserContext(aContext);
         IAeExpressionParser parser = createExpressionParser(parserCtx);
         IAeExpressionParseResult parseResult = parser.parse(aExpression);
         return parseResult.getVarDataList();
      }
      catch (Exception e)
      {
         AeException.logError(e, e.getLocalizedMessage());
         return Collections.EMPTY_LIST;
      }
   }

   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#getVarPropertyList(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String)
    */
   public List getVarPropertyList(IAeExpressionAnalyzerContext aContext, String aExpression)
   {
      if (AeUtil.isNullOrEmpty(aExpression))
         return Collections.EMPTY_LIST;

      try
      {
         IAeExpressionParserContext parserCtx = createParserContext(aContext);
         IAeExpressionParser parser = createExpressionParser(parserCtx);
         IAeExpressionParseResult parseResult = parser.parse(aExpression);
         return parseResult.getVarPropertyList();
      }
      catch (Exception e)
      {
         AeException.logError(e, e.getLocalizedMessage());
         return Collections.EMPTY_LIST;
      }
   }

   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#getStylesheetURIs(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String)
    */
   public Set getStylesheetURIs(IAeExpressionAnalyzerContext aContext, String aExpression)
   {
      if (AeUtil.isNullOrEmpty(aExpression))
         return Collections.EMPTY_SET;

      try
      {
         IAeExpressionParserContext parserCtx = createParserContext(aContext);
         IAeExpressionParser parser = createExpressionParser(parserCtx);
         IAeExpressionParseResult parseResult = parser.parse(aExpression);
         List stylesheetURIs = parseResult.getStylesheetURIList();
         Set set = new HashSet();
         set.addAll(stylesheetURIs);
         return set;
      }
      catch (Exception e)
      {
         AeException.logError(e, e.getLocalizedMessage());
         return Collections.EMPTY_SET;
      }
   }

   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#getVariables(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String)
    */
   public Set getVariables(IAeExpressionAnalyzerContext aContext, String aExpression)
   {
      if (AeUtil.isNullOrEmpty(aExpression))
         return Collections.EMPTY_SET;

      try
      {
         IAeExpressionParserContext parserCtx = createParserContext(aContext);
         IAeExpressionParser parser = createExpressionParser(parserCtx);
         IAeExpressionParseResult parseResult = parser.parse(aExpression);
         return parseResult.getVarNames();
      }
      catch (Exception e)
      {
         AeException.logError(e, e.getLocalizedMessage());
         return Collections.EMPTY_SET;
      }
   }

   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#renameVariable(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String, java.lang.String, java.lang.String)
    * 
    * todo I'm a little worried about unicode chars in var names here...
    * todo Eliminate hard-coded function names. Currently these function name statics are defined in the org.activebpel.rt.bpel package which this class doesn't have access to.  Future refactoring needs to be done.
    */
   public String renameVariable(IAeExpressionAnalyzerContext aContext, String aExpression, String aOldVarName,
         String aNewVarName)
   {
      if (AeUtil.isNullOrEmpty(aExpression))
         return null;

      // Pattern for functions with a variable name as the first argument.
      String pattern = "(getVariableData|getVariableProperty|removeAttachment|getAttachmentType" +      //$NON-NLS-1$
                       "|getAttachmentSize|getAttachmentCount|createAttachment|removeAllAttachments" +  //$NON-NLS-1$
                       "|getAttachmentProperty|copyAttachment|replaceAttachment|copyAllAttachments)" +  //$NON-NLS-1$
                       "([^'^\"]*)(['\"])" + aOldVarName + "(['\"])";                                   //$NON-NLS-1$ //$NON-NLS-2$
      String replacement = "$1$2$3" + aNewVarName + "$4"; //$NON-NLS-1$ //$NON-NLS-2$
      String newExpr = aExpression.replaceAll(pattern, replacement);
      
      // Pattern for functions with a variable name as the second argument.
      pattern = "(copyAllAttachments)([^'^\"]*)(['\"])([^'^\"]*)(['\"])([^'^\"]*)(['\"])" + aOldVarName + "(['\"])"; //$NON-NLS-1$ //$NON-NLS-2$
      replacement = "$1$2$3$4$5$6$7" + aNewVarName + "$8"; //$NON-NLS-1$ //$NON-NLS-2$
      newExpr = newExpr.replaceAll(pattern, replacement);
      
      // Pattern for functions with a variable name as the third argument.
      pattern = "(copyAttachment|replaceAttachment)([^'^\"]*)(['\"])([^'^\"]*)(['\"])([^'^\"]*)(['\"])([^'^\"]*)(['\"])([^'^\"]*)(['\"])" + aOldVarName + "(['\"])"; //$NON-NLS-1$ //$NON-NLS-2$
      replacement = "$1$2$3$4$5$6$7$8$9$10$11" + aNewVarName + "$12"; //$NON-NLS-1$ //$NON-NLS-2$
      newExpr = newExpr.replaceAll(pattern, replacement);
      
      if (aExpression.equals(newExpr))
      {
         return null;
      }
      else
      {
         return newExpr;
      }
   }
}
