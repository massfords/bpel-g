//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeAbstractDeploymentProvider.java,v 1.1 2007/09/02 17:17:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy; 

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.server.IAeDeploymentProvider;

/**
 * Base class for deployment providers
 */
public abstract class AeAbstractDeploymentProvider implements IAeDeploymentProvider
{
   /**
    * @see org.activebpel.rt.bpel.server.IAeDeploymentProvider#findService(java.lang.String, javax.xml.namespace.QName, java.lang.String)
    */
   public AeRoutingInfo findService(String aService, QName aPortType, String aOperation) throws AeServiceNotFoundException, AeOperationNotImplementedException
   {
      AeRoutingInfo info = null;
      try
      {
         info = getRoutingInfoByServiceName(aService);
      }
      catch (AeBusinessProcessException e)
      {
         throw new AeServiceNotFoundException(aService, e);
      }

      if (!info.isImplemented(aPortType, aOperation))
      {
         throw new AeOperationNotImplementedException(aService, aPortType, aOperation);
      }
      return info;
   }

}
 