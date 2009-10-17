// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeBpelState.java,v 1.19 2006/11/08 23:06:07 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;


/**
 * Models the state of a bpel object. A Bpel object must be in one of the following
 * states: INACTIVE, READY_TO_EXECUTE, EXECUTING, FINISHED, FAULTED, DEAD_PATH, QUEUED_BY_PARENT
 */
public class AeBpelState
{
   /** constant for allowable state transition */
   public static final int OK = 0;
   /** constant for a transition that indicates a small amount of evil in the code */
   public static final int WARN = 1;
   /** constant for a transition that indicates a large amount of evil in the code */
   public static final int ERROR = 2;
   
   /** models the allowable state transitions */
   private static final int[][] ALLOWABLE_STATE_CHANGES =
   {
      // ------------------------------------------------------------------------------------------------------
      // |            |             Transitioning to...                                                       |
      // |            |---------------------------------------------------------------------------------------|
      // | From State | Unknown, Ready, Executing, Finished, Faulted, Dead Path, Queued, Terminated, Faulting |
      // -----------------------------------------------------------------------------------------------------
      /*  Inactive  */  {OK,     ERROR, ERROR,     ERROR,    OK,      OK,        OK,      WARN,       ERROR   },
      /*  Ready     */  {OK,     WARN,  OK,        ERROR,    OK,      OK,        ERROR,   WARN,       ERROR   },
      /*  Executing */  {OK,     ERROR, ERROR,     OK,       OK,      ERROR,     ERROR,   OK,         OK      },
      /*  Finished  */  {OK,     ERROR, ERROR,     ERROR,    ERROR,   ERROR,     ERROR,   ERROR,      OK      },
      /*  Faulted   */  {OK,     ERROR, ERROR,     ERROR,    ERROR,   ERROR,     ERROR,   ERROR,      ERROR   },
      /*  Dead Path */  {OK,     ERROR, ERROR,     ERROR,    ERROR,   ERROR,     ERROR,   WARN,       ERROR   },
      /*  Queued    */  {OK,     OK,    ERROR,     ERROR,    OK,      OK,        WARN,    WARN,       ERROR   },
      /*  Terminated*/  {OK,     ERROR, ERROR,     ERROR,    ERROR,   WARN,      ERROR,   WARN,       ERROR   },
      /*  Faulting  */  {OK,     OK,    ERROR,     OK,       OK,      ERROR,     ERROR,   OK,         WARN    }
   };

   /** numeric constant for inactive state */   
   private static final int INACTIVE_CODE = 0;
   /** numeric constant for ready state */   
   private static final int READY_CODE = 1;
   /** numeric constant for executing state */   
   private static final int EXECUTING_CODE = 2;
   /** numeric constant for finished state */   
   private static final int FINISHED_CODE = 3;
   /** numeric constant for faulted state */   
   private static final int FAULTED_CODE = 4;
   /** numeric constant for dead path state */   
   private static final int DEADPATH_CODE = 5;
   /** numeric constant for queued state */   
   private static final int QUEUED_BY_PARENT_CODE = 6;
   /** numeric constant for terminated state */   
   private static final int TERMINATED_CODE = 7;
   /** numeric constant for faulting state */   
   private static final int FAULTING_CODE = 8;
   
   /** All Bpel objects are in the inactive state when the Process starts */
   public static final AeBpelState INACTIVE = new AeBpelState(INACTIVE_CODE, "Inactive", false, false); //$NON-NLS-1$

   /** Describes objects that are ready to execute. These objects have been queued by their parent and their join condition has evaluated to true */
   public static final AeBpelState READY_TO_EXECUTE = new AeBpelState(READY_CODE, "ReadyToExecute", false, false); //$NON-NLS-1$

   /** Describes objects that are currently executing */
   public static final AeBpelState EXECUTING = new AeBpelState(EXECUTING_CODE, "Executing", false, false); //$NON-NLS-1$

   /** Describes objects that have finished executing w/o a fault  */
   public static final AeBpelState FINISHED = new AeBpelState(FINISHED_CODE, "Finished", true, false); //$NON-NLS-1$

