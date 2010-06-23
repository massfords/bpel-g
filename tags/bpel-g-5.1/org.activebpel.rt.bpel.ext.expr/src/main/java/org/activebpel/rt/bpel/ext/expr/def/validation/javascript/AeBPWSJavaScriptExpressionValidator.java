// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/validation/javascript/AeBPWSJavaScriptExpressionValidator.java,v 1.2 2008/01/25 21:28:25 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.def.validation.javascript;

import org.activebpel.rt.bpel.ext.expr.def.javascript.AeBPWSJavaScriptExpressionParser;
import org.activebpel.rt.expr.def.IAeExpressionParser;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;

/**
 * A BPEL 1.1 implementation of a JavaScript expression validator.
 */
public class AeBPWSJavaScriptExpressionValidator extends AeAbstractJavaScriptExpressionValidator
{
   /**
    * Default c'tor.
    */
   public AeBPWSJavaScriptExpressionValidator()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.ext.expr.def.validation.javascript.AeAbstractJavaScriptExpressionValidator#createExpressionParser(org.activebpel.rt.expr.def.IAeExpressionParserContext)
    */
   protected IAeExpressionParser createExpressionParser(IAeExpressionParserContext aContext)
   {
      return new AeBPWSJavaScriptExpressionParser(aContext);
   }
}
