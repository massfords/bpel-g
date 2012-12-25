//$Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/IAeMessageAcknowledgeCallback.java,v 1.2 2008/02/17 22:01:32 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio;

/**
 * Callback interface that is used to acknowledge a receipt of a inbound 
 * or out bound message.
 */
public interface IAeMessageAcknowledgeCallback
{

   /**
    * Called after a message has been to handed over to another module.
    * @param aMessageId unique id such as a transmission or journal id. This parameter maybe null.
    */
   public void onAcknowledge(String aMessageId) throws AeMessageAcknowledgeException;
   
   /**
    * Called if the message cannot be delivered. A typical use case if a one-way correlated message
    * was queued to the engine and the engine did not find matching message receiver with in a
    * predetermined time.
    * @param aReason Optional exception that was the root cause of non-delivery. This parameter may be null.
    */
   public void onNotAcknowledge(Throwable aReason);
}
