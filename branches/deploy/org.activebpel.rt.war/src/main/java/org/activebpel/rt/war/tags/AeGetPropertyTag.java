//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeGetPropertyTag.java,v 1.1 2007/04/24 17:23:13 kroe Exp $
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
 * Equivalent class of the jsp:getProperty bean but allows one to
 * nest property names. For example, property "address.city" is equivalent
 * to getAddress().getCity()
 */
public class AeGetPropertyTag extends AeAbstractBeanPropertyTag
{
   /**
    * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      write(String.valueOf( getPropertyFromBean() ));
      return SKIP_BODY;
   }

}
