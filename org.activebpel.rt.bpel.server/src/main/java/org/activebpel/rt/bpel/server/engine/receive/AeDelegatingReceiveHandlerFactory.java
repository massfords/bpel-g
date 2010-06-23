//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/receive/AeDelegatingReceiveHandlerFactory.java,v 1.2 2008/02/17 21:38:45 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.receive; 

import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeReceiveHandler;
import org.activebpel.rt.bpel.server.engine.AeDelegatingHandlerFactory;
import org.activebpel.rt.bpel.server.engine.IAeReceiveHandlerFactory;
import org.activebpel.wsio.receive.IAeMessageContext;

/**
 * A receive handler factory that delegates to other factories based on the protocol in the URN. 
 */
public class AeDelegatingReceiveHandlerFactory extends AeDelegatingHandlerFactory implements IAeReceiveHandlerFactory
{
   /**
    * Base class Constructor loads the delegate factories from the protocol map in the config.
    * @param aConfig
    */
   public AeDelegatingReceiveHandlerFactory(Map aConfig) throws AeException
   {
      super(aConfig);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeReceiveHandlerFactory#createReceiveHandler(java.lang.String)
    */
   public IAeReceiveHandler createReceiveHandler(String aProtocol) throws AeBusinessProcessException
   {
      IAeReceiveHandlerFactory delegate = (IAeReceiveHandlerFactory) getDelegate(aProtocol);
      return delegate.createReceiveHandler(aProtocol);
   }

   /**
    * Gets the factory to delegate the requests to.
    * @param aContext
    */
   protected IAeReceiveHandlerFactory getDelegate(IAeMessageContext aContext) throws AeBusinessProcessException 
   {
      return (IAeReceiveHandlerFactory) getDelegate(aContext.getReceiveHandler());
   }

}
 