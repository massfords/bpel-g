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

import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.wsio.receive.IAeMessageContext;

/**
 * Security provider that acesses login and authorization providers
 * configured in the engine config
 */
public class AeSecurityProvider implements IAeSecurityProvider
{
   private Map mConfig;
   private IAeLoginProvider mLoginProvider;
   private IAeAuthorizationProvider mAuthProvider;
   
   /**
    * Constructor that takes a map of configuration settings
    * 
    * @param aConfig
    * @throws AeSecurityException
    */
   public AeSecurityProvider(Map aConfig) throws AeSecurityException
   {
      mConfig = aConfig;
      if (aConfig != null)
      {
         Map loginConfig = (Map) aConfig.get(LOGIN_PROVIDER_ENTRY);
         mLoginProvider = createLoginProvider(loginConfig);
         Map authConfig = (Map) aConfig.get(AUTHORIZATION_PROVIDER_ENTRY);
         mAuthProvider = createAuthorizationProvider(authConfig);
      }
   }
  
   /**
    * @see org.activebpel.rt.bpel.server.security.IAeSecurityProvider#authenticate(java.lang.String, java.lang.String)
    */
   public void authenticate(String aUsername, String aPassword) throws AeSecurityException
   {
      IAeLoginProvider provider = getLoginProvider();
      if (provider != null)
      {
         provider.authenticate(aUsername, aPassword);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.security.IAeSecurityProvider#authenticate(java.lang.String, java.lang.String)
    */
   public void authenticate(String aUsername, String aPassword, Subject aSubject) throws AeSecurityException
   {
      IAeLoginProvider provider = getLoginProvider();
      if (provider != null)
      {
         provider.authenticate(aUsername, aPassword, aSubject);
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.security.IAeAuthorizationProvider#authorize(javax.security.auth.Subject, org.activebpel.wsio.receive.IAeMessageContext)
    */
   public boolean authorize(Subject aSubject, IAeMessageContext aContext) throws AeSecurityException
   {
      IAeAuthorizationProvider provider = getAuthorizationProvider();
      if (provider != null)
      {
         return provider.authorize(aSubject, aContext);
      }
      else
      {
         return true;
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.security.IAeAuthorizationProvider#authorize(javax.security.auth.Subject, java.util.Set)
    */
   public boolean authorize(Subject aSubject, Set aAllowedRoles) throws AeSecurityException
   {
      IAeAuthorizationProvider provider = getAuthorizationProvider();
      if (provider != null)
      {
         return provider.authorize(aSubject, aAllowedRoles);
      }
      else
      {
         return true;
      }
   }
   
   /**
    * Creates a login provider from the configuration
    * 
    * @param aConfig
    * @return login provider
    * @throws AeSecurityException
    */
   protected IAeLoginProvider createLoginProvider(Map aConfig) throws AeSecurityException
   {
      IAeLoginProvider provider = null;
      if (!AeUtil.isNullOrEmpty(aConfig))
      {
         try
         {
            provider = (IAeLoginProvider) AeEngineFactory.createConfigSpecificClass(aConfig);
         }
         catch (AeException ae)
         {
            throw new AeSecurityException(ae.getLocalizedMessage(), ae);
         }
      }
      return provider;
   }

   /**
    * Creates a auth provider from the configuration
    * 
    * @param aConfig
    * @return authorization provider
    * @throws AeSecurityException
    */
   protected IAeAuthorizationProvider createAuthorizationProvider(Map aConfig) throws AeSecurityException
   {
      IAeAuthorizationProvider provider = null;
      if (!AeUtil.isNullOrEmpty(aConfig))
      {
         try
         {
            provider = (IAeAuthorizationProvider) AeEngineFactory.createConfigSpecificClass(aConfig);
         }
         catch (AeException ae)
         {
            throw new AeSecurityException(ae.getLocalizedMessage(), ae);
         }
      }
      return provider;
   }
   
   /**
    * @return the authentication provider
    */
   protected IAeAuthorizationProvider getAuthorizationProvider()
   {
      return mAuthProvider;
   }

   /**
    * @param aAuthModule the authentication provider to set
    */
   protected void setAuthorizationProvider(IAeAuthorizationProvider aAuthModule)
   {
      mAuthProvider = aAuthModule;
   }

   /**
    * @return the loginProvider
    */
   protected IAeLoginProvider getLoginProvider()
   {
      return mLoginProvider;
   }

   /**
    * @param aLoginProvider the login provider to set
    */
   protected void setLoginProvider(IAeLoginProvider aLoginProvider)
   {
      mLoginProvider = aLoginProvider;
   }

   /**
    * @return the config
    */
   protected Map getConfig()
   {
      return mConfig;
   }

   /**
    * @see org.activebpel.rt.bpel.server.security.IAeSecurityProvider#login(java.lang.String, java.lang.String, org.activebpel.wsio.receive.IAeMessageContext)
    */
   public void login(String aUsername, String aPassword, IAeMessageContext aContext) throws AeSecurityException
   {
      Subject subject = new Subject(); 
      authenticate(aUsername, aPassword, subject);
      if (!authorize(subject, aContext))
      {
         throw new AeSecurityException(AeMessages.format("AeSecurityProvider.0", aUsername));  //$NON-NLS-1$
      }
   }

}
