//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/AeProtocolMessage.java,v 1.2 2008/03/20 19:15:43 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord;

import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.coord.IAeProtocolMessage;

/**
 * Basic implementation of the coordination protocol message.
 */
public class AeProtocolMessage implements IAeProtocolMessage
{
   /**
    * The command or signal of this message.
    */
   private String mSignal = null;
   
   /**
    * Coordination id.
    */
   private String mCoordinationId = null;
   
   /**
    * Fault data.
    */
   private IAeFault mFault = null;
   
   
   /**
    * Constructs the object given the protocol signal. 
    */
   public AeProtocolMessage(String aSignal)
   {
      this(aSignal, null, null);
   }

   /**
    * Constucts a message given signal, coordination id and fault.
    * @param aSignal
    * @param aCoordinationId
    * @param aFault
    */
   public AeProtocolMessage(String aSignal, String aCoordinationId, IAeFault aFault)
   {
      setSignal(aSignal);
      setCoordinationId(aCoordinationId);
      setFault(aFault);
   }
   
   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.coord.IAeProtocolMessage#getSignal()
    */
   public String getSignal()
   {
      return mSignal;
   }
   
   /**
    * Sets the protocol signal.
    * @param aSignal
    */
   protected void setSignal(String aSignal)
   {
      mSignal = aSignal;
   }
   
   /** 
    * @return Returns the coordination id.
    */
   public String getCoordinationId()
   {
      return mCoordinationId;
   }
   
   /**
    * Sets the coordination id.
    * @param aId
    */
   protected void setCoordinationId(String aId)
   {
      mCoordinationId = aId;
   }
   
   /**
    * @return Returns the fault if any or null otherwise.
    */
   public IAeFault getFault()
   {
      return mFault;
   }
   
   /**
    * Sets the fault data.
    * @param aFault
    */
   protected void setFault(IAeFault aFault)
   {
      mFault = aFault;
   }
   
   /** 
    * Returns true if the signal of the IAeProtocolMessage being 
    * compared with equals this instance's signal.
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equalsSignal(IAeProtocolMessage aOther)
   {
      if (aOther != null)
      {
         return getSignal().equalsIgnoreCase(aOther.getSignal() );
      }
      else
      {
         return false;
      }
   }   

   /** 
    * Overrides method to the message signal. 
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      return getSignal();
   }
}
