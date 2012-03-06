// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeExecutionQueue.java,v 1.26 2008/03/28 01:41:50 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.activity.IAeMessageDispatcher;
import org.activebpel.rt.message.IAeMessageData;

import java.util.*;

/**
 * Used in conjunction with the process to ensure that we're only executing a
 * single object at a time. Simply add objects to the queue to have them get
 * executed.
 */
public class AeExecutionQueue
{
   // Note: Access to mExecutionQueue was previously synchronized but this is
   //       unnecessary now since there will only ever be a single thread in the
   //       process at any given time.

   /** Holds the objects that we want to execute. */
   private final LinkedList<IAeExecutableQueueItem> mExecutionQueue;
   /** flag for whether execution is suspended */
   private boolean mSuspended = false;
   /** Number of pending calls to <code>execute</code>. */
   private int mExecuteDepth = 0;
   /** Number of pending calls to <code>executeObject</code>. */
   private int mExecuteObjectDepth = 0;
   /** The owner process. */
   private IAeBusinessProcessInternal mProcess;
   /** Location paths to be resumed. */
   private final Set<String> mPendingResumePaths = new HashSet<String>();

   /**
    * Constructs empty queue.
    *
    * @param aProcess
    */
   public AeExecutionQueue(IAeBusinessProcessInternal aProcess)
   {
      this(aProcess, false, Collections.<IAeExecutableQueueItem>emptyList());
   }

   /**
    * Construct execution queue from a specific list of objects.
    *
    * @param aProcess the owner process
    * @param aSuspended flag for whether execution is suspended
    * @param aQueue initial queue of objects
    */
   public AeExecutionQueue(IAeBusinessProcessInternal aProcess, boolean aSuspended, List<IAeExecutableQueueItem> aQueue)
   {
      mProcess = aProcess;
      mSuspended = aSuspended;
      mExecutionQueue = new LinkedList<IAeExecutableQueueItem>(aQueue);
   }

   /**
    * Adds the object to the execution queue. If the queue is already executing
    * then the object will be executed when its turn comes. If the queue
    * isn't being executed, then adding an object for execution will immediately
    * start the execution process.
    * @param aObject
    */
   protected void add(IAeExecutableQueueItem aObject) throws AeBusinessProcessException
   {
      // if this is being called as a result of a retry - remove the
      // 'old' object from the execution queue
      mExecutionQueue.remove( aObject );

      // If we're suspended and tried to resume this object earlier, then
      // execute it now.
      if (isSuspended() && mPendingResumePaths.contains(aObject.getLocationPath()))
      {
         mPendingResumePaths.remove(aObject.getLocationPath());
         executeObject(aObject);
      }
      // Otherwise, add the object to the queue.
      else
      {
         mExecutionQueue.add(aObject);

         if (!isExecuting())
         {
            // call execute to kick off the execution process
            execute();
         }
      }
   }

   /**
    * Returns true if the queue is currently being executed. Called from the add
    * method to see if the queue is being executed. If not, then the add process
    * will start the queue executing.
    */
   private boolean isExecuting()
   {
      return mExecuteDepth > 0;
   }

   /**
    * Pops an object off the queue and executes it. Once called, this method 
    * will run until there are no more objects to execute.
    */
   protected void execute() throws AeBusinessProcessException
   {
      // Increment the number of outstanding execute() calls to divert future
      // calls in add().
      ++mExecuteDepth;

      try
      {
         while (!isSuspended() && hasObjectsToExecute())
         {
            // pull the next object for execution
            executeObject(getNextObjectToExecute());
         }
      }
      finally
      {
         // Always remember to decrement the number of outstanding
         // execute() calls.
         --mExecuteDepth;
      }
   }

   /**
    * Returns true if there are objects in the queue to execute
    */
   private boolean hasObjectsToExecute()
   {
      return !mExecutionQueue.isEmpty();
   }

   /**
    * Gets the next object from the queue to execute.
    */
   private IAeExecutableQueueItem getNextObjectToExecute()
   {
      return mExecutionQueue.removeFirst();
   }

