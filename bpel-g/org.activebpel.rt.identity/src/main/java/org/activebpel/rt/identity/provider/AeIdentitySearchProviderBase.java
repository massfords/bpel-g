//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/provider/AeIdentitySearchProviderBase.java,v 1.3 2008/02/17 21:54:48 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.provider;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.identity.IAeIdentity;
import org.activebpel.rt.identity.search.AeIdentityQuery;
import org.activebpel.rt.identity.search.AeIdentityResultSet;
import org.activebpel.rt.identity.search.AeIdentitySearchException;

/**
 * Base class for the <code>IAeIdentitySearchProvider</code> implementation.
 */
public abstract class AeIdentitySearchProviderBase implements IAeIdentitySearchProvider
{
   /** Config settings map.*/
   private Map mConfig;

   /**
    * ctor.
    * @param aConfig
    */
   protected AeIdentitySearchProviderBase(Map aConfig)
   {
      setConfig(aConfig);
   }

   /**
    * @return the config
    */
   protected Map getConfig()
   {
      return mConfig;
   }

   /**
    * @param aConfig the config to set
    */
   protected void setConfig(Map aConfig)
   {
      mConfig = aConfig;
   }
   
   /**
    * @see org.activebpel.rt.identity.provider.IAeIdentitySearchProvider#findIdentity(java.lang.String)
    */
   public IAeIdentity findIdentity(String aPrincipalName) throws AeIdentitySearchException
   {
      return getIdentityByPrincipal(aPrincipalName);
   }   

   /**
    * @see org.activebpel.rt.identity.provider.IAeIdentitySearchProvider#findIdentities(org.activebpel.rt.identity.search.AeIdentityQuery)
    */
   public AeIdentityResultSet findIdentities(AeIdentityQuery aQuery) throws AeIdentitySearchException
   {
      AeIdentityResultSet rval = new AeIdentityResultSet(aQuery);
      Set tempIdSet = new HashSet();
      try
      {
         // get inclusion set by role
         Set includeIdentitiesByRole = getIdentitiesByRoles( aQuery.includeRoles() );
         tempIdSet.addAll(includeIdentitiesByRole);
         // get inclusion set by principal
         Set includeIdentitiesByPrincipal = getIdentitiesByPrincipals( aQuery.includePrincipals() );
         tempIdSet.addAll(includeIdentitiesByPrincipal);
   
         // get exclusion set by role
         Set excludeIdentitiesByRole = getIdentitiesByRoles( aQuery.excludeRoles() );
         filterExclusionSet(tempIdSet, excludeIdentitiesByRole);
         Set excludeIdentitiesByPrincipal = getIdentitiesByPrincipals( aQuery.excludePrincipals() );
         filterExclusionSet(tempIdSet, excludeIdentitiesByPrincipal);
      }
      catch(AeIdentitySearchException ise)
      {
         throw ise;
      }
      catch(Throwable t)
      {
         throw new AeIdentitySearchException(t);
     }
      rval.setTotalMatched( tempIdSet.size() );
      Iterator idResults = tempIdSet.iterator();
      while ( idResults.hasNext() )
      {
         IAeIdentity identity = (IAeIdentity) idResults.next();
         rval.add(identity);
      }
      return rval;
   }
   
   /**
    * Removes entries in aExclusionSet the from aIdentitySet collection
    * @param aIdentitySet identity inclusion collection
    * @param aExclusionSet exclusion collection.
    */
   protected void filterExclusionSet(Set aIdentitySet, Set aExclusionSet)
   {
      aIdentitySet.removeAll(aExclusionSet);
   }

   /**
    * Returns set of identities for the given role.
    * @param aRoleName
    * @return set of identities.
    */
   protected abstract Set getIdentitiesByRole(String aRoleName) throws AeIdentitySearchException;

   /**
    * Returns identity given principal name.
    * @param aPrincipalName
    */
   protected abstract IAeIdentity getIdentityByPrincipal(String aPrincipalName) throws AeIdentitySearchException;

   /**
    * Returns set of identities for the given an iterator to role names.
    * @param aRoleNameIterator iterator to role names
    * @return set of identities.
    */
   protected Set getIdentitiesByRoles(Iterator aRoleNameIterator) throws AeIdentitySearchException
   {
      Set rval = new LinkedHashSet();
      while (aRoleNameIterator.hasNext())
      {
         String role = (String) aRoleNameIterator.next();
         Set idSet = getIdentitiesByRole(role);
         rval.addAll(idSet);
      }
      return rval;
   }

   /**
    * Returns set of identities for the given an iterator to principal names.
    * @param aPrincipalNameIterator iterator to principal names
    * @return set of identities.
    */
   protected Set getIdentitiesByPrincipals(Iterator aPrincipalNameIterator) throws AeIdentitySearchException
   {
      Set rval = new LinkedHashSet();
      while (aPrincipalNameIterator.hasNext())
      {
         String principal = (String) aPrincipalNameIterator.next();
         IAeIdentity identity = getIdentityByPrincipal(principal);
         if (identity != null)
         {
            rval.add(identity);
         }
      }
      return rval;
   }

}
