//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/provider/ldap/AeLdapEntry.java,v 1.4.4.1 2008/04/21 16:16:25 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.provider.ldap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.util.AeUtil;

/**
 * Base class for an LDAP object such the LDAP groups and users.
 */
public class AeLdapEntry
{
   /** LDAP properties map. LDAP property may be string or a list for multivalue properties. */
   private Map mProperties;
   
   /** The attribute used as the entry name. The default value is 'cn'. */
   private String mNameAttribute = "cn"; //$NON-NLS-1$

   /**
    * Default ctor.
    */
   public AeLdapEntry()
   {
      this((Map)null);
   }

   /**
    * Copy constuctor.
    * @param aOtherEntry LDAP entry.
    */
   public AeLdapEntry(AeLdapEntry aOtherEntry)
   {
      this(aOtherEntry.getProperties());
      if (AeUtil.notNullOrEmpty( aOtherEntry.getNameAttribute()))
      {
         setNameAttribute(aOtherEntry.getNameAttribute());
      }
   }

   /**
    * Contructs a AeLdapEntry with the given the LDAP entry properties.
    * @param aProperties LDAP entry properties.
    */
   public AeLdapEntry(Map aProperties)
   {
      mProperties = new HashMap();
      if (aProperties != null)
      {
         mProperties.putAll(aProperties);
      }
   }

   /**
    * Returns the object's distinguished name.
    * @return DN string.
    */
   public String getDn()
   {
      return getProperty("dn"); //$NON-NLS-1$
   }

   /**
    * Returns the object's common name.
    * @return CN string.
    */
   public String getCn()
   {
      return getProperty("cn"); //$NON-NLS-1$
   }

   /** 
    * @return Name of the ldap attribute used for extracting the name.
    */
   public String getNameAttribute()
   {
      return mNameAttribute;
   }

   /**
    * Sets the name of the attribute used as the entry name.
    * @param aNameAttribute name of attribute. E.g. CN.
    */
   public void setNameAttribute(String aNameAttribute)
   {
      mNameAttribute = aNameAttribute;
   }

   /**
    * Returns entry name. This method return the value of the 'name attribute' attribute.
    * @return entry name.
    */
   public String getName()
   {
      return getProperty( getNameAttribute() );
   }

   /**
    * Returns string property value.
    * @param aName
    * @return property value or <code>null</code> if not found.
    */
   public String getProperty(String aName)
   {
      String rval = null;
      aName = aName.trim();
      Object obj = getProperties().get(aName);
      if (obj != null && obj instanceof List)
      {
         rval = (String) ((List)obj).get(0);
      }
      else if (obj != null)
      {
         rval = obj.toString();
      }
      return rval;
   }

   /**
    * Returns list of properties given a multi-value property name.
    * @param aName
    * @return list of property values.
    */
   public List getPropertyList(String aName)
   {
      List rval = null;
      aName = aName.trim();
      Object obj = getProperties().get(aName);
      if (obj != null && obj instanceof List)
      {
         rval = (List)obj;
      }
      else if (obj != null)
      {
         rval = new ArrayList();
         rval.add(obj);
      }
      else
      {
         rval = Collections.EMPTY_LIST;
      }
      return rval;
   }

   /**
    * Checks to see if the given property with value exists.
    * @param aName
    * @param aValue
    * @return true if the property with value exists.
    */
   public boolean hasProperty(String aName, String aValue)
   {
      return hasProperty(aName, aValue, true);
   }

   /**
    * Checks to see if the given property with value exists.
    * @param aName
    * @param aValue
    * @param aCaseSensitive if trues, does a case-sensitive match.
    * @return true if the property with value exists.
    */
   public boolean hasProperty(String aName, String aValue, boolean aCaseSensitive)
   {
      if (AeUtil.notNullOrEmpty(aName) && AeUtil.notNullOrEmpty(aValue));
      List list = getPropertyList(aName);
      for (int i = 0; i < list.size(); i++)
      {
         String s = (String )list.get(i);
         if ( (aCaseSensitive && aValue.equals(s)) || (!aCaseSensitive && aValue.equalsIgnoreCase(s)))
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Adds the given property. If the property already exists, then the property is
    * added a list assuming it is a multi value property.
    * @param aName
    * @param aValue
    */
   public void addProperty(String aName, String aValue)
   {
      aName = aName.trim();
      if (getProperties().get(aName) == null)
      {
         // add new entry as a string value.
         getProperties().put(aName, aValue);
      }
      else
      {
         // get existing property and add as a multi-value property list.
         List valueList;
         Object obj = getProperties().get(aName);
         if (obj instanceof List)
         {
            valueList = (List)obj;
         }
         else
         {
            // current value is a string -> convert to a group.
            valueList = new ArrayList();
            getProperties().put(aName, valueList);
            valueList.add(obj);
         }
         valueList.add(aValue);
      }
   }

   /**
    * Returns an iterator to the list of property names.
    */
   public Iterator propertyNames()
   {
      Set names = new HashSet( getProperties().keySet());
      return names.iterator();
   }
   /**
    * @return the properties
    */
   protected Map getProperties()
   {
      return mProperties;
   }

   /**
    * Overrides method to return the DN.
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      return getProperty("dn"); //$NON-NLS-1$
   }

}
