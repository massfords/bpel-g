// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.activity.support;

import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;

/**
 * Base class for all defs that specify an expression, such as transitionCondition, joinCondition, etc...
 */
public abstract class AeExpressionBaseDef extends AeBaseDef implements IAeExpressionDef
{
   /** The join condition construct's 'expressionLanguage' attribute. */
   private String mExpressionLanguage;
   /** The value of the joinCondition, which is the boolean expression. */
   private String mExpression;

   /**
    * Default c'tor.
    */
   public AeExpressionBaseDef()
   {
      super();
   }
   
   /**
    * @return Returns the expressionLanguage.
    */
   public String getExpressionLanguage()
   {
      return mExpressionLanguage;
   }

   /**
    * @param aExpressionLanguage The expressionLanguage to set.
    */
   public void setExpressionLanguage(String aExpressionLanguage)
   {
      mExpressionLanguage = aExpressionLanguage;
   }

   /**
    * @return Returns the expression.
    */
   public String getExpression()
   {
      return mExpression;
   }

   /**
    * @param aExpression The expression to set.
    */
   public void setExpression(String aExpression)
   {
      mExpression = aExpression;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.IAeExpressionDef#getBpelNamespace()
    */
   public String getBpelNamespace()
   {
      return AeDefUtil.getProcessDef(this).getNamespace();
   }
}
