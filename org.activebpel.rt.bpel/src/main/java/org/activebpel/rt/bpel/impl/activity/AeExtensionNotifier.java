//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeExtensionNotifier.java,v 1.1 2007/11/21 03:22:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity; 

import java.util.Collection;
import java.util.Iterator;

import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.util.AeUtil;

/**
 * Helper class that notifies all of the extension adapter objects within the 
 * activity of its state change. 
 */
public abstract class AeExtensionNotifier
{
   /** Invokes the onExecute() on the adapter  */
   private static AeExtensionNotifier sExecute = new AeExtensionNotifier()
   {
      /**
       * @see org.activebpel.rt.bpel.impl.activity.AeExtensionNotifier#notifyExtension(org.activebpel.rt.bpel.impl.activity.AeActivityImpl, org.activebpel.rt.bpel.IAeFault, org.activebpel.rt.bpel.impl.activity.IAeExtensionLifecycleAdapter)
       */
      protected void notifyExtension(AeActivityImpl aImpl, IAeFault aFault,
            IAeExtensionLifecycleAdapter aAdapter)
      {
         aAdapter.onExecute();
      }
      
   };
   
   /** Invokes the onComplete() on the adapter */
   private static AeExtensionNotifier sComplete = new AeExtensionNotifier()
   {
      /**
       * @see org.activebpel.rt.bpel.impl.activity.AeExtensionNotifier#notifyExtension(org.activebpel.rt.bpel.impl.activity.AeActivityImpl, org.activebpel.rt.bpel.IAeFault, org.activebpel.rt.bpel.impl.activity.IAeExtensionLifecycleAdapter)
       */
      protected void notifyExtension(AeActivityImpl aImpl, IAeFault aFault,
            IAeExtensionLifecycleAdapter aAdapter)
      {
         aAdapter.onComplete();
      }
   };

   /** Invokes the onInitialize() on the adapter */
   private static AeExtensionNotifier sInitialize = new AeExtensionNotifier()
   {
      /**
       * @see org.activebpel.rt.bpel.impl.activity.AeExtensionNotifier#notifyExtension(org.activebpel.rt.bpel.impl.activity.AeActivityImpl, org.activebpel.rt.bpel.IAeFault, org.activebpel.rt.bpel.impl.activity.IAeExtensionLifecycleAdapter)
       */
      protected void notifyExtension(AeActivityImpl aImpl, IAeFault aFault,
            IAeExtensionLifecycleAdapter aAdapter)
      {
         aAdapter.onInitialize();
      }
   };
   
   /** Invokes the onFault on the adapter */
   private static AeExtensionNotifier sFault = new AeExtensionNotifier()
   {
      /**
       * @see org.activebpel.rt.bpel.impl.activity.AeExtensionNotifier#notifyExtension(org.activebpel.rt.bpel.impl.activity.AeActivityImpl, org.activebpel.rt.bpel.IAeFault, org.activebpel.rt.bpel.impl.activity.IAeExtensionLifecycleAdapter)
       */
      protected void notifyExtension(AeActivityImpl aImpl, IAeFault aFault,
            IAeExtensionLifecycleAdapter aAdapter)
      {
         aAdapter.onFault(aFault);
      }
   };

   /** Invokes the onTerminate() on the adapter */
   private static AeExtensionNotifier sTerminate = new AeExtensionNotifier()
   {
      /**
       * @see org.activebpel.rt.bpel.impl.activity.AeExtensionNotifier#notifyExtension(org.activebpel.rt.bpel.impl.activity.AeActivityImpl, org.activebpel.rt.bpel.IAeFault, org.activebpel.rt.bpel.impl.activity.IAeExtensionLifecycleAdapter)
       */
      protected void notifyExtension(AeActivityImpl aImpl, IAeFault aFault,
            IAeExtensionLifecycleAdapter aAdapter)
      {
         aAdapter.onTerminate();
      }
   };
   
   /**
    * Calls the onIntialize method on all of the extension adapter objects 
    * within the impl
    * @param aImpl
    */
   public static void onInitialize(AeActivityImpl aImpl)
   {
      sInitialize.notifyExtensions(aImpl, null);
   }
   
   /**
    * Calls the onExecute method on all of the extension adapter objects 
    * within the impl
    * @param aImpl
    */
   public static void onExecute(AeActivityImpl aImpl)
   {
      sExecute.notifyExtensions(aImpl, null);
   }
   
   /**
    * Calls the onComplete method on all of the extension adapter objects 
    * within the impl
    * @param aImpl
    */
   public static void onComplete(AeActivityImpl aImpl)
   {
      sComplete.notifyExtensions(aImpl, null);
   }
   
   /**
    * Calls the onFault method on all of the extension adapter objects 
    * within the impl
    * @param aImpl
    * @param aFault
    */
   public static void onFault(AeActivityImpl aImpl, IAeFault aFault)
   {
      sFault.notifyExtensions(aImpl, aFault);
   }
   
   /**
    * Calls the onTerminate method on all of the extension adapter objects 
    * within the impl
    * @param aImpl
    */
   public static void onTerminate(AeActivityImpl aImpl)
   {
      sTerminate.notifyExtensions(aImpl, null);
   }

   /**
    * private Ctor to force callers to use statics 
    */
   private AeExtensionNotifier()
   {
   }
   
   /**
    * Walks the extensions and notifies each extension of the event.
    * @param aImpl
    * @param aFault
    */
   protected void notifyExtensions(AeActivityImpl aImpl, IAeFault aFault)
   {
      Collection extensions = aImpl.getExtensions();
      if (AeUtil.notNullOrEmpty(extensions))
      {
         for (Iterator it = extensions.iterator(); it.hasNext();)
         {
            IAeExtensionLifecycleAdapter adapter = (IAeExtensionLifecycleAdapter) it.next();
            notifyExtension(aImpl, aFault, adapter);
         }
      }
   }
   
   /**
    * Abstract method impl'd by the above singletons
    * @param aImpl
    * @param aFault
    * @param aAdapter
    */
   protected abstract void notifyExtension(AeActivityImpl aImpl, IAeFault aFault, IAeExtensionLifecycleAdapter aAdapter);

}
 