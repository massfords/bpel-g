//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeStringPropertySetterTag.java,v 1.1 2007/04/24 17:23:13 kroe Exp $
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

import org.activebpel.rt.util.AeUtil;

/**
 * Tag that allows ones to set string values, including empty strings..
 */
public class AeStringPropertySetterTag extends AeAbstractFormPropertyTag
{
   /**
    * Sets string value on the property for the given form parameter.
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      setPropertyOnBean(AeUtil.getSafeString( getParamValue() ), String.class);
      return SKIP_BODY;
   }
}
