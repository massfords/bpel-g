// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/fastdom/AeFastNode.java,v 1.1 2004/09/07 22:08:24 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.fastdom;

/**
 * Abstract base class for nodes in the fast, lightweight DOM.
 */
public abstract class AeFastNode implements IAeVisitable
{
   /** The node's parent. */
   private IAeFastParent mParent;

   /**
    * Default constructor.
    */
   public AeFastNode()
   {
   }

   /**
    * Constructs a new with the specified parent.
    *
    * @param aParent
    */
   public AeFastNode(IAeFastParent aParent)
   {
      setParent(aParent);
   }

   /**
    * Detaches this node from its parent, or does nothing if this node has no
    * parent.
    *
    * @return This node detached.
    */
   public AeFastNode detach()
   {
      if (getParent() != null)
      {
         getParent().removeChild(this);
      }

      return this;
   }

   /**
    * Returns this node's parent.
    */
   public IAeFastParent getParent()
   {
      return mParent;
   }

   /**
    * Sets this node's parent to be the specified node.
    *
    * @param aParent
    */
   public void setParent(IAeFastParent aParent)
   {
      mParent = aParent;
   }
}
