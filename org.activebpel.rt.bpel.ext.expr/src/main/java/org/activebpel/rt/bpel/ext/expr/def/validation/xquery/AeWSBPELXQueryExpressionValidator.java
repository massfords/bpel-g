// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/validation/xquery/AeWSBPELXQueryExpressionValidator.java,v 1.5 2008/01/25 21:28:25 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.def.validation.xquery;

import org.activebpel.rt.bpel.ext.expr.def.xquery.AeWSBPELXQueryExpressionParser;
import org.activebpel.rt.expr.def.IAeExpressionParser;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;

/**
 * A WS-BPEL version of an XQuery expression validator.
 */
public class AeWSBPELXQueryExpressionValidator extends AeAbstractXQueryExpressionValidator
{
   /**
    * Default c'tor.
    */
   public AeWSBPELXQueryExpressionValidator()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.ext.expr.def.validation.xquery.AeAbstractXQueryExpressionValidator#createExpressionParser(org.activebpel.rt.expr.def.IAeExpressionParserContext)
    */
   protected IAeExpressionParser createExpressionParser(IAeExpressionParserContext aContext)
   {
      return new AeWSBPELXQueryExpressionParser(aContext);
   }
}
