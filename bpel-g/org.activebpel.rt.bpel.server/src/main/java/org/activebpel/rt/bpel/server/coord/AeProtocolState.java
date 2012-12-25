//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/AeProtocolState.java,v 1.1 2005/10/28 21:10:30 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord;

import org.activebpel.rt.bpel.coord.IAeProtocolState;

/**
 * Implementation of the coordination protocol state.
 */
public class AeProtocolState implements IAeProtocolState
{
   /**
    * state.
    */
   private String mState = null;
   
   /**
    * Constructs the state given the state. 
    */
   public AeProtocolState(String aState)
   {
      mState = aState;
   }

   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.coord.IAeProtocolState#getState()
    */
   public String getState()
   {
      return mState;
   }

   /**
    * Returns true of the state of the IAeProtocolState being compared to is the same
    * as the state of this instance.
    */
   public boolean equals(Object other)
   {
      if (other != null && other instanceof IAeProtocolState)
      {
         return getState().equalsIgnoreCase( (( IAeProtocolState)other).getState() );
      }
      else
      {
         return false;
      }
   }
   
   /** 
    * Overrides method to return the state. 
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      return getState();
   }
}
