//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/invoke/AePojoHandlerFactory.java,v 1.1 2005/06/22 16:53:56 MFord Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.invoke; 

import java.util.Hashtable;
import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeInvokeHandlerUri;
import org.activebpel.rt.bpel.server.engine.IAeInvokeHandlerFactory;
import org.activebpel.wsio.invoke.IAeInvoke;
import org.activebpel.wsio.invoke.IAeInvokeHandler;

/**
 * Handles the java protocol where the invoke handlers are "plain old java objects"
 * or POJO. The fully qualified class names appear in the custom invoker uri immediately
 * following the protocol but before any query params.
 * 
 * i.e. java:com.my.custom.InvokerHandler?name=value 
 * 
 * The objects are instantiated and cached so they should be reentrant. 
 */
public class AePojoHandlerFactory implements IAeInvokeHandlerFactory
{
   /** map of class names to instances */
   private Map mInvokers = new Hashtable();
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeInvokeHandlerFactory#createInvokeHandler(org.activebpel.wsio.invoke.IAeInvoke)
    */
   public IAeInvokeHandler createInvokeHandler(IAeInvoke aInvoke)
         throws AeBusinessProcessException
   {
      String clazz = AeInvokeHandlerUri.getInvokerString(aInvoke.getInvokeHandler());
      IAeInvokeHandler handler = (IAeInvokeHandler) getInvokers().get(clazz);
      if (handler == null)
      {
         try
         {
            handler = (IAeInvokeHandler) Class.forName(clazz).newInstance();
            getInvokers().put(clazz, handler);
         }
         catch(Exception e)
         {
            throw new AeBusinessProcessException( AeMessages.format("AePojoHandlerFactory.ERROR_CREATING_HANDLER", clazz), e ); //$NON-NLS-1$
         }
      }
      return handler;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeInvokeHandlerFactory#getQueryData(org.activebpel.wsio.invoke.IAeInvoke)
    */
   public String getQueryData(IAeInvoke aInvoke)
   {
      return AeInvokeHandlerUri.getQueryString(aInvoke.getInvokeHandler());
   }
   
   /**
    * @return Returns the invokers.
    */
   protected Map getInvokers()
   {
      return mInvokers;
   }
}
 