//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/AeIdentityRole.java,v 1.1 2007/02/20 17:47:40 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity;

/**
 * Basic implementation of a role.
 */
public class AeIdentityRole implements IAeIdentityRole
{
   /** role id. */
   private String mId;
   /**
    * Role name.
    */
   private String mName;
   
   /**
    * Ctor for AeIdentityRole where id and the name are the same.
    * @param aId name or id of the role.
    */
   public AeIdentityRole(String aId)
   {
      this(aId, aId);
   }
   
   /**
    * Ctor.
    * @param aId
    * @param aName
    */
   public AeIdentityRole(String aId, String aName)
   {
      setId(aId);
      setName(aName);
   }
   
   /**
    * @return the id
    */
   public String getId()
   {
      return mId;
   }

   /**
    * @param aId the id to set
    */
   protected void setId(String aId)
   {
      mId = aId;
   }

   /** 
    * @see org.activebpel.rt.identity.IAeIdentityRole#getName()
    */
   public String getName()
   {
      return mName;
   }

   /**
    * Sets the name of the role.
    * @param aName
    */
   protected void setName(String aName)
   {
      mName = aName;
   }
   
   /** 
    * Overrides method to see if the role ids are the same. 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object other)
   {
      if (other instanceof IAeIdentityRole)
      {
         IAeIdentityRole otherRole = (IAeIdentityRole) other;
         return getId().equals( otherRole.getId() );
      }
      else
      {
         return false;
      }
   }

   /** 
    * Overrides method to hash code of the role name. 
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
