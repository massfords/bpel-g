//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeAbstractExtensionImpl.java,v 1.1 2007/11/21 03:22:16 mford Exp $
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
import org.activebpel.rt.xml.def.IAeAdapter;
import org.w3c.dom.Element;

/**
 * Base class for extensions to provide runtime behavior
 */
public abstract class AeAbstractExtensionImpl implements IAeExtensionLifecycleAdapter, IAeAdapter
{
   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeExtensionLifecycleAdapter#onInstalled(org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   public void onInstalled(IAeBpelObject aBpelObject)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeExtensionLifecycleAdapter#onComplete()
    */
   public void onComplete()
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeExtensionLifecycleAdapter#onExecute()
    */
   public void onExecute()
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeExtensionLifecycleAdapter#onFault(org.activebpel.rt.bpel.IAeFault)
    */
   public void onFault(IAeFault aFault)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeExtensionLifecycleAdapter#onInitialize()
    */
   public void onInitialize()
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeExtensionLifecycleAdapter#onRestore(org.w3c.dom.Element)
    */
   public void onRestore(Element aElement)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeExtensionLifecycleAdapter#onSave(org.w3c.dom.Element)
    */
   public void onSave(Element aElement)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeExtensionLifecycleAdapter#onTerminate()
    */
   public void onTerminate()
   {
   }
}
 