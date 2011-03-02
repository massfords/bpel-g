//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/reply/IAeDurableReplyInfo.java,v 1.2 2006/07/10 16:32:48 EWittmann Exp $
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

/**
 * Contains durable reply information.
 *
 */
public interface IAeDurableReplyInfo
{

   /** 
    * Returns the prototype for the durable reply. The prototype is used to create an 
    * concrete instance of a durable reply receiver.
    * @return the durable reply type.
    */
   public String getType();
   
   /**
    * Returns properties associated with a specific durable reply.
    */
   public Map getProperties();
}
