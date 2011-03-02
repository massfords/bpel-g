//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/handlers/AeSimpleAuthorizationHandler.java,v 1.5 2006/05/18 20:00:46 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.handlers;

import java.text.MessageFormat;
import java.util.StringTokenizer;

import org.activebpel.rt.axis.bpel.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.security.AuthenticatedUser;
import org.apache.axis.security.SecurityProvider;
import org.apache.axis.utils.Messages;

/**
 * A slight change on Axis's authorization handler that checks to see if the
 * user is authorized to hit the service. This impl allows for unauthenticated
 * requests to pass through in cases where the target service does not have any
 * security roles declared in its "allowedRoles" handler param. We'll also allow
 * requests to pass through in the event that there is no authenticated user present.
 * If the admin for the server didn't configure security in for the axis WAR then
 * we'll allow everything through.
 */
public class AeSimpleAuthorizationHandler extends BasicHandler
{
   /**
    * @see org.apache.axis.Handler#invoke(org.apache.axis.MessageContext)
    */
   public void invoke(MessageContext msgContext) throws AxisFault
   {
      String allowedRoles = getAllowedRoles(msgContext);
      
      if (AeUtil.notNullOrEmpty(allowedRoles))
      {
         // there are roles defined for this service, we need to make sure that 
         // the caller has those roles.

         AuthenticatedUser user = (AuthenticatedUser) msgContext.getProperty(MessageContext.AUTHUSER);
         
         // if the user was null then the server was not configured w/ security so
         // we'll ignore the allowed roles unless the engine config tells us differently
         
         if (user != null)
         {
            SecurityProvider provider = getSecurityProvider(msgContext);
   
            StringTokenizer st = new StringTokenizer(allowedRoles, ","); //$NON-NLS-1$
            while (st.hasMoreTokens())
            {
               String thisRole = st.nextToken();
               if (provider.userMatches(user, thisRole))
               {
                  return;
               }
            }

            // if we get here then we've tried every role on the service and the
            // user didn't match to any of them. ergo, throw.
            throw new SecurityException(MessageFormat.format("AeSimpleAuthorizationHandler.ERROR_0", //$NON-NLS-1$
                                                             new Object[] {user.getName(), msgContext.getService().getName()})); 
         }
         else if (AeEngineFactory.getEngineConfig().isAllowedRolesEnforced())
         {
            // there was no user and the allowed roles is being enforced so we need to throw.
            throw new SecurityException(AeMessages.format("AeSimpleAuthorizationHandler.ERROR_1", msgContext.getService().getName())); //$NON-NLS-1$
         }
      }
   }

   /**
    * Gets the allowedRoles option from the service being hit.
    * 
    * @param aMsgContext
    * @throws AxisFault thrown if there is no service bound to the context (meaning something went horribly wrong in axis).
    */
   protected String getAllowedRoles(MessageContext aMsgContext) throws AxisFault
   {
      if (aMsgContext.getService() == null)
      {
         throw new AxisFault(Messages.getMessage("needService00")); //$NON-NLS-1$
      }

      return (String) aMsgContext.getService().getOption("allowedRoles"); //$NON-NLS-1$
   }

   /**
    * Gets the security provider from the context
    * 
    * @param aMsgContext
    * @throws AxisFault thrown if it isn't present (default should have been set by authentication handler)
    */
   protected SecurityProvider getSecurityProvider(MessageContext aMsgContext) throws AxisFault
   {
      SecurityProvider provider = (SecurityProvider) aMsgContext.getProperty(MessageContext.SECURITY_PROVIDER);
      if (provider == null)
         throw new AxisFault(Messages.getMessage("noSecurity00")); //$NON-NLS-1$
      return provider;
   }

}