//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/reply/AeReplyReceiverBase.java,v 1.3.16.1 2008/04/21 16:09:44 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.reply;

import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.impl.AeFaultFactory;
import org.activebpel.rt.message.IAeMessageData;

/**
 * Base implementation of the reply receiver.
 */
public abstract class AeReplyReceiverBase implements IAeReplyReceiver
{
   /** The fault returned from this response receiver */
   private IAeFault mFault;
   /** The message data returned from this response receiver */
   private IAeMessageData mMessageData;
   /** The Business Process properties. */
   private Map mProcessProperties;

   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#onMessage(org.activebpel.rt.message.IAeMessageData, java.util.Map)
    */
   public abstract void onMessage(IAeMessageData aMessage, Map aProcessProperties) throws AeBusinessProcessException;

   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#onFault(org.activebpel.rt.bpel.IAeFault, java.util.Map)
    */
   public abstract void onFault(IAeFault aFault, Map aProcessProperties) throws AeBusinessProcessException;

   /**
    * Overrides method to return <code>null</code> to indicate that the reply is not durable (by default).
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#getDurableReplyInfo()
    */
   public IAeDurableReplyInfo getDurableReplyInfo()
   {
      return null;
   }

   /**
    * Returns the fault or null if none was returned.
    */
   public IAeFault getFault()
   {
      return mFault;
   }

   /**
    * @param aFault The fault to set.
    */
   protected void setFault(IAeFault aFault)
   {
      if (aFault != null)
      {
         mFault = AeFaultFactory.makeUncaughtFault(aFault);
      }
      else
      {
         mFault = null;
      }
   }

   /**
    * Returns the message data or null if none was returned.
    */
   public IAeMessageData getMessageData()
   {
      return mMessageData;
   }

   /**
    * @param aMessageData The messageData to set.
    */
   protected void setMessageData(IAeMessageData aMessageData)
   {
      mMessageData = aMessageData;
   }

   /**
    * Returns the process process properties or null of none were set.
    */
   public Map getBusinessProcessProperties()
   {
      return mProcessProperties;
   }

   /**
    * @param aProcessProperties The processProperties to set.
    */
   protected void setBusinessProcessProperties(Map aProcessProperties)
   {
      // Work around for defect 2247. Coordination specific.
      // Remove WSCOORD_ID. This id should only exist in participants (child process)
      // and should not passed back into the parent (coordinator) process.
      // (In AE version 2.0, we use the existance of bus-property WSCOORD_ID to identify it
      //  as a participant process. In 2.1 and later, the <process> element in the state
      //  document has first class attributes 'coordinator' and 'participant' to indicate
      // if the process is a coordinator and/or participant. For legacy support, AE 2.0, 
      // WSCOORD_ID bus-property is also checked to indicate if a process is a participant.
      // [So, if this property is passed back to the parent process, then the parent process
      // may also be flagged as a participant even though it is not].
      // 
      if (aProcessProperties != null)
      {
         aProcessProperties.remove(IAeCoordinating.WSCOORD_ID);
      }      
      mProcessProperties = aProcessProperties;
   }

}
