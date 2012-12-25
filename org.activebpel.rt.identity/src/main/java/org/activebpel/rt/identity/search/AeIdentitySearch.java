//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/search/AeIdentitySearch.java,v 1.9 2008/02/17 21:54:48 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.search;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.identity.AeIdentity;
import org.activebpel.rt.identity.AeMessages;
import org.activebpel.rt.identity.IAeIdentity;
import org.activebpel.rt.identity.IAeIdentityRole;
import org.activebpel.rt.identity.provider.IAeIdentitySearchProvider;

/**
 * Basic implementatin of IAeIdentitySearch. 
 */
public class AeIdentitySearch implements IAeIdentitySearch
{
   /** Search content provider.*/
   private IAeIdentitySearchProvider mProvider;
   
   /**
    * Default ctor.
    * @param aConfig
    * @throws AeException
    */
   public AeIdentitySearch(Map aConfig, IAeIdentitySearchProvider aProvider) throws AeException
   {
      setProvider(aProvider);
   }
      
   /**
    * @return the provider
    */
   protected IAeIdentitySearchProvider getProvider()
   {
      return mProvider;
   }

   /**
    * @param aProvider the provider to set
    */
   protected void setProvider(IAeIdentitySearchProvider aProvider)
   {
      mProvider = aProvider;
   }

   /**
    * @see org.activebpel.rt.identity.search.IAeIdentitySearch#findIdentities(org.activebpel.rt.identity.search.AeIdentityQuery)
    */
   public AeIdentityResultSet findIdentities(AeIdentityQuery aQuery) throws AeIdentitySearchException
   {
      return getProvider().findIdentities(aQuery);
   }

   /**
    * @see org.activebpel.rt.identity.search.IAeIdentitySearch#findIdentitiesByRole(java.lang.String)
    */
   public IAeIdentity[] findIdentitiesByRole(String aRoleName) throws AeIdentitySearchException
   {
      AeIdentityQuery query = new AeIdentityQuery();
      query.includeRole(aRoleName);
      AeIdentityResultSet resultSet = findIdentities(query);
      AeIdentity rval[] = new AeIdentity[resultSet.size()];
      resultSet.getIdentities().toArray(rval);
      return rval;
   }

   /**
    * @see org.activebpel.rt.identity.search.IAeIdentitySearch#findRolesByPrincipal(java.lang.String)
    */
   public IAeIdentityRole[] findRolesByPrincipal(String aPrincipalName) throws AeIdentitySearchException
   {
      IAeIdentity identity = findIdentity(aPrincipalName);
      if (identity != null)
      {
         return identity.getRoles();
      }
      else
      {
         IAeIdentityRole[] roles = new IAeIdentityRole[0];
         return roles;
      }     
   }
   
   /**
    * Returns all of the roles that the provider knows about. 
    * @see org.activebpel.rt.identity.search.IAeIdentitySearch#findRoles()
    */
   public IAeIdentityRole[] findRoles() throws AeIdentitySearchException
   {
      IAeIdentityRole[] roles = getProvider().findRoles();
      return roles;
   }
   
   /**
    * Find the identity for the given principal
    * @param aPrincipalName
    * @throws AeIdentitySearchException
    */
   protected IAeIdentity findIdentity(String aPrincipalName) throws AeIdentitySearchException
   {
      IAeIdentity identity = getProvider().findIdentity(aPrincipalName);
      return identity;
   }

   /**
    * @see org.activebpel.rt.identity.search.IAeIdentitySearch#assertPrincipalInQueryResult(java.lang.String, org.activebpel.rt.identity.search.AeIdentityQuery)
    */
   public void assertPrincipalInQueryResult(String aPrincipalName, AeIdentityQuery aQuery) throws AeIdentitySearchException
   {
      assertPrincipalInQueryResult(aPrincipalName, Collections.singletonList(aQuery));
   }

   /**
    * @see org.activebpel.rt.identity.search.IAeIdentitySearch#assertPrincipalInQueryResult(java.lang.String, java.util.List)
    */
   public void assertPrincipalInQueryResult(String aPrincipalName, List aList) throws AeIdentitySearchException
   {
      runQueries(aPrincipalName, aList);
   }

   /**
    * @see org.activebpel.rt.identity.search.IAeIdentitySearch#assertPrincipalInQueryResultWithResponse(java.lang.String, java.util.List)
    */
   public AeAssertionQueryResponse assertPrincipalInQueryResultWithResponse(
         String aPrincipalName, List aQueryList)
         throws AeIdentitySearchException
   {
      int result = runQueries(aPrincipalName, aQueryList);
      return new AeAssertionQueryResponse(result);
   }
   
   /**
    * Runs the queries in the list, stopping after the first one that matches.
    * 
    * @param aPrincipalName
    * @param aList
    * @throws AeIdentitySearchException
    */
   protected int runQueries(String aPrincipalName, List aList) throws AeIdentitySearchException
   {
      IAeIdentity principalIdentity = findIdentity(aPrincipalName);
      
      // notice that the result starts with 1 since it's the XPath way.
      int result=1;

      // fixme (PJ) what about paging? There's probably a more efficient way of doing this. Revisit.
      for (Iterator it = aList.iterator(); it.hasNext(); result++)
      {
         AeIdentityQuery query = (AeIdentityQuery) it.next();
         AeIdentityResultSet resultSet = findIdentities(query);
         if (resultSet.contains(principalIdentity))
         {
            return result;
         }
      }
      
      // if we get down here, then we didn't find a match.
      throw new AeIdentitySearchException(AeIdentitySearchException.NOT_FOUND_EXCEPTION, AeMessages.format("AeIdentitySearch.ASSERT_PRINCIPAL_IN_QUERY_FAILURE", aPrincipalName)); //$NON-NLS-1$
   }
}
