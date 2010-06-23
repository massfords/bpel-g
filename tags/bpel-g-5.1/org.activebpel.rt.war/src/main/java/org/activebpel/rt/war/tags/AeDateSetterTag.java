//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeDateSetterTag.java,v 1.2 2008/02/17 21:57:11 MFord Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags; 

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.jsp.JspException;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.war.AeMessages;

/**
 * Enables the setting of date properties on a bean from date values passed
 * in as request params. 
 */
public class AeDateSetterTag extends AePropertyDateFormatterTag
{
   /** name of the param to read from the request */
   private String mParam;
   
   /**
    * Sets the value of the parsed date from the request param on the bean.
    * 
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      try
      {         
         Date date = (Date) getDateFromParam();
         setPropertyOnBean(date, Date.class);
      }
      catch(AeException ae)
      {
         Object bean = getBean();
         if (bean instanceof IAeErrorAwareBean)
         {
            ((IAeErrorAwareBean)bean).addError(getProperty(), getParamValue(), AeMessages.getString("AeDateSetterTag.0") + getParamValue()); //$NON-NLS-1$
         }
      }
      return SKIP_BODY;
   }
   
   /**
    * Parses a <code>java.util.Date</code> from the request param identified by 
    * the param property.
    */
   protected Date getDateFromParam() throws AeException
   {
      Date d = null;
      String value = getParamValue();
      if (AeUtil.notNullOrEmpty(value))
      {
         try 
         {
            SimpleDateFormat sdf = (SimpleDateFormat) getResolvedFormatter();
            d = sdf.parse(value);
         }
         catch(ParseException parseException)
         {
            throw new AeException(parseException);
         }
         catch(AeException ae)
         { 
            throw ae;
         }
      }
      return d;
   }
   
   /**
    * Gets the param value that's converted to a date.
    */
   protected String getParamValue()
   {
      return pageContext.getRequest().getParameter(getParam());
   }

   /**
    * @return Returns the param.
    */
   public String getParam()
   {
      return mParam;
   }
   /**
    * @param aParam The param to set.
    */
   public void setParam(String aParam)
   {
      mParam = aParam;
   }
}
 