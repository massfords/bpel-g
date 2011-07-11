//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/sampledata/IAeSampleDataPreferences.java,v 1.6 2008/03/20 14:27:22 kpease Exp $
////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.schema.sampledata;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * Preferences interface for schema to XML sample document generation.
 */
public interface IAeSampleDataPreferences
{
   // Choice group preferences.
   /** Generate the first choice instance. */
   public static int CHOICE_STYLE_FIRST  = 0;
   /** Generate the first choice instance with the remaining instances commented out. */
   public static int CHOICE_STYLE_FIRST_WITH_COMMENT = 1;

   /**
    * Indicates if optional Attributes should be created.
    * @return boolean true if optional attributes are to be created, otherwise false.
    */
   public boolean isCreateOptionalAttributes();

   /**
    * Indicates if the specific optional Attribute should be created.
    *
    * @param aAttributeName
    * @return boolean true if optional attribute is to be created, otherwise false.
    */
   public boolean isCreateOptionalAttribute(QName aAttributeName);

   /**
    * Indicates if optional Elements should be created
    * @return boolean true if optional elements are to be created, otherwise false.
    */
   public boolean isCreateOptionalElements();

   /**
    * Indicates if the specific optional Element should be created.
    *
    * @param aElementName the name of the optional element
    * @return boolean true if optional element is to be created
    */
   public boolean isCreateOptionalElement(QName aElementName);

   /**
    * Returns the limit or depth of recursive structure references.
    * @return int.
    */
   public int getRecursionLimit();

   /**
    * Gets the text data to use for the given element QName
    * @param aElementName
    * @param aType
    */
   public String getElementData(QName aElementName, QName aType);

   /**
    * Gets the text data to use for the given element QName, 
    * bound by the minimum and maximum value constraints.
    * 
    * @param aElementName
    * @param aType
    * @param aMinInclusive
    * @param aMaxInclusive
    * @param aMinExclusive
    * @param aMaxExclusive
    */
   public String getElementData(QName aElementName, QName aType, String aMinInclusive, String aMaxInclusive, String aMinExclusive, String aMaxExclusive);
   
   /**
    * Gets the text data to use for the given attribute QName
    * @param aParentElementName
    * @param aAttributeName
    * @param aType
    */
   public String getAttributeData(QName aParentElementName, QName aAttributeName, QName aType);

   /**
    * Gets the preferred number of times to repeat elements. If this value out of range for a
    * given element then the minOccurs for that element will be assumed and generated.
    *
    * @return int
    */
   public int getNumberOfRepeatingElements();

   /**
    * Gets the preferred number of times to repeat the given element. If this
    * value out of range for a given element then the minOccurs for that element
    * will be assumed and generated.
    * 
    * @param aElementQName
    * @return int
    */
   public int getNumberOfRepeatingElements(QName aElementQName);

   /**
    * Gets the preference behavior for processing a choice group.
    *
    * @return int
    */
   public int getChoiceStyle();

   /**
    * Returns the preferred namespace prefix. E.g. "ns".
    *
    * @return String
    */
   public String getPreferredPrefix(String aNamespace);

   /**
    * For nillable elements this flag indicates if content should be generated.
    *
    * @return boolean if true do not provide content for nillable elements, otherwise provide content.
    */
   public boolean isGenerateNillableContent();

   /**
    * Gets the mixed content for the given element qname
    * @param aName
    */
   public String getMixedContent(QName aName);

   /**
    * Returns true if we're generating sample data for elements
    */
   public boolean isGenerateElementData();

   /**
    * Returns true if we're generating sample data for attributes
    */
   public boolean isGenerateAttributeData();

   /**
    * Selects one of the values from the list of available values.
    * @param aName
    * @param aEnumRestrictions
    */
   public String selectElementValue(QName aName, List aEnumRestrictions);

   /**
    * Selects one of the values from the list of available values
    * @param aParentElement
    * @param aName
    * @param aEnumRestrictions
    */
   public String selectAttributeValue(QName aParentElement, QName aName, List aEnumRestrictions);
}

