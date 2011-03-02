//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/reply/IAeDurableReplyFactory.java,v 1.4 2008/02/17 21:37:09 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.reply;

import org.activebpel.rt.bpel.AeBusinessProcessException;

/**
 * Interface for the factory which is responsible for recreating reply receivers
 * using the prototype pattern.
 */
public interface IAeDurableReplyFactory
{
   
   /**
    * Creates a durable reply given the reply type and its properties.
    * @param aReplyId replyId of the corresponding inbound receive.
    * @param aInfo durable reply properties.
    * @return durable reply receiver or AeMissingReplyReceiver if the type is not supported.
    */ 
   public IAeReplyReceiver createReplyReceiver(long aReplyId, IAeDurableReplyInfo aInfo) throws AeBusinessProcessException;
   
   /**
    * Creates and returns the default missing reply receiver implementation.
    * 
    * @param aReplyId
    * @return AeMissingReplyReceiver class.
    */
   public IAeReplyReceiver createMissingReplyReceiver(long aReplyId);
}
