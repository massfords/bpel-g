//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/state/AeStateTable.java,v 1.2 2005/11/16 16:48:19 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord.state;

import java.util.Hashtable;
import java.util.Map;

import org.activebpel.rt.bpel.coord.IAeProtocolState;

/**
 * Class which implements state lookup table.
 */
public class AeStateTable
{
   /**
    * Map containing table row entry for a given state.
    */
   private Map mStateMap = new Hashtable();
   
   /**
    * Default constructor.
    */
   public AeStateTable()
   {     
   }
   
   /**
    * Adds the given table entry and associates it with the state.
    * @param aState
    * @param aEntry
    */
   public void add(IAeProtocolState aState, AeStateTableEntry aEntry)
   {
      mStateMap.put(aState.getState(), aEntry);
   }
   
   /**
    * Returns the table entry for the given state.
    * @param aState
    */
   public AeStateTableEntry get(IAeProtocolState aState)
   {
     return (AeStateTableEntry) mStateMap.get(aState.getState());  
   }   

}
