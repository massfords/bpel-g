//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/reply/IAeReplyReceiverFactory.java,v 1.2 2006/07/10 16:32:48 EWittmann Exp $
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

import org.activebpel.rt.AeException;

/**
 * Factory that creates a reply receiver given its properties. 
 */
public interface IAeReplyReceiverFactory
{

   /**
    * Creates a reply receiver given its properties. This method is assumed to be thread safe.
    * @param aProperties
    * @throws AeException
    */
   public IAeReplyReceiver createReplyReceiver(Map aProperties) throws AeException;
}
