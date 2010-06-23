//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/reply/AeDurableExtensionReplyReceiverFactory.java,v 1.2 2008/03/11 03:09:29 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.reply;

import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.impl.reply.AeExtensionActivityDurableInfo;
import org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver;
import org.activebpel.rt.bpel.impl.reply.IAeReplyReceiverFactory;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

/**
 * ReplyReceiver factory that is responsible for recreating ExtensionReplyReceivers.
 */
public class AeDurableExtensionReplyReceiverFactory implements IAeReplyReceiverFactory
{
   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeReplyReceiverFactory#createReplyReceiver(java.util.Map)
    */
   public IAeReplyReceiver createReplyReceiver(Map aProperties) throws AeException
   {
      AeDurableExtensionReplyReceiver replyReceiver = new AeDurableExtensionReplyReceiver(new AeExtensionActivityDurableInfo(aProperties));
      // fixme (MF-recovery) don't propagate this static getter. Instead, make the setting of the engine something that happens by the loading of this factory
      replyReceiver.setEngine(AeEngineFactory.getEngine());
      return replyReceiver;
   }

}
