//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/state/AeStateTableEntry.java,v 1.2 2005/11/16 16:48:19 EWittmann Exp $
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

import org.activebpel.rt.bpel.coord.IAeProtocolMessage;
import org.activebpel.rt.bpel.coord.IAeProtocolState;

/**
 * This class contains a map associating various coordination signals (messages) with states.
 * This object is used as a entry of each of the entries in the AeStateTable.
 */
public class AeStateTableEntry
{
   /**
    * Map containing various states, keyed by message.
    */
   private Map mMessageStateMap = new Hashtable();
   
   /**
    * Default construct.
    */
   public AeStateTableEntry()
   {     
   }
   
   /**
    * Adds and associates the given message with the state (that should be transitioned to).
    * @param aMessage
    * @param aState
    */
   public void add(IAeProtocolMessage aMessage, IAeProtocolState aState)
   {
      mMessageStateMap.put(aMessage.getSignal(), aState);
   }
   
   /**
    * Returns the state (which should be transitioned to), given the message signal.
    * @param aMessage
    */
   public IAeProtocolState get(IAeProtocolMessage aMessage)
   {
     return (IAeProtocolState) mMessageStateMap.get(aMessage.getSignal());  
   }    
}
