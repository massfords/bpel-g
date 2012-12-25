//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/AePolicyInvokeHandlerFactory.java,v 1.2 2008/01/14 21:18:40 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.server.engine.AeInvokeHandlerUri;
import org.activebpel.rt.bpel.server.engine.IAeInvokeHandlerFactory;
import org.activebpel.wsio.invoke.IAeInvoke;
import org.activebpel.wsio.invoke.IAeInvokeHandler;

/**
 * Factory for the default axis invoke handler.
 */
public class AePolicyInvokeHandlerFactory implements IAeInvokeHandlerFactory
{
   private static final String POLICY_ERROR_MSG = AeMessages.getString("AePolicyInvokeHandlerFactory.0"); //$NON-NLS-1$

   /** The default invoke handler */
   private static final IAeInvokeHandler DEFAULT_HANDLER = new AeAxisInvokeHandler();
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeInvokeHandlerFactory#createInvokeHandler(org.activebpel.wsio.invoke.IAeInvoke)
    */
   public IAeInvokeHandler createInvokeHandler(IAeInvoke aInvoke)
         throws AeBusinessProcessException
   {
      try 
      {
         return DEFAULT_HANDLER;
      }
      catch (Exception ae)
      {
        throw new AeBusinessProcessException(POLICY_ERROR_MSG, ae);
      }
  }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeInvokeHandlerFactory#getQueryData(org.activebpel.wsio.invoke.IAeInvoke)
    */
   public String getQueryData(IAeInvoke aInvoke)
   {
      return AeInvokeHandlerUri.getInvokerString(aInvoke.getInvokeHandler());
   }
    
}
 