//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/sampledata/structure/AeSimpleElement.java,v 1.3 2008/03/20 14:27:22 kpease Exp $
////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.schema.sampledata.structure; 

import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.xml.schema.sampledata.IAeSampleDataVisitor;

/**
 * Model class for a Schema Simple Type.
 */
public class AeSimpleElement extends AeBaseElement
{
   /** The data type of this element. */
   private QName mDataType;
   
   // Note that a simpleType's "default" and "fixed" attributes are optional and mutually exclusive. 
   
   /** The default value for this element. */
   private String mDefaultValue;

   /** The  fixed value for this element. **/
   private String mFixed;

   /** List of enumerated restrictions. (String objects). */
   private List mEnumRestrictions;
   
   private String mMinExclusive;
   private String mMaxExclusive;
   private String mMinInclusive;
   private String mMaxInclusive;

   /**
    * Constructor.
    */
   public AeSimpleElement()
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
    * @return QName
    */
   public QName getDataType()
   {
      return mDataType;
   }

   /**
    * @param aDataType
    */
   public void setDataType(QName aDataType)
   {
      mDataType = aDataType;
   }

   /**
    * @return String
    */
   public String getDefaultValue()
   {
      return mDefaultValue;
   }

   /**
    * @param aDefaultValue
    */
   public void setDefaultValue(String aDefaultValue)
   {
      mDefaultValue = aDefaultValue;
   }
   /**
    * Gets the fixed value for this element. 
    * 
    * @return String.
    */
   public String getFixedValue()
   {
      return mFixed;
   }

   /**
    * Sets the fixed value for this element.
    * 
    * @param aFixed
    */
   public void setFixedValue(String aFixed)
   {
      mFixed = aFixed;
   }

   /**
    * @return List
    */
   public List getEnumRestrictions()
   {
      return mEnumRestrictions;
   }

   /**
    * @param aEnumRestrictions
    */
   public void setEnumRestrictions(List aEnumRestrictions)
   {
      mEnumRestrictions = aEnumRestrictions;
   }

   /**
    * @return the minExclusive
    */
   public String getMinExclusive()
   {
      return mMinExclusive;
   }

   /**
    * @param aMinExclusive the minExclusive to set
    */
   public void setMinExclusive(String aMinExclusive)
   {
      mMinExclusive = aMinExclusive;
   }

   /**
    * @return the maxExclusive
    */
   public String getMaxExclusive()
   {
      return mMaxExclusive;
   }

   /**
    * @param aMaxExclusive the maxExclusive to set
    */
   public void setMaxExclusive(String aMaxExclusive)
   {
      mMaxExclusive = aMaxExclusive;
   }

   /**
    * @return the minInclusive
    */
   public String getMinInclusive()
   {
      return mMinInclusive;
   }

   /**
    * @param aMinInclusive the minInclusive to set
    */
   public void setMinInclusive(String aMinInclusive)
   {
      mMinInclusive = aMinInclusive;
   }

   /**
    * @return the maxInclusive
    */
   public String getMaxInclusive()
   {
      return mMaxInclusive;
   }

   /**
    * @param aMaxInclusive the maxInclusive to set
    */
   public void setMaxInclusive(String aMaxInclusive)
   {
      mMaxInclusive = aMaxInclusive;
   }

}
 
