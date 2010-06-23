//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/IAeDeploymentSummary.java,v 1.1 2004/12/10 15:59:24 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.List;

import org.w3c.dom.Document;

/**
 * This interface defines the methods necessary to implement a deployment summary.
 */
public interface IAeDeploymentSummary
{
   /**
    * Returns a list of all deployment info objects.
    */
   public List getDeploymentInfoList();

   /**
    * Returns the total number of warnings for the BPR deployment.
    */
   public int getTotalWarnings();

   /**
    * Returns the total number of errors for the BPR deployment.
    */
   public int getTotalErrors();

   /**
    * Converts the deployment summary to an XML dom.
    */
   public Document toDocument();
}
