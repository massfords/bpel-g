//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeAbstractExtensionActivityImpl.java,v 1.3 2008/03/11 03:05:23 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.message.IAeMessageData;
import org.w3c.dom.Element;

/**
 * Base class for extension activity contributions 
 */
public abstract class AeAbstractExtensionActivityImpl implements IAeActivityLifeCycleAdapter, IAeImplAdapter
{
   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleAdapter#onStateChange(org.activebpel.rt.bpel.impl.AeBpelState, org.activebpel.rt.bpel.impl.AeBpelState)
    */
   public void onStateChange(AeBpelState aOldState, AeBpelState aNewState)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleAdapter#execute(org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleContext)
    */
   public void execute(IAeActivityLifeCycleContext aContext)
         throws AeBusinessProcessException
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleAdapter#onFault(org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleContext, org.activebpel.rt.bpel.IAeFault)
    */
   public void onFault(IAeActivityLifeCycleContext aContext, IAeFault aFault)
         throws AeBusinessProcessException
   {
      aContext.completedWithFault(aFault);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleAdapter#onMessage(org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleContext, org.activebpel.rt.message.IAeMessageData)
    */
   public void onMessage(IAeActivityLifeCycleContext aContext, IAeMessageData aMessageData)
         throws AeBusinessProcessException
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleAdapter#restore(org.w3c.dom.Element)
    */
   public void restore(Element aElement)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleAdapter#save(org.w3c.dom.Element)
    */
   public void save(Element aElement)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleAdapter#terminate(org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleContext)
    */
   public void terminate(IAeActivityLifeCycleContext aContext)
         throws AeBusinessProcessException
   {
      aContext.complete();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleAdapter#getImplAdapter(java.lang.Class)
    */
   public IAeImplAdapter getImplAdapter(Class aClass)
   {
      if (aClass.isAssignableFrom(getClass()))
         return this;
      return null;
   }
}
 