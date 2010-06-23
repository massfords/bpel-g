// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/fastdom/AeForeignNode.java,v 1.1 2004/09/07 22:08:24 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.fastdom;

import org.w3c.dom.Node;

/**
 * Implements a reference to an external XML <code>Node</code>.
 */
public class AeForeignNode extends AeFastNode
{
   /** The external XML <code>Node</code>. */
   private final Node mNode;

   /**
    * Constructs a reference to the specified external XML <code>Node</code>.
    *
    * @param aNode
    */
   public AeForeignNode(Node aNode)
   {
      mNode = aNode;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.fastdom.IAeVisitable#accept(org.activebpel.rt.bpel.impl.fastdom.IAeVisitor)
    */
   public void accept(IAeVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * Returns the external XML <code>Node</code>.
    */
   public Node getNode()
   {
      return mNode;
   }
}
