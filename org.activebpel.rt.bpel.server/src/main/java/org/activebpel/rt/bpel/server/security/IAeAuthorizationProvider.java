//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/security/IAeAuthorizationProvider.java,v 1.1 2007/02/13 15:26:59 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.security;

import java.util.Set;

import javax.security.auth.Subject;

import org.activebpel.wsio.receive.IAeMessageContext;

/**
 * Interface for pluggable authorization providers
 */
public interface IAeAuthorizationProvider
{
      
   /**
    * Determines if a subject is authorized to invoke the service 
    * described by the message context
    * 
    * @param aSubject Authenticated Subject
    * @param aContext Message Context for request
    * @return true if authorized
    * @throws AeSecurityException unable to authorize request
    */
   public boolean authorize(Subject aSubject, IAeMessageContext aContext ) throws AeSecurityException;

   /**
    * Determines if a subject is authorized to perform an action on a resource by checking
    * if the principals are in the comma-separated list of allowed roles 
    * 
    * @param aSubject Authenticated Subject
    * @param aAllowedRoles Set of allowed role strings
    * 
    * @return true if authorized
    * @throws AeSecurityException unable to authorize request
    */
   public boolean authorize(Subject aSubject, Set aAllowedRoles ) throws AeSecurityException;
}
