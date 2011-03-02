//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/search/AeAssertionQueryResponse.java,v 1.2 2008/02/17 21:54:48 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.search; 


/**
 * models response for an assertion query. You'll only get this if the principal
 * matched at least one of the queries.  
 */
public class AeAssertionQueryResponse
{
   /** query that matched the principal */
   private int mResult;
   
   /**
    * Ctor
    * @param aResult
    */
   public AeAssertionQueryResponse(int aResult)
   {
      setResult(aResult);
   }

   /**
    * @return the results
    */
   public int getResult()
   {
      return mResult;
   }

   /**
    * @param aResult the results to set
    */
   public void setResult(int aResult)
   {
      mResult = aResult;
   }
}
 