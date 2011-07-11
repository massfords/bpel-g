//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/security/AeSimpleNamePrincipal.java,v 1.1 2007/02/13 15:26:59 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.security;

/**
 * Simple implementation of a principal that holds the name of a user, group or role
 */
public class AeSimpleNamePrincipal implements IAePrincipal
{
   private String mName;

   /**
    * Constructor with name of user or group
    * @param aName
    */
   public AeSimpleNamePrincipal(String aName)
   {
      mName = aName;
   }

   /**
    * Returns true if the name matches the rolename
    * 
    * @see org.activebpel.rt.bpel.server.security.IAePrincipal#isUserInRole(java.lang.String)
    */
   public boolean isUserInRole(String aRolename)
   {
      return getName().equals(aRolename);
   }

   /**
    * @see java.security.Principal#getName()
    */
   public String getName()
   {
      return mName;
   }

}
