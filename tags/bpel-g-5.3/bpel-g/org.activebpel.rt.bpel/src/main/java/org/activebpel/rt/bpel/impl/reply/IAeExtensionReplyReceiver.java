//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/reply/IAeExtensionReplyReceiver.java,v 1.1 2007/11/02 15:59:27 ppatruni Exp $
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
 * Extension ReplyReceiver Interface  
 */
public interface IAeExtensionReplyReceiver extends IAeReplyReceiver
{
   /**
    * Sets engine on the reply receiver
    * @param aEngine
    */
   public void setEngine(IAeBusinessProcessEngineInternal aEngine);
   
   /**
    * @return long processId
    */
   public long getProcessId();
   
   /**
    * @return String Location Path
    */
   public String getLocationPath();
}
