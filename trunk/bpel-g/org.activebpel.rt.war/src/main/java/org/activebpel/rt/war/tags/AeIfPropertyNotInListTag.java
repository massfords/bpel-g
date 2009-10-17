//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeIfPropertyNotInListTag.java,v 1.1 2007/04/24 17:23:13 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

/**
 * A tag that includes its body content only if the given property value is
 * <strong>not</strong> in the given list of strings. The list of strings are separated by a comma.
 * This class does a case-insensitve test against the given list of values.
 * <br/>
 * The property value must be a string.
 */

public class AeIfPropertyNotInListTag extends AeIfPropertyInListTag
{

   /**
    * Returns true if the string representation of <code>aActualValue</code> is 
    * <strong>not</strong> in a given list of comma separated strings.
    * @param aActualValue property object to compared to
    * @return true if property is  not in the comma separated list of values.
    */   
   protected boolean handleCompareValue(Object aActualValue)
   {
      return !( super.handleCompareValue(aActualValue.toString()) ); 
   }   
   

}