   /** Describes objects that have finished executing with a fault  */
   public static final AeBpelState FAULTED = new AeBpelState(FAULTED_CODE, "Faulted", true, true); //$NON-NLS-1$

   /** Describes objects that have been removed from the execution path due to dead path elimination  */
   public static final AeBpelState DEAD_PATH = new AeBpelState(DEADPATH_CODE, "DeadPath", true, true); //$NON-NLS-1$

   /** Describes objects that have been queued for execution by parent */
   public static final AeBpelState QUEUED_BY_PARENT = new AeBpelState(QUEUED_BY_PARENT_CODE, "QueuedByParent", false, false); //$NON-NLS-1$

   /** Describes objects that have been terminated*/
   public static final AeBpelState TERMINATED = new AeBpelState(TERMINATED_CODE, "Terminated", true, true); //$NON-NLS-1$

   /** Describes objects that are in the process of faulting (suspended due to an uncaught fault). */
   public static final AeBpelState FAULTING = new AeBpelState(FAULTING_CODE, "Faulting", false, false); //$NON-NLS-1$

   /** models the propagable state transitions */
   private static final AeBpelState[] PROPAGABLE_STATE_CHANGES =
   {
      // -----------------------------------------------------------------------
      // | If the parent's |                                                   |
      // | state changes   |                                                   |
      // | to...           | Then the children's state changes to...           |
      // -----------------------------------------------------------------------
      /*  Unknown   */        INACTIVE,
      /*  Ready     */        null,       // no propagation here
      /*  Executing */        null, 		// no propagation here
      /*  Finished  */        null,       // no propagation here
      /*  Faulted   */        null,       // no propagation here
      /*  Dead Path */        DEAD_PATH,
      /*  Queued    */        null,       // no propagation here
      /*  Terminated*/        null,       // no propagation here
      /*  Faulting  */        null        // no propagation here
   };

   /** Array of all state objects. */
   private static final AeBpelState[] sStates = new AeBpelState[]
   {
      INACTIVE,
      READY_TO_EXECUTE,
      EXECUTING,
      FINISHED,
      FAULTED,
      DEAD_PATH,
      QUEUED_BY_PARENT,
      TERMINATED,
      FAULTING
   };
   
   /** Maps state names to state objects. Initialized and used by <code>forName</code>. */
   private static Map sStatesMap;

   /**
    * Ensures that the change to from the object's current state to the new state
    * is valid. 
    * @param aObject
    * @param aNewState
    */
   public static void validateStateChange(IAeBpelObject aObject, AeBpelState aNewState)
   {
      AeBpelState currentState = aObject.getState();
      
      int result = ALLOWABLE_STATE_CHANGES[currentState.getCode()][aNewState.getCode()];
      if (result == WARN)
      {
         logInvalidStateChange(aObject, aNewState, currentState);
      }
      else if (result == ERROR)
      {
         logInvalidStateChange(aObject, aNewState, currentState);
      }
   }

   /**
    * Logs the invalid state change to the process log
    * @param aObject
    * @param aNewState
    * @param currentState
    */
   private static void logInvalidStateChange(IAeBpelObject aObject, 
         AeBpelState aNewState, AeBpelState currentState)
   {
      String message = AeMessages.format("AeBpelState.0", //$NON-NLS-1$
                                         new Object[] {
                           aObject.getLocationPath(), currentState, aNewState});

      AeException.logWarning(message);

      // fire an info event so the process has a record of the error
      AeProcessInfoEvent infoEvent = new AeProcessInfoEvent(
            aObject.getProcess().getProcessId(),
            aObject.getLocationPath(),
            IAeProcessInfoEvent.GENERIC_INFO_EVENT,
            null,
            message);
      aObject.getProcess().getEngine().fireInfoEvent(infoEvent);
   }
   
   /** numeric value for state */
   private int mCode;
   /** name of the state */
   private String mName;
   /** true if the state is final, meaning that it will not naturally transition to another state */
   private boolean mFinal;
   /** true if this state causes an activity's outbound links to resolve to false */
   private boolean mLinksBecomeFalse;

