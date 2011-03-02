//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeIfPropertyNotMatchesTag.java,v 1.2 2007/04/27 21:53:53 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

import javax.servlet.jsp.JspException;

/**
 * A tag that includes its body content only if the 
 * given value property does not match the string value of the
 * specified property for a named bean.
 * NOTE: the return type of the bean property can be
 * any type, but the evaluation will be performed 
 * against its toString method.
 */
public class AeIfPropertyNotMatchesTag extends AeIfPropertyMatchesTag
{

   /**
    * Returns true of the string value of the actual argument does not match the tag value.
    * @param aActualValue property object to compared to
    * @return true if property does not matche the value.
    */
   protected boolean handleCompareValue(Object aActualValue) throws JspException
   {
      return !( super.handleCompareValue(aActualValue.toString()) ); 
   }   

}
