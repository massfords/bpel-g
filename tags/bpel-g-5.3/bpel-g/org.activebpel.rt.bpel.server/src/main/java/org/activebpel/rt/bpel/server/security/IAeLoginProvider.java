//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/security/IAeLoginProvider.java,v 1.1 2007/02/13 15:26:59 kpease Exp $
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

/**
 * Interface for pluggable login providers
 */
public interface IAeLoginProvider
{
   
   public static final String USERNAME_ENTRY = "Username"; //$NON-NLS-1$
   public static final String PASSWORD_ENTRY = "Password"; //$NON-NLS-1$
   
   /**
    * Authenticates a user with a set of username/password credentials
    * 
    * @param aUsername
    * @param aPassword
    * @throws AeSecurityException if user not authenticated
    */
   public void authenticate(String aUsername, String aPassword) throws AeSecurityException;

   /**
    * Authenticates a user with a set of username/password credentials, updating the subject
    * given in the parameters.
    * 
    * @param aUsername
    * @param aPassword
    * @param aSubject
    * @throws AeSecurityException if user not authenticated
    */
   public void authenticate(String aUsername, String aPassword, Subject aSubject) throws AeSecurityException;
   
}
