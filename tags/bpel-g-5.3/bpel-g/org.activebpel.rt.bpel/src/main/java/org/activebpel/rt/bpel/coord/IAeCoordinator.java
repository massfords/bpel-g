//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/coord/IAeCoordinator.java,v 1.4 2008/03/28 01:39:14 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.coord;

/**
 * Defines the interface for object that acts as the coordinator.
 */
public interface IAeCoordinator extends IAeCoordinating
{
   /**
    * Registers with the process's enclosing scope. The scope or process is not completed
    * until all activities under coordination has completed and deregistered.
    * 
    * @throws AeCoordinationException
    */
   public void register() throws AeCoordinationException;
   
   /**
    * Initiates the compensation of the activity under coordination (ie - the participant)
    * by sending the participiant a COMPENSATE (or equivalent) message. This method is normally
    * invoked (indirectly) by process's (or scope's) compensation handler. For example, when the BPEL compensate
    * activity is executed.
    */
   public void compensate() throws AeCoordinationException;
   
   /**
    * Signals the activity under coordination (ie - the participant) to either compensate (if
    * it has completed) or cancel (it if is still running).
    * 
    * This method is normally send to participants in 'active' state during fault/comp handler execution.
    */
   public void compensateOrCancel() throws AeCoordinationException;   
   
   /**
    * Signals the activity under coordination (ie - the participant) to cancel /terminate (it if is still
    * running). If the participant is compensating, then the compensation handler will be terminated.
    * 
    * This method is normally send to participants in 'active' or 'compensating' state.
    */
   public void cancel() throws AeCoordinationException;     
   
}
