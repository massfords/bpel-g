//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/state/AeAbstractProtocolStateTable.java,v 1.2 2005/11/16 16:48:19 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord.state;

import org.activebpel.rt.bpel.coord.IAeProtocolMessage;
import org.activebpel.rt.bpel.coord.IAeProtocolState;
import org.activebpel.rt.bpel.server.coord.IAeProtocolStateTable;

/**
 * Base class which implements a protocol state transition table.
 */
public abstract class AeAbstractProtocolStateTable implements IAeProtocolStateTable
{
   /**
    * state table.
    */
   private AeStateTable mStateTable = null;
   
   /**
    * Default constructor.
    */
   public AeAbstractProtocolStateTable()
   {
   }

   /**
    * Overrides method to return the next state or null if the state change is illegal.
    * @param aCurrentState current state
    * @param aMessage the protocol message that triggers the state change.
    * @return next valid state or null if invalid. 
    * @see org.activebpel.rt.bpel.server.coord.IAeProtocolStateTable#getNextState(org.activebpel.rt.bpel.coord.IAeProtocolState, org.activebpel.rt.bpel.coord.IAeProtocolMessage)
    */
   public IAeProtocolState getNextState(IAeProtocolState aCurrentState, IAeProtocolMessage aMessage)
   {
      IAeProtocolState retState = null;
      AeStateTableEntry entry = getTableEntry(aCurrentState);
      if (entry != null)
      {
         retState = entry.get(aMessage);
      }
      return retState;
   }
   
   /** 
    * Overrides method to return the initial state.
    * @return initial state (e.g. active). 
    * @see org.activebpel.rt.bpel.server.coord.IAeProtocolStateTable#getInitialState()
    */
   public abstract IAeProtocolState getInitialState();
   
   /**
    * Returns the table entry (or row) given the state.
    * @param aState
    * @return  table entry for the given state.
    */
   protected AeStateTableEntry getTableEntry(IAeProtocolState aState)
   {
      AeStateTable table = getStateTable();
      AeStateTableEntry entry = (AeStateTableEntry) table.get(aState);
      return entry;
   }
   
   /**
    * @return returns the state transition table.
    */
   protected AeStateTable getStateTable()
   {
      if (mStateTable == null)
      {
         mStateTable = createTable();
      }
      return mStateTable;
   }
      
   /**
    * Delegates to the concrete class to create a map containing TableEntry objects keyed by the
    * state.
    */
   protected abstract AeStateTable createTable();
   
}

