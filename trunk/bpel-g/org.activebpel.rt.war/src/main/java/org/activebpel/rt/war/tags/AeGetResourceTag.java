// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeGetResourceTag.java,v 1.1 2007/04/24 17:23:13 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

import javax.servlet.jsp.JspException;

import org.activebpel.rt.util.AeUtil;

/**
 * <p>
 * This tag will retrieve a value from the configured resource bundle.
 * </p><p>
 * Normal usage is to get the resource given the bundle key name.
 * E.g: &lt;ae:GetResource name="key_name" /&gt;
 * </p><p>
 * Another option is to specify the bundle key name via the request attribute.
 * E.g.  &lt;ae:GetResource attributeName="request_att_name" /&gt;. In this case
 * the bundle key name is the value specified by the http servlet request attribute
 * "request_att_name".
 * </p><p>
 * The final option is to get the bundle key name from a bean property.
 * E.g.  &lt;ae:GetResource name="bean_name" property="bean_property" /&gt;.
 * </p>
 */
public class AeGetResourceTag extends AeAbstractBeanPropertyTag
{
   /** Indirect method to access the key name via request attribute. */
   protected String mAttributeName;

   /**
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      String key = null;
      // first check to see if the request attribute name is specified
      if (AeUtil.notNullOrEmpty(getAttributeName()))
      {
         key = (String)pageContext.getRequest().getAttribute( getAttributeName());
      }
      else if (AeUtil.notNullOrEmpty(getName()) && AeUtil.notNullOrEmpty(getProperty()))
      {
         // check if key name is from bean property
         key = String.valueOf( getPropertyFromBean() );
      }
      else
      {
         // default case.
         key = getName();
      }
      String value = getResourceString( AeUtil.getSafeString(key));
      write(AeUtil.getSafeString(value));
      return SKIP_BODY;
   }

   /**
    * @return the attributeName
    */
   public String getAttributeName()
   {
      return mAttributeName;
   }

   /**
    * @param aAttributeName the attributeName to set
    */
   public void setAttributeName(String aAttributeName)
   {
      mAttributeName = aAttributeName;
   }


}
