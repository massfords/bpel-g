//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeCheckboxPropertySetterTag.java,v 1.1 2007/04/24 17:23:12 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

import javax.servlet.jsp.JspException;

/**
 * Tag that allows ones to set the boolean value of a html form checkbox.
 */
public class AeCheckboxPropertySetterTag extends AeAbstractFormPropertyTag
{

   /**
    * Sets boolean value on the property for the given check box form parameter.
    * A value of boolean true is used if the form data parameter exists,
    * otherwise, false value is assign.
    * 
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      setPropertyOnBean( new Boolean(getParamValue() != null), boolean.class);
      return SKIP_BODY;
   }
   
}
