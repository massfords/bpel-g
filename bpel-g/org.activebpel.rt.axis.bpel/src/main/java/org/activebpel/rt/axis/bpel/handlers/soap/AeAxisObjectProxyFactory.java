// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/handlers/soap/AeAxisObjectProxyFactory.java,v 1.4 2006/11/09 18:34:46 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.handlers.soap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import org.activebpel.rt.AeException;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;

/**
 * A factory for creating proxies of Axis SOAP objects.  This exists so that we can return our own 
 * versions of the Axis soap objects that fix some problems with them.
 */
public class AeAxisObjectProxyFactory
{
   /**
    * Wrap the <code>MessageContext</code> in the <code>InvocationHandler</code> instance.
    * 
    * @param aMessageContext
    */
   public static SOAPMessageContext getMessageContextProxy(MessageContext aMessageContext)
   {
      return getMessageContextProxy(aMessageContext, SOAPMessageContext.class);
   }
   
   /**
    * Wrap the <code>MessageContext</code> in the <code>InvocationHandler</code> instance.
    * 
    * @param aMessageContext
    * @param aClass SOAPMessageContext class
    */
   public static SOAPMessageContext getMessageContextProxy(MessageContext aMessageContext, Class aClass)
   {
      InvocationHandler handler = new AeAxisMessageContextInvocationHandler(aMessageContext);

      try
      {
         // Fix for defect 1957, which is essentially about things breaking on
         // newer versions of JBoss 4.0: use the context class loader here
         // instead of what we used to use (see below). The context class loader
         // at this point is the active-bpel web application class loader.
         return (SOAPMessageContext) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
            new Class[] { aClass }, handler);
      }
      catch (Throwable t)
      {
         AeException.logError(t);

         // This is what we used to do. Fall back to it, but it won't work on
         // JBoss 4.0.4 (and other JBoss versions after 4.0.0).
         return (SOAPMessageContext) Proxy.newProxyInstance(aMessageContext.getClass().getClassLoader(),
            new Class[] { aClass }, handler);
      }
   }

   /**
    * Wrap the <code>Message</code> in the <code>InvocationHandler</code> instance.
    * 
    * @param aMessage
    */
   public static SOAPMessage getMessageProxy(Message aMessage)
   {
      return new AeSOAPMessageWrapper(aMessage);
   }

   /**
    * Wrap the <code>SOAPHeader</code> in the <code>InvocationHandler</code> instance.
    * 
    * @param aHeader
    * @param aClass SOAPHeader class
    */
   public static SOAPHeader getSOAPHeaderProxy(org.apache.axis.message.SOAPHeader aHeader, Class aClass)
   {
      InvocationHandler handler = new AeAxisSOAPHeaderInvocationHandler(aHeader);

      try
      {
         // Fix for defect 1957, which is essentially about things breaking on
         // newer versions of JBoss 4.0: use the context class loader here
         // instead of what we used to use (see below). The context class loader
         // at this point is the active-bpel web application class loader.
         return (SOAPHeader) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
            new Class[] { aClass }, handler);
      }
      catch (Throwable t)
      {
         AeException.logError(t);

         // This is what we used to do. Fall back to it, but it won't work on
         // JBoss 4.0.4 (and other JBoss versions after 4.0.0).
         return (SOAPHeader) Proxy.newProxyInstance(aHeader.getClass().getClassLoader(),
            new Class[] { aClass }, handler);
      }
   }
   
   /**
    * Wrap the <code>SOAPHeader</code> in the <code>InvocationHandler</code> instance.
    * 
    * @param aHeader
    */
   public static SOAPHeader getSOAPHeaderProxy(org.apache.axis.message.SOAPHeader aHeader)
   {
      return getSOAPHeaderProxy(aHeader, SOAPHeader.class);
   }
}
