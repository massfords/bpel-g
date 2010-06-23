// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/util/AeVariableData.java,v 1.2 2004/07/08 13:09:55 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.util;

/**
 * Helper class to package data from the bpws:getVariableData function.
 */
public class AeVariableData
{
   /** The variable name parameter of the function call */
   private String mVarName;

   /** The part name parameter of the function call (optional) */
   private String mPartName;

   /** The query expression parameter of the function call (optional) */
   private String mQueryName;
   
   /**
    * Constructor for variableData element which takes the parameter values as
    * input. 
    * @param aVarName the variable name we are requesting data for
    * @param aPartName the part name we are referencing or null
    * @param aQueryName the query expression or null
    */
   public AeVariableData(String aVarName, String aPartName, String aQueryName)
   {
      mVarName   = aVarName;
      mPartName  = aPartName;
      mQueryName = aQueryName;
   }
   
   /**
    * Returns the variable name part of the bpws:getVariableData function.
    */
   public String getVarName()
   {
      return mVarName;
   }

   /**
    * Returns the part name part of the bpws:getVariableData function. Note that
    * this value is not required to be set and may be null.
    */
   public String getPart()
   {
      return mPartName;
   }

   /**
    * Returns the part name part of the bpws:getVariableData function. Note that
    * this value is not required to be set and may be null.
    */
   public String getQuery()
   {
      return mQueryName;
   }
}
