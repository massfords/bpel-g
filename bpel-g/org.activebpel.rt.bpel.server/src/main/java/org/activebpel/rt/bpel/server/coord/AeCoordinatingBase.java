//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/AeCoordinatingBase.java,v 1.3 2008/03/28 01:43:51 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.IAeBusinessProcessEngine;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.coord.AeCoordinationException;
import org.activebpel.rt.bpel.coord.AeCoordinationFaultException;
import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.coord.IAeCoordinationContext;
import org.activebpel.rt.bpel.coord.IAeProtocolMessage;
import org.activebpel.rt.bpel.coord.IAeProtocolState;
import org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;

/**
 * Base class for any object that is participating in a coordinated activity.
 * This class provides a basic framework for managing protocol signals and state transitions.
 */
public abstract class AeCoordinatingBase implements IAeCoordinating
{
   /**
    * The coordination manager. 
    */
   private IAeCoordinationManagerInternal mCoordinationManager = null;
   
   /**
    * Current state.
    */
   private IAeProtocolState mState = null;
   
   /**
    * Look up table for state transitions.
    */
   private IAeProtocolStateTable mStateTable = null;
   
   /**
    * Process id of the activity participating in the coordination.
    */
   private long mProcessId = 0;
   
   /**
    * Location path.
    */
   private String mLocationPath;
   
   /** Coordiantion id */
   private String mCoordinationId = null;
   
   /** Coordination context. */
   private IAeCoordinationContext mContext = null;

   /**
    * Base constructor given the context and the manager.
    */
   public AeCoordinatingBase(IAeCoordinationContext aContext, IAeCoordinationManagerInternal aCoordinationManager)
   {
      mCoordinationManager = aCoordinationManager;
      mContext = aContext;
      setCoordinationId(mContext.getIdentifier());
      setLocationPath(aContext.getProperty(IAeCoordinating.AE_COORD_LOCATION_PATH));
      setProcessId(aContext.getProperty(IAeCoordinating.AE_COORD_PID));      
   }   
   
   /***
    * Convenience method that returns the engine instance.
    * @return engine instance.
    */
   protected IAeBusinessProcessEngine getEngine()
   {
      return AeEngineFactory.getEngine();
   }
   
   /**
    * @return the coordination manager instance.
    */
   protected IAeCoordinationManagerInternal getCoordinationManager()
   {
      return mCoordinationManager;
   }
   
   /**
    * Returns the transition table. If one has not been initialized, this method calls
    * createProtocolTable method to create one.
    * 
    * @return protocol table.
    */
   protected IAeProtocolStateTable getTable()
   {
      if (mStateTable == null)
      {
         mStateTable = createProtocolTable();
      }
      return mStateTable;
   }
   
   /**
    * Creates a protocol state table. The subclasses (protocol specific) are responsible for returning
    * a table.
    * @return protocol table. 
    */
   protected abstract IAeProtocolStateTable createProtocolTable();

   /**
    * Returns the current state. 
    * @see org.activebpel.rt.bpel.coord.IAeCoordinating#getState()
    */
   public IAeProtocolState getState()
   {
      if (mState == null)
      {
         mState = getTable().getInitialState();
      }
      return mState;
   }
   
   /**
    * Sets the current state.
    * @param aState current state.
    */
   public void setState(IAeProtocolState aState) throws AeCoordinationException
   {
      if (aState != null)
      {
         mState = aState;
      }
      else
      {
         // illegal state change! throw?
         throw new AeCoordinationFaultException(AeCoordinationFaultException.INVALID_STATE);
      }      
   }
   
   /** 
    * Dispatches the protocol message to either the coordinator or the participant.
    * i.e. delivers the message to the target's objects onMessage(...) method.
    * @param aMessage
    * @param aViaProcessExeQueue
    */
   protected void dispatchMessage(IAeProtocolMessage aMessage, boolean aViaProcessExeQueue)
   {      
      // dispatch message to coordinator or participant.
      getCoordinationManager().dispatch(aMessage, aViaProcessExeQueue);
   }
   
   /**
    * Moves the coordination object to the next state based on the message. This
    * affects the in-memory state only, it does not persist it to the db.
    * @param aMessage
    * @throws AeCoordinationException
    */
   protected void changeStateNoPersist(IAeProtocolMessage aMessage)
         throws AeCoordinationException
   {
      // get the current state
      IAeProtocolState currState = getState();
      // determine the next state that should be transitioned to for the sent/received message.
      IAeProtocolState nextState = getTable().getNextState(currState, aMessage);
      // set the next state
      setState(nextState);
   }   
   
   /**
    * Returns true if the message is a valid message to be dispatched in the current state.
    * @param aMessage
    * @return true if the message is valid for the given state.
    */
   protected abstract boolean canDispatch(IAeProtocolMessage aMessage);


   /**
    * Handles protocol messages and transitions to the appropriate state.
    * @param aMessage protocol message.
    */
   public abstract void queueReceiveMessage(IAeProtocolMessage aMessage) throws AeCoordinationException;
       
   /**
    * Callback when a process is complete.
    * 
    * @param aFaultObject fault object if the process completed with a fault.
    * @param aNormalCompletion indiciates that the process completed normally and is eligible fo compensation. 
    */   
   public abstract void onProcessComplete(IAeFault aFaultObject, boolean aNormalCompletion);
   
   /**
    * @see org.activebpel.rt.bpel.coord.IAeCoordinating#getCoordinationContext()
    */
   public IAeCoordinationContext getCoordinationContext()
   {
      return mContext;
   }  
   
   /**
    * @return Returns the coordinationId.
    */
   public String getCoordinationId()
   {
      return mCoordinationId;
   }
   
   /**
    * @param aCoordinationId The coordinationId to set.
    */
   public void setCoordinationId(String aCoordinationId)
   {
      mCoordinationId = aCoordinationId;
   }
   
   /**
    * @return Returns the locationPath.
    */
   public String getLocationPath()
   {
      return mLocationPath;
   }
   
   /**
    * @param aLocationPath The locationPath to set.
    */
   public void setLocationPath(String aLocationPath)
   {
      mLocationPath = aLocationPath;
   }
   
   /**
    * @return Returns the processId.
    */
   public long getProcessId()
   {
      return mProcessId;
   }
   
   /**
    * @param aProcessId The processId to set.
    */
   public void setProcessId(long aProcessId)
   {
      mProcessId = aProcessId;
   }
   
   /**
    * Sets the process id.
    * @param aPidStr
    */
   protected void setProcessId(String aPidStr)
   {
      if (AeUtil.notNullOrEmpty(aPidStr))
      {
         try
         {
            setProcessId(Long.parseLong(aPidStr));
         }
         catch (Exception e)
         {
            //ignore
            AeException.logError(e, e.getMessage());
         }
      }      
   }
}
