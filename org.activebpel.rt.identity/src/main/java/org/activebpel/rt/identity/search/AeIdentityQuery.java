//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/search/AeIdentityQuery.java,v 1.3 2008/02/17 21:54:48 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.search;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Defines an identity query that is comprised of include and exclude values of roles
 * and principals. The roles or principals listed under the include collection will be included in
 * the result set while the ones listed under the excluded collection will be excluded.
 */
public class AeIdentityQuery
{
   /** Optional non-zero value of the start location of the expected result set. */
   private int mStartIndex;   
   /** Optional upper limit on number of items to be retrieved.*/
   private int mMaxSize;
   /** Collection of role names to be included in the result set. */
   private Set mIncludeRolesSet = new HashSet();
   /** Collection of role names to be excluded from the result set. */
   private Set mExcludeRolesSet = new HashSet();   
   /** Collection of principals to be included in the result set. */
   private Set mIncludePrincipalsSet = new HashSet();
   /** Collection of principals to be excluded in the result set. */
   private Set mExcludePrincipalsSet = new HashSet();
   
   /**
    * Default ctor for an empty query.
    */
   public AeIdentityQuery()
   {     
   }

   /**
    * @return the maxSize
    */
   public int getMaxSize()
   {
      return mMaxSize;
   }

   /**
    * @param aMaxSize the maxSize to set
    */
   public void setMaxSize(int aMaxSize)
   {
      mMaxSize = aMaxSize;
   }

   /**
    * @return the startIndex
    */
   public int getStartIndex()
   {
      return mStartIndex;
   }

   /**
    * @param aStartIndex the startIndex to set
    */
   public void setStartIndex(int aStartIndex)
   {
      mStartIndex = aStartIndex;
   }
   
   /**
    * Add a role to the exclusion filter collection.
    * @param aRoleName
    */
   public void excludeRole(String aRoleName)
   {
      getExcludeRolesSet().add(aRoleName);
   }
   
   /**
    * Adds the roles to the exclusion filter
    * @param aRoleNames
    */
   public void excludeRoles(List aRoleNames)
   {
      getExcludeRolesSet().addAll(aRoleNames);
   }
   
   /** 
    * @return Iterator to set of role names to be excluded.
    */
   public Iterator excludeRoles()
   {
      return (new HashSet(getExcludeRolesSet()) ).iterator();
   }

   /**
    * Add a role to the inclusion filter collection.
    * @param aRoleName
    */   
   public void includeRole(String aRoleName)
   {
      getIncludeRolesSet().add(aRoleName);
   }
   
   /**
    * Adds the roles to the inclusion filter collection
    * @param aRoleNames
    */
   public void includeRoles(List aRoleNames)
   {
      getIncludeRolesSet().addAll(aRoleNames);
   }
   
   /** 
    * @return Iterator to set of role names to be included.
    */
   public Iterator includeRoles()
   {
      return (new HashSet(getIncludeRolesSet()) ).iterator();
   }   
   
   /**
    * Add a principal to the exclusion filter collection.
    * @param aPrincipalName
    */   
   public void excludePrincipal(String aPrincipalName)
   {
      getExcludePrincipalsSet().add(aPrincipalName);
   }
   
   
   /**
    * Adds principals to the exclusion filter
    * @param aPrincipalNames
    */
   public void excludePrincipals(List aPrincipalNames)
   {
      getExcludePrincipalsSet().addAll(aPrincipalNames);
   }
   
   /** 
    * @return Iterator to set of principal names to be excluded.
    */
   public Iterator excludePrincipals()
   {
      return (new HashSet(getExcludePrincipalsSet()) ).iterator();
   }
   
   /**
    * Add a principal to the include filter collection.
    * @param aPrincipalName
    */   
   public void includePrincipal(String aPrincipalName)
   {
      getIncludePrincipalsSet().add(aPrincipalName);
   }   
   
   /**
    * Adds the principals to the include filter collection
    * @param aPrincipalNames
    */
   public void includePrincipals(List aPrincipalNames)
   {
      getIncludePrincipalsSet().addAll(aPrincipalNames);
   }
   
   /** 
    * @return Iterator to set of principal names to be included.
    */
   public Iterator includePrincipals()
   {
      return (new HashSet(getIncludePrincipalsSet()) ).iterator();
   }
   
   
   /**
    * @return the excludePrincipalsSet
    */
   protected Set getExcludePrincipalsSet()
   {
      return mExcludePrincipalsSet;
   }

   /**
    * @return the excludeRolesSet
    */
   protected Set getExcludeRolesSet()
   {
      return mExcludeRolesSet;
   }

   /**
    * @return the includePrincipalsSet
    */
   protected Set getIncludePrincipalsSet()
   {
      return mIncludePrincipalsSet;
   }

   /**
    * @return the includeRolesSet
    */
   protected Set getIncludeRolesSet()
   {
      return mIncludeRolesSet;
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aObj)
   {
      // This equals is here mainly for the unit tests
      if (aObj instanceof AeIdentityQuery)
      {
         AeIdentityQuery other = (AeIdentityQuery) aObj;
         return getIncludePrincipalsSet().equals(other.getIncludePrincipalsSet()) &&
            getIncludeRolesSet().equals(other.getIncludeRolesSet()) &&
            getExcludePrincipalsSet().equals(other.getExcludePrincipalsSet()) &&
            getExcludeRolesSet().equals(other.getExcludeRolesSet());
      }
      return super.equals(aObj);
   }
}
