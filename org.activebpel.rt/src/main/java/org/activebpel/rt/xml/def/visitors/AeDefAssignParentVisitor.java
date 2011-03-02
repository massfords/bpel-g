// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/visitors/AeDefAssignParentVisitor.java,v 1.1.4.1 2008/04/21 16:05:13 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.visitors;

import java.util.Stack;

import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * <p>Visitor responsible for assigning parent-to-child relationships among the def model.</p>
 * <p>
 *
 * Use visitor whenever the parent assignments should be updated 
 * (but <i>after</i> the def model is completely built).
 * </p>
 * <p>
 * A traversal visitor must be set prior to using this visitor.
 * </p>
 */
public class AeDefAssignParentVisitor extends AeTraversingXmlDefVisitor
{
   /** Stores the stack of objects that we're visiting */
   protected Stack mStack = new Stack();

   /**
    * Default c'tor.
    */
   public AeDefAssignParentVisitor()
   {
   }

   /**
    * Pushes a def onto the stack, making it the current parent.
    * @param aDef
    */
   protected void push( AeBaseXmlDef aDef )
   {
      mStack.push( aDef );
   }

   /**
    * Pops the current parent def from the stack.
    */
   protected void pop()
   {
      mStack.pop();
   }

   /**
    * Peeks at the current parent on the stack.
    */
   protected AeBaseXmlDef peek()
   {
      if ( mStack.isEmpty())
         return null ;
      else
         return (AeBaseXmlDef)mStack.peek();
   }

   /**
    * Set the current def's parent, then traverse any children.
    *
    * @param aDef The def to assign parent for, and then traverse.
    * @see org.activebpel.rt.xml.def.visitors.AeTraversingXmlDefVisitor#traverse(org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   protected void traverse( AeBaseXmlDef aDef )
   {
      if ( peek() != null )
         aDef.setParentXmlDef( peek());

      // Push the current def - if it has any children, this will be assigned as their parent.
      //
      push( aDef );
      super.traverse( aDef );
      pop();
   }
}
