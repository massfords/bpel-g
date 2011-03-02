//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/sampledata/structure/AeAny.java,v 1.3 2008/02/17 21:09:19 mford Exp $
////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.schema.sampledata.structure; 

import javax.xml.namespace.QName;

import org.activebpel.rt.xml.schema.sampledata.IAeSampleDataVisitor;

/**
 * Model class for the Schema any element wildcard.
 */
public class AeAny extends AeStructure
{
   /** The name of this element. Namespace will be empty if the name is unqualified. */
   private QName mName;
   
   /**
    * Constructor.
    */
   public AeAny()
   {
   }

   /**
    * Gets the name of this element.
    * 
    * @return QName.
    */
   public QName getName()
   {
      return mName;
   }

   /**
    * Sets the name of this element.
    * 
    * @param aName
    */
   public void setName(QName aName)
   {
      mName = aName;
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.structure.AeStructure#accept(org.activebpel.rt.xml.schema.sampledata.IAeSampleDataVisitor)
    */
   public void accept(IAeSampleDataVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.structure.AeStructure#getType()
    */
   public int getType()
   {
      return AeStructure.ANY_TYPE;
   }

}
 
