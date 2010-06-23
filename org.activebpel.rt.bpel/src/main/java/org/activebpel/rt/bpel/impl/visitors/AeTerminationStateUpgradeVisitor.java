//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/visitors/AeTerminationStateUpgradeVisitor.java,v 1.5.4.1 2008/04/21 16:09:43 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.visitors; 

import java.util.Iterator;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.AeBusinessProcess;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.activity.AeActivityForEachImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityForEachParallelImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityWhileImpl;
import org.activebpel.rt.bpel.impl.activity.AeLoopActivity;
import org.activebpel.rt.bpel.impl.activity.support.IAeLoopActivity;

/**
 * This visitor applies some changes to the process state in order to update the
 * state of the activities to make them consistent with changes made to the termination
 * routine.
 * 
 * Only needs to be run on processes that are still executing. This includes processes
 * that are being compensated.
 * 
 * The following changes in the engine made this visitor necessary:
 * <table>
 * <tr>
 * 
 * <!--          serial vs parallel termination             -->
 * 
 * <th>Description of state change:</th>   <td>Child activites used to be terminated in serial, this now 
 *                                             happens in parallel. The old code would terminate children 
 *                                             one at a time and upon receiving a <code>childTerminated()</code> 
 *                                             callback the next eligible child would be terminated.</td>
 * <th>How this visitor handles this:</th> <td>If an impl's terminating flag is true (<code>aImpl.isTerminating()</code>)
 *                                             then walk all of its eligible children for termination and if one is 
 *                                             found that is not currently terminating then clear the terminating 
 *                                             flag on the impl. The result will be that code in the standard 
 *                                             callback method <code>childTerminated</code> will see that the
 *                                             there was a child terminated w/o this flag being set on itself (the parent)
 *                                             and then call <code>startTermination</code> which will cause any 
 *                                             remaining children to be terminated.</td>
 *    
 * <!--          exiting flag on process                    -->
 * 
 * <th>Description of state change:</th>   <td>AeBusinessProcess has a new field for indicating if the process is exiting.
 *                                             A process will exit as the result of adminsitrative termination or 
 *                                             through the execution of an exit activity. In these cases, there is
 *                                             no fault handling, termination handling, or compensation. The old code
 *                                             used getProcess().isTerminating() to check to see if these FCT handlers
 *                                             should execute. The issue is that it became confusing when dealing with
 *                                             this terminating flag as a special case for the process. The change was
 *                                             to add a new exiting flag on the process.</td>
 * <th>How this visitor handles this:</th> <td>If the process has its terminating flag set then set its exiting flag</td>
 *    
 * <!--          suspend flag on uncaught fault            -->
 * 
 * <th>Description of state change:</th>   <td>An uncaught fault that caused an activity to enter the faulting state was
 *                                             having its suspendable flag set to false as soon as the activity entered 
 *                                             the faulting state. The new code delays setting this flag on the fault until
 *                                             it is determined that the fault will be propagated. This solved a subtle issue
 *                                             introduced by the termination refactoring.</td>
 * <th>How this visitor handles this:</th> <td>If a faulting activity has a fault (which it should) then set the fault's 
 *                                             suspendable flag to true.</td>
 *    
 * <!--          early termination behavior                -->
 * 
 * <th>Description of state change:</th>   <td>An loop container may terminate early due to a continue or break activity
 *                                             within it executing. In this case, the loop would record the reason for the
 *                                             termination and then begin terminating its children. Upon completion of the
 *                                             termination process, the loop would either continue or break depending on the
 *                                             cause of the termination. The old code relied on the absence of the terminating
 *                                             flag or fault to indicate that the activity terminated due to a loop control.
 *                                             The new code makes this explicit by adding an internal fault for early termination.
 *                                             </td>
 * <th>How this visitor handles this:</th> <td>If a loop control (forEach or while) is executing with an early termination value
 *                                             then the special early termination fault will be installed on the activity.</td>
 *                                             
 * </tr>
 * </table> 
 * 
 */
