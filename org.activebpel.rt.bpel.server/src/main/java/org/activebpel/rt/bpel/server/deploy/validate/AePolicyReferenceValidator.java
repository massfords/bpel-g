//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AePolicyReferenceValidator.java,v 1.2 2008/02/19 15:44:04 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import java.util.Iterator;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeWSDLPolicyHelper;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentException;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource;
import org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo;
import org.activebpel.rt.bpel.server.deploy.pdd.AePartnerLinkDescriptor;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;

/**
 * Emit a warning for myRole and static endpoint references with policy references that
 * can't be resolved from WSDL.
 */
public class AePolicyReferenceValidator
{
   /**
    * Emits warnings if partner endpoints or services have policy references that can't be resolved
    * from the context WSDL
    * 
    * @param aReporter
    * @param aDeployment
    */
   public static void validate(IAeBaseErrorReporter aReporter, IAeProcessDeployment aDeployment)
   {
      for (Iterator it = aDeployment.getProcessDef().getAllPartnerLinkDefs(); it.hasNext(); )
      {
         AePartnerLinkDef plinkDef = (AePartnerLinkDef) it.next();
         IAeEndpointReference partnerRef = aDeployment.getPartnerEndpointRef(plinkDef.getLocationPath());
         if (partnerRef != null)
            validatePartnerReference(aReporter, aDeployment, partnerRef);
         IAeServiceDeploymentInfo service = aDeployment.getServiceInfo(plinkDef.getLocationPath());
         if (service != null)
            validateServiceReferences(aReporter, aDeployment, service);
      }
   }
   
   /**
    * Emits warnings if partner endpoints or services have policy references that can't be resolved 
    * from the context WSDL
    * 
    * @param aReporter
    * @param aProvider
    * @param aSource
    */
   public static void validate(IAeBaseErrorReporter aReporter, IAeContextWSDLProvider aProvider,
         IAeDeploymentSource aSource)
   {
      // check partner role endpoints 
      for (Iterator iter = aSource.getPartnerLinkDescriptors().iterator(); iter.hasNext(); )
      {
         AePartnerLinkDescriptor desc = (AePartnerLinkDescriptor)iter.next();
         IAeEndpointReference partnerRef = desc.getPartnerEndpointReference();
         if (partnerRef != null)
            validatePartnerReference(aReporter, aProvider, partnerRef);
      }
      
      // check myRole services
      try
      {
         IAeServiceDeploymentInfo[] services = aSource.getServices();
         for (int i = 0; i < services.length; i++)
         {
            validateServiceReferences(aReporter, aProvider, services[i]);            
         }
      }
      catch (AeDeploymentException ex)
      {
         AeException.logError(ex);
      }
   }

   /**
    * Validates policy references for a service deployment against the WSDL
    * 
    * @param aReporter
    * @param aPartnerReference
    * @param aWsdlProvider
    * @param aProcessName
    */
   private static void validateServiceReferences(IAeBaseErrorReporter aReporter, IAeContextWSDLProvider aWsdlProvider, IAeServiceDeploymentInfo aServiceInfo)
   {
      AeWSDLPolicyHelper.resolvePolicyReferences(aWsdlProvider, aServiceInfo.getPolicies(), aReporter);      
   }
   
   /**
    * Validates policy references for a partner endpoint against the WSDL
    * 
    * @param aReporter
    * @param aPartnerReference
    * @param aWsdlProvider
    * @param aProcessName
    */
   private static void validatePartnerReference(IAeBaseErrorReporter aReporter, IAeContextWSDLProvider aWsdlProvider, IAeEndpointReference aPartnerReference)
   {
      AeWSDLPolicyHelper.getEffectiveWSDLPolicies(aWsdlProvider, aPartnerReference, aReporter);      
   }
}
