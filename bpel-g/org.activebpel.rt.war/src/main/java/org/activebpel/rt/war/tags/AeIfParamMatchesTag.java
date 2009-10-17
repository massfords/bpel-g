// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeIfParamMatchesTag.java,v 1.1 2007/04/24 17:23:12 kroe Exp $
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
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * A tag that will include its body content if
 * the value of the named http servlet request parameter
 * matches the expected value.
 */
public class AeIfParamMatchesTag extends BodyTagSupport
{
   /** The key for the http request parameter */
   protected String mProperty;
   /** The value to match against. */
   protected String mValue;

   /**
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      if( shouldEvaluateBody() )
      {
         return EVAL_BODY_INCLUDE;
      }
      return SKIP_BODY;
   }
   
   /**
    * Returns true if the request parameter matches the 
    * expected value.
    */
   protected boolean shouldEvaluateBody()
   {
      String paramValue = pageContext.getRequest().getParameter( getProperty() );
      return getValue().equals( paramValue );
   }
   
   /**
    * Accessor for the request parameter key.
    */
   public String getProperty()
   {
      return mProperty;
   }

   /**
    * Accessor for the "expected" value to compare against.
    */
   public String getValue()
   {
      return mValue;
   }

   /**
    * Setter for the request parameter key value.
    * @param aString
    */
   public void setProperty(String aString)
   {
      mProperty = aString;
   }

   /**
    * Setter for the "expected" value to compare against.
    * @param aString
    */
   public void setValue(String aString)
   {
      mValue = aString;
   }

   /**
    * @see javax.servlet.jsp.tagext.Tag#release()
    */
   public void release()
   {
      super.release();
      mProperty = null;
      mValue = null;
   }
}
