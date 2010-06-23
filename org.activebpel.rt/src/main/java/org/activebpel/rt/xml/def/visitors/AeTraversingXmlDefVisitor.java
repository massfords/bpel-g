//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/visitors/AeTraversingXmlDefVisitor.java,v 1.2 2007/10/23 12:28:29 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.visitors; 

import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * Base visitor with traversal 
 */
public class AeTraversingXmlDefVisitor extends AeBaseXmlDefVisitor implements IAeBaseXmlDefVisitor
{
   /** Used in conjunction with the traversal object to traverse the object model */
   private IAeBaseXmlDefVisitor mTraversalVisitor;
   
   /**
    * @see org.activebpel.rt.xml.def.visitors.AeBaseXmlDefVisitor#visitBaseXmlDef(org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   protected void visitBaseXmlDef(AeBaseXmlDef aDef)
   {
      traverse(aDef);
   }

   /**
    * Calls accept on the def object, passing in the traversal visitor.
    * @param aDef
    */
   protected void traverse(AeBaseXmlDef aDef)
   {
      aDef.accept(getTraversalVisitor());
   }

   /**
    * Setter for the traversal visitor.
    * @param traversalVisitor
    */
   public void setTraversalVisitor(IAeBaseXmlDefVisitor traversalVisitor)
   {
      mTraversalVisitor = traversalVisitor;
   }

   /**
    * Getter for the traversal visitor
    */
   public IAeBaseXmlDefVisitor getTraversalVisitor()
   {
      return mTraversalVisitor;
   }
}
 