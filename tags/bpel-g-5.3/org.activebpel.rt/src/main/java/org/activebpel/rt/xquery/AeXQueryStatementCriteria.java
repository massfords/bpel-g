// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xquery/AeXQueryStatementCriteria.java,v 1.1 2008/03/19 16:35:56 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xquery;

/**
 * Simple data object that holds some XQuery criteria that was
 * generated by Java code.
 */
public class AeXQueryStatementCriteria
{
   /** The xquery statement criteria string. */
   private String mCriteriaString;
   
   /**
    * C'tor.
    * 
    * @param aCriteriaString
    */
   public AeXQueryStatementCriteria(String aCriteriaString)
   {
      setCriteriaString(aCriteriaString);
   }

   /**
    * @return Returns the criteriaString.
    */
   protected String getCriteriaString()
   {
      return mCriteriaString;
   }

   /**
    * @param aCriteriaString the criteriaString to set
    */
   protected void setCriteriaString(String aCriteriaString)
   {
      mCriteriaString = aCriteriaString;
   }
}