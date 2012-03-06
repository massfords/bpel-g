//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/security/AePrincipalAuthProvider.java,v 1.2 2008/02/17 21:38:54 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.security;

import bpelg.services.processes.types.ServiceDeployment;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AePreferences;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeDeploymentProvider;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.wsio.receive.IAeMessageContext;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Authorization provider that checks if one of a subject's principals is in
 * one of the configured allowed roles. 
 */
public class AePrincipalAuthProvider implements IAeAuthorizationProvider
{
   /**
    * @see org.activebpel.rt.bpel.server.security.IAeAuthorizationProvider#authorize(javax.security.auth.Subject, org.activebpel.wsio.receive.IAeMessageContext)
    */
   public boolean authorize(Subject aSubject, IAeMessageContext aContext) throws AeSecurityException
   {
      if (AeUtil.isNullOrEmpty(aContext.getProcessName()))
      {
         throw new AeSecurityException(AeMessages.getString("AePrincipalAuthProvider.0")); //$NON-NLS-1$
      }
      
      try
      {
         // Lookup the allowed roles from the service deployment
         IAeProcessDeployment deployment = getDeploymentPlan(aContext.getProcessName());
         AePartnerLinkDef plinkDef = getPartnerLinkDef(deployment, aContext);
         ServiceDeployment service = deployment.getServiceInfo(plinkDef.getLocationPath());
         
         // roles to set
         // FIXME not very efficient, but I don't think this gets used very often
         Set<String> roles = null;
         if (AeUtil.isNullOrEmpty(service.getAllowedRoles())) {
        	 roles = Collections.emptySet();
         } else {
        	 String[] rolls = service.getAllowedRoles().split(",");
        	 roles = new HashSet<String>();
        	 for(String r : rolls) {
        		 roles.add(r.trim());
        	 }
         }
         
         return authorize(aSubject, roles);
      }
      catch (AeBusinessProcessException ae)
      {
         throw new AeSecurityException(ae.getLocalizedMessage(), ae);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.security.IAeAuthorizationProvider#authorize(javax.security.auth.Subject, java.util.Set)
    */
   public boolean authorize(Subject aSubject, Set aAllowedRoles) throws AeSecurityException
   {
      if (AeUtil.isNullOrEmpty(aAllowedRoles))
      {
         // No role restrictions configured
         return true;
      }

      // We're not enforcing roles
      if (!AePreferences.isAllowedRolesEnforced())
      {
         return true;
      }
      
      if (aSubject == null)
      {
         // No login, return false 
         return false;
      }
      
      // Check the subject for each role
      
      for (Iterator it = aAllowedRoles.iterator(); it.hasNext();)
      {
         String thisRole = (String) it.next();
         if (isSubjectInRole(aSubject, thisRole))
         {
            return true;
         }
      }
      
      // No principals matched any roles
      return false;      
   }
   
   /**
    * Checks to see if one of the subject's principals is in the target role  
    * 
    * @param aSubject
    * @param aRolename
    * @return true if match found
    */
   protected boolean isSubjectInRole(Subject aSubject, String aRolename)
   {
      for (Iterator it = aSubject.getPrincipals().iterator(); it.hasNext();)
      {
         Principal p = (Principal) it.next();
         if (p instanceof IAePrincipal)
         {
            return ((IAePrincipal) p).isUserInRole(aRolename);
         }
         else if (aRolename.equals(p.getName()))
         {
            return true;
         }
      }
      
      return false;
   }

   /**
    * Returns the partner link definition 
    * 
    * @param aPlan
    * @param aContext
    * @return the partner link definition
    */
   protected AePartnerLinkDef getPartnerLinkDef(IAeProcessPlan aPlan, IAeMessageContext aContext)
   {
      return aPlan.getProcessDef().findPartnerLink(aContext.getPartnerLink());
   }
   
   /**
    * Gets the deployment plan for this service.
    * 
    * @param aProcessName
    * @throws AeBusinessProcessException
    */
   protected IAeProcessDeployment getDeploymentPlan(QName aProcessName) throws AeBusinessProcessException
   {
      IAeProcessDeployment deploymentPlan = AeEngineFactory.getBean(IAeDeploymentProvider.class).findCurrentDeployment(aProcessName);
      return deploymentPlan;
   }
   
}
