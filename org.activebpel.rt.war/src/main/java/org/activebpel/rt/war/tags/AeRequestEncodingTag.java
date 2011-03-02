// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeRequestEncodingTag.java,v 1.1 2007/04/24 17:23:12 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

import java.io.UnsupportedEncodingException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.activebpel.rt.war.AeMessages;

/**
 * Sets the character encoding for the page's request parameters. 
 */
public class AeRequestEncodingTag extends TagSupport
{
   /** The character encoding value. */
   protected String mValue;

   /**
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      try
      {
         pageContext.getRequest().setCharacterEncoding(getValue());
      }
      catch (UnsupportedEncodingException e)
      {
         throw new JspException(AeMessages.getString("AeRequestEncodingTag.0") + getValue(), e); //$NON-NLS-1$
      }

      return SKIP_BODY;
   }

   /**
    * Returns the character encoding value.
    */
   protected String getValue()
   {
      return mValue;
   }

   /**
    * Sets the character encoding value.
    */
   public void setValue(String aValue)
   {
      mValue = aValue;
   }
}
