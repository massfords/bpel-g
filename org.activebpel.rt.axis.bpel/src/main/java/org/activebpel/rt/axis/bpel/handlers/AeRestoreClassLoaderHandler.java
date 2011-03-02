//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/handlers/AeRestoreClassLoaderHandler.java,v 1.1 2006/03/09 14:28:20 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002, 2003, 2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.handlers;

import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;

/**
 * This class restores the original classloader which was replaced
 * with the services context classloader by
 * <code>org.activebpel.rt.tomcat.AeSetClassLoader</code>.
 */
public class AeRestoreClassLoaderHandler extends BasicHandler
{
   /**
    * Restore original classloader.
    * @see org.apache.axis.Handler#invoke(org.apache.axis.MessageContext)
    */
   public void invoke(MessageContext aMessageContext)
   {
      restoreClassLoader(aMessageContext);
   }

   /**
    * Restore original classloader.
    * @see org.apache.axis.Handler#onFault(org.apache.axis.MessageContext)
    */
   public void onFault(MessageContext aMessageContext)
   {
      restoreClassLoader(aMessageContext);
   }
   
   /** 
    * Puts the original classloader context back in place from the one
    * saved in the passed message context.
    * @param aMessageContext the message context to restore the classloader for.
    */
   protected void restoreClassLoader(MessageContext aMessageContext)
   {
      ClassLoader loader = 
         (ClassLoader) aMessageContext.getProperty(AeSetClassLoaderHandler.SAVED_CLASSLOADER_PROPERTY);
      if (loader != null)
      {
         aMessageContext.setProperty(AeSetClassLoaderHandler.SAVED_CLASSLOADER_PROPERTY, null);
         Thread.currentThread().setContextClassLoader(loader);
      }
   }
}