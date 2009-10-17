// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/IAePropertyAlias.java,v 1.7 2006/08/01 17:47:46 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def;

import java.util.Map;

import javax.xml.namespace.QName;

/**
 * This interface represents a Message Property Alias extention element.  It
 * contains information about operations associated with this Property
 * Alias element.
 */
public interface IAePropertyAlias
{
   /** constant indicates that this property alias is for a wsdl message */
   public static final int MESSAGE_TYPE = 0;
   /** constant indicates that this property alias is for a schema element */
   public static final int ELEMENT_TYPE = 1;
   /** constant indicates that this property alias is for a schema type */
   public static final int TYPE = 2;
   
   /**
    * Get the Property Name value.
    * 
    * @return QName the propertyName value.
    */
   public QName getPropertyName();

   /**
    * Set the Property Name value.
    * 
    * @param aPropName QName propertyName.
    */
   public void setPropertyName(QName aPropName);
   
   /**
    * Gets the QName of the underlying variable type without regard to whether its
    * message, element, or a complex type. 
    */
   public QName getQName();

   /**
    * Get the Message name value.
    * 
    * @return QName the messageType value or null if not a prop alias for message type
    */
   public QName getMessageName();
   
   /**
    * Set the Message name value.
    * 
    * @param aMsgType
    */
   public void setMessageName(QName aMsgType);
   
   /**
    * Setter for the element name
    * @param aElementName
    */
   public void setElementName(QName aElementName);
   
   /**
    * Getter for the element name.
    * @return QName or null if not a prop alias for element type
    */
   public QName getElementName();
   
   /**
    * Setter for the type name
    * 
    * @param aTypeName
    */
   public void setTypeName(QName aTypeName);
   
   /**
    * Getter for the type name
    * @return QName or null if not a prop alias for a complex type
    */
   public QName getTypeName();
   
   /**
    * Returns one of the constants above which indicate what type of variable this 
    * prop alias is for (message, element, complex type)
    */
   public int getType();

   /**
    * Get the Part value.
    * 
    * @return String
    */
   public String getPart();

   /**
    * Set the Part value.
    * 
    * @param aPart
    */
   public void setPart(String aPart);

   /**
    * Get the Query value.
    * 
    * @return String
    */
   public String getQuery();

   /**
    * Set the Query value.
    * 
    * @param aQuery
    */
   public void setQuery(String aQuery);
   
   /**
    * Getter for the query language
    */
   public String getQueryLanguage();
   
   /**
    * Setter for the query language
    * @param aLanguage
    */
   public void setQueryLanguage(String aLanguage);

   /**
    * Returns a hashtable of namespaces in use by this property alias.
    */
   public Map getNamespaces();

   /**
    * Sets the namespaces in use by this property alias.
    */
   public void setNamespaces(Map aNamespaces);
}
