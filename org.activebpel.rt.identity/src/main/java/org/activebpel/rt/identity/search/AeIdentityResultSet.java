//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/search/AeIdentityResultSet.java,v 1.3 2007/04/12 21:07:35 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.search;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.activebpel.rt.identity.IAeIdentity;

/**
 * Identity search result set.
 */
public class AeIdentityResultSet
{
   /** Reference to the query that issued the search.*/
   private AeIdentityQuery mQuery;
   /** Number of identities that matched the query. */
   private int mTotalMatched;
   /** Identity result set. */
   private Set mIdentitySet = new LinkedHashSet();
   
   /**
    * Construts an empty result set with the query that was used initiate the search.
    * @param aQuery
    */
   public AeIdentityResultSet(AeIdentityQuery aQuery)
   {      
      setQuery(aQuery);
   }

   /**
    * @return the totalMatched
    */
   public int getTotalMatched()
   {
      return mTotalMatched;
   }

   /**
    * @param aTotalMatched the totalMatched to set
    */
   public void setTotalMatched(int aTotalMatched)
   {
      mTotalMatched = aTotalMatched;
   }
   
   /**
    * @return the query
    */
   public AeIdentityQuery getQuery()
   {
      return mQuery;
   }

   /**
    * @param aQuery the query to set
    */
   protected void setQuery(AeIdentityQuery aQuery)
   {
      mQuery = aQuery;
   }
   
   /**
    * Adds an identity to the result set.
    * @param aIdentity
    */
   public void add(IAeIdentity aIdentity)
   {
      getIdentitySet().add(aIdentity);
   }
   
   /**
    * Returns size of result set.
    * @return number of identities in the result set.
    */
   public int size()
   {
      return getIdentitySet().size();
   }
      
   /**
    * @return the identities
    */
   public Collection getIdentities()
   {
      return Collections.unmodifiableCollection( getIdentitySet() );
   }
   
   /**
    * Returns true if the identity is contained within the result set
    * @param aIdentity
    */
   public boolean contains(IAeIdentity aIdentity)
   {
      return getIdentitySet().contains(aIdentity);
   }
   
   /**
    * @return the identities
    */
   protected Set getIdentitySet()
   {
      return mIdentitySet;
   }   
}
