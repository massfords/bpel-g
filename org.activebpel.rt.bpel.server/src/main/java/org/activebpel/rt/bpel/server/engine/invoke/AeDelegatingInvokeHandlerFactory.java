//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/invoke/AeDelegatingInvokeHandlerFactory.java,v 1.3 2007/01/26 22:54:08 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.invoke; 

import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeDelegatingHandlerFactory;
import org.activebpel.rt.bpel.server.engine.IAeInvokeHandlerFactory;
import org.activebpel.wsio.invoke.IAeInvoke;
import org.activebpel.wsio.invoke.IAeInvokeHandler;

/**
 * An invoke handler factory that delegates to other factories based on the protocol in the URN. 
 */
public class AeDelegatingInvokeHandlerFactory extends AeDelegatingHandlerFactory implements IAeInvokeHandlerFactory
{
   
   /**
    * Base Class constructor loads the delegate factories from the protocol map in the config.
    * @param aConfig
    */
   public AeDelegatingInvokeHandlerFactory(Map aConfig) throws AeException
   {
      super(aConfig);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeInvokeHandlerFactory#createInvokeHandler(org.activebpel.wsio.invoke.IAeInvoke)
    */
   public IAeInvokeHandler createInvokeHandler(IAeInvoke aInvoke)
         throws AeBusinessProcessException
   {
      IAeInvokeHandlerFactory delegate = getDelegate(aInvoke);
      if (delegate == null)
      {
         Object[] args = new Object[2];
         args[0] = getProtocol(aInvoke.getInvokeHandler());
         args[1] = aInvoke.getInvokeHandler();
         throw new AeBusinessProcessException(AeMessages.format("AeDelegatingHandlerFactory.MissingHandlerFactory", args)); //$NON-NLS-1$
      }
      return delegate.createInvokeHandler(aInvoke);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeInvokeHandlerFactory#getQueryData(org.activebpel.wsio.invoke.IAeInvoke)
    */
   public String getQueryData(IAeInvoke aInvoke)
   {
      try
      {
         IAeInvokeHandlerFactory delegate = getDelegate(aInvoke);
         return delegate.getQueryData(aInvoke);
      }
      catch (AeBusinessProcessException e)
      {
         throw new IllegalStateException(e.getLocalizedMessage());
      }
   }

   /**
    * Gets the factory to delegate the requests to.
    * @param aInvoke
    */
   protected IAeInvokeHandlerFactory getDelegate(IAeInvoke aInvoke) throws AeBusinessProcessException 
   {
      return (IAeInvokeHandlerFactory) getDelegate(aInvoke.getInvokeHandler());
   }
}
 