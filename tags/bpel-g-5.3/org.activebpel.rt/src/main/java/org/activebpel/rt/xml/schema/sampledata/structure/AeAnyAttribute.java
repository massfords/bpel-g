//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/sampledata/structure/AeAnyAttribute.java,v 1.2 2007/02/20 21:57:11 mford Exp $
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
 *  Model class for the Schema wildcard attribute (anyAttribute).
 */
public class AeAnyAttribute extends AeBaseAttribute
{
   /** The replacement namespace. Will be null for unqalified namespace, i.e. ##local. */
   private String mNamespace;
   
   /**
    * Constructor.
    */
   public AeAnyAttribute()
   {
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.structure.AeStructure#accept(org.activebpel.rt.xml.schema.sampledata.IAeSampleDataVisitor)
    */
   public void accept(IAeSampleDataVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * Gets the replacement attribute namespace.
    * 
    * @return String
    */
   protected String getNamespace()
   {
      return mNamespace;
   }

   /**
    * Sets the replacement attribute namepace.
    * 
    * @param namespace
    */
   public void setNamespace(String namespace)
   {
      mNamespace = namespace;
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.structure.AeStructure#getType()
    */
   public int getType()
   {
      return AeStructure.ANY_ATTRIBUTE_TYPE;
   }
   
}
 
