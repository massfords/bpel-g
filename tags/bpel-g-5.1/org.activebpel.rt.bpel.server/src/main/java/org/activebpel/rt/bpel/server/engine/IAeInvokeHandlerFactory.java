//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/IAeInvokeHandlerFactory.java,v 1.2 2005/01/19 22:47:58 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.wsio.invoke.IAeInvoke;
import org.activebpel.wsio.invoke.IAeInvokeHandler;

/**
 *  Factory interface for the creation of <code>IAeInvokeHandler</code> impls.
 */
public interface IAeInvokeHandlerFactory
{
   /**
    * Create an new <code>IAeInvokeHandler</code> instance.
    * @param aInvoke
    * @throws AeBusinessProcessException
    */
   public IAeInvokeHandler createInvokeHandler( IAeInvoke aInvoke ) throws AeBusinessProcessException;
   
   /**
    * Return any custom query data that should be passed into the invoke 
    * handler.  This will be null if none was specified.
    * @param aInvoke
    */
   public String getQueryData( IAeInvoke aInvoke );
}
