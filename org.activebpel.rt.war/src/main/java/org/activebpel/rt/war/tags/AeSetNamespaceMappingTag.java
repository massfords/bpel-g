//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeSetNamespaceMappingTag.java,v 1.1 2008/01/11 15:17:44 PJayanetti Exp $
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
 * Tag which allows one to set a namespace prefix to uri mapping on the request context.
 * &lt;ae:SetNamespaceMapping prefix="" uri="" /&gt;
 */
public class AeSetNamespaceMappingTag extends AeAbstractXpathTag
{
   /** NS prefix. */
   private String mPrefix;
   /** NS uri */
   private String mUri;
   
   /** 
    * Overrides method to set the prefix to uri mapping in the request context so that it can 
    * be accessed by other beans. 
    * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      if (AeUtil.notNullOrEmpty( getPrefix()) && AeUtil.notNullOrEmpty( getUri() ))
      {
         getNamespaceMap().put(getPrefix().trim(), getUri().trim() );
      }
      return SKIP_BODY;
   }
   
   /** 
    * @return Returns NS prefix.
    */
   public String getPrefix()
   {
      return mPrefix;
   }
   
   /**
    * Sets the NS prefix.
    * @param aPrefix prefix
    */
   public void setPrefix(String aPrefix)
   {
      mPrefix = aPrefix;
   }
   
   /** 
    * @return Returns NS URI.
    */
   public String getUri()
   {
      return mUri;
   }
   
   /**
    * Sets the NS uri.
    * @param aUri
    */
   public void setUri(String aUri)
   {
      mUri = aUri;
   }
}
