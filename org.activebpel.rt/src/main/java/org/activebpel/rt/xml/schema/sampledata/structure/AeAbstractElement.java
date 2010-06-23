//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/sampledata/structure/AeAbstractElement.java,v 1.2 2007/02/20 21:57:11 mford Exp $
////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.schema.sampledata.structure; 

import org.activebpel.rt.xml.schema.sampledata.IAeSampleDataVisitor;


/**
 * Model class for a Schema abstract complexType definition.
 */
public class AeAbstractElement extends AeBaseElement
{
   /**
    * Ctor 
    */
   public AeAbstractElement()
   {
   }
   
   /**
    * @see org.activebpel.rt.xml.schema.sampledata.structure.AeBaseElement#isAbstractElement()
    */
   public boolean isAbstractElement()
   {
      return true;
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.structure.AeStructure#accept(org.activebpel.rt.xml.schema.sampledata.IAeSampleDataVisitor)
    */
   public void accept(IAeSampleDataVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
 
