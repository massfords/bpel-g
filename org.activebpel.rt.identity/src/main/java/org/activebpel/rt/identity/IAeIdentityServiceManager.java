//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/IAeIdentityServiceManager.java,v 1.1 2007/04/12 21:07:35 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity;

import org.activebpel.rt.identity.search.IAeIdentitySearch;

/**
 * Interface for the identity service manager.
 */
public interface IAeIdentityServiceManager
{
   /**
    * Returns the identity search instance based on the current configuration.
    * @return IAeIdentitySearch implementation.
    */
   public IAeIdentitySearch getIdentitySearch() throws AeIdentityException;   
   
}
