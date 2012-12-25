// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/xpath/ast/AeXPathTextNodeStepNode.java,v 1.1 2006/07/21 16:03:31 ewittmann Exp $
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
 * An XPath node for a text node step.
 */
public class AeXPathTextNodeStepNode extends AeAbstractXPathAxisNode
{
   /**
    * Default c'tor.
    */
   public AeXPathTextNodeStepNode(int aAxis)
   {
      super(AeAbstractXPathNode.NODE_TYPE_TEXT_NODE_STEP, aAxis);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathNode#accept(org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor)
    */
   public void accept(IAeXPathNodeVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
