//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeJSStringFormatterTag.java,v 1.1 2007/04/24 17:23:12 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

import java.text.Format;

import javax.servlet.jsp.JspException;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeHTMLFormatter;

/**
 * JSP Tag to convert a Java multi-line string into a JavaScript compatible composite string.
 */
public class AeJSStringFormatterTag extends AeAbstractPropertyFormatterTag
{
   /**
    * Overrides method to convert a Java multi-line string into a JavaScript
    * compatible composite string.
    *
    * @see org.activebpel.rt.war.tags.AeAbstractPropertyFormatterTag#getFormattedText()
    */
   protected String getFormattedText() throws JspException
   {
      try
      {
         String value = (String) getPropertyFromBean();
         return AeHTMLFormatter.formatJavascriptString(value);
      }
      catch (ClassCastException e)
      {
         throw new JspException(e);
      }
   }

   /**
    * Overrides method to do nothing.
    * 
    * @see org.activebpel.rt.war.tags.AeAbstractPropertyFormatterTag#createFormatter(java.lang.String)
    */
   protected Format createFormatter(String aPattern) throws AeException
   {
      return null;
   }
}
