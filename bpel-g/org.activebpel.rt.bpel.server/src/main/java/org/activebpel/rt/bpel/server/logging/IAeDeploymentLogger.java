//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/logging/IAeDeploymentLogger.java,v 1.3 2004/12/10 15:59:24 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.logging;

import bpelg.services.deploy.types.DeploymentResponse;
import bpelg.services.deploy.types.Msg;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;

import java.util.Collection;

/**
 * Interface for reporting errors, warnings, and progress information during
 * deployments. An instance will be used for the deployment of a single BPR file
 * which may contain multiple .pdd files.
 */
public interface IAeDeploymentLogger extends IAeBaseErrorReporter {
    /**
     * Sets the name of the container that we're deploying. This is typically the
     * name of the BPR file that was uploaded.
     *
     * @param containerName
     */
    public void setContainerName(String containerName);

    /**
     * Sets the name of the pdd currently being deployed.  This method is called each time the engine
     * begins deploying a new deployment unit (PDD).
     *
     * @param pddName
     */
    public void setPddName(String pddName);

    /**
     * This method is called when the processing of a PDD has finished (either successfully or
     * not).
     *
     * @param success true if the PDD was actually deployed, false if it was not (for whatever reason)
     */
    public void processDeploymentFinished(boolean success);

    public Collection<DeploymentResponse.DeploymentInfo> getDeploymentInfos();

    public void addContainerMessage(Msg msg);

    public Collection<Msg> getContainerMessages();
}
