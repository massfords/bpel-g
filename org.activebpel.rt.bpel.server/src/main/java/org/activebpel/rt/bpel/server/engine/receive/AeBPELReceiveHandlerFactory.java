//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/receive/AeBPELReceiveHandlerFactory.java,v 1.2 2008/02/17 21:38:45 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.receive; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeReceiveHandler;
import org.activebpel.rt.bpel.server.engine.IAeReceiveHandlerFactory;

/**
 * A receive handler factory for the BPEL Receive handler. 
 */
public class AeBPELReceiveHandlerFactory implements IAeReceiveHandlerFactory
{
   private static IAeReceiveHandler mHandler = new AeDefaultReceiveHandler();
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeReceiveHandlerFactory#createReceiveHandler(java.lang.String)
    */
   public IAeReceiveHandler createReceiveHandler(String aProtocol) throws AeBusinessProcessException
   {
      return mHandler;
   }

}
 