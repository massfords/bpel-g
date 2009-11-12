// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AeDuplicateServiceNameValidator.java,v 1.8 2006/07/18 20:05:32 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.AeRoutingInfo;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Validates that same service name is not used twice (by two diff myRole elements)
 * within a BPR file.
 */
public class AeDuplicateServiceNameValidator implements IAePredeploymentValidator
{
   /** Error msg pattern for duplicate service name desc within the BPR. */
   private static final String DUPLICATE_SERVICE_WITHIN_BPR = AeMessages.getString("AeDuplicateServiceNameValidator.0"); //$NON-NLS-1$
   /** Error msg pattern for duplicate service name with another BPR */
   private static final String DUPLICATE_SERVICE_OTHER_BPR = AeMessages.getString("AeDuplicateServiceNameValidator.OtherBpr"); //$NON-NLS-1$
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.validate.IAePredeploymentValidator#validate(org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr, org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter)
    */
   public void validate(IAeBpr aBprFile, IAeBaseErrorReporter aReporter)
      throws AeException
   {
      Map myRoleServices = new HashMap();
      
      for( Iterator iter = aBprFile.getPddResources().iterator(); iter.hasNext(); )
      {
         String pddName = (String) iter.next();
         Document pddDom = aBprFile.getResourceAsDocument( pddName );

         NodeList myRoles = pddDom.getElementsByTagNameNS( pddDom.getDocumentElement().getNamespaceURI(), MYROLE_ELEMENT );
         for( int i = 0; i < myRoles.getLength(); i++ )
         {
            Element myRoleEl = (Element)myRoles.item(i);
            String serviceName = myRoleEl.getAttribute( SERVICE_ATTR );
            
            if( myRoleServices.containsKey(serviceName) )
            {
               AeDeploymentTuple tuple = (AeDeploymentTuple) myRoleServices.get(serviceName);
               String otherPdd = (String)tuple.getPddName();
               String[] args = { serviceName, pddName, otherPdd, aBprFile.getBprFileName() };
               aReporter.addError( DUPLICATE_SERVICE_WITHIN_BPR, args, null );
            }
            else
            {
               myRoleServices.put( serviceName, new AeDeploymentTuple(serviceName, pddName) );

               // check to see if the plan is already deployed in another bpr
               AeRoutingInfo routingInfo = null;
               try
               {
                  routingInfo = AeEngineFactory.getDeploymentProvider().getRoutingInfoByServiceName(serviceName);
                  QName conflictingProcess = routingInfo.getServiceData().getProcessQName();
                  String[] args = { serviceName, pddName, aBprFile.getBprFileName(), conflictingProcess.getNamespaceURI(), conflictingProcess.getLocalPart() };
                  aReporter.addError( DUPLICATE_SERVICE_OTHER_BPR, args, null );
               }
               catch(AeBusinessProcessException e)
               {
                  // an exception means that there is no process deployed using this service
               }
            }
         }
      }
   }
   
   /**
    * simple struct, service name, process qname, and pdd name
    */
   private static class AeDeploymentTuple
   {
      /** service name */
      private String mService;
      /** pdd name */
      private String mPddName;
      
      /**
       * ctor takes service and pdd name
       * @param aService
       * @param aPddName
       */
      public AeDeploymentTuple(String aService, String aPddName)
      {
         mPddName = aPddName;
         mService = aService;
      }

      /**
       * @return Returns the service.
       */
      public String getService()
      {
         return mService;
      }

      /**
       * @return Returns the pddName.
       */
      public String getPddName()
      {
         return mPddName;
      }
   }
}
