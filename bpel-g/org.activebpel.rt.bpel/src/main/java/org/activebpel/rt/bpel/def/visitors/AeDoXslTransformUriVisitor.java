// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeDoXslTransformUriVisitor.java,v 1.3 2008/01/25 21:01:18 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.visitors;

import java.util.HashSet;
import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.expr.def.AeExpressionAnalyzerContext;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzer;
import org.activebpel.rt.xml.def.AeBaseDefNamespaceContext;

/**
 * Visits the process def and builds a list of doXslTransform() resources.
 */
public class AeDoXslTransformUriVisitor extends AeAbstractExpressionDefVisitor
{
   /** The expression language factory. */
   private IAeExpressionLanguageFactory mFactory;
   /** The set of stylesheets found during visiting. */
   private Set mStylesheets;
   
   /**
    * Default c'tor.
    */
   public AeDoXslTransformUriVisitor(IAeExpressionLanguageFactory aFactory)
   {
      super();
      setFactory(aFactory);
      setStylesheets(new HashSet());
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractExpressionDefVisitor#visitExpressionDef(org.activebpel.rt.bpel.def.IAeExpressionDef)
    */
   protected void visitExpressionDef(IAeExpressionDef aExpressionDef)
   {
      try
      {
         String exprLanguage = AeDefUtil.getExpressionLanguage(aExpressionDef);
         String bpelNS = getProcessDef().getNamespace();
         IAeExpressionAnalyzer analyzer = getFactory().createExpressionAnalyzer(bpelNS, exprLanguage);
         AeExpressionAnalyzerContext context = new AeExpressionAnalyzerContext(new AeBaseDefNamespaceContext(
               (AeBaseDef) aExpressionDef));
         Set stylesheets = analyzer.getStylesheetURIs(context, aExpressionDef.getExpression());
         getStylesheets().addAll(stylesheets);
      }
      catch (AeException ex)
      {
         AeException.logError(ex);
      }
   }

   /**
    * @return Returns the factory.
    */
   protected IAeExpressionLanguageFactory getFactory()
   {
      return mFactory;
   }

   /**
    * @param aFactory The factory to set.
    */
   protected void setFactory(IAeExpressionLanguageFactory aFactory)
   {
      mFactory = aFactory;
   }

   /**
    * @return Returns the stylesheets.
    */
   public Set getStylesheets()
   {
      return mStylesheets;
   }

   /**
    * @param aStylesheets The stylesheets to set.
    */
   protected void setStylesheets(Set aStylesheets)
   {
      mStylesheets = aStylesheets;
   }
}
