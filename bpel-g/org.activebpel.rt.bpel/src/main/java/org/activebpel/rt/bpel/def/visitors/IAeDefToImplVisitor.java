//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/IAeDefToImplVisitor.java,v 1.3 2006/09/27 00:36:26 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;

/**
 * interface for the def to impl visitor 
 */
public interface IAeDefToImplVisitor extends IAeDefVisitor
{
   /**
    * Gets the process that was created
    */
   public IAeBusinessProcessInternal getProcess();
   
   /**
    * Gets the traversal visitor
    */
   public IAeDefVisitor getTraversalVisitor();
   
   /**
    * Sets the traversal visitor
    * @param aDefVisitor
    */
   public void setTraversalVisitor(IAeDefVisitor aDefVisitor);
   
   /**
    * This will notify the process of all of the variables, partner links, and bpel objects created
    * by the visitor. 
    */
   public void reportObjects();
}
 