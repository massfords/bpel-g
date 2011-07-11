// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/recovered/AeRecoveredReplyItem.java,v 1.1 2005/07/12 00:26:04 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.recovered;

import org.activebpel.rt.bpel.impl.queue.AeReply;

/**
 * Base class for recovered reply items.
 */
public abstract class AeRecoveredReplyItem implements IAeRecoveredItem
{
   /** The reply object. */
   private final AeReply mReply;

   /**
    * Constructs a recovered reply item.
    */
   protected AeRecoveredReplyItem(AeReply aReply)
   {
      mReply = aReply;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem#getLocationId()
    */
   public int getLocationId()
   {
      // Return 0, because location id not used for matching recovered replies.
      return 0;
   }

   /**
    * Returns the reply object.
    */
   public AeReply getReply()
   {
      return mReply;
   }
}