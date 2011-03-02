// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/util/AeVariableProperty.java,v 1.3 2006/06/26 16:50:48 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.util;

import javax.xml.namespace.QName;

/**
 * Helper class to package data from the bpws:getVariableProperty function.
 */
public class AeVariableProperty
{
   /** The variable name parameter of the function call */
   private String mVarName;
   /** The variable property parameter of the function call */
   private QName mProperty;

   /**
    * Constructor for variableProperty element which takes the parameter values
    * as input.
    * @param aVarName the variable name we are requesting data for
    * @param aPropertyName the property name we wish to access
    */
   public AeVariableProperty(String aVarName, QName aPropertyName)
   {
      mVarName = aVarName;
      mProperty = aPropertyName;
   }

   /**
    * Returns the variable name part of the bpws:getVariableProperty function.
    */
   public String getVarName()
   {
      return mVarName;
   }

   /**
    * Returns the property name part of the bpws:getVariableProperty function.
    */
   public QName getProperty()
   {
      return mProperty;
   }
}

