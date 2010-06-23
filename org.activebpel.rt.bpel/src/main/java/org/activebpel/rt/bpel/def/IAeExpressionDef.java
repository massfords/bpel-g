// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def;

/**
 * Interface for all defs that can be expressions.
 */
public interface IAeExpressionDef
{
   /**
    * Gets the expression language configured for this def.  This may be null or empty, in which
    * case the process level expressionLanguage setting should be used.
    */
   public String getExpressionLanguage();
   
   /**
    * Sets the expression language URI on the def.
    * 
    * @param aLanguageURI
    */
   public void setExpressionLanguage(String aLanguageURI);

   /**
    * Gets the expression.
    */
   public String getExpression();
   
   /**
    * Sets the expression.
    * 
    * @param aExpression
    */
   public void setExpression(String aExpression);
   
   /**
    * Gets the BPEL namespace of the process that this expression is nested within.
    */
   public String getBpelNamespace();
}
