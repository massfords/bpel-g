// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/IAeDeploymentContainer.java,v 1.5 2007/02/13 15:26:59 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;
import org.w3c.dom.Document;

/**
 * Top level interface for wrapping of the deployment details.
 */
public interface IAeDeploymentContainer extends IAeBpr, IAeDeploymentContext
{
   /**
    * Get the web services specific deployment/undeployment document.
    */
   public Document getWsddData();

   /**
    * Set the web services specific deployment/undeployment document.
    * @param aDocument
    */
   public void setWsddData( Document aDocument );
   
   /**
    * @return service deployment information
    */
   public IAeServiceDeploymentInfo[] getServiceDeploymentInfo();
   
   /**
    * @param aServiceInfo service deployment information
    */
   public void setServiceDeploymentInfo(IAeServiceDeploymentInfo[] aServiceInfo);

   /**
    * @param aServiceInfo service deployment information
    */
   public void addServiceDeploymentInfo(IAeServiceDeploymentInfo[] aServiceInfo);
   
   /**
    * Return any special classloaders needed for web services deployment.
    */
   public ClassLoader getWebServicesClassLoader();
   
   /**
    * @return the type of deployment (wsdd, bpel, etc)
    */
   public String getDeploymentType();
}
