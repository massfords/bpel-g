//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AeStaticEndpointReferenceValidator.java,v 1.6 2008/01/11 19:40:17 dvilaverde Exp $
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

import javax.wsdl.Service;

import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource;
import org.activebpel.rt.bpel.server.deploy.pdd.AePartnerLinkDescriptor;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.IAeWSDLProvider;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;

/**
 * Emit a warning for static endpoint references with service names if
 * we can't find a matching port in the catalog.
 */
public class AeStaticEndpointReferenceValidator
{
   // WARNING constants
   private static final String COULD_NOT_FIND_PORT = AeMessages.getString("AeStaticEndpointReferenceValidator.NO_PORT_FOUND"); //$NON-NLS-1$

   /**
    * Validates a partner reference against the WSDL.
    * 
    * @param aReporter
    * @param aPartnerReference
    * @param aWsdlProvider
    * @param aProcessName
    */
   private static void validatePartnerReference(IAeBaseErrorReporter aReporter,
         IAeEndpointReference aPartnerReference, IAeWSDLProvider aWsdlProvider, String aProcessName)
   {
      if (aPartnerReference != null && aPartnerReference.getServiceName() != null)
      {
         AeBPELExtendedWSDLDef wsdlDef = AeWSDLDefHelper.getWSDLDefinitionForService( aWsdlProvider, aPartnerReference.getServiceName() );
         if( wsdlDef != null )
         {
            Service wsdlService = (Service)wsdlDef.getServices().get( aPartnerReference.getServiceName() );
            
            if( wsdlService != null && wsdlService.getPort( aPartnerReference.getServicePort() ) == null )
            {
               String[] args = { aProcessName, aPartnerReference.getServicePort(), AeUtil.qNameToString(aPartnerReference.getServiceName()) };
               aReporter.addWarning( COULD_NOT_FIND_PORT, args, null );
            }
         }
      }
   }
   
   /**
    * Emit a warning for static endpoint references with service names if
    * we can't find a matching port in the catalog.
    * @param aReporter
    * @param aDeployment
    */
   public static void validate(IAeBaseErrorReporter aReporter, IAeProcessDeployment aDeployment)
   {
      for (Iterator it = aDeployment.getProcessDef().getAllPartnerLinkDefs(); it.hasNext(); )
      {
         AePartnerLinkDef plinkDef = (AePartnerLinkDef) it.next();

         IAeEndpointReference partnerRef = aDeployment.getPartnerEndpointRef(plinkDef.getLocationPath());
         validatePartnerReference(aReporter, partnerRef,  aDeployment, aDeployment.getProcessDef().getName());
      }
   }
   
   /**
    * Emit a warning for static endpoint references with service names if
    * we can't find a matching port in the catalog.
    * 
    * @param aReporter
    * @param aProvider
    * @param aSource
    */
   public static void validate(IAeBaseErrorReporter aReporter, IAeContextWSDLProvider aProvider,
         IAeDeploymentSource aSource)
   {
      for (Iterator iter = aSource.getPartnerLinkDescriptors().iterator(); iter.hasNext(); )
      {
         AePartnerLinkDescriptor desc = (AePartnerLinkDescriptor)iter.next();
         IAeEndpointReference partnerRef = desc.getPartnerEndpointReference();
         validatePartnerReference(aReporter, partnerRef,  aProvider, aSource.getProcessDef().getName());
      }
   }
}
