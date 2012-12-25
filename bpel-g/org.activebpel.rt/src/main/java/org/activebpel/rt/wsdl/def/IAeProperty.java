// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/IAeProperty.java,v 1.3 2006/06/26 16:46:43 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def;

import javax.xml.namespace.QName;

/**
 * This interface represents a Message Property extention element.  It contains
 * information about operations associated with this Property element.
 */
public interface IAeProperty
{
   /**
    * Get the Message Property name.
    * 
    * @return QName
    */
   public QName getQName();

   /**
    * Set the Message Property name.
    * 
    * @param aName
    */
   public void setQName(QName aName);

   /**
    * Get the Message Property type QName.
    * 
    * @return QName
    */
   public QName getTypeName();

   /**
    * Set the Message Property type QName.
    * 
    * @param aType
    */
   public void setTypeName(QName aType);
   
   /**
    * Getter for the element name or null if property is a type
    */
   public QName getElementName();
   
   /**
    * Setter for the element name
    * @param aQName
    */
   public void setElementName(QName aQName);

}