public class AeTerminationStateUpgradeVisitor extends AeImplTraversingVisitor
{
   /**
    *  ctor
    */
   public AeTerminationStateUpgradeVisitor()
   {
      super();
   }
   
   /**
    * Traverses the impl in order to process any of its child activities. Also applies any checks that apply to all of the impls.
    * 
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visitBase(org.activebpel.rt.bpel.impl.AeAbstractBpelObject)
    */
   protected void visitBase(AeAbstractBpelObject aImpl) throws AeBusinessProcessException
   {
      if (isExecutingOrFaulting(aImpl))
      {
         // no point traversing this node if it's not executing or faulting
         super.visitBase(aImpl);
      }

      if (aImpl.getFault() != null && aImpl.getState() == AeBpelState.FAULTING && !aImpl.getFault().isSuspendable())
      {
         // if the object is faulting, then change its fault to allow it to remain suspendable. This flag used to
         // get set immediately once the object entered the faulting state but we now leave it as suspendable until
         // the admin resumes the activity and allows the fault to propagate. In that case, the fault will no
         // longer cause any suspension and the activity will be terminated and propagate the fault to its parent.
         aImpl.getFault().setSuspendable(true);
      }
      
      if (aImpl.isTerminating())
      {
         // make sure that all of its children are either terminating or in a final state
         // if they're not, then clear the terminating flag for the impl. 
         // The absence of a terminating flag on a parent that receives a childTerminated() call
         // will signal the engine to start termination on the node in order to set all of the 
         // children terminating.
         for (Iterator iter = aImpl.getChildrenForTermination(); iter.hasNext();)
         {
            IAeBpelObject child = (IAeBpelObject) iter.next();
            if (!child.isTerminating() && !child.getState().isFinal())
            {
               aImpl.setTerminating(false);
               break;
            }
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visit(org.activebpel.rt.bpel.impl.AeBusinessProcess)
    */
   public void visit(AeBusinessProcess aImpl) throws AeBusinessProcessException
   {
      if (aImpl.isTerminating())
      {
         // introduced a new exiting flag on the process to differentiate the process's exit call from normal termination
         // of the process activities due to fault handling
         aImpl.setFault(null);
         aImpl.setExiting(true);
      }

      visit((AeActivityScopeImpl)aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeAbstractImplVisitor#visitScope(org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl)
    */
   protected void visitScope(AeActivityScopeImpl aImpl) throws AeBusinessProcessException
   {
      if (aImpl.isCompensating())
      {
         aImpl.getCompensationHandler().accept(this);
      }
      else
      {
         super.visitScope(aImpl);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityForEachImpl)
    */
   public void visit(AeActivityForEachImpl aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);
      checkForEarlyTermination(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityForEachParallelImpl)
    */
   public void visit(AeActivityForEachParallelImpl aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);
      checkForEarlyTermination(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityWhileImpl)
    */
   public void visit(AeActivityWhileImpl aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);
      checkForEarlyTermination(aImpl);
   }
   
   /**
    * Checks to see if the loop is processing an early termination signal. If it is then we'll set the
    * internal fault on the activity to make this termination explicit.
    * @param aImpl
    */
   protected void checkForEarlyTermination(AeLoopActivity aImpl)
   {
      if (isExecutingOrFaulting(aImpl))
      {
         if (aImpl.getEarlyTerminationReason() == IAeLoopActivity.REASON_BREAK || aImpl.getEarlyTerminationReason() == IAeLoopActivity.REASON_CONTINUE)
         {
            aImpl.setFault(aImpl.getFaultFactory().getEarlyTerminationFault());
         }
      }
   }

   /**
    * Returns true if the impl is executing or faulting 
    * @param aImpl
    */
   protected boolean isExecutingOrFaulting(AeAbstractBpelObject aImpl)
   {
      return aImpl.getState() == AeBpelState.EXECUTING || aImpl.getState() == AeBpelState.FAULTING;
   }
}
 