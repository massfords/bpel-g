//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeXpathNotExistTag.java,v 1.1 2008/01/11 15:17:44 PJayanetti Exp $
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
 * <p>Tag which evaluates the body if the xpath exists</p>
 * <pre>
 * &lt;ae:XpathNotExists name="beanName" property="contextNode" query="xpath" &gt;
 *  body
 *  &lt;/ae:XpathNotExists &gt;
 * </pre>
 */
public class AeXpathNotExistTag extends AeXpathExistTag
{
   /**
    * Overrides method to return false if the path exists.
    * @see org.activebpel.rt.war.tags.AeXpathExistTag#pathExists()
    */
   protected boolean pathExists() throws JspException
   {
      return !super.pathExists();
   }
}
