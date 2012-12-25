//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/coord/IAeCoordinating.java,v 1.2 2005/11/16 16:22:33 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.coord;

import org.activebpel.rt.bpel.IAeFault;

/**
 * Defines an activity that is either the coordinator or the participant (coordinatable).
 */
public interface IAeCoordinating
{

   /**
    * Indicates that the process should be coordinated.
    */
   public static final String COORDINATION_CONTEXT = "activebpel:coord:CoordinationContext"; //$NON-NLS-1$

   /**
    * Coordination Id (context id)
    */
   public static final String WSCOORD_ID = "wscoord:Id"; //$NON-NLS-1$
   
   /**
    * Coordination type to use.
    */
   public static final String WSCOORD_TYPE = "wscoord:Type"; //$NON-NLS-1$
   
   /**
    * Coordinator-participant protocol.
    */
   public static final String WSCOORD_PROTOCOL = "wscoord:Protcol"; //$NON-NLS-1$
      
   /**
    * Subprocess coordination type.
    */
   public static final String AE_SUBPROCESS_COORD_TYPE  = "activebpel:coord:SubProcess"; //$NON-NLS-1$
      
   /**
    * Process id.
    */
   public static final String AE_COORD_PID    = "activebpel:coord:ProcessId"; //$NON-NLS-1$
      
   /**
    * Process's (coordinator or participant)activity location path.
    */
   public static final String AE_COORD_LOCATION_PATH = "activebpel:coord:LocationPath"; //$NON-NLS-1$

   /** Coordinator role type. */
   public static final int COORDINATOR_ROLE = 0;
   
   /** Participant role type. */
   public static final int PARTICIPANT_ROLE = 1;
   
   /**
    * Returns the coordination context.
    * @return coordination context.
    */
   public IAeCoordinationContext getCoordinationContext();
   
   /** 
    * @return Returns the current coordination .state.
    */
   public IAeProtocolState getState();
   
   /**
    * Handles a coordination protocol message.
    * @param aMessage
    * @throws AeCoordinationException
    */
   public void queueReceiveMessage(IAeProtocolMessage aMessage) throws AeCoordinationException;
      
   /**
    * Notification via the process manager when a process has completed.
    * @param aFaultObject fault, if process completed with a fault; or null otherwise.
    * @param aNormalCompletion indicates process completed normally and is eligible for compensation.
    */   
   public void onProcessComplete(IAeFault aFaultObject, boolean aNormalCompletion);
   
   /**
    * Returns the coordination id.
    */
   public String getCoordinationId();
   
   /**
    * Returns the process id of the coordinator (parent process) or the participant (sub process). 
    * @return process id.
    */
   public long getProcessId();
   
   /**
    * Returns the location path of the activity.
    * @return location path.
    */
   public String getLocationPath();
}
