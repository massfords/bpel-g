//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/preprocess/strategies/wsio/AeBPWSMessageDataStrategyMatcher.java,v 1.2 2006/09/11 23:06:28 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors.preprocess.strategies.wsio; 



/**
 * Contains the valid strategy patterns for BPEL4WS 1.1 
 * 
 * The only valid message data consumer or producer strategy for 1.1 is the message-variable
 * strategy. The base class provides this strategy so there's really nothing here.
 */
public class AeBPWSMessageDataStrategyMatcher extends AeBaseMessageDataStrategyMatcher
{
   /**
    * Ctor
    */
   public AeBPWSMessageDataStrategyMatcher()
   {
   }
}
 