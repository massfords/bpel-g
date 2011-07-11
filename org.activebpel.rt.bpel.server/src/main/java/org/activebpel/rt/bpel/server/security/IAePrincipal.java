//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/security/IAePrincipal.java,v 1.1 2007/02/13 15:26:59 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.security;

import java.security.Principal;

/**
 * Extension of the java.security.Principal interface for principal implementations that
 * a) do not implement Principal and/or
 * b) support role checks directly
 * 
 * .NET and Axis principals fall into both categories above
 */
public interface IAePrincipal extends Principal
{
   /**
    * Checks if a user is in a given role
    * @param aRolename
    * @return true if user in role
    */
   public boolean isUserInRole(String aRolename);
}
