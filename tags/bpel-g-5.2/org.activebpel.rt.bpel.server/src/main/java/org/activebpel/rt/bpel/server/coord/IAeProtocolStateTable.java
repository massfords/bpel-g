//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/IAeProtocolStateTable.java,v 1.2 2005/11/16 16:48:18 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord;

import org.activebpel.rt.bpel.coord.IAeProtocolMessage;
import org.activebpel.rt.bpel.coord.IAeProtocolState;

/**
 * Interface for a protocol state transition table.
 */
public interface IAeProtocolStateTable
{   
   /**
    * Returns the state that should be transitioned to given the received message or the message about to be sent.
    * @param aCurrentState current state
    * @param aMessage message received or about to be sent.
    * @return the next state.
    */
   public IAeProtocolState getNextState(IAeProtocolState aCurrentState, IAeProtocolMessage aMessage);
   
   /**
    * Returns the initial state.
    */
   public IAeProtocolState getInitialState();
}
