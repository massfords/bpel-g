// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeRequestParamTag.java,v 1.1 2007/04/24 17:23:13 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.war.AeMessages;

/**
 * Writes out the value of the named request parameter to
 * the <code>JspWriter</code>. 
 */
public class AeRequestParamTag extends TagSupport
{
   /** The key value for the request parameter. */
   protected String mParameterName;

   /**
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      String value = pageContext.getRequest().getParameter( getParameterName() );
      try
      {
         pageContext.getOut().write( AeUtil.getSafeString(value) );
         pageContext.getOut().flush();
      }
      catch (IOException e)
      {
         throw new JspException(AeMessages.getString("AeRequestParamTag.ERROR_0") + e.getMessage() ); //$NON-NLS-1$
      }      
      return SKIP_BODY;
   }
   
   /**
    * Getter for the parameter name.
    */
   protected String getParameterName()
   {
      return mParameterName;
   }
   
   /**
    * Setter for the parameter name.
    */
   public void setParameterName( String aName )
   {
      mParameterName = aName;
   }
}
