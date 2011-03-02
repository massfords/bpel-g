//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/handlers/IAePolicyHandler.java,v 1.1 2006/05/15 21:25:51 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.handlers;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAePolicyConstants;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.deployment.wsdd.WSDDService;

/**
 * Interface for Policy driven handlers acting as pivots.  
 */
public interface IAePolicyHandler extends IAePolicyConstants
{
   /**
    * Initializes pivot handler with service and engine configuration
    * @param aService  Service deployment
    * @param aConfig   Engine configuration
    * @throws AeException
    */
   public abstract void init(WSDDService aService, EngineConfiguration aConfig) throws AeException;
}
