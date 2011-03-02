//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/reply/AeMissingReplyReceiverException.java,v 1.2 2006/06/08 19:30:55 PJayanetti Exp $
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
import org.activebpel.rt.bpel.AeMessages;

/**
 * Implements an exception that reports a missing reply receiver.
 */
public class AeMissingReplyReceiverException extends AeBusinessProcessException
{  
   /**
    * Constructs the exception with reply id.
    * @param aReplyId
    */
   public AeMissingReplyReceiverException(long aReplyId)
   {
      super(AeMessages.format("AeMissingReplyReceiverException.REPLY_NOT_AVAIALBLE", aReplyId)); //$NON-NLS-1$
   }

}
