//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/sampledata/structure/AeBaseElement.java,v 1.3 2008/02/17 21:09:19 mford Exp $
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
 * Base class for element models (e.g. complex, simple, abstract.)
 */
public abstract class AeBaseElement extends AeStructure
{
   /** The name of this element. Namespace will be empty if the name is unqualified. */
   private QName mName;

   /** Nillable indicator. */
   private boolean mNillable;

   /**
    * Called to accept the sample data type visitor.  All implementations should simply call
    * <code>aVisitor.visit(this)</code>.
    * 
    * @param aVisitor
    */
   public abstract void accept(IAeSampleDataVisitor aVisitor);

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
    * @return boolean.
    */
   public boolean isNillable()
   {
      return mNillable;
   }

   /**
    * @param aNillable
    */
   public void setNillable(boolean aNillable)
   {
      mNillable = aNillable;
   }

   /**
    * @return boolean
    */
   public boolean isAbstractElement()
   {
      return false;
   }
   
   /**
    * @see org.activebpel.rt.xml.schema.sampledata.structure.AeStructure#getType()
    */
   public int getType()
   {
      return AeStructure.ELEMENT_TYPE;
   }
}
 
