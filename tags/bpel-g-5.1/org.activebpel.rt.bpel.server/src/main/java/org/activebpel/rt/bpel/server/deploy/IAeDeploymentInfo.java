//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/IAeDeploymentInfo.java,v 1.1 2004/12/10 15:59:24 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This interface defines the methods necessary to implement an object that represents the 
 * deployment result for a single PDD in a BPR.
 */
public interface IAeDeploymentInfo
{
   /**
    * Returns the name of the PDD for this deployment.
    */
   public String getPddName();

   /**
    * Returns a flag indicating if the PDD was deployed.
    */
   public boolean isDeployed();
   
   /**
    * Gets the number of errors found when deploying this PDD.
    */
   public int getNumErrors();

   /**
    * Gets the number of warnings found when deploying this PDD.
    */
   public int getNumWarnings();

   /**
    * Gets the deployment log for this PDD.
    */
   public String getLog();
   
   /**
    * Converts this deployment info object into an XML element.  Uses the supplied DOM to 
    * create the element.
    * 
    * @param aDom
    */
   public Element toElement(Document aDom);
}
