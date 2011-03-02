//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeSetResourceTag.java,v 1.1 2007/04/24 17:23:13 kroe Exp $
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
 * Tag to set the resource bundle value on a bean.
 * E.g &lt;ae:SetResource key="bundle_key" name="bean_name" property="bean_property" /&gt;
 */
public class AeSetResourceTag extends AeAbstractBeanPropertyTag
{
   /** Key name. */
   private String mKey;

   /**
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      String value = getResourceString( getKey() );
      setPropertyOnBean( value, String.class);
      return SKIP_BODY;
   }
      
   /**
    * @return the key
    */
   public String getKey()
   {
      return mKey;
   }

   /**
    * @param aKey the key to set
    */
   public void setKey(String aKey)
   {
      mKey = aKey;
   }
}
