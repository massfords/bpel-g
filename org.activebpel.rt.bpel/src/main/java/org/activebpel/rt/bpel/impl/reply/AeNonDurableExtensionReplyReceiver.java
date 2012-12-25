//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/reply/AeNonDurableExtensionReplyReceiver.java,v 1.3 2008/03/11 03:05:23 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.reply;

import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;

/**
 * Non Durable reply receiver for extensions 
 */
public class AeNonDurableExtensionReplyReceiver extends AeExtensionActivityReplyReceiver
{
   /** Reference to Engine */
   private IAeBusinessProcessEngineInternal mEngine;

   /**
    * @param aProcessId
    * @param aLocationPath
    * @param aTransmissionId
    */
   public AeNonDurableExtensionReplyReceiver(long aProcessId, String aLocationPath, long aTransmissionId)
   {
      super(aProcessId, aLocationPath, aTransmissionId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.reply.AeExtensionActivityReplyReceiver#setEngine(org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal)
    */
   public void setEngine(IAeBusinessProcessEngineInternal aEngine)
   {
      mEngine = aEngine;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.reply.AeExtensionActivityReplyReceiver#getEngine()
    */
   protected IAeBusinessProcessEngineInternal getEngine()
   {
      return mEngine;
   }
}