   /**
    * Calls the object's execute method.
    * @param aExecutable
    */
   private void executeObject(IAeExecutableQueueItem aExecutable)
      throws AeBusinessProcessException
   {
      // Increment the number of outstanding executeObject() calls for
      // isQuiescent() test.
      ++mExecuteObjectDepth;

      try
      {
         // checking the state here because the process could have been 
         // terminated while suspended and the object in question wouldn't need 
         // to execute.
         if (aExecutable.getState() == AeBpelState.READY_TO_EXECUTE)
         {
            aExecutable.setState(AeBpelState.EXECUTING);
            try
            {
               aExecutable.execute();
            }
            catch(Throwable ex)
            {
               AeException.logError(ex, AeMessages.format("AeExecutionQueue.ERROR_0", getProcess().getProcessId())); //$NON-NLS-1$

               // Special exception handling needed in order for loop activities that could themselves reexecute through the
               // completion of their child activity. The issue is that the parent activity (while, repeatUntil, forEach) could
               // rexecute as a result of its child completed all on the same call stack. This execution could trigger a fault
               // at which point the aExecutable from above is NOT the activity that should receive the fault.
               
               // fixme (MF-engine) Come back and rework this. Ideally the loop activities would requeue themselves through the engine
               if (aExecutable.getState() != AeBpelState.EXECUTING && aExecutable instanceof AeAbstractBpelObject)
               {
                  AeAbstractBpelObject bo = (AeAbstractBpelObject) aExecutable;
                  getProcess().handleExecutableItemException(bo.getParent(), ex);
               }
               else
               {
                  getProcess().handleExecutableItemException(aExecutable, ex);
               }
            }
         }
      }
      finally
      {
         // Always remember to decrement the number of outstanding
         // executeObject() calls.
         --mExecuteObjectDepth;
      }
   }

   /**
    * Returns <code>true</code> if and only if this execution queue is quiescent.
    */
   private boolean isQuiescent()
   {
      // This execution queue is quiescent if there are no pending calls to
      // executeObject() AND the queue is empty or suspended.
      return (mExecuteObjectDepth == 0) && (mExecutionQueue.isEmpty() || isSuspended());
   }

   /**
    * Returns <code>true</code> if and only if this execution queue is suspended.
    */
   private boolean isSuspended()
   {
      return mSuspended;
   }

   /**
    * Returns the location paths of the objects in this queue in the same order 
    * as the queue.
    */
   public List<String> getLocationPaths()
   {
      List<String> locationPaths = new LinkedList<String>();

       for (IAeExecutableQueueItem ex : mExecutionQueue) {
           String locationPath = ex.getLocationPath();
           locationPaths.add(locationPath);
       }

      return locationPaths;
   }

   /**
    * Add the faulting object to the execution queue and resume its execution.
    * @param aFaultedObject
    * @param aUncaughtFault
    * @throws AeBusinessProcessException
    */
   public void addFaultingObject( AeAbstractBpelObject aFaultedObject, IAeFault aUncaughtFault )
   throws AeBusinessProcessException
   {
      add( new AeResumeFaultedProcessToTerminationStub(aFaultedObject, aUncaughtFault) );
   }

   /**
    * Add the object to be completed to the execution queue and complete it.
    * @param aObjectToComplete
    * @throws AeBusinessProcessException
    */
   public void addCompletedObject( AeAbstractBpelObject aObjectToComplete )
   throws AeBusinessProcessException
   {
      mExecutionQueue.remove( aObjectToComplete );
      add( new AeCompleteObjectStub(aObjectToComplete) );
   }

   /**
    * Resumes execution of objects in the queue.
    *
    * @param aExecute <code>true</code> to execute immediately or
    * <code>false</code> to put the queue in a running state without executing
    * immediately.
    */
   public void resume(boolean aExecute) throws AeBusinessProcessException
   {
      mSuspended = false;
      mPendingResumePaths.clear();

      if (aExecute)
      {
         execute();
      }
   }

   /**
    * Checks the suspended execution queue and resumes the specified object
    * if it is in the queue.
    */
   public void resume(String aLocationPath) throws AeBusinessProcessException
   {
      if (aLocationPath != null)
      {
         boolean found = false;
         IAeExecutableQueueItem object = null;

         // Scan the execution queue for the specified location path.
         for (Iterator<IAeExecutableQueueItem> i = mExecutionQueue.iterator(); i.hasNext() && !found; )
         {
            object = i.next();
            found = aLocationPath.equals(object.getLocationPath());
         }

         if (found)
         {
            // Remove the object from the queue, and execute it now.
            mExecutionQueue.remove(object);
            executeObject(object);
         }
         else
         {
            // Execute the object later when it is queued.
            mPendingResumePaths.add(aLocationPath);
         }
      }
   }

   /**
    * Marks this execution queue as suspended.
    */
   public void suspend()
   {
      mSuspended = true;
   }

