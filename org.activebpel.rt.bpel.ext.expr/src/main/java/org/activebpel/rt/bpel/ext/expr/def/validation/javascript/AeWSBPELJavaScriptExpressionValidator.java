// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/validation/javascript/AeWSBPELJavaScriptExpressionValidator.java,v 1.3 2008/01/25 21:28:25 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.def.validation.javascript;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.expr.AeExpressionLanguageUtil;
import org.activebpel.rt.bpel.ext.expr.def.javascript.AeWSBPELJavaScriptExpressionParser;
import org.activebpel.rt.expr.def.IAeExpressionParser;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;

/**
 * A BPEL 2.0 implementation of a JavaScript expression validator.
 */
public class AeWSBPELJavaScriptExpressionValidator extends AeAbstractJavaScriptExpressionValidator
{
   /** The Set of allowed join condition functions for WSBPEL Javascript expressions. */
   private static Set sAllowedJoinConditionFunctions;

   /**
    * Default c'tor.
    */
   public AeWSBPELJavaScriptExpressionValidator()
   {
      super();
   }
   
   /**
    * @see org.activebpel.rt.bpel.ext.expr.def.validation.javascript.AeAbstractJavaScriptExpressionValidator#createExpressionParser(org.activebpel.rt.expr.def.IAeExpressionParserContext)
    */
   protected IAeExpressionParser createExpressionParser(IAeExpressionParserContext aContext)
   {
      return new AeWSBPELJavaScriptExpressionParser(aContext);
   }

   /**
    * Override this in order to supply the bpel.getLinkStatus() function as a valid function
    * for JavaScript join conditions.
    * 
    * @see org.activebpel.rt.bpel.def.validation.expr.AeAbstractExpressionValidator#getJoinConditionAllowedFunctions()
    */
   protected Set getJoinConditionAllowedFunctions()
   {
      if (sAllowedJoinConditionFunctions == null)
      {
         Set set = new HashSet(super.getJoinConditionAllowedFunctions());
         set.add(new QName(IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI, AeExpressionLanguageUtil.LINK_STATUS_FUNC_NAME));
         sAllowedJoinConditionFunctions = set;
      }

      return sAllowedJoinConditionFunctions;
   }
}
