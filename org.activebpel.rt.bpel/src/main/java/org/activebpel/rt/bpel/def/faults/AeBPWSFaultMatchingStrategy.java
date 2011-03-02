//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/faults/AeBPWSFaultMatchingStrategy.java,v 1.2 2006/09/22 22:20:26 TZhang Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.faults; 

/**
 * Fault matching strategy for BPEL4WS 1.1
 */
public class AeBPWSFaultMatchingStrategy extends AeBaseFaultMatchingStrategy
{
   /** The rules to execute for 1.1 fault matching. Order isn't important here since there is only 1 rule that is not a "best match". */
   private static final IAeFaultMatchingRule[] RULES = 
   {
      new AeFaultNameOnly(),
      new AeFaultNameAndData(),
      new AeVariableOnly()
   };
   
   /**
    * Initialize the rule's priority.  
    */
   static
   {
      for (int i = 0; i < RULES.length; i++)
      {
         RULES[i].setPriority(i);
      }
   }
   /**
    * @see org.activebpel.rt.bpel.def.faults.AeBaseFaultMatchingStrategy#getRules()
    */
   protected IAeFaultMatchingRule[] getRules()
   {
      return RULES;
   }
} 