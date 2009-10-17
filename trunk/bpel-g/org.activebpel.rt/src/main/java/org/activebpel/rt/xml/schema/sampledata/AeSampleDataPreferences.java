//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/sampledata/AeSampleDataPreferences.java,v 1.11 2008/03/20 14:27:22 kpease Exp $
////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.schema.sampledata; 

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.util.AeUtil;


/**
 * A preferences class for sample data generation.
 */
public class AeSampleDataPreferences implements IAeSampleDataPreferences
{
   /** Choice group preference. */
   private int mChoiceStyle = CHOICE_STYLE_FIRST;
   
   /** The preferred number of times to repeat elements. */ 
   private int mElementRepeatCount = 1;

   /** The preferred namespace prefix map. */
   private Map mNamespaceMap = new HashMap();

   /** The limit or depth of recursive structure references. */
   private int mRecursionLimit = 2;
   
   /** Flag indicating if optional Attributes should be created. */
   private boolean mCreateOptionalAttr = true;
   
   /** Flag indicating if optional Elements should be created */
   private boolean mCreateOptionalElements = true;

   /** Flag indicating if sample data should be generated for attributes. */
   private boolean mGenAttrSampleData = true;

   /** Flag indicating if sample data should be generated for elements. */
   private boolean mGenElementSampleData = true;
   
   /** Flag indicating if nillable elements should have their contents generated. */
   private boolean mGenerateNillableContent = true;
   
   /** Generates sample data for schema simple types */
   private AeSimpleTypeSampleDataProducer mDataProducer = new AeSimpleTypeSampleDataProducer();
   
   /** map of QNames to data to use for mixed complex types */
   private Map mMixedDataMap = new HashMap();
   
   /** The preferred display prefix string. */
   private static final String PREFERRED_PREFIX = "ns"; //$NON-NLS-1$


   /**
    * Constructor.
    */
   public AeSampleDataPreferences()
   {
      // prime the pump w/ some popular namespace prefix mappings.
      setPreferredPrefix("xsi",     IAeConstants.W3C_XML_SCHEMA_INSTANCE); //$NON-NLS-1$
      setPreferredPrefix("xsd",     IAeConstants.W3C_XML_SCHEMA);          //$NON-NLS-1$
      setPreferredPrefix("wsa",     IAeConstants.WSA_NAMESPACE_URI);       //$NON-NLS-1$
      setPreferredPrefix("soapenv", IAeConstants.SOAP_NAMESPACE_URI);      //$NON-NLS-1$
      setPreferredPrefix("xml",     IAeConstants.W3C_XML_NAMESPACE);      //$NON-NLS-1$
   }
   
   /**
    * Gets the preference behavior for processing a choice group.
    * 
    * @return int
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences#getChoiceStyle()
    */
   public int getChoiceStyle()
   {
      return mChoiceStyle;
   }
   
   /**
    * Sets the Choice group preference.
    * 
    * @param aPref
    */
   public void setChoiceStyle(int aPref)
   {
      mChoiceStyle = aPref;
   }

   /**
    * Gets the preferred number of times to repeat elements. If this value out of range for a 
    * given element then the minOccurs for that element will be assumed and generated.
    * 
    * @return int
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences#getNumberOfRepeatingElements()
    */
   public int getNumberOfRepeatingElements()
   {
      return mElementRepeatCount;
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences#getNumberOfRepeatingElements(javax.xml.namespace.QName)
    */
   public int getNumberOfRepeatingElements(QName aElementQName)
   {
      return mElementRepeatCount;
   }
   
   /**
    * Sets the preferred number of times to repeat elements. If this value out of range for a 
    * given element then the minOccurs for that element will be assumed and generated.
    *
    * @param aCount
    */
   public void setNumberOfRepeatingElements(int aCount)
   {
      mElementRepeatCount = aCount;
   }
   
   /**
    * Returns the preferred namespace prefix.
    * 
    * @return String
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences#getPreferredPrefix(java.lang.String)
    */
   public String getPreferredPrefix(String aNamespace)
   {
      String prefix = (String) mNamespaceMap.get(aNamespace);
      if (prefix == null)
         prefix = PREFERRED_PREFIX; 
      return prefix;
   }

   /**
    * Returns the preferred namespace prefix. If the prefix is not mapped for the given
    * namespace then map it.
    * 
    * @return String
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences#getPreferredPrefix(java.lang.String)
    */
   public String getAndMapPrefix(String aNamespace)
   {
      String preferredPrefix = PREFERRED_PREFIX;
      String prefix = (String) mNamespaceMap.get(aNamespace);
      if (prefix == null)
      { 
         // Create a unique prefix
         int i = 1;
         prefix = preferredPrefix; 
         while ( mNamespaceMap.containsKey(prefix) )
            prefix = preferredPrefix + i++;

         setPreferredPrefix(prefix, aNamespace);
      }
      return prefix;
   }

   /**
    * Sets the preferred namespace prefix.
    * 
    * @param aPrefix
    * @param aNamespace
    */
   public void setPreferredPrefix(String aPrefix, String aNamespace)
   {
      mNamespaceMap.put(aNamespace, aPrefix);
   }

   /**
    * Returns the limit or depth of recursive structure references.
    * 
    * @return int.
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences#getRecursionLimit()
    */
   public int getRecursionLimit()
   {
      return mRecursionLimit;
   }

   /**
    * Sets the limit or depth of recursive structure references.
    * 
    * @param aLimit
    */
   public void setRecursionLimit(int aLimit)
   {
      mRecursionLimit = aLimit;
   }
   
   /**
    * Indicates if optional Attributes should be created.
    *  
    * @return boolean true if optional attributes are to be created, otherwise false.
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences#isCreateOptionalAttributes()
    */
   public boolean isCreateOptionalAttributes()
   {
      return mCreateOptionalAttr;
   }
   
   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences#isCreateOptionalAttribute(javax.xml.namespace.QName)
    */
   public boolean isCreateOptionalAttribute(QName aAttributeName)
   {
      // This class does not provide a way to control the generation of
      // optional attributes at this granularity.
      return true;
   }

   /**
    * Sets the flag indicating if optional Attributes should be created.
    *  
    * @param aCreate
    */
   public void setCreateOptionalAttributes(boolean aCreate)
   {
      mCreateOptionalAttr =  aCreate;
   }
   
   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences#getAttributeData(javax.xml.namespace.QName, javax.xml.namespace.QName, javax.xml.namespace.QName)
    */
   public String getAttributeData(QName aParentName, QName aAttributeName, QName aType)
   {
      if (isGenerateAttributeData())
      {
         return getDataProducer().getSampleData(aType);
      }
      return ""; //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences#getElementData(javax.xml.namespace.QName, javax.xml.namespace.QName)
    */
   public String getElementData(QName aElementName, QName aType)
   {
      if (isGenerateElementData())
      {
         return getDataProducer().getSampleData(aType);
      }
      return ""; //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences#getElementData(javax.xml.namespace.QName, javax.xml.namespace.QName, java.lang.String)
    */
   public String getElementData(QName aElementName, QName aType, String aMinInclusive, String aMaxInclusive, String aMinExclusive, String aMaxExclusive)
   {
      if (isGenerateElementData())
      {
         return getDataProducer().getSampleData(aType, aMinInclusive, aMaxInclusive, aMinExclusive, aMaxExclusive);
      }
      return ""; //$NON-NLS-1$
   }
   
   /**
    * Sets the flag indicating if sample data should be generated for attributes.
    * 
    * @param aGenData
    */
   public void setGenerateAttributeSampleData(boolean aGenData)
   {
      mGenAttrSampleData = aGenData;
   }
   
   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences#isGenerateElementData()
    */
   public boolean isGenerateElementData()
   {
      return mGenElementSampleData;
   }

   /**
    * Sets the flag indicating if sample data should be generated for elements.
    * 
    * @param aGenData
    */
   public void setGenerateElementSampleData(boolean aGenData)
   {
      mGenElementSampleData = aGenData;
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences#isGenerateNillableContent()
    */
   public boolean isGenerateNillableContent()
   {
      return mGenerateNillableContent;
   }

   /**
    * Sets the flag indicating if nillable elements should provide content.
    * 
    * @param aFlag true to generate content for nillable elements
    */
   public void setGenerateNillableContent(boolean aFlag)
   {
      mGenerateNillableContent = aFlag;
   }

   /**
    * @return the dataProducer
    */
   protected AeSimpleTypeSampleDataProducer getDataProducer()
   {
      return mDataProducer;
   }

   /**
    * @param aDataProducer the dataProducer to set
    */
   protected void setDataProducer(AeSimpleTypeSampleDataProducer aDataProducer)
   {
      mDataProducer = aDataProducer;
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences#getMixedContent(javax.xml.namespace.QName)
    */
   public String getMixedContent(QName aName)
   {
      String value = (String) getMixedDataMap().get(aName);
      return AeUtil.getSafeString(value);
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences#isGenerateAttributeData()
    */
   public boolean isGenerateAttributeData()
   {
      return mGenAttrSampleData;
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences#selectElementValue(javax.xml.namespace.QName, java.util.List)
    */
   public String selectElementValue(QName aName, List aEnumRestrictions)
   {
      return (String) aEnumRestrictions.get(0);
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences#selectAttributeValue(javax.xml.namespace.QName, javax.xml.namespace.QName, java.util.List)
    */
   public String selectAttributeValue(QName aParentElement, QName aName, List aEnumRestrictions)
   {
      return (String) aEnumRestrictions.get(0);
   }

   /**
    * @return the mixedDataMap
    */
   protected Map getMixedDataMap()
   {
      return mMixedDataMap;
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences#isCreateOptionalElements()
    */
   public boolean isCreateOptionalElements()
   {
      return mCreateOptionalElements;
   }
   
   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences#isCreateOptionalElement(javax.xml.namespace.QName)
    */
   public boolean isCreateOptionalElement(QName aElementName)
   {
      // This class does not provide a way to control the generation of
      // optional elements at this granularity.
      return true;
   }
   
   /**
    * @param aCreateOptionalElements
    */
   public void setCreateOptionalElements(boolean aCreateOptionalElements)
   {
      mCreateOptionalElements = aCreateOptionalElements;
   }
}
