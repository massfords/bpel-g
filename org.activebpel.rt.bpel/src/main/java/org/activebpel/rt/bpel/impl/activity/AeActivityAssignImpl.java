// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityAssignImpl.java,v 1.61 2007/11/21 03:22:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity; 

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;
import org.activebpel.rt.bpel.def.activity.AeActivityAssignDef;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeFaultFactory;
import org.activebpel.rt.bpel.impl.AeProcessInfoEvent;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.activity.assign.AeAtomicCopyOperationContext;
import org.activebpel.rt.bpel.impl.activity.assign.IAeAssignOperation;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;

/**
 * Implementation of the BPEL assign activity.
 */
public class AeActivityAssignImpl extends AeActivityImpl
{
   /** list of copy operations to get executed */
   private List mCopyOperations = new LinkedList();
   
   /** Copy operation context used by assign activity */
   private IAeCopyOperationContext mCopyOperationContext;
   
   /**
    * Ctor accepts the def and parent
    * 
    * @param aAssign
    * @param aParent
    */
   public AeActivityAssignImpl(AeActivityAssignDef aAssign, IAeActivityParent aParent)
   {
      super(aAssign, aParent);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept(IAeImplVisitor aVisitor) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }
   
   /**
    * Returns a copy operation context for the assign activity.
    */
   public IAeCopyOperationContext getCopyOperationContext()
   {
      if (mCopyOperationContext == null)
         mCopyOperationContext = new AeAtomicCopyOperationContext(this);
      
      return mCopyOperationContext;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      super.execute();
      boolean success = false;
      AeAtomicCopyOperationContext copyContext = (AeAtomicCopyOperationContext)getCopyOperationContext();
      
      try
      {
         executeOperations();
         copyContext.clearRollback();
         success = true;
      }
      catch(Throwable t)
      {
         // Restore data to the initial state and signal a fault
         copyContext.rollback();
         if (t instanceof AeBpelException)
         {
            objectCompletedWithFault(((AeBpelException)t).getFault());
         }
         else
         {
            objectCompletedWithFault(AeFaultFactory.getSystemErrorFault(t));
         }
      }
      
      if (success)
         objectCompleted();
   }

   /**
    * Executes all of the copy operations as well as any extensible operations
    * in the order that they appeared in the def. If there are any errors during
    * the execution then we'll throw and the assign will rollback any modified 
    * variables.
    * 
    * @throws AeBusinessProcessException
    */
   protected void executeOperations() throws AeBusinessProcessException
   {
      int index = 0;
      try
      {
         for (Iterator iter = getCopyOperations().iterator(); iter.hasNext(); index++)
         {
            IAeAssignOperation operation = (IAeAssignOperation) iter.next();
            operation.execute();
         }
      }
      catch(Throwable t)
      {
         // Log info error message to give user clue as to which operation failed.
         // Note we are sending the index of the copy operation which is translated in the msg formatter
         AeProcessInfoEvent evt = new AeProcessInfoEvent(getProcess().getProcessId(),
                                                 getLocationPath(),
                                                 IAeProcessInfoEvent.ERROR_ASSIGN_ACTIVITY,
                                                 "", //$NON-NLS-1$
                                                 Integer.toString(index));
         getProcess().getEngine().fireInfoEvent(evt);
         
         if (t instanceof AeBusinessProcessException)
            throw (AeBusinessProcessException)t;
         else
            throw new AeBusinessProcessException(t.getMessage(), t);
      }
   }
   
   /**
    * Adds the copy operation to our list
    * 
    * @param aCopyOperation
    */
   public void addCopyOperation(IAeAssignOperation aCopyOperation)
   {
      getCopyOperations().add(aCopyOperation);
   }

   /**
    * @return Returns the copyOperations.
    */
   protected List getCopyOperations()
   {
      return mCopyOperations;
   }

   /**
    * @param aCopyOperations The copyOperations to set.
    */
   protected void setCopyOperations(List aCopyOperations)
   {
      mCopyOperations = aCopyOperations;
   }
}