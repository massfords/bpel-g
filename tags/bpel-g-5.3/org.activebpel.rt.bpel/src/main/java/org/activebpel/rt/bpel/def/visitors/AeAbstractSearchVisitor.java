//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeAbstractSearchVisitor.java,v 1.4 2007/10/01 17:11:13 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor;

/**
 * Base class for visitors that are searching for a particular def and want to
 * stop traversing after its found. 
 */
public abstract class AeAbstractSearchVisitor extends AeAbstractDefVisitor
{
   /**
    * No arg ctor 
    */
   public AeAbstractSearchVisitor()
   {
      setTraversalVisitor(new AeTraversalVisitor(new AeTraverseWhileNotFound(), this)); 
   }
   
   /**
    * Return true to stop searching
    */
   public abstract boolean isFound();

   /**
    * keeps traversing the def until we find what we're looking for.
    */
   protected class AeTraverseWhileNotFound extends AeDefTraverser
   {
      /**
       * @see org.activebpel.rt.bpel.def.visitors.AeDefTraverser#callAccept(org.activebpel.rt.xml.def.AeBaseXmlDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
       */
      protected void callAccept(AeBaseXmlDef aDef, IAeBaseXmlDefVisitor aVisitor)
      {
         if (!isFound())
            super.callAccept(aDef, aVisitor);
      }
   }
}
 