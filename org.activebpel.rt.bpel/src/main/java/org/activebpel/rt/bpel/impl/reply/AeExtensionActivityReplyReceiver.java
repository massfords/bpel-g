//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/reply/AeExtensionActivityReplyReceiver.java,v 1.4 2008/03/11 03:05:23 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.reply;

import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.message.IAeMessageData;

/**
 * Base class for extension reply receiver
 */
public class AeExtensionActivityReplyReceiver implements IAeExtensionReplyReceiver
{
   /** Reference to the Engine */
   private IAeBusinessProcessEngineInternal mEngine;
   /** The message data returned from this response receiver */
   private IAeMessageData mMessageData;
   /** Process Id of invoke */
   private long mProcessId;
   /** LocationPath of invoke */
   private String mLocationPath;
   /** transmission id */
   private long mTransmissionId;
   
   /**
    * @param aProcessId
    * @param aLocationPath
    */
   public AeExtensionActivityReplyReceiver(long aProcessId, String aLocationPath, long aTransmissionId)
   {
      setProcessId(aProcessId);
      setLocationPath(aLocationPath);
      setTransmissionId(aTransmissionId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeExtensionReplyReceiver#setEngine(org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal)
    */
   public void setEngine(IAeBusinessProcessEngineInternal aEngine)
   {
      mEngine = aEngine;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#getDurableReplyInfo()
    */
   public IAeDurableReplyInfo getDurableReplyInfo()
   {
      return null;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#onFault(org.activebpel.rt.bpel.IAeFault, java.util.Map)
    */
   public void onFault(IAeFault aFault, Map aProcessProperties) throws AeBusinessProcessException
   {
      getEngine().queueInvokeFault(getProcessId(), getLocationPath(), getTransmissionId(), aFault, aProcessProperties);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#onMessage(org.activebpel.rt.message.IAeMessageData, java.util.Map)
    */
   public void onMessage(IAeMessageData aMessage, Map aProcessProperties) throws AeBusinessProcessException
   {
      getEngine().queueInvokeData(getProcessId(), getLocationPath(), getTransmissionId(), aMessage, aProcessProperties);
   }
   
   /**
    * @return IAeBusinessProcessEngineInternal engine
    */
   protected IAeBusinessProcessEngineInternal getEngine()
   {
      return mEngine;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeExtensionReplyReceiver#getLocationPath()
    */
   public String getLocationPath()
   {
      return mLocationPath;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeExtensionReplyReceiver#getProcessId()
    */
   public long getProcessId()
   {
      return mProcessId;
   }

   /**
    * @return the messageData
    */
   public IAeMessageData getMessageData()
   {
      return mMessageData;
   }

   /**
    * @param aMessageData the messageData to set
    */
   public void setMessageData(IAeMessageData aMessageData)
   {
      mMessageData = aMessageData;
   }

   /**
    * @return the transmissionId
    */
   protected long getTransmissionId()
   {
      return mTransmissionId;
   }

   /**
    * @param aTransmissionId the transmissionId to set
    */
   protected void setTransmissionId(long aTransmissionId)
   {
      mTransmissionId = aTransmissionId;
   }

   /**
    * @param aProcessId the processId to set
    */
   protected void setProcessId(long aProcessId)
   {
      mProcessId = aProcessId;
   }

   /**
    * @param aLocationPath the locationPath to set
    */
   protected void setLocationPath(String aLocationPath)
   {
      mLocationPath = aLocationPath;
   }

}