   /**
    * Private ctor to prevent external instantiation.
    */
   private AeBpelState(int aCode, String aName, boolean aFinalFlag, boolean aLinksBecomeFalseFlag)
   {
      mCode = aCode;
      mName = aName;
      mFinal = aFinalFlag;
      mLinksBecomeFalse = aLinksBecomeFalseFlag;
   }
   
   /**
    * Returns true if this state can transition to dead path. 
    */
   public boolean canTransitionToDeadPath()
   {
      return OK == ALLOWABLE_STATE_CHANGES[this.getCode()][DEAD_PATH.getCode()];
   }
   
   /**
    * Returns true if the state is one in which an activity's links are set to false
    * when the activity enters it. This is true for faulted, dead path, and
    * terminated states.
    */
   public boolean linksBecomeFalse()
   {
      return mLinksBecomeFalse;
   }
   
   /**
    * Returns true if the state is final, meaning that it will not naturally 
    * transition into another state. These states include: Finished, Faulted,
    * Dead Path, and Terminated. The other states are not considered final since they will
    * likely transition into one of the final states. 
    * 
    * Unknown will go to Ready or Dead Path, 
    * Ready will go to Executing, Faulted, or Dead Path
    * Executing will go into Finished or Faulted 
    */
   public boolean isFinal()
   {
      return mFinal;
   }
   
   /**
    * Getter for the code, used in the lookup for the allowable state transitions
    */
   public int getCode()
   {
      return mCode;
   }
   
   /**
    * Returns the name of the state. Useful for debugging only.
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      return mName;
   }
   
   /**
    * Returns true if the state should be propagated to child objects.  
    */
   public boolean isPropagable()
   {
      return PROPAGABLE_STATE_CHANGES[getCode()] != null;
   }
   
   /**
    * Returns the state object that we're going to propagate. This is not necessarily
    * the same state as was set on the parent object. For example, if a parent
    * object enters the executing state, then its child objects should be 
    * set to the inactive since they may have already executed once. 
    */
   public AeBpelState getStateForPropagation()
   {
      return PROPAGABLE_STATE_CHANGES[getCode()];
   }
   
   /**
    * Propagates the objects state change to its children.
    * @param aObject
    */
   public void propagate(IAeBpelObject aObject) throws AeBusinessProcessException
   {
      AeBpelState stateForPropagation = aObject.getState().getStateForPropagation();

      for(Iterator it = aObject.getChildrenForStateChange(); it.hasNext();)
      {
         IAeBpelObject child = (IAeBpelObject) it.next();
         child.setState(stateForPropagation);
      }
   }

   /**
    * Reports whether an object in this state can be terminated. Final states cannot
    * be terminated. Neither can inactive and finished sans links. 
    */
   public boolean isTerminatable()
   {
      return !isFinal() && (this != AeBpelState.INACTIVE);
   }
   
   /**
    * Returns this state's name.
    */
   private String getName()
   {
      return mName;
   }

   /**
    * Returns map of state names to state objects.
    */
   private static Map getStatesMap()
   {
      // First time through, construct and populate the map.
      if (sStatesMap == null)
      {
         Map map = new HashMap();
         
         for (int i = sStates.length; --i >= 0; )
         {
            AeBpelState state = sStates[i];
            map.put(state.getName(), state);
         }

         sStatesMap = map;
      }

      return sStatesMap;
   }

   /**
    * Returns the instance of <code>AeBpelState</code> whose name is <code>aName</code>.
    *
    * @param aName the state's name.
    * @return AeBpelState the state whose name is <code>aName</code>.
    */   
   public static AeBpelState forName(String aName) throws AeBusinessProcessException
   {
      if (aName == null)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeBpelState.ERROR_14")); //$NON-NLS-1$
      }

      AeBpelState state = (AeBpelState) getStatesMap().get(aName);

      if (state == null)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeBpelState.ERROR_15")); //$NON-NLS-1$
      }
      
      return state;
   }
}
