// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/convert/visitors/AeBPWSToWSBPELExpressionVisitor.java,v 1.9 2007/09/26 02:21:05 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.convert.visitors;

import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.def.activity.support.AeQueryDef;
import org.activebpel.rt.bpel.def.activity.support.IAeQueryParentDef;
import org.activebpel.rt.bpel.def.convert.xpath.AeBPWSToWSBPELXPathConverter;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.def.visitors.AeAbstractExpressionDefVisitor;
import org.activebpel.rt.bpel.def.visitors.AeDefTraverser;
import org.activebpel.rt.bpel.def.visitors.AeTraversalVisitor;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.IAeMutableNamespaceContext;
import org.activebpel.rt.xml.def.AeBaseDefNamespaceContext;

/**
 * This visitor converts BPEL 1.1 expressions into BPEL 2.0 expressions.
 */
public class AeBPWSToWSBPELExpressionVisitor extends AeAbstractExpressionDefVisitor
{
   /**
    * Constructor.
    */
   public AeBPWSToWSBPELExpressionVisitor()
   {
      setTraversalVisitor(new AeTraversalVisitor(new AeDefTraverser(), this));
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.convert.visitors.AeAbstractBPWSToWSBPELVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      if (IAeBPELConstants.BPWS_XPATH_EXPR_LANGUAGE_URI.equals(aDef.getExpressionLanguage()))
      {
         aDef.setExpressionLanguage(IAeBPELConstants.WSBPEL_EXPR_LANGUAGE_URI);
      }
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeQueryDef)
    */
   public void visit(AeQueryDef aDef)
   {
      String query = aDef.getQuery();
      if (AeUtil.notNullOrEmpty(query))
      {
         IAeMutableNamespaceContext nsContext = new AeBaseDefNamespaceContext((AeBaseDef) aDef);
         String newQuery = AeBPWSToWSBPELXPathConverter.convertQuery(query, nsContext);
         if (AeUtil.isNullOrEmpty(newQuery))
         {
            IAeQueryParentDef parentDef = (IAeQueryParentDef) aDef.getParent();
            parentDef.removeQueryDef();
         }
         else
         {
            aDef.setQuery(newQuery);
         }
      }

      super.visit(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractExpressionDefVisitor#visitExpressionDef(org.activebpel.rt.bpel.def.IAeExpressionDef)
    */
   protected void visitExpressionDef(IAeExpressionDef aDef)
   {
      String language = AeDefUtil.getExpressionLanguage(aDef, getProcessDef());
      // If the expression language was explicitely set to xpath 1.0, then
      // change the namespace (BPEL 1.1 and BPEL 2.0 have different namespaces
      // to indicate xpath).
      if (IAeBPELConstants.BPWS_XPATH_EXPR_LANGUAGE_URI.equals(language))
      {
         language = IAeBPELConstants.WSBPEL_EXPR_LANGUAGE_URI;
         aDef.setExpressionLanguage(language);
      }
      if (AeUtil.isNullOrEmpty(language) || IAeBPELConstants.WSBPEL_EXPR_LANGUAGE_URI.equals(language))
      {
         IAeMutableNamespaceContext nsContext = new AeBaseDefNamespaceContext((AeBaseDef) aDef);
         String expression = aDef.getExpression();
         if (AeUtil.notNullOrEmpty(expression))
         {
            String newExpression = AeBPWSToWSBPELXPathConverter.convertExpression(expression, nsContext);
            aDef.setExpression(newExpression);
         }
      }
   }
}
