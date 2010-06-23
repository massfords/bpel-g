//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/subprocess/AeSpCoordinationStates.java,v 1.1 2005/10/28 21:10:33 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord.subprocess;

import org.activebpel.rt.bpel.coord.IAeProtocolState;
import org.activebpel.rt.bpel.server.coord.AeProtocolState;

/**
 * AE subprocess protocol coordination states loosely based on Business Agreement protocol described in
 * the BPEL-4WS 1.1, appendix C. 
 */
public class AeSpCoordinationStates
{
   /** Initial state. */
   public static final IAeProtocolState NONE = new AeProtocolState("aesp:None"); //$NON-NLS-1$

   /** Active state */
   public static final IAeProtocolState ACTIVE = new AeProtocolState("aesp:Active"); //$NON-NLS-1$
   
   /** Canceling state.*/
   public static final IAeProtocolState CANCELING = new AeProtocolState("aesp:Canceling"); //$NON-NLS-1$
   
   /** Completed state.*/
   public static final IAeProtocolState COMPLETED = new AeProtocolState("aesp:Completed"); //$NON-NLS-1$
   
   /** Closing state. */
   public static final IAeProtocolState CLOSING = new AeProtocolState("aesp:Closing"); //$NON-NLS-1$
   
   /** Compensating state. */
   public static final IAeProtocolState COMPENSATING = new AeProtocolState("aesp:Compensating"); //$NON-NLS-1$
   
   ///** Faulted state. */
   //public static final IAeProtocolState FAULTED = new AeProtocolState("aesp:Faulted"); //$NON-NLS-1$
   
   /** Ended state. */
   public static final IAeProtocolState ENDED = new AeProtocolState("aesp:Ended"); //$NON-NLS-1$
   
   /** Faulted while active state. */
   public static final IAeProtocolState FAULTED_ACTIVE = new AeProtocolState("aesp:FaultedActive"); //$NON-NLS-1$
   
   /** Faulted while compensating state. */
   public static final IAeProtocolState FAULTED_COMPENSATING = new AeProtocolState("aesp:FaultedCompensating"); //$NON-NLS-1$
   
   /** Compensating or canceling state. */
   public static final IAeProtocolState COMPENSATING_OR_CANCELING = new AeProtocolState("aesp:CompensatingOrCanceling"); //$NON-NLS-1$
   
}
