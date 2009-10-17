// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/AePropertyAliasImpl.java,v 1.12 2006/09/11 17:44:11 JPerrotto Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def;

import java.util.HashMap;
import java.util.Map;

import javax.wsdl.extensions.ExtensibilityElement;
import javax.xml.namespace.QName;

import org.activebpel.rt.util.AeUtil;

/**
 * This class represents an implementation mapping to a Message Property
 * Alias extension within a WSDL document. It contains information about
 * operations associated with this Message Property Alias element.
 * 
 */
public class AePropertyAliasImpl
   implements ExtensibilityElement, IAePropertyAlias, IAeBPELExtendedWSDLConst
{
   /** The QName of this extension element. */
   private QName mElementType;

   /** Indicates if the semantics of this extension are required. */
   private Boolean mRequired;

   /** Property Name. */
   private QName mPropName;
   
   /** type - either a message, element, or complex type name */
   private QName mQName;
   
   /** indicates which type the above qname is for */
   private int mType;

   /** Part value. */
   private String mPart;

   /** Query value. */
   private String mQuery;
   
   /** Query language */
   private String mQueryLanguage;
   
   // Table of namespaces defined
   private Map mNamespaces = new HashMap();

   /**
    * Constructor.  This contructor is generally used for modeling a new
    * Property Alias extension element. I.e. this extension implementation is
    * not being generated via a deserializer.
    */
   public AePropertyAliasImpl()
   {
      setElementType(new QName(PROPERTY_1_1_NAMESPACE, PROPERTY_ALIAS_TAG));
   }

   /**
    * Get whether or not the semantics of this extension are required.
    * 
    * @return Boolean
    */
   public Boolean getRequired()
   {
      return mRequired;
   }
   
   /**
    * Set whether or not the semantics of this extension are required.
    * 
    * @param aRequired
    */
   public void setRequired(Boolean aRequired)
   {
      mRequired = aRequired;
   }
   
   /**
    * Set the QName of this Property element.
    * 
    * @param aElementType
    */
   public void setElementType(QName aElementType)
   {
      mElementType = aElementType;
   }

   /**
    * Get the QName of this Property element.
    * 
    * @return QName
    */
   public QName getElementType()
   {
      return mElementType;
   }

   /**
    * Get the Property Name value.
    * 
    * @return QName the propertyName value.
    */
   public QName getPropertyName()
   {
      return mPropName;
   }

   /**
    * Set the Property Name value.
    * 
    * @param aPropName QName propertyName.
    */
   public void setPropertyName(QName aPropName)
   {
      mPropName = aPropName;
   }
   
   /**
    * Getter for the QName
    */
   public QName getQName()
   {
      return mQName;
   }
   
   /**
    * Setter for the qname
    * 
    * @param aQName
    */
   protected void setQName(QName aQName)
   {
      mQName = aQName;
   }
   
   /**
    * @see org.activebpel.rt.wsdl.def.IAePropertyAlias#getType()
    */
   public int getType()
   {
      return mType;
   }
   
   /**
    * Setter for the type
    * @param aType
    */
   protected void setType(int aType)
   {
      mType = aType;
   }

   /**
    * Get the Message Type value.  This maps to a standard WSDL Message element.
    * 
    * @return QName the messageType value.
    */
   public QName getMessageName()
   {
      if (getType() == IAePropertyAlias.MESSAGE_TYPE)
         return getQName();
      else
         return null;
   }

   /**
    * Set the Message Type value.
    * 
    * @param aMsgType QName MessageType.
    */
   public void setMessageName(QName aMsgType)
   {
      setQName(aMsgType);
      setType(IAePropertyAlias.MESSAGE_TYPE);
   }

   /**
    * @see org.activebpel.rt.wsdl.def.IAePropertyAlias#getElementName()
    */
   public QName getElementName()
   {
      if (getType() == IAePropertyAlias.ELEMENT_TYPE)
         return getQName();
      else
         return null;
   }

   /**
    * @see org.activebpel.rt.wsdl.def.IAePropertyAlias#setElementName(javax.xml.namespace.QName)
    */
   public void setElementName(QName aElementName)
   {
      setQName(aElementName);
      setType(IAePropertyAlias.ELEMENT_TYPE);
   }

   /**
    * @see org.activebpel.rt.wsdl.def.IAePropertyAlias#getTypeName()
    */
   public QName getTypeName()
   {
      if (getType() == IAePropertyAlias.TYPE)
         return getQName();
      else
         return null;
   }

   /**
    * @see org.activebpel.rt.wsdl.def.IAePropertyAlias#setTypeName(javax.xml.namespace.QName)
    */
   public void setTypeName(QName aTypeName)
   {
      setQName(aTypeName);
      setType(IAePropertyAlias.TYPE);
   }

   /**
    * Get the Part value.
    * 
    * @return String
    */
   public String getPart()
   {
      return mPart;
   }

   /**
    * Set the Part value.
    * 
    * @param aPart
    */
   public void setPart(String aPart)
   {
      mPart = aPart;
   }

   /**
    * Get the Query value.
    * 
    * @return String
    */
   public String getQuery()
   {
      return mQuery;
   }

   /**
    * Set the Query value.
    * 
    * @param aQuery
    */
   public void setQuery(String aQuery)
   {
      if (AeUtil.isNullOrEmpty(aQuery))
      {
         mQuery = null;
      }
      else 
      {
         mQuery = aQuery;
      }
   }

   /**
    * Returns a hashtable of namespaces in use by this property alias.
    */
   public Map getNamespaces()
   {
      return mNamespaces;
   }

   /**
    * Sets the namespaces in use by this property alias.
    */
   public void setNamespaces(Map aNamespaces)
   {
      mNamespaces = aNamespaces;
   }
   
   /**
    * @see org.activebpel.rt.wsdl.def.IAePropertyAlias#getQueryLanguage()
    */
   public String getQueryLanguage()
   {
      return mQueryLanguage;
   }

   /**
    * @see org.activebpel.rt.wsdl.def.IAePropertyAlias#setQueryLanguage(java.lang.String)
    */
   public void setQueryLanguage(String aLanguage)
   {
      mQueryLanguage = aLanguage;
   }

   /**
    * Override the comparison operator to determine if objects are equal.  
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aObj)
   {
      // Make sure we have the proper object for comparison
      if (! (aObj instanceof AePropertyAliasImpl))
         return false;
      
      AePropertyAliasImpl target = (AePropertyAliasImpl)aObj;
      
      return AeUtil.compareObjects(mElementType, target.getElementType())
         && AeUtil.compareObjects(mPropName, target.getPropertyName())
         && AeUtil.compareObjects(mQName, target.getQName())
         && mType == target.getType()
         && AeUtil.compareObjects(mPart, target.getPart())
         && AeUtil.compareObjects(mQuery, target.getQuery())
         && AeUtil.compareObjects(mQueryLanguage, target.getQueryLanguage())
         && AeUtil.compareObjects(mRequired, target.getRequired());
   }
}
