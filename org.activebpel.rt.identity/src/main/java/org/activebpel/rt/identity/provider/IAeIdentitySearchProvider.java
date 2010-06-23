//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/provider/IAeIdentitySearchProvider.java,v 1.3 2007/05/01 18:57:28 TZhang Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.provider;

import org.activebpel.rt.AeException;
import org.activebpel.rt.identity.IAeIdentity;
import org.activebpel.rt.identity.IAeIdentityRole;
import org.activebpel.rt.identity.search.AeIdentityQuery;
import org.activebpel.rt.identity.search.AeIdentityResultSet;
import org.activebpel.rt.identity.search.AeIdentitySearchException;

/**
 * Defines the interface for an identity provider store
 */
public interface IAeIdentitySearchProvider
{     
   /**
    * Initialized the provider.
    * @throws AeException
    */
   public void initialize() throws AeException;
   
   /**
    * Finds the identity given user principal. 
    * @param aPrincipalName
    * @return Identity or <code>null</code> if not found.
    */
   public IAeIdentity findIdentity(String aPrincipalName) throws AeIdentitySearchException;
      
   /**
    * Issues a query and returns a resultset with matching identities.
    * @param aQuery
    * @return query resultset.
    */
   public AeIdentityResultSet findIdentities(AeIdentityQuery aQuery) throws AeIdentitySearchException;
   
   /**
    * Finds all of the roles that the provider knows about. 
    * @return all identity roles.
    */
   public IAeIdentityRole[] findRoles() throws AeIdentitySearchException;

}
