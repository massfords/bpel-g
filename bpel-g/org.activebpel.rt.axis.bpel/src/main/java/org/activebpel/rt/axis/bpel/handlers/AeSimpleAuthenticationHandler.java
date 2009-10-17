//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/handlers/AeSimpleAuthenticationHandler.java,v 1.3 2008/02/07 22:29:29 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.handlers;

import javax.security.auth.Subject;

import org.activebpel.rt.axis.AeAxisPrincipal;
import org.activebpel.rt.util.AeUtil;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.security.AuthenticatedUser;
import org.apache.axis.security.SecurityProvider;
import org.apache.axis.security.simple.SimpleSecurityProvider;

/**
 * A slight change on Axis's authentication handler. The default Axis handler was
 * throwing a fault if it was in the request flow and a request came in without
 * authentication information. This implementation differs in that it allows
 * unauthenticated requests to pass through. 
 * 
 * We are not doing any authentication here. Instead, it is up to the admin to
 * secure the web.xml if they want to have security enabled. This allows us to
 * run out of the box easily without any configuration. 
 */
public class AeSimpleAuthenticationHandler extends BasicHandler
{
   protected static final String AUTH_SUBJECT_PROPERTY = Subject.class.getName();

   /**
    * Pretty much the Axis's implementation except to allow
    * the passing of unauthenticated requests after clearing the user name from
    * the context.
    * 
    * @see org.apache.axis.Handler#invoke(org.apache.axis.MessageContext)
    */
   public void invoke(MessageContext aMsgContext) throws AxisFault
   {
      if (hasCredentials(aMsgContext))
      {
         authenticate(aMsgContext);
      }
   }

   /**
    * Implements the authentication routine by way of the SecurityProvider that's
    * installed on the context. 
    * 
    * @param aMsgContext
    * @return true if the user was authenticated, false otherwise.
    * @throws AxisFault thrown if the user was not authenticated
    */
   protected boolean authenticate(MessageContext aMsgContext) throws AxisFault
   {
      SecurityProvider provider = getProvider(aMsgContext);

      AuthenticatedUser authUser = provider.authenticate(aMsgContext);

      aMsgContext.setProperty(MessageContext.AUTHUSER, authUser);
      
      // Add the subject to the message context for engine authorization
      Subject subject = getSubject(aMsgContext);
      updateSubject(authUser, subject);
      aMsgContext.setProperty(AUTH_SUBJECT_PROPERTY, subject);
      
      return authUser != null;
   }

   /**
    * Returns true if the user name or password are present on the context. It's possible that	
    * the authentication scheme only requires one of these values in order to authenticate the
    * user.
    *
    * @param aMsgContext
    */
   protected boolean hasCredentials(MessageContext aMsgContext)
   {
      return AeUtil.notNullOrEmpty(aMsgContext.getUsername()) || 
            AeUtil.notNullOrEmpty(aMsgContext.getPassword());
   }

   /**
    * The security provider will have been set on the context if the inbound
    * request passed through a security layer, otherwise we'll fallback on Axis's
    * default which is file based.
    * 
    * @param aMsgContext
    */
   protected SecurityProvider getProvider(MessageContext aMsgContext)
   {
      SecurityProvider provider = (SecurityProvider) aMsgContext
            .getProperty(MessageContext.SECURITY_PROVIDER);
      if (provider == null)
      {
         provider = new SimpleSecurityProvider();
         aMsgContext.setProperty(MessageContext.SECURITY_PROVIDER, provider);
      }
      return provider;
   }
   
   /**
    * Gets the cached subject from the message context or creates a new one
    * @param aMsgContext
    */
   protected Subject getSubject(MessageContext aMsgContext)
   {
      Subject subject = (Subject) aMsgContext.getProperty(AUTH_SUBJECT_PROPERTY);
      if (subject == null)
      {
         subject = new Subject();
      }
      return subject;
   }
   
   /**
    * Adds the Axis authenticated user principal to the subject
    * 
    * @param aUser
    * @param aSubject
    */
   protected void updateSubject(AuthenticatedUser aUser, Subject aSubject)
   {
      AeAxisPrincipal principal = new AeAxisPrincipal(aUser);
      aSubject.getPrincipals().add(principal);
   }
}