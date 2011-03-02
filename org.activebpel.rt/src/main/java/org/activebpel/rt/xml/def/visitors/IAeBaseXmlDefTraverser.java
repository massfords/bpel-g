//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/visitors/IAeBaseXmlDefTraverser.java,v 1.1 2007/11/08 12:31:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.visitors; 

import org.activebpel.rt.xml.def.AeDocumentationDef;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.activebpel.rt.xml.def.AeExtensionElementDef;

/**
 * Traverser interface that includes methods for the base def objects 
 */
public interface IAeBaseXmlDefTraverser
{
   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeExtensionAttributeDef aDef, IAeBaseXmlDefVisitor aVisitor);   
   
   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeExtensionElementDef aDef, IAeBaseXmlDefVisitor aVisitor);   

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeDocumentationDef aDef, IAeBaseXmlDefVisitor aVisitor);
   
}
 