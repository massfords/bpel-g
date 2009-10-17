//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeIfPropertyInListTag.java,v 1.1 2007/04/24 17:23:13 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

import org.activebpel.rt.util.AeUtil;

/**
 * A tag that includes its body content only if the given property value is
 * in the given list of strings. The list of strings are separated by a comma.
 * This class does a case-insensitve test against the given list of values.
 * <br/>
 * The property value must be a string.
 */

public class AeIfPropertyInListTag extends AeIfPropertyMatchesTag
{

   /**
    * Returns true if the string representation of <code>aActualValue</code> is in a given list of
    * comma separated strings.
    * @param aActualValue property object to compared to
    * @return true if property is in the comma separated list of values.
    */
   protected boolean handleCompareValue(Object aActualValue)
   {
      boolean rVal = false;
      if (aActualValue != null) 
      {
         rVal = AeUtil.isStringInCsvList(aActualValue.toString(), getValue(), false);
      }
      return rVal;
   }
   
}
