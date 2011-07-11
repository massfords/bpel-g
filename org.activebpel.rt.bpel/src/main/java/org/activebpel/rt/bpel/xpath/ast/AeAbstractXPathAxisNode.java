// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/xpath/ast/AeAbstractXPathAxisNode.java,v 1.2 2006/09/07 15:06:26 EWittmann Exp $
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
 * This class extends the basic xpath node and acts as a base class for any node that contains
 * an axis.
 */
public abstract class AeAbstractXPathAxisNode extends AeAbstractXPathNode
{
   /** The axis of this node (parent, ancestor, child, etc...). */
   private int mAxis;
   
   /**
    * Constructor.
    * 
    * @param aType
    * @param aAxis
    */
   public AeAbstractXPathAxisNode(String aType, int aAxis)
   {
      super(aType);
      setAxis(aAxis);
   }

   /**
    * Gets the axis.
    */
   public int getAxis()
   {
      return mAxis;
   }

   /**
    * @param aAxis The axis to set.
    */
   protected void setAxis(int aAxis)
   {
      mAxis = aAxis;
   }
}
