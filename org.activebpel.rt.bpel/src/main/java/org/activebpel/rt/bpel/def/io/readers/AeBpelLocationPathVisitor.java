//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/readers/AeBpelLocationPathVisitor.java,v 1.1 2007/10/01 17:11:14 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.readers; 

import org.activebpel.rt.bpel.def.visitors.AeDefTraverser;
import org.activebpel.rt.bpel.def.visitors.AeTraversalVisitor;
import org.activebpel.rt.xml.def.IAePathSegmentBuilder;
import org.activebpel.rt.xml.def.visitors.AeLocationPathVisitor;

/**
 * Subclass that sets up the traversal in the ctor 
 */
public class AeBpelLocationPathVisitor extends AeLocationPathVisitor
{

   /**
    * Set up the traverser
    * @param aSegmentPathBuilder
    */
   public AeBpelLocationPathVisitor(IAePathSegmentBuilder aSegmentPathBuilder)
   {
      super(aSegmentPathBuilder);
      setTraversalVisitor(new AeTraversalVisitor(new AeDefTraverser(), this));
   }

}
 