//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/visitors/AeBaseXmlDefTraverser.java,v 1.3 2007/11/15 22:30:10 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.visitors; 

import java.util.Iterator;

import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeDocumentationDef;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.activebpel.rt.xml.def.AeExtensionElementDef;

/**
 * base traverser that provides methods for traversing the core xml defs 
 */
public class AeBaseXmlDefTraverser implements IAeBaseXmlDefTraverser
{
   /**
    * @see org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefTraverser#traverse(org.activebpel.rt.xml.def.AeDocumentationDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeDocumentationDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefTraverser#traverse(org.activebpel.rt.xml.def.AeExtensionAttributeDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeExtensionAttributeDef aDef,
         IAeBaseXmlDefVisitor aVisitor)
   {
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefTraverser#traverse(org.activebpel.rt.xml.def.AeExtensionElementDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeExtensionElementDef aDef,
         IAeBaseXmlDefVisitor aVisitor)
   {
   }

   /**
    * Called to traverse any extension defs that may exist.
    * @param aDef
    * @param aVisitor
    */
   protected void traverseExtensionDefs(AeBaseXmlDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      callAccept(aDef.getExtensionElementDefs().iterator(), aVisitor);
      callAccept(aDef.getExtensionAttributeDefs().iterator(), aVisitor);
   }

   /**
    * Traverse the documentation node.
    * @param aBaseDef
    * @param aVisitor
    */
   protected void traverseDocumentationDefs(AeBaseXmlDef aBaseDef, IAeBaseXmlDefVisitor aVisitor)
   {
      callAccept(aBaseDef.getDocumentationDefs().iterator(), aVisitor);
   }

   /**
    * Calls <code>accept</code> on each of the definition objects in the Iterator
    * @param aIterator
    * @param aVisitor
    */
   protected void callAccept(Iterator aIterator, IAeBaseXmlDefVisitor aVisitor)
   {
      while (aIterator.hasNext())
      {
         AeBaseXmlDef def = (AeBaseXmlDef)aIterator.next();
         callAccept(def, aVisitor);
      }
   }

   /**
    * Calls <code>accept</code> on the def.
    * @param aDef can be null.
    * @param aVisitor
    */
   protected void callAccept(AeBaseXmlDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      if ( aDef != null )
      {
         aDef.accept(aVisitor);
      }
   }

}
 