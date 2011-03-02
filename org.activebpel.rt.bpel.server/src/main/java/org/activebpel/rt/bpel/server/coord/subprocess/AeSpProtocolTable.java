//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/subprocess/AeSpProtocolTable.java,v 1.2 2005/11/09 21:58:33 PJayanetti Exp $
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
import org.activebpel.rt.bpel.server.coord.state.AeAbstractProtocolStateTable;
import org.activebpel.rt.bpel.server.coord.state.AeStateTable;
import org.activebpel.rt.bpel.server.coord.state.AeStateTableEntry;

/**
 * Defines a AE Subprocess protocol state table.
 * 
 */
public class AeSpProtocolTable extends AeAbstractProtocolStateTable
{

   /**
    * Default ctor.
    */
   public AeSpProtocolTable()
   {
      super();
   }

   /**
    * 
    * Overrides method to initial state as NONE. 
    * @see org.activebpel.rt.bpel.server.coord.IAeProtocolStateTable#getInitialState()
    */
   public IAeProtocolState getInitialState()
   {
      return AeSpCoordinationStates.NONE;
   }   
   
   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.coord.state.AeAbstractProtocolStateTable#createTable()
    */
   protected AeStateTable createTable()
   {
      AeStateTable table = new AeStateTable();
      AeStateTableEntry entry = null;
      
      // for state ACTIVE
      entry = new AeStateTableEntry();
      // add valid entries where the state can transition from Active to another state based on the message.
      // received messages
      entry.add(AeSpCoordinationMessages.CANCEL, AeSpCoordinationStates.CANCELING);
      // sent messages
      entry.add(AeSpCoordinationMessages.EXITED, AeSpCoordinationStates.ENDED);
      entry.add(AeSpCoordinationMessages.COMPLETED, AeSpCoordinationStates.COMPLETED);
      entry.add(AeSpCoordinationMessages.FAULTED_ACTIVE, AeSpCoordinationStates.FAULTED_ACTIVE);
      entry.add(AeSpCoordinationMessages.COMPENSATE_OR_CANCEL, AeSpCoordinationStates.COMPENSATING_OR_CANCELING);
      table.add(AeSpCoordinationStates.ACTIVE, entry);
      
      // for state CANCELING
      entry = new AeStateTableEntry();
      // sent messages
      entry.add(AeSpCoordinationMessages.CANCELED, AeSpCoordinationStates.ENDED);
      table.add(AeSpCoordinationStates.CANCELING, entry);
      
      // for state COMPLETED
      entry = new AeStateTableEntry();
      // received messages
      entry.add(AeSpCoordinationMessages.CLOSE, AeSpCoordinationStates.CLOSING);
      entry.add(AeSpCoordinationMessages.COMPENSATE, AeSpCoordinationStates.COMPENSATING);
      entry.add(AeSpCoordinationMessages.COMPENSATE_OR_CANCEL, AeSpCoordinationStates.COMPENSATING_OR_CANCELING);
      table.add(AeSpCoordinationStates.COMPLETED, entry);
      
      // for state CLOSING
      entry = new AeStateTableEntry();
      // sent messages
      entry.add(AeSpCoordinationMessages.CLOSED, AeSpCoordinationStates.ENDED);      
      table.add(AeSpCoordinationStates.CLOSING, entry);
      
      // for state COMPENSATING
      entry = new AeStateTableEntry();
      // received messages
      entry.add(AeSpCoordinationMessages.CANCEL, AeSpCoordinationStates.CANCELING);
      // sent messages
      entry.add(AeSpCoordinationMessages.COMPENSATED, AeSpCoordinationStates.ENDED);
      entry.add(AeSpCoordinationMessages.FAULTED_COMPENSATING, AeSpCoordinationStates.FAULTED_COMPENSATING);
      table.add(AeSpCoordinationStates.COMPENSATING, entry);
      
      // for state FAULTED_ACTIVE and FAULTED_COMPENSATING
      entry = new AeStateTableEntry();
      // received messages
      entry.add(AeSpCoordinationMessages.FORGET, AeSpCoordinationStates.ENDED);  
      entry.add(AeSpCoordinationMessages.COMPENSATE_OR_CANCEL, AeSpCoordinationStates.COMPENSATING_OR_CANCELING);
      table.add(AeSpCoordinationStates.FAULTED_ACTIVE, entry);
      table.add(AeSpCoordinationStates.FAULTED_COMPENSATING, entry);
      
      // for state ENDED
      entry = new AeStateTableEntry();
      entry.add(AeSpCoordinationMessages.CANCEL, AeSpCoordinationStates.ENDED);
      table.add(AeSpCoordinationStates.ENDED, entry);
      
      // for COMPENSATING_OR_CANCELING
      entry = new AeStateTableEntry();
      // basically no change
      entry.add(AeSpCoordinationMessages.COMPENSATE_OR_CANCEL, AeSpCoordinationStates.COMPENSATING_OR_CANCELING);
      entry.add(AeSpCoordinationMessages.COMPLETED, AeSpCoordinationStates.COMPENSATING_OR_CANCELING);            
      // done with comp or cancel state (and ignore errors)
      entry.add(AeSpCoordinationMessages.FAULTED_COMPENSATING, AeSpCoordinationStates.ENDED);
      entry.add(AeSpCoordinationMessages.COMPENSATED, AeSpCoordinationStates.ENDED);
      entry.add(AeSpCoordinationMessages.FAULTED_ACTIVE, AeSpCoordinationStates.ENDED);
      entry.add(AeSpCoordinationMessages.EXITED, AeSpCoordinationStates.ENDED);
      entry.add(AeSpCoordinationMessages.CANCELED, AeSpCoordinationStates.ENDED);
      entry.add(AeSpCoordinationMessages.CLOSED, AeSpCoordinationStates.ENDED);            
      table.add(AeSpCoordinationStates.COMPENSATING_OR_CANCELING, entry);
      
      return table;
   }

}
