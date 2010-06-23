// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeIfTrueTag.java,v 1.1 2007/04/24 17:23:12 kroe Exp $
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

/**
 * A simple tag that will only include its body content
 * if the named property on the specified bean evaluates
 * to true.
 */
public class AeIfTrueTag extends AeAbstractBeanPropertyTag
{
   /**
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      if( isPropertyTrue() )
      {
         return EVAL_BODY_INCLUDE;
      }
      else
      {
         return SKIP_BODY;
      }
   }

   /**
    * Returns true if the named property of the bean
    * object evaluates to true.
    * @throws JspException
    */
   protected boolean isPropertyTrue() throws JspException
   {
      Boolean b = (Boolean) getPropertyFromBean();
      return (b != null && b.booleanValue() );
   }
}
