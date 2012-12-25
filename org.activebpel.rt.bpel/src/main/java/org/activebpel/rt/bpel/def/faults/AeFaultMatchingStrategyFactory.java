//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/faults/AeFaultMatchingStrategyFactory.java,v 1.1 2006/09/11 23:06:27 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.faults; 

import org.activebpel.rt.bpel.def.IAeBPELConstants;

/**
 * Strategy that implements the fault matching rules for the version of BPEL we're executing/validating 
 */
public class AeFaultMatchingStrategyFactory
{
   /** strategy for fault matching for a 1.1 scope */
   private static final IAeFaultMatchingStrategy BPEL4WS_FaultMatchingStrategy = new AeBPWSFaultMatchingStrategy();

   /** strategy for fault matching for a 2.0 scope */
   private static final IAeFaultMatchingStrategy WSBPEL_FaultMatchingStrategy = new AeWSBPELFaultMatchingStrategy();

   /**
    * Returns an instance of the strategy for the given namespace
    * @param aBPELNamespace
    */
   public static IAeFaultMatchingStrategy getInstance(String aBPELNamespace)
   {
      if (aBPELNamespace.equals(IAeBPELConstants.BPWS_NAMESPACE_URI))
      {
         return BPEL4WS_FaultMatchingStrategy;
      }
      else
      {
         return WSBPEL_FaultMatchingStrategy;
      }
   }
}
 