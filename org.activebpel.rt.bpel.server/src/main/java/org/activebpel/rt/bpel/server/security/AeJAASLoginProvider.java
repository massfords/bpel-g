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

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;

/**
 * Login provider that uses a JAAS Login Context to authenticate user credentials
 */
public class AeJAASLoginProvider implements IAeLoginProvider
{
   /** Name of entry for login module entry. */
   public static final String APPLICATION_NAME_ENTRY = "ApplicationConfigName"; //$NON-NLS-1$
   /** Name of entry for login module entry. */
   public static final String LOGIN_MODULE_ENTRY = "LoginModule"; //$NON-NLS-1$
   /** Name of entry for login module entry. */
   public static final String CALLBACK_HANDLER_ENTRY = "CallbackHandler"; //$NON-NLS-1$
   
   private Map mLoginConfig;
   private Map mCallbackConfig;
   private String mAppName;
   
   /**
    * Constructor with a configuration map
    * 
    * @param aConfig
    * @throws AeSecurityException
    */
   public AeJAASLoginProvider(Map aConfig) throws AeSecurityException
   {
      if (aConfig != null)
      {
         mLoginConfig = (Map) aConfig.get(LOGIN_MODULE_ENTRY);
         mCallbackConfig = (Map) aConfig.get(CALLBACK_HANDLER_ENTRY);
         mAppName = (String) aConfig.get(APPLICATION_NAME_ENTRY);
         if (!AeUtil.isNullOrEmpty(mLoginConfig))
         {
            AeJAASConfiguration config = new AeJAASConfiguration(mLoginConfig, mAppName);
            Configuration.setConfiguration(config);
         }
      }
   }
  
   /**
    * @see org.activebpel.rt.bpel.server.security.IAeSecurityProvider#authenticate(java.lang.String, java.lang.String)
    */
   public void authenticate(String aUsername, String aPassword) throws AeSecurityException
   {
      Subject subject = new Subject();
      authenticate(aUsername, aPassword, subject);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.security.IAeLoginProvider#authenticate(java.lang.String, java.lang.String, javax.security.auth.Subject)
    */
   public void authenticate(String aUsername, String aPassword, Subject aSubject) throws AeSecurityException
   {
      try
      {
         LoginContext context = createLoginContext(aUsername, aPassword, aSubject);
         context.login();
      }
      catch(SecurityException se) 
      {
         throw new AeSecurityException(se.getLocalizedMessage(), se);         
      }
      catch(LoginException le) 
      {
         throw new AeSecurityException(le.getLocalizedMessage(), le);
      }
   }

   /**
    * @return the appName
    */
   protected String getAppName()
   {
      return mAppName;
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
   protected LoginContext createLoginContext(String aUsername, String aPassword, Subject aSubject) throws AeSecurityException
   {
      LoginContext context = null;
      if (!AeUtil.isNullOrEmpty(getAppName()))
      {
         CallbackHandler callback = createCallbackHandler(getCallbackConfig(), aUsername, aPassword);
         try
         {
            return new LoginContext(getAppName(), callback);
         }
         catch (Throwable ae)
         {
            throw new AeSecurityException(AeMessages.format("AeJAASLoginProvider.0", getAppName()), ae);  //$NON-NLS-1$
         }
      }

      return context;
   }
   
   /**
    * Creates an instance of the callback handler for the username and password.
    * 
    * @param aConfig
    * @param aUsername
    * @param aPassword
    * @return JAAS callback handler 
    * @throws AeSecurityException
    */
   protected CallbackHandler createCallbackHandler(Map aConfig, String aUsername, String aPassword) throws AeSecurityException
   {
      CallbackHandler handler = null;
      if (!AeUtil.isNullOrEmpty(aConfig))
      {
         String mgrName = (String) aConfig.get(IAeEngineConfiguration.CLASS_ENTRY);
         if (!AeUtil.isNullOrEmpty(mgrName))
         {
            try
            {
               HashMap config = new HashMap();
               config.putAll(aConfig);
               config.put(USERNAME_ENTRY, aUsername);
               config.put(PASSWORD_ENTRY, aPassword);
               return (CallbackHandler) AeEngineFactory.createConfigSpecificClass(config);
            }
            catch (Throwable ae)
            {
               throw new AeSecurityException(AeMessages.format("AeJAASLoginProvider.1", mgrName), ae);  //$NON-NLS-1$
            }
         }

      }
      return handler;
   }

   /**
    * @return the callbackConfig
    */
   protected Map getCallbackConfig()
   {
      return mCallbackConfig;
   }
   
}
