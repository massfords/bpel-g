//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/AeIdentityProperty.java,v 1.1 2007/02/20 17:47:40 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity;

import org.activebpel.rt.util.AeUtil;

/**
 * String based identity property.
 */
public class AeIdentityProperty implements IAeIdentityProperty
{
   /** Property name. */
   private String mName;
   /** Property value. */
   private String mValue;
   
   /**
    * Ctor.
    * @param aName
    * @param aValue
    */
   public AeIdentityProperty(String aName, String aValue)
   {
      setName(aName);
      setValue(aValue);
   }

   /**
    * @see org.activebpel.rt.identity.IAeIdentityProperty#getName()
    */
   public String getName()
   {
      return mName;
   }
   
   /**
    * Sets the property name.
    * @param aName
    */
   protected void setName(String aName)
   {
      mName = aName;
   }

   /**
    * @see org.activebpel.rt.identity.IAeIdentityProperty#getValue()
    */
   public String getValue()
   {
      return mValue;
   }

   /**
    * Sets the property value.
    * @param aValue
    */
   protected void setValue(String aValue)
   {
      mValue = aValue;
   }
   
   /** 
    * Overrides method to see if the property names are the same. 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object other)
   {
      if (other instanceof IAeIdentityProperty)
      {
         IAeIdentityProperty otherProp = (IAeIdentityProperty) other;
         return getName().equals( otherProp.getName() ) && AeUtil.compareObjects(getValue(), otherProp.getValue());
      }
      else
      {
         return false;
      }
   }

   /** 
    * Overrides method to hash code of the property name. 
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return getName().hashCode();
   }   
}