   /**
    * Dispatches a runnable object.
    * @param aRunnable
    * @throws AeBusinessProcessException
    */
   public void dispatchRunnable(Runnable aRunnable) throws AeBusinessProcessException
   {
      // Perform this action via executeObject().
      AeRunnableObjectStub stub = new AeRunnableObjectStub(aRunnable);
      executeObject( stub );
   }

   /**
    * Adds a runnable object to the execution queue.
    * @param aRunnable
    * @throws AeBusinessProcessException
    */
   public void addRunnable(Runnable aRunnable) throws AeBusinessProcessException
   {
      // Perform this action via executeObject().
      add( new AeRunnableObjectStub(aRunnable) );
   }

   /**
    * Dispatches an alarm to its receiver.
    *
    * @param aReceiver
    * @throws AeBusinessProcessException
    */
   public void dispatchAlarm(final IAeAlarmReceiver aReceiver, final int aAlarmId) throws AeBusinessProcessException
   {
      // Perform this action via executeObject().
      executeObject(
         new AeExecutableObjectStub()
         {
            public void execute() throws AeBusinessProcessException
            {               
               // fire alarm (call back OnAlarm) iff alarm ids match or if alarm 
               // id is zero (legacy support for alarms with unassigned ids)
               if (aAlarmId == 0 || aAlarmId == aReceiver.getAlarmId())
               {
                  aReceiver.setAlarmId(-1);
                  aReceiver.onAlarm();                  
               }
            }

            protected IAeBpelObject getFaultObject()
            {
               return (IAeBpelObject) aReceiver;
            }
         });
   }

   /**
    * Dispatches message data to a queued invoke.
    *
    * @param aReceiver
    * @param aData
    * @throws AeBusinessProcessException
    */
   public void dispatchInvokeData(final IAeMessageReceiver aReceiver, final IAeMessageData aData) throws AeBusinessProcessException
   {
      // Perform this action via executeObject().
      executeObject(
         new AeExecutableObjectStub()
         {
            public void execute() throws AeBusinessProcessException
            {
               // fixme (PJ) check unmatched invoke here (if possible) instead of in AeBusinessProcessEngine::queueInvokeData()
               aReceiver.onMessage(aData);
            }

            protected IAeBpelObject getFaultObject()
            {
               return (IAeBpelObject) aReceiver;
            }
         });
   }

   /**
    * Dispatches a fault to a queued invoke.
    *
    * @param aReceiver
    * @param aFault
    * @throws AeBusinessProcessException
    */
   public void dispatchInvokeFault(final IAeMessageReceiver aReceiver, final IAeFault aFault) throws AeBusinessProcessException
   {
      // Perform this action via executeObject().
      executeObject(
         new AeExecutableObjectStub()
         {
            public void execute() throws AeBusinessProcessException
            {
               aReceiver.onFault(aFault);
            }

            protected IAeBpelObject getFaultObject()
            {
               return (IAeBpelObject) aReceiver;
            }
         });
   }

   /**
    * Dispatches message data to a message receiver.
    *
    * @param aDispatcher
    * @param aData
    * @throws AeBusinessProcessException
    */
   public void dispatchReceiveData(final IAeMessageDispatcher aDispatcher, final IAeMessageData aData) throws AeBusinessProcessException
   {
      // Perform this action via executeObject().
      executeObject(
         new AeExecutableObjectStub()
         {
            public void execute() throws AeBusinessProcessException
            {
               aDispatcher.onMessage(aData);
            }

            protected IAeBpelObject getFaultObject()
            {
               return aDispatcher.getTarget();
            }
         });
   }

   /**
    * Returns owner process.
    */
   protected IAeBusinessProcessInternal getProcess()
   {
      return mProcess;
   }

   /**
    * Restores the queue's state.
    *
    * @param aSuspended the suspended state to set.
    * @param aQueue the list of executable objects to set for the queue.
    * @throws AeBusinessProcessException
    */
   public void setQueueData(boolean aSuspended, List<IAeExecutableQueueItem> aQueue) throws AeBusinessProcessException
   {
      if (!isQuiescent())
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeExecutionQueue.ERROR_2")); //$NON-NLS-1$
      }

      mSuspended = aSuspended;

