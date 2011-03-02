//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeXpathSelectNodeDateTag.java,v 1.1 2008/01/11 15:17:44 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

import java.text.Format;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import javax.servlet.jsp.JspException;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.war.AeMessages;
import org.activebpel.rt.xml.schema.AeSchemaDateTime;

/**
 * <p>Tag which allows one to get the xml node text formatted as a date.</p>
 * <pre>
 * &lt;ae:XpathSelectNodeDate name="beanName" property="contextNode"
 *         query="xpath" patternKey="resource_bundle_key_to_date_time_pattern"  /&gt;
 * </pre>
 */
public class AeXpathSelectNodeDateTag extends AeAbstractXpathNodeTextFormatterTag
{
   /**
    * Creates and returns the concrete DateFormat object.
    *
    * @param aPattern
    * @return DateFormat object.
    * @throws AeException if unable to create and return a DateFormat object.
    */
   protected Format createFormatter(String aPattern) throws AeException
   {
      try
      {
         Format formatter = null;
         if (!AeUtil.isNullOrEmpty(aPattern) )
         {
            formatter = new SimpleDateFormat(aPattern);
         }
         else
         {
            // pattern not given - use default locale
            formatter = new SimpleDateFormat();
         }
         return formatter;
      }
      catch(Exception e)
      {
         // Catch:
         // NullPointerException - if the given pattern is null
         // IllegalArgumentException - if the given pattern is invalid
         String err = MessageFormat.format(AeMessages.getString("AePropertyDateFormatterTag.1"), //$NON-NLS-1$
               new Object[] {e.getMessage()} );
         throw new AeException(err);

      }
   }

   /**
    * Overrides method to format the text as a date time.
    * @see org.activebpel.rt.war.tags.AeAbstractXpathNodeTextFormatterTag#formatText(java.lang.String)
    */
   protected String formatText(String aText) throws AeException, JspException
   {
      if (AeUtil.notNullOrEmpty( aText ))
      {
         AeSchemaDateTime dt = new AeSchemaDateTime(aText);
         // call base class to get the appropriate text formatter.
         return getResolvedFormatter().format(dt.getCalendar().getTime());
      }
      else
      {
         // if property is null, then display empty string.
         return "";  //$NON-NLS-1$
      }
   }

}
