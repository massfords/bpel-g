//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/reply/AeQueuingReplyReceiver.java,v 1.8 2008/03/28 01:45:46 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.reply;

import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcessEngine;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.IAeInvokeInternal;
import org.activebpel.rt.bpel.impl.reply.AeReplyReceiverBase;
import org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;

/**
 * Provides a way of passing the outcome of the invocation back into the engine
 * by queuing the message data or the fault (instead of calling notifyAll).
 */
public class AeQueuingReplyReceiver extends AeReplyReceiverBase
{
   /**
    * Process Id of invoke.
    */
   private long mProcessId;
   /**
    * LocationPath of invoke.
    */
   private String mLocationPath;

   /** invoke transmission id. */
   private long mTransmissionId = IAeTransmissionTracker.NULL_TRANSREC_ID;

   /**
    * Creates a reply receiver given the queued Invoke object's process id and location path.
    * @param aProcessId process id of the Invoke object.
    * @param aLocationPath location path of the Invoke object.
    * @param aTransmissionId invoke activity transmission id.
    */
   public AeQueuingReplyReceiver(long aProcessId, String aLocationPath, long aTransmissionId)
   {
      mProcessId = aProcessId;
      mLocationPath = aLocationPath;
      setTransmissionId(aTransmissionId);
   }

   /**
    * Creates the receiver using the process id and location path from the invoke object
    * @param aInvoke invoke that is queued in the engine.
    */
   public AeQueuingReplyReceiver(IAeInvokeInternal aInvoke)
   {
      this(aInvoke.getProcessId(), aInvoke.getLocationPath(), aInvoke.getTransmissionId());
   }

   /**
    * @return process id of invoke activity.
    */
   protected long getProcessId()
   {
      return mProcessId;
   }

   /**
    * @return location path of invoke object.
    */
   protected String getLocationPath()
   {
      return mLocationPath;
   }

   /**
    * @return Returns the transmission d.
    */
   protected long getTransmissionId()
   {
      return mTransmissionId;
   }

   /**
    * @param aTransmissionId
    */
   public void setTransmissionId(long aTransmissionId)
   {
      mTransmissionId = aTransmissionId;
   }

   /**
    * Queues the fault into the engine.
    * @param aFault fault
    * @param aProcessProperties business process properties.
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#onFault(org.activebpel.rt.bpel.IAeFault, java.util.Map)
    */
   public void onFault(IAeFault aFault, Map aProcessProperties) throws AeBusinessProcessException
   {
      queueInvokeFault(aFault, aProcessProperties, null);
   }

   /**
    * Queues the message data into the engine.
    * @param aMessageData message data.
    * @param aProcessProperties business process properties.
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#onMessage(org.activebpel.rt.message.IAeMessageData, java.util.Map)
    */
   public void onMessage(IAeMessageData aMessageData, Map aProcessProperties) throws AeBusinessProcessException
   {
      queueInvokeData(aMessageData, aProcessProperties, null);
   }

   /**
    * Queue's the invoke's fault back into the engine.
    * @param aFault
    * @param aAckCallback optional message acknoledge callback.
    * @throws AeBusinessProcessException
    */
   protected void queueInvokeFault(IAeFault aFault, Map aProcessProperties, IAeMessageAcknowledgeCallback aAckCallback) throws AeBusinessProcessException
   {
      setFault(aFault);
      setBusinessProcessProperties(aProcessProperties);
      try
      {
         getEngine().queueInvokeFault(getProcessId(), getLocationPath(), getTransmissionId(), getFault(), getBusinessProcessProperties(), aAckCallback );
      }
      catch (AeBusinessProcessException e)
      {
         // The fault is likely due to the invoke already having had processed
         // the fault or data. It's also possible that the invoke could have
         // been terminated and no longer in a state to receive the response.
         // In either case, doing nothing here since we already would have logged 
         // the exception on the receiving end. Also, there's no reason to 
         // propagate the fault to the reply activity.
         // One way to avoid this issue entirely is to journal that the reply
         // was received, but this isn't something that we're currently doing,
         // nor does it seem quite worth it.
      }
   }

   /**
    * Queue's the invoke's data back into the engine.
    * @param aMessageData
    * @param aAckCallback
    * @throws AeBusinessProcessException
    */
   protected void queueInvokeData(IAeMessageData aMessageData, Map aProcessProperties, IAeMessageAcknowledgeCallback aAckCallback) throws AeBusinessProcessException
   {
      setMessageData(aMessageData);
      setBusinessProcessProperties(aProcessProperties);
      try
      {
         getEngine().queueInvokeData(getProcessId(), getLocationPath(), getTransmissionId(), aMessageData, getBusinessProcessProperties(), aAckCallback );
      }
      catch (AeBusinessProcessException e)
      {
         // The fault is likely due to the invoke already having had processed
         // the fault or data. It's also possible that the invoke could have
         // been terminated and no longer in a state to receive the response.
         // In either case, doing nothing here since we already would have logged 
         // the exception on the receiving end. Also, there's no reason to 
         // propagate the fault to the reply activity.
         // One way to avoid this issue entirely is to journal that the reply
         // was received, but this isn't something that we're currently doing,
         // nor does it seem quite worth it.
      }
   }

   /***
    * Returns the engine instance.
    * @return engine instance.
    */
   protected IAeBusinessProcessEngine getEngine()
   {
      return AeEngineFactory.getEngine();
   }
}
