// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/xpath/ast/AeAbstractXPathOperatorNode.java,v 1.1 2006/07/21 16:03:32 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.xpath.ast;

/**
 * A base class for xpath nodes that have operators (equality, relational, additive, multiplicative).
 */
public abstract class AeAbstractXPathOperatorNode extends AeAbstractXPathNode
{
   /** The operator. */
   private int mOperator;

   /**
    * Default c'tor.
    */
   public AeAbstractXPathOperatorNode(String aType)
   {
      super(aType);
   }

   /**
    * @return Returns the operator.
    */
   public int getOperator()
   {
      return mOperator;
   }

   /**
    * @param aOperator The operator to set.
    */
   public void setOperator(int aOperator)
   {
      mOperator = aOperator;
   }
   
   /**
    * @see org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathNode#normalize()
    */
   public AeAbstractXPathNode normalize()
   {
      // Omit this node if it doesn't have a valid operator.
      if (getOperator() == 0)
      {
         return normalizeOmitSelf();
      }
      else
      {
         return super.normalize();
      }
   }
}
