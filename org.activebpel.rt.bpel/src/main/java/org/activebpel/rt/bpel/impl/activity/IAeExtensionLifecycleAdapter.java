//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/IAeExtensionLifecycleAdapter.java,v 1.2 2008/02/17 21:37:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity; 

import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.w3c.dom.Element;

/**
 * Adapter for extensions on activities 
 */
public interface IAeExtensionLifecycleAdapter extends IAeLifecycleAdapterConstants
{
   /**
    * Called after the adapter has been installed on the impl
    * @param aBpelObject
    */
   public void onInstalled(IAeBpelObject aBpelObject);
   
   
   /**
    * Notifies the extension that the activity is about to be initialized. This
    * method is only called for extensions on the process or a scope. 
    */
   public void onInitialize();
   
   /**
    * Notifies the extension that the activity is going to be executing. This
    * method is called prior to any behavior for the activity having been 
    * executed.
    */
   public void onExecute();
   
   /**
    * Notifies the extension that the activity is about to complete.
    */
   public void onComplete();

   /**
    * Notifies the extension that the activity is about to fault.
    */
   public void onFault(IAeFault aFault);

   /**
    * Notifies the extension that the activity is about to terminate
    */
   public void onTerminate();

   /**
    * Saves state of extension activity impl
    * @param aElement
    */
   public void onSave(Element aElement);

   /**
    * Restore state of extension activity impl
    * @param aElement
    */
   public void onRestore(Element aElement);
}
 