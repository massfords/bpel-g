//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/coord/IAeProtocolMessage.java,v 1.1 2005/10/28 21:07:19 PJayanetti Exp $
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
 * Interface for a WS-Coordination protocol message that is exchanged between the 
 * coordinator and the participant. 
 * 
 * Note: This is an internal implementation tailored to be used with requirement 111. 
 * 
 */
public interface IAeProtocolMessage
{
   /** 
    * @return Returns protocol specific message signal.
    */
   public String getSignal();
   
   /** 
    * @return Returns the coordination id.
    */
   public String getCoordinationId();
   
   /**
    * @return Returns the fault if any or null otherwise.
    */
   public IAeFault getFault();
   
   /** 
    * Returns true if the signal of the IAeProtocolMessage being 
    * compared with equals this instance's signal.
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equalsSignal(IAeProtocolMessage aOther);   
}
