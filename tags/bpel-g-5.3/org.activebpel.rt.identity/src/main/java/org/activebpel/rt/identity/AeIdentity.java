//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/AeIdentity.java,v 1.2 2007/03/26 19:30:23 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.util.AeUtil;

/**
 * Basic implementation of IAeIdentity.
 */
public class AeIdentity implements IAeIdentity
{
   /**
    * Identifier string.
    */
   private String mId;
   
   /** Name of identity */
   private String mName;
   
   /**
    * Set of properties belonging to this Identity.
    */
   private Map mPropertiesMap = new HashMap();
   
   /**
    * Set of roles.
    */
   private Set mRolesSet = new HashSet();
   
   /**
    * Ctor.
    * @param aId
    * @param aName
    */
   public AeIdentity(String aId, String aName)
   {      
      setId(aId);
      setName(aName);
   }
   
   /**
    * @see org.activebpel.rt.identity.IAeIdentity#getId()
    */
   public String getId()
   {
      return mId;
   }
   
   /**
    * Sets the id
    * @param aId
    */
   protected void setId(String aId)
   {
      mId = aId;
   }
   
   /**
    * @return the name
    */
   public String getName()
   {
      return mName;
   }

   /**
    * @param aName the name to set
    */
   protected void setName(String aName)
   {
      mName = aName;
   }

   /** 
    * @return map containing properties.
    */
   protected Map getPropertiesMap()
   {
      return mPropertiesMap;
   }
   
   /**
    * @see org.activebpel.rt.identity.IAeIdentity#getProperties()
    */
   public IAeIdentityProperty[] getProperties()
   {
      List propList = new ArrayList();
      Iterator it = getPropertiesMap().entrySet().iterator();
      while (it.hasNext())
      {
         Map.Entry entry = (Map.Entry) it.next();
         propList.add( entry.getValue() );
      }
      IAeIdentityProperty rval[] = new IAeIdentityProperty[ propList.size()];
      propList.toArray(rval);
      return rval;
   }
   
   /**
    * @see org.activebpel.rt.identity.IAeIdentity#getProperty(java.lang.String)
    */
   public IAeIdentityProperty getProperty(String aName)
   {
      if (AeUtil.notNullOrEmpty(aName))
      {
         return (IAeIdentityProperty) getPropertiesMap().get( aName.trim() );
      }
      return null;
   }
   
   /**
    * Sets the given property.
    * @param aProperty
    */
   public void setProperty(IAeIdentityProperty aProperty)
   {
      getPropertiesMap().put(aProperty.getName(), aProperty);
   }   

   /** 
    * @return Collection containing roles.
    */
   protected Set getRolesSet()
   {
      return mRolesSet;
   }
   
   /**
    * @see org.activebpel.rt.identity.IAeIdentity#getRoles()
    */
   public IAeIdentityRole[] getRoles()
   {
      IAeIdentityRole rval[] = new IAeIdentityRole[ getRolesSet().size() ];
      getRolesSet().toArray(rval);
      return rval;
   }
   
   /**
    * Adds a role to this user.
    * @param aRole
    */
   public void addRole(IAeIdentityRole aRole)
   {
      getRolesSet().add(aRole);
   }
   
   /**
    * Adds the set of roles.
    * @param aIdentityRoles
    */
   public void addRoles(Set aIdentityRoles)
   {
      getRolesSet().addAll(aIdentityRoles);
   }
   
   /**
    * @see org.activebpel.rt.identity.IAeIdentity#hasRole(String)
    */
   public boolean hasRole(String aRoleName)
   {
      Iterator it = getRolesSet().iterator();
      while (it.hasNext())
      {
         IAeIdentityRole role = (IAeIdentityRole) it.next();
         if (role.getName().equals(aRoleName))
         {
            return true;
         }
      }
      return false;
   }
   
   /** 
    * Overrides method to see if the ids are the same. 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object other)
   {
      if (other instanceof IAeIdentity)
      {
         IAeIdentity otherId = (IAeIdentity) other;
         return getId().equals( otherId.getId() );
      }
      else
      {
         return false;
      }
   }

   /** 
    * Overrides method to hash code of the id. 
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return getId().hashCode();
   }
   
   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      return getId();
   }

}
