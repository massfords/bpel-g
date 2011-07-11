//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/reply/AeDurableExtensionReplyReceiver.java,v 1.2 2008/03/11 03:09:29 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.reply;

import org.activebpel.rt.bpel.impl.reply.AeExtensionActivityDurableInfo;
import org.activebpel.rt.bpel.impl.reply.AeExtensionActivityReplyReceiver;
import org.activebpel.rt.bpel.impl.reply.IAeDurableReplyInfo;

/**
 * Persistent (durable) implementation of the reply receiver for people activity to  
 * invoke call backs with messages and faults on the extension activity impl directly.
 */
public class AeDurableExtensionReplyReceiver extends AeExtensionActivityReplyReceiver
{
   /** Durable reply info. */
   private AeExtensionActivityDurableInfo mDurableReplyInfo = null;

   /**
    * C'tor
    * @param aDurableReplyInfo
    */
   public AeDurableExtensionReplyReceiver(AeExtensionActivityDurableInfo aDurableReplyInfo)
   {
      super(aDurableReplyInfo.getProcessId(),aDurableReplyInfo.getLocationPath(), aDurableReplyInfo.getTransmissionId());
      mDurableReplyInfo = aDurableReplyInfo;
   }

   /** 
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver#getDurableReplyInfo()
    */
   public IAeDurableReplyInfo getDurableReplyInfo()
   {
      return mDurableReplyInfo;
   }   

}
