//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/subprocess/IAeSpCoordinating.java,v 1.3 2006/05/24 23:16:35 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord.subprocess;

import org.activebpel.rt.bpel.coord.IAeCoordinating;

/**
 * Interface indicating a AE subprocess protocol type coordinating activity.
 * The AE subprocess protocol is loosely based on Business Agreement protocol described in
 * the BPEL-4WS 1.1, appendix C. 
 */
public interface IAeSpCoordinating extends IAeCoordinating
{
   /**
    * Indicates to use AE subprocess Participant Completion coordination protocol.
    */
   public static final String AESP_PARTICIPANT_COMPLETION_PROTOCOL = "activebpel:coord:aesp:ParticipantCompletion"; //$NON-NLS-1$
   
   /**
    * Protocol message target process id .
    */
   public static final String PROTOCOL_DESTINATION_PROCESS_ID = "activebpel:coord:aesp:TargetProcessId"; //$NON-NLS-1$

   /**
    * Protocol message target location path .
    */
   public static final String PROTOCOL_DESTINATION_LOCATION_PATH = "activebpel:coord:aesp:TargetLocationPath"; //$NON-NLS-1$   

}
