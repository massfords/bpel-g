//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/reply/AeDurableQueuingReplyReceiver.java,v 1.3 2006/10/24 21:30:05 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.reply;

import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.IAeInvokeInternal;
import org.activebpel.rt.bpel.impl.reply.AeDurableReplyInfo;
import org.activebpel.rt.bpel.impl.reply.IAeDurableReplyInfo;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.wsio.AeMessageAcknowledgeException;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;

/**
 * Persistent (durable) implementation of the reply receiver which passes the messages and
 * faults to the engine via queueInvokeData() and queueInvokeFault().
 *
 */
public class AeDurableQueuingReplyReceiver extends AeQueuingReplyReceiver implements IAeMessageAcknowledgeCallback
{
   /** Durable reply prototype that is used by the factory. */
   public static String TYPE = "DurableQueuedInvoke"; //$NON-NLS-1$
   /** Property key for invoke objects process id. */
   public static String PROCESS_ID = "ProcessId"; //$NON-NLS-1$
   /** Property key for invoke object's location path. */
   public static String LOCATION_PATH = "LocationPath"; //$NON-NLS-1$
   /** Property key for transmission Id */
   public static String TRANSMISSION_ID = "TransmissionId"; //$NON-NLS-1$   
   /** Durable reply info. */
   private IAeDurableReplyInfo mDurableReplyInfo = null;
   
   /**
    * Creates a reply receiver given the queued Invoke object's process id and location path.
    * @param aProcessId process id of the Invoke object.
    * @param aLocationPath location path of the Invoke object.
    * @param aTransmissionId durable invoke tranmission id.
    */
   public AeDurableQueuingReplyReceiver(long aProcessId, String aLocationPath, long aTransmissionId)
   {
      super(aProcessId, aLocationPath, aTransmissionId);
   }
   
   /** 
    * Creates the receiver using the process id and location path from the invoke object 
    * @param aInvoke invoke that is queued in the engine.
    */
   public AeDurableQueuingReplyReceiver(IAeInvokeInternal aInvoke)
   {
      super(aInvoke);
   }
   

   /**
    * Queues the fault into the engine.
    * @param aFault fault
    * @param aProcessProperties business process properties.
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#onFault(org.activebpel.rt.bpel.IAeFault, java.util.Map)  
    */
   public void onFault(IAeFault aFault, Map aProcessProperties) throws AeBusinessProcessException
   {
      queueInvokeFault(aFault, aProcessProperties, this);
   }

   /**
    * Queues the message data into the engine.
    * @param aMessageData message data.
    * @param aProcessProperties business process properties.
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#onMessage(org.activebpel.rt.message.IAeMessageData, java.util.Map)     
    */
   public void onMessage(IAeMessageData aMessageData, Map aProcessProperties) throws AeBusinessProcessException
   {
      // queue invoke response data iff this has not been queued before.         
      queueInvokeData(aMessageData, aProcessProperties, this);
   }       
   
   /** 
    * Overrides method to return an instance of the durable reply data.
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#getDurableReplyInfo()
    */
   public IAeDurableReplyInfo getDurableReplyInfo()
   {
      if (mDurableReplyInfo == null)
      {
         mDurableReplyInfo = new AeDurableReplyInfo(TYPE);
         mDurableReplyInfo.getProperties().put( PROCESS_ID, String.valueOf( getProcessId() ) );
         mDurableReplyInfo.getProperties().put( LOCATION_PATH, String.valueOf( getLocationPath() ) );
         mDurableReplyInfo.getProperties().put( TRANSMISSION_ID, String.valueOf( getTransmissionId() ) );
      }
      return mDurableReplyInfo;
   }   
   
   /** 
    * Overrides method to record the acknowledgement. 
    * @see org.activebpel.wsio.IAeMessageAcknowledgeCallback#onAcknowledge(java.lang.String)
    */
   public void onAcknowledge(String aMessageId) throws AeMessageAcknowledgeException
   {
      // Message receipt call back interface. The aMessageId is ignored.
      // The engine calls back this method once the data has been delivered to engine
      // and the engine has journaled the receipt of the invoke's response data.            
      
      // Consume this message, though a this class does not use this notification for anything else.
   }

   /**
    * @see org.activebpel.wsio.IAeMessageAcknowledgeCallback#onNotAcknowledge(java.lang.Throwable)
    */
   public void onNotAcknowledge(Throwable aReason)
   {
      // This callback indicates that the engine accepted the data, but was not delivered to
      // the process due to errors, such as unmatched invokes.
      //
      // Consume this message, though a this class does not use this notification for anything else.
   }

}
