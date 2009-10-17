//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/from/AeFromExpression.java,v 1.3.16.1 2008/04/21 16:09:44 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.from; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;

/**
 * Handles executing an expression in order to determine the &lt;from&gt; value
 */
public class AeFromExpression extends AeFromBase
{
   /** expression to execute */
   private IAeExpressionDef mExpressionDef;
   
   /**
    * Ctor accepts def
    * 
    * @param aFromDef
    */
   public AeFromExpression(AeFromDef aFromDef)
   {
      this((IAeExpressionDef)aFromDef);
   }
   
   /**
    * Ctor accepts expression def
    * @param aExpressionDef
    */
   public AeFromExpression(IAeExpressionDef aExpressionDef)
   {
      setExpressionDef(aExpressionDef);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeFrom#getFromData()
    */
   public Object getFromData() throws AeBusinessProcessException
   {
      return getCopyOperation().getContext().executeExpression(getExpressionDef());
   }

   /**
    * @return Returns the expressionDef.
    */
   public IAeExpressionDef getExpressionDef()
   {
      return mExpressionDef;
   }

   /**
    * @param aExpressionDef The expressionDef to set.
    */
   public void setExpressionDef(IAeExpressionDef aExpressionDef)
   {
      mExpressionDef = aExpressionDef;
   }
}
 