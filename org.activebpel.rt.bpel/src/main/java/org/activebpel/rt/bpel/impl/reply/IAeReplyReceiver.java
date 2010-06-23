//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/reply/IAeReplyReceiver.java,v 1.2 2006/06/05 20:40:22 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.reply;

import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.message.IAeMessageData;

/**
 *  The callback object for Reply activities.
 */
public interface IAeReplyReceiver
{
   /** Constant to indicate an unassigned reply id. */
   public static int NULL_REPLY_ID = 0;
   /**
    * Callback when a message arrives for a message receiver.
    * @param aMessage The message which has been received.
    * @param aProcessProperties Business Process properties.
    * @throws AeBusinessProcessException Allows the receiver to throw an exception.
    */
   public void onMessage(IAeMessageData aMessage, Map aProcessProperties) throws AeBusinessProcessException;

   /**
    * Callback when a fault arrives instead of the expected message.
    * @param aFault The fault which has been received.
    * @param aProcessProperties Business Process properties.  This map will be
    * null if the fault was generated from a correlation violation or an
    * unmatched receive.
    * @throws AeBusinessProcessException Allows the receiver to throw an exception.
    */
   public void onFault(IAeFault aFault, Map aProcessProperties ) throws AeBusinessProcessException;
      
   /**
    * Returns the durable reply information if the reply receiver is supports durable
    * replies. Otherwise, returns <code>null</code>. Implementations that are durable
    * must return an instance of <code>IAeDurableReplyInfo</code>.
    * 
    * @return Durable reply information if available or <code>null</code> if not available.
    */
   public IAeDurableReplyInfo getDurableReplyInfo();
}
