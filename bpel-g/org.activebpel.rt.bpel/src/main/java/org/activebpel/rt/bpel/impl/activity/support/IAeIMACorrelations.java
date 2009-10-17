//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/IAeIMACorrelations.java,v 1.1 2006/10/26 13:58:59 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support; 

import java.util.Map;
import java.util.Set;

import org.activebpel.rt.bpel.impl.AeCorrelationViolationException;

/**
 * Provides additional methods for correlations used for inbound message 
 * activities. Inbound message activities include <code>receive</code>, 
 * <code>onMessage</code>, and <code>onEvent</code>
 */
public interface IAeIMACorrelations extends IAeCorrelations
{
   /**
    * Creates a map of correlation properties. These properties and values are 
    * used to correlate inbound messages to the activity. 
    * @throws AeCorrelationViolationException thrown if the one or more 
    *         correlation sets were supposed to initiated but weren't
    */
   public Map getInitiatedProperties() throws AeCorrelationViolationException;
   
   /**
    * Creates a set of location paths for the correlationSets used for the 
    * activity. This is used in order to detect conflicting receives.
    */
   public Set getCSPathsForConflictingReceives();
}
 