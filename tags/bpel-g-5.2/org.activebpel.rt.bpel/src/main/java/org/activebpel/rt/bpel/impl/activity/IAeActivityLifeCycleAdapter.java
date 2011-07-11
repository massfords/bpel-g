//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/IAeActivityLifeCycleAdapter.java,v 1.5 2008/03/11 03:05:23 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.xml.def.IAeAdapter;
import org.w3c.dom.Element;

/**
 * This interface provides lifecycle support for extension activity impl 
 */
public interface IAeActivityLifeCycleAdapter extends IAeAdapter, IAeLifecycleAdapterConstants
{
   /**
    * Called prior to the activity tranistioning from the old state to the new state
    * @param aOldState
    * @param aNewState
    */
   public void onStateChange(AeBpelState aOldState, AeBpelState aNewState);
   
   /**
    * Provides execution for extension activity impl
    * @param aContext
    * @throws AeBusinessProcessException 
    */
   public void execute(IAeActivityLifeCycleContext aContext) throws AeBusinessProcessException;
   
   /**
    * Provides termination for extension activity impl
    * @param aContext
    * @throws AeBusinessProcessException 
    */
   public void terminate(IAeActivityLifeCycleContext aContext) throws AeBusinessProcessException;

   /**
    * Saves state of extension activity impl
    * @param aElement
    */
   public void save(Element aElement);

   /**
    * Restore state of extension activity impl
    * @param aElement
    */
   public void restore(Element aElement);
   
   /**
    * Callback when a message arrives for a message receiver.
    * @param aContext
    * @throws AeBusinessProcessException Allows to throw an exception.
    */
   public void onMessage(IAeActivityLifeCycleContext aContext, IAeMessageData aMessage) throws AeBusinessProcessException;

   /**
    * Callback when a fault arrives instead of the expected message.
    * @param aContext
    * @throws AeBusinessProcessException Allows to throw an exception.
    */
   public void onFault(IAeActivityLifeCycleContext aContext, IAeFault aFault) throws AeBusinessProcessException;
   
   /**
    * Gets an adapter impl given the class
    * @param aClass
    */
   public IAeImplAdapter getImplAdapter(Class aClass);
}
