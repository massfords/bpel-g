//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/IAeReceiveHandlerFactory.java,v 1.2 2008/02/17 21:38:48 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeReceiveHandler;

/**
 *  Factory interface for the creation of <code>IAeReceiveHandler</code> impls.
 */
public interface IAeReceiveHandlerFactory
{
   /**
    * Create a new <code>IAeReceiveHandler</code> instance.
    * @param aProtocol
    * @throws AeBusinessProcessException
    */
   public IAeReceiveHandler createReceiveHandler( String aProtocol ) throws AeBusinessProcessException;

}
