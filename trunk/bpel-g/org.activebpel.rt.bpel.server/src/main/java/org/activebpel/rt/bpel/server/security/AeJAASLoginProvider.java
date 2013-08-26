//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/security/AeJAASLoginProvider.java,v 1.1 2007/02/13 15:26:59 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.security;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.util.AeUtil;

/**
 * Login provider that uses a JAAS Login Context to authenticate user
 * credentials
 */
public class AeJAASLoginProvider implements IAeLoginProvider {
    private String mAppName;
    private AeJAASConfiguration mJAASConfiguration;
    private IAeCallbackHandlerFactory mCallbackHandlerFactory;

    public void init() {
        if (getJAASConfiguration() != null) {
            getJAASConfiguration().setAppName(getAppName());
            Configuration.setConfiguration(getJAASConfiguration());
        }
    }

    /**
     * @see org.activebpel.rt.bpel.server.security.IAeSecurityProvider#authenticate(java.lang.String,
     *      java.lang.String)
     */
    public void authenticate(String aUsername, String aPassword)
            throws AeSecurityException {
        Subject subject = new Subject();
        authenticate(aUsername, aPassword, subject);
    }

    /**
     * @see org.activebpel.rt.bpel.server.security.IAeLoginProvider#authenticate(java.lang.String,
     *      java.lang.String, javax.security.auth.Subject)
     */
    public void authenticate(String aUsername, String aPassword,
                             Subject aSubject) throws AeSecurityException {
        try {
            LoginContext context = createLoginContext(aUsername, aPassword,
                    aSubject);
            context.login();
        } catch (SecurityException | LoginException se) {
            throw new AeSecurityException(se.getLocalizedMessage(), se);
        }
    }

    /**
     * @return the appName
     */
    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String aAppName) {
        mAppName = aAppName;
    }

    /**
     * Creates a JAAS Login context for the configured application name
     *
     * @param aUsername
     * @param aPassword
     * @param aSubject
     * @return context
     * @throws AeSecurityException
     */
    protected LoginContext createLoginContext(String aUsername,
                                              String aPassword, Subject aSubject) throws AeSecurityException {
        LoginContext context = null;
        if (!AeUtil.isNullOrEmpty(getAppName())) {
            CallbackHandler callback = createCallbackHandler(aUsername,
                    aPassword);
            try {
                return new LoginContext(getAppName(), callback);
            } catch (Throwable ae) {
                throw new AeSecurityException(AeMessages.format(
                        "AeJAASLoginProvider.0", getAppName()), ae); //$NON-NLS-1$
            }
        }

        return context;
    }

    /**
     * Creates an instance of the callback handler for the username and
     * password.
     *
     * @param aUsername
     * @param aPassword
     * @return JAAS callback handler
     * @throws AeSecurityException
     */
    protected CallbackHandler createCallbackHandler(String aUsername,
                                                    String aPassword) throws AeSecurityException {
        return getCallbackHandlerFactory().create(aUsername, aPassword);
    }

    public AeJAASConfiguration getJAASConfiguration() {
        return mJAASConfiguration;
    }

    public void setJAASConfiguration(AeJAASConfiguration aJAASConfiguration) {
        mJAASConfiguration = aJAASConfiguration;
    }

    public IAeCallbackHandlerFactory getCallbackHandlerFactory() {
        return mCallbackHandlerFactory;
    }

    public void setCallbackHandlerFactory(
            IAeCallbackHandlerFactory aCallbackHandlerFactory) {
        mCallbackHandlerFactory = aCallbackHandlerFactory;
    }

}
