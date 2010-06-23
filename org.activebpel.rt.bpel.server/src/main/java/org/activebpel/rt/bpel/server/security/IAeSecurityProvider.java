//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/security/IAeSecurityProvider.java,v 1.1 2007/02/13 15:26:59 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.security;

import org.activebpel.wsio.receive.IAeMessageContext;

/**
 * Security manager interface for Authentication and Authorization of service requests
 */
public interface IAeSecurityProvider extends IAeLoginProvider, IAeAuthorizationProvider
{
   /** Name of entry for login module. */
   public static final String LOGIN_PROVIDER_ENTRY = "LoginProvider"; //$NON-NLS-1$
   /** Name of entry for authorization module. */
   public static final String AUTHORIZATION_PROVIDER_ENTRY = "AuthorizationProvider"; //$NON-NLS-1$
   
   /**
    * Authenticates and authorizes a set of user credentials for a request described in 
    * the message context
    * 
    * @param aUsername
    * @param aPassword
    * @param aContext
    */
   public void login(String aUsername, String aPassword, IAeMessageContext aContext) throws AeSecurityException;
   
}
