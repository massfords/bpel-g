//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityChildExtensionActivityImpl.java,v 1.8 2008/03/11 03:05:23 mford Exp $
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
import org.activebpel.rt.bpel.IAeInvokeActivity;
import org.activebpel.rt.bpel.IAeInvokeActivityAdapter;
import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.IAeMessageReceiver;
import org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;
import org.activebpel.rt.message.IAeMessageData;

/**
 * Implentation of BPEL 2.0 extension activity
 */
public class AeActivityChildExtensionActivityImpl extends AeActivityImpl 
implements IAeMessageReceiver, IAeInvokeActivity
{
   /** lifecycle adapter that provides all of the extension behavior */
   private IAeActivityLifeCycleAdapter mLifeCycleAdapter;
   /** transmission id */
   private long mTransmissionId;

   /**
    * @param aActivityDef
    * @param aParent
    */
   public AeActivityChildExtensionActivityImpl(AeChildExtensionActivityDef aActivityDef, IAeActivityParent aParent)
   {
      super(aActivityDef, aParent);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept(IAeImplVisitor aVisitor) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      super.execute();

      getProcess().assignTransmissionId(this);
      
      AeActivityLifeCycleContext context = createContext();
      if (getLifeCycleAdapter() != null)
      {
         getLifeCycleAdapter().execute(context);
      }
   }
   
   /**
    * @return the instanceObject
    */
   public IAeActivityLifeCycleAdapter getLifeCycleAdapter()
   {
      return mLifeCycleAdapter;
   }

   /**
    * @param aLifeCycleAdapter adapter to set
    */
   public void setLifeCycleAdapter(IAeActivityLifeCycleAdapter aLifeCycleAdapter)
   {
      mLifeCycleAdapter = aLifeCycleAdapter;
   }

   /**
    * @return AeChildExtensionActivityDef
    */
   public AeChildExtensionActivityDef getDef()
   {
      return (AeChildExtensionActivityDef) getDefinition();
   }

   /**
    * On receiving the call back this method delegates it to the adapter installed on it
    * @param aMessage
    * @throws AeBusinessProcessException
    */
   public void onMessage(IAeMessageData aMessage) throws AeBusinessProcessException
   {
      AeActivityLifeCycleContext context = createContext();
      getLifeCycleAdapter().onMessage(context, aMessage);
   }
 
   /**
    * On receiving the call back this method delegates it to the adapter installed on it
    * @param aFault
    * @throws AeBusinessProcessException
    */
   public void onFault(IAeFault aFault) throws AeBusinessProcessException
   {
      AeActivityLifeCycleContext context = createContext();
      getLifeCycleAdapter().onFault(context, aFault);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#terminate()
    */
   public void terminate() throws AeBusinessProcessException
   {
      if (getLifeCycleAdapter() != null)
      {
         setTerminating(true);
         AeActivityLifeCycleContext context = createContext();
         getLifeCycleAdapter().terminate(context);
      }
      else
      {
         super.terminate();
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.IAeInvokeActivity#getTransmissionId()
    */
   public long getTransmissionId()
   {
      return mTransmissionId;
   }

   /**
    * This method shouldn't be called unless the extension activity is providing
    * some behavior related to the sending or a message. If the adapter to 
    * complete this call isn't present, then the method return false by default.
    * 
    * @see org.activebpel.rt.bpel.IAeInvokeActivity#isOneWay()
    */
   public boolean isOneWay()
   {
      IAeInvokeActivityAdapter invokeActivity = getInvokeActivityAdapter();
      if (invokeActivity != null)
         return invokeActivity.isOneWay();
      return false;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeInvokeActivity#setTransmissionId(long)
    */
   public void setTransmissionId(long aTransmissionId)
   {
      mTransmissionId = aTransmissionId;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityImpl#setState(org.activebpel.rt.bpel.impl.AeBpelState)
    */
   public void setState(AeBpelState aNewState)
         throws AeBusinessProcessException
   {
      if (aNewState.isFinal())
         setTransmissionId(IAeTransmissionTracker.NULL_TRANSREC_ID);
      if (getLifeCycleAdapter() != null)
         getLifeCycleAdapter().onStateChange(getState(), aNewState);
      super.setState(aNewState);
   }

   /**
    * Gets the adapter necessary to provide the transmission data
    */
   protected IAeInvokeActivityAdapter getInvokeActivityAdapter()
   {
      IAeInvokeActivityAdapter adapter = null;
      if (getLifeCycleAdapter() != null)
      {
         adapter = (IAeInvokeActivityAdapter) getLifeCycleAdapter().getImplAdapter(IAeInvokeActivityAdapter.class);
      }
      return adapter;
   }

   /**
    * Delegates the work to noMoreChildrenToTerminate() on AeAbstractBpelObject
    * @throws AeBusinessProcessException
    */
   protected void terminationComplete() throws AeBusinessProcessException
   {
      noMoreChildrenToTerminate();
   }
   
   /**
    * Create context with this
    */
   private AeActivityLifeCycleContext createContext()
   {
      return new AeActivityLifeCycleContext(this);
   }
}
