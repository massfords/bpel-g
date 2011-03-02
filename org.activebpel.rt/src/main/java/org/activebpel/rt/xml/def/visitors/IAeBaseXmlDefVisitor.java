//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/visitors/IAeBaseXmlDefVisitor.java,v 1.3 2007/12/05 22:42:36 EWittmann Exp $
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
import org.activebpel.rt.xml.def.AeDocumentationDef;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.activebpel.rt.xml.def.AeExtensionElementDef;

/**
 * Interface for performing operations on base def objects
 */
public interface IAeBaseXmlDefVisitor
{
   /**
    * Generic method for all base defs
    * 
    * @param aDef
    */
   public void visit(AeBaseXmlDef aDef);
   
   /**
    * Visits extension elements
    * @param aDef
    */
   public void visit(AeExtensionElementDef aDef);
   
   /**
    * Visits extension attributes
    * @param aDef
    */
   public void visit(AeExtensionAttributeDef aDef);
   
   /**
    * Visits documentation defs
    * @param aDef
    */
   public void visit(AeDocumentationDef aDef);
}
 