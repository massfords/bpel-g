//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeIfUserInRoleTag.java,v 1.1 2007/04/24 17:23:13 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags; 

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Custom tag that will evaluate its contents if the user is in the role specified
 * or if there was no authentication information available. This is used to hide
 * some UI elements that aren't available to all users. It should only be used
 * in cases where there are a few elements on a page to hide. If this tag is used
 * a lot on a page then it's probably better to have a separate JSP for each role.
 */
public class AeIfUserInRoleTag extends TagSupport
{
   /** name of the role to check for */
   private String mRole;
   
   /**
    * Will evaluate the body contents if the user is in the specified role or
    * if there is no principal available.
    * 
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      if( isUserInRole() )
      {
         return EVAL_BODY_INCLUDE;
      }
      else
      {
         return SKIP_BODY;
      }
   }
   
   /**
    * Returns true if the user is in the specified role or if there is no principal
    * available
    */
   protected boolean isUserInRole()
   {
      HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
      Principal p = req.getUserPrincipal();
      if (p == null)
      {
         return true;
      }
      else
      {
         return req.isUserInRole(getRole());
      }
   }

   /**
    * @return Returns the role.
    */
   public String getRole()
   {
      return mRole;
   }
   
   /**
    * @param aRole The role to set.
    */
   public void setRole(String aRole)
   {
      mRole = aRole;
   }
}
 