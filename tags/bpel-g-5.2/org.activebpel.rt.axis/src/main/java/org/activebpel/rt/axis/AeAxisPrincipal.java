//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/AeAxisPrincipal.java,v 1.1 2007/02/13 15:33:23 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis;

import org.activebpel.rt.bpel.server.security.IAePrincipal;
import org.apache.axis.security.AuthenticatedUser;
import org.apache.axis.security.servlet.ServletAuthenticatedUser;

/**
 * Principal wrapper for Axis AuthenticatedUser principal
 */
public class AeAxisPrincipal implements IAePrincipal
{
   private AuthenticatedUser mUser;

   /**
    * @param aUser authenticated user from Axis
    */
   public AeAxisPrincipal(AuthenticatedUser aUser)
   {
      mUser = aUser;
   }

   /**
    * @see org.activebpel.rt.bpel.server.security.IAePrincipal#isUserInRole(java.lang.String)
    */
   public boolean isUserInRole(String aRolename)
   {
      AuthenticatedUser user = getUser();
      if (user == null)
      {
         return false;
      }
      else if (user instanceof ServletAuthenticatedUser)
      {
         // Use the servlet request to check user role
         ServletAuthenticatedUser servletUser = (ServletAuthenticatedUser) user;
         if (servletUser.getRequest() != null)
         {
            return servletUser.getRequest().isUserInRole(aRolename);
         }
         else
         {
            return false;
         }
      }
      else
      {
         // just do a simple name match
         return user.getName().equals(aRolename);
      }
   }

   public String getName()
   {
      return getUser().getName();
   }
   
   /**
    * @return the user
    */
   public AuthenticatedUser getUser()
   {
      return mUser;
   }

}
