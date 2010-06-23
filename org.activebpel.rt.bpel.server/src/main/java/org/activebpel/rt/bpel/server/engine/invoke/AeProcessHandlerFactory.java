//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/invoke/AeProcessHandlerFactory.java,v 1.3 2006/05/24 23:16:33 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.invoke; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.server.engine.AeInvokeHandlerUri;
import org.activebpel.rt.bpel.server.engine.IAeInvokeHandlerFactory;
import org.activebpel.wsio.invoke.IAeInvoke;
import org.activebpel.wsio.invoke.IAeInvokeHandler;

/**
 * Handler factory to create Invoke handler for the 'process' protocol.   
 */
public class AeProcessHandlerFactory implements IAeInvokeHandlerFactory
{
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeInvokeHandlerFactory#createInvokeHandler(org.activebpel.wsio.invoke.IAeInvoke)
    */
   public IAeInvokeHandler createInvokeHandler(IAeInvoke aInvoke)
         throws AeBusinessProcessException
   {
      IAeInvokeHandler rHandler = null;
      String invokerType = AeInvokeHandlerUri.getInvokerString(aInvoke.getInvokeHandler());
      // create new handler objects based on the invoke type.
      // process/subprocess handlers are stateful (not re-entrant), hence new instances are created. 
      if ("subprocess".equals(invokerType)) //$NON-NLS-1$
      {
         rHandler = new AeSubprocessInvokeHandler();
      }
      else
      {
         rHandler = new AeProcessInvokeHandler();
      }
      return rHandler;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeInvokeHandlerFactory#getQueryData(org.activebpel.wsio.invoke.IAeInvoke)
    */
   public String getQueryData(IAeInvoke aInvoke)
   {
      return AeInvokeHandlerUri.getQueryString(aInvoke.getInvokeHandler());
   }
      
}
 