//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/search/IAeIdentitySearch.java,v 1.7 2008/02/17 21:54:48 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.search;

import java.util.List;

import org.activebpel.rt.identity.IAeIdentity;
import org.activebpel.rt.identity.IAeIdentityRole;

/**
 * IAeIdentitySearch interfaces to an identity storage
 * layer to allow one to query for identities by name or role
 */
public interface IAeIdentitySearch
{
   /**
    * Returns list of roles given an principal name.
    * @param aPrincipalName
    * @return list of roles.
    */
   public IAeIdentityRole [] findRolesByPrincipal(String aPrincipalName) throws AeIdentitySearchException;
   

   /**
    * Returns all of the roles that the provider knows about. 
    * @throws AeIdentitySearchException
    */
   public IAeIdentityRole[] findRoles() throws AeIdentitySearchException;
   
   /**
    * Returns a list of identities who has a specified role.
    * @param aRoleName
    * @return list of identities with given role.
    */
   public IAeIdentity [] findIdentitiesByRole(String aRoleName) throws AeIdentitySearchException;
   
   /**
    * Returns a list of identities given a query with a inclusion and exclusion set.
    * @param aQuery
    * @return AeIdentityResultSet
    */
   public AeIdentityResultSet findIdentities(AeIdentityQuery aQuery) throws AeIdentitySearchException;
   
   /**
    * Asserts that the given principal is returned as part of the identity query
    * @param aPrincipalName
    * @param aQuery
    * @throws AeIdentitySearchException - thrown if the principal is not within 
    *                                     the query's result set
    */
   // fixme (MF-identity) should remove this in favor of the List version
   public void assertPrincipalInQueryResult(String aPrincipalName, AeIdentityQuery aQuery) throws AeIdentitySearchException; 

   /**
    * Asserts that the given principal is returned as part of one of the identity 
    * queries passed in.
    * @param aPrincipalName
    * @param aQueryList
    * @throws AeIdentitySearchException - thrown if the principal is not within 
    *                                     the query's result set
    */
   public void assertPrincipalInQueryResult(String aPrincipalName, List aQueryList) throws AeIdentitySearchException; 

   /**
    * Asserts that the given principal is returned as part of one of the identity
    * queries passed in. ALL of the identity queries passed in will be evaluated
    * and the response will indicate which results contained the principal.
    * @param aPrincipalName
    * @param aQueryList
    * @throws AeIdentitySearchException
    */
   public AeAssertionQueryResponse assertPrincipalInQueryResultWithResponse(String aPrincipalName, List aQueryList) throws AeIdentitySearchException; 
}