      mExecutionQueue.clear();
      mExecutionQueue.addAll(aQueue);
   }

   /**
    * Base class for stub objects that can be executed by <code>executeObject</code>.
    */
   protected abstract static class AeExecutableObjectStub implements IAeExecutableQueueItem
   {
      protected abstract IAeBpelObject getFaultObject();

      /**
       * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#getLocationPath()
       */
      public String getLocationPath()
      {
         throw new UnsupportedOperationException(AeMessages.getString("AeExecutionQueue.ERROR_1")); //$NON-NLS-1$
      }

      /**
       * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#getState()
       */
      public AeBpelState getState()
      {
         // Always ready to execute.
         return AeBpelState.READY_TO_EXECUTE;
      }

      /**
       * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#objectCompletedWithFault(org.activebpel.rt.bpel.IAeFault)
       */
      public void objectCompletedWithFault(IAeFault fault) throws AeBusinessProcessException
      {
         // Report fault to process.
         getFaultObject().getProcess().objectCompletedWithFault(getFaultObject(), fault);
      }

      /**
       * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#setState(org.activebpel.rt.bpel.impl.AeBpelState)
       */
      public void setState(AeBpelState aState)
      {
         // Nothing to do.
      }
   }

   /**
    * A executable queue object that accepts a Runnable.
    */
   public class AeRunnableObjectStub extends AeExecutableObjectStub
   {
      /** the Runnable that will be executed by the queue */
      private Runnable mRunnable;

      /**
       * Constructor.
       * @param aRunnable
       */
      public AeRunnableObjectStub( Runnable aRunnable )
      {
         mRunnable = aRunnable;
      }

      /**
       * Overrides method to
       * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#execute()
       */
      public void execute() throws AeBusinessProcessException
      {
         mRunnable.run();
      }
      /**
       * Returns null be default.
       * @see org.activebpel.rt.bpel.impl.AeExecutionQueue.AeExecutableObjectStub#getFaultObject()
       */
      protected IAeBpelObject getFaultObject()
      {
         return null;
      }

   }

   /**
    * Base stub class for resuming objects in a faulting state.
    */
   private abstract class AeBaseResumeObjectStub extends AeExecutableObjectStub
   {
      /** the object that generated the faulting condition */
      private AeAbstractBpelObject mBpelObject;

      /**
       * Constructor.
       * @param aBpelObject
       */
      protected AeBaseResumeObjectStub( AeAbstractBpelObject aBpelObject )
      {
         mBpelObject = aBpelObject;
      }

      /**
       * Returns null be default.
       * @see org.activebpel.rt.bpel.impl.AeExecutionQueue.AeExecutableObjectStub#getFaultObject()
       */
      protected IAeBpelObject getFaultObject()
      {
         return null;
      }

      /**
       * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#getLocationPath()
       */
      public String getLocationPath()
      {
         return getBpelObject().getLocationPath();
      }

      /**
       * Getter for the <code>AeAbstractBpelObject</code> that generated the
       * faulting condition.
       */
      protected AeAbstractBpelObject getBpelObject()
      {
         return mBpelObject;
      }
   }

   /**
    * An <code>AeExecutableStub</code> subclass that will call 
    * objectCompletedWithFault on its faulting <code>AeAbstractBpelObject</code> 
    * when it is executed.
    */
   private class AeResumeFaultedProcessToTerminationStub extends AeBaseResumeObjectStub
   {
      /** the uncaught fault */
      private IAeFault mUncaughtFault;

      /**
       * Constructor.
       * @param aBpelObject
       * @param aFault
       */
      public AeResumeFaultedProcessToTerminationStub( AeAbstractBpelObject aBpelObject, IAeFault aFault )
      {
         super( aBpelObject );
         mUncaughtFault = aFault;
      }

      /**
       * @see org.activebpel.rt.bpel.impl.AeExecutionQueue.AeExecutableObjectStub#getFaultObject()
       */
      protected IAeBpelObject getFaultObject()
      {
         return getBpelObject();
      }

      /**
       * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#execute()
       */
      public void execute() throws AeBusinessProcessException
      {
         getBpelObject().exceptionManagementResumeUncaughtFault(mUncaughtFault);
      }
   }

   /**
    * An <code>AeExecutableStub</code> subclass that will call objectCompleted
    * on its faulting <code>AeAbstractBpelObject</code> when it is executed.
    */
   private class AeCompleteObjectStub extends AeBaseResumeObjectStub
   {
      /**
       * Constructor.
       * @param aBpelObject
       */
      public AeCompleteObjectStub( AeAbstractBpelObject aBpelObject )
      {
         super( aBpelObject );
      }

      /**
       * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#execute()
       */
      public void execute() throws AeBusinessProcessException
      {  
         getBpelObject().exceptionManagementCompleteActivity();
      }
   }
}
