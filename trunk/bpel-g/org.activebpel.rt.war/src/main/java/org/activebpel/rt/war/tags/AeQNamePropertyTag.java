//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeQNamePropertyTag.java,v 1.1 2007/04/24 17:23:13 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags; 

import javax.servlet.jsp.JspException;
import javax.xml.namespace.QName;

/**
 * Extracts the local name or the namespace uri from a property that is a qname.
 */
public class AeQNamePropertyTag extends AeAbstractBeanPropertyTag
{
   /** part of the qname that we're reading, either "uri" or "local" */
   protected String mPart;
   
   /**
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      QName qname = (QName) getPropertyFromBean();
      if (qname != null)
      {
         if ("local".equalsIgnoreCase(getPart())) //$NON-NLS-1$
         {
            write(qname.getLocalPart());
         }
         else
         {
            write(qname.getNamespaceURI());
         }
            }
      return SKIP_BODY;
   }
   
   /**
    * @return Returns the part.
    */
   public String getPart()
   {
      return mPart;
   }
   /**
    * @param aPart The part to set.
    */
   public void setPart(String aPart)
   {
      mPart = aPart;
   }
}
 
