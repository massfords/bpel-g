//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/security/AeSecurityProvider.java,v 1.2 2008/02/17 21:38:50 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.security;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.wsio.receive.IAeMessageContext;

import javax.inject.Singleton;
import javax.security.auth.Subject;
import java.util.Set;

/**
 * Security provider that acesses login and authorization providers
 * configured in the engine config
 */
@Singleton
public class AeSecurityProvider implements IAeSecurityProvider {
    private IAeLoginProvider mLoginProvider;
    private IAeAuthorizationProvider mAuthProvider;

    /**
     * @see org.activebpel.rt.bpel.server.security.IAeSecurityProvider#authenticate(java.lang.String, java.lang.String)
     */
    public void authenticate(String aUsername, String aPassword) throws AeSecurityException {
        IAeLoginProvider provider = getLoginProvider();
        if (provider != null) {
            provider.authenticate(aUsername, aPassword);
        }
    }

    /**
     * @see org.activebpel.rt.bpel.server.security.IAeSecurityProvider#authenticate(java.lang.String, java.lang.String)
     */
    public void authenticate(String aUsername, String aPassword, Subject aSubject) throws AeSecurityException {
        IAeLoginProvider provider = getLoginProvider();
        if (provider != null) {
            provider.authenticate(aUsername, aPassword, aSubject);
        }
    }

    /**
     * @see org.activebpel.rt.bpel.server.security.IAeAuthorizationProvider#authorize(javax.security.auth.Subject, org.activebpel.wsio.receive.IAeMessageContext)
     */
    public boolean authorize(Subject aSubject, IAeMessageContext aContext) throws AeSecurityException {
        IAeAuthorizationProvider provider = getAuthorizationProvider();
        if (provider != null) {
            return provider.authorize(aSubject, aContext);
        } else {
            return true;
        }
    }

    /**
     * @see org.activebpel.rt.bpel.server.security.IAeAuthorizationProvider#authorize(javax.security.auth.Subject, java.util.Set)
     */
    public boolean authorize(Subject aSubject, Set aAllowedRoles) throws AeSecurityException {
        IAeAuthorizationProvider provider = getAuthorizationProvider();
        if (provider != null) {
            return provider.authorize(aSubject, aAllowedRoles);
        } else {
            return true;
        }
    }

    /**
     * @return the authentication provider
     */
    public IAeAuthorizationProvider getAuthorizationProvider() {
        return mAuthProvider;
    }

    /**
     * @param aAuthModule the authentication provider to set
     */
    public void setAuthorizationProvider(IAeAuthorizationProvider aAuthModule) {
        mAuthProvider = aAuthModule;
    }

    /**
     * @return the loginProvider
     */
    public IAeLoginProvider getLoginProvider() {
        return mLoginProvider;
    }

    /**
     * @param aLoginProvider the login provider to set
     */
    public void setLoginProvider(IAeLoginProvider aLoginProvider) {
        mLoginProvider = aLoginProvider;
    }

    /**
     * @see org.activebpel.rt.bpel.server.security.IAeSecurityProvider#login(java.lang.String, java.lang.String, org.activebpel.wsio.receive.IAeMessageContext)
     */
    public void login(String aUsername, String aPassword, IAeMessageContext aContext) throws AeSecurityException {
        Subject subject = new Subject();
        authenticate(aUsername, aPassword, subject);
        if (!authorize(subject, aContext)) {
            throw new AeSecurityException(AeMessages.format("AeSecurityProvider.0", aUsername));  //$NON-NLS-1$
        }
    }

}
