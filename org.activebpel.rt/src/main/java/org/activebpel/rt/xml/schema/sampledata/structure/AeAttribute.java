//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/sampledata/structure/AeAttribute.java,v 1.3 2008/02/17 21:09:19 mford Exp $
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
 *  Model of a Schema attribute.
 */
public class AeAttribute extends AeBaseAttribute
{
   /** This attributes simpleType. */
   private QName mDataType;

   // Note that an attribute's "default", "prohibited" and "fixed" attributes are
   // mutually exclusive. The default value is "optional".
   
   /** Flag indicating if this attribute is optional. (default value if not specified. */
   private boolean mOptional = true;

   /** Flag indicating if this attribute is required. */
   private boolean mRequired;
   
   /** The default value for this attribute. */
   private String mDefault;

   /** The  fixed value for this attribute. **/
   private String mFixed;

   /** List of enumerated restrictions. (String objects). */
   private List mEnumRestrictions;
   
   /** The name of this attribute. Note: namespaceURI will be empty if this attribute is unqualified. */
   private QName mName;

   /**
    * Constructor.
    */
   public AeAttribute()
   {
   }

   /**
    * Gets this attributes simple type.
    * 
    * @return QName
    */
   public QName getDataType()
   {
      return mDataType;
   }
   
   /**
    * Sets this attributes simple type.
    * 
    * @param aDataType
    */
   public void setDataType(QName aDataType)
   {
      mDataType = aDataType;
   }
   
   /**
    * @return boolean
    */
   public boolean isRequired()
   {
      return mRequired;
   }
   
   /**
    * @param aRequired
    */
   public void setRequired(boolean aRequired)
   {
      mRequired = aRequired;
   }

   /**
    * Gets the default value for this attribute.  
    * Returns null if no default value was specified or the attribute is
    * required.
    * 
    * @return String
    */
   public String getDefaultValue()
   {
      return mDefault;
   }

   /**
    * Sets the default value for this attribute.
    * 
    * @param aDefault
    */
   public void setDefaultValue(String aDefault)
   {
      mDefault = aDefault;
   }

   /**
    * Returns true if the use of this attribute is optional.
    *
    * @return boolean true if attribute optional, otherwise it's required.
    */
   public boolean isOptional()
   {
      return mOptional;
   }

   /**
    * Sets the flag indicating that this attribute is optional.
    * 
    * @param aOptional true if optional, false if required.
    */
   public void setOptional(boolean aOptional)
   {
      mOptional = aOptional;
   }

   /**
    * Gets the fixed value for this attribute. Returns null if the 
    * attribute is required.
    * 
    * @return String.
    */
   public String getFixedValue()
   {
      return mFixed;
   }

   /**
    * Sets the fixed value for this attribute.
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
      return AeStructure.ATTRIBUTE_TYPE;
   }

   /**
    * Gets the name of this attribute.
    * 
    * @return QName
    */
   public QName getName()
   {
      return mName;
   }
   
   /**
    * Sets the name of this attribute.
    * 
    * @param aName
    */
   public void setName(QName aName)
   {
      mName = aName;
   }
}
 
