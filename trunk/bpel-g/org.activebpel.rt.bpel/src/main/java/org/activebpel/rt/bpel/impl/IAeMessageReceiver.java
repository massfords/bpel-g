// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeMessageReceiver.java,v 1.8 2006/05/24 23:07:00 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.message.IAeMessageData;

/**
 * Defines a callback interface for objects that expect to receive messages. 
 */
public interface IAeMessageReceiver
{
   /**
    * Callback when a message arrives for a message receiver.
    * @param aMessage The message which has been received.
    * @throws AeBusinessProcessException Allows the receiver to throw an exception.
    */
   public void onMessage(IAeMessageData aMessage) throws AeBusinessProcessException;

   /**
    * Callback when a fault arrives instead of the expected message.
    * @param aFault The fault which has been received.
    * @throws AeBusinessProcessException Allows the receiver to throw an exception.
    */
   public void onFault(IAeFault aFault) throws AeBusinessProcessException;

   /**
    * Returns the unique location path within the process for this receiver.
    */
   public String getLocationPath();
   
   /**
    * Returns the unique location id within the process for this receiver.
    */
   public int getLocationId();
   
}
