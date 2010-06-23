//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeCheckStartActivityVisitor.java,v 1.9 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeCatchAllDef;
import org.activebpel.rt.bpel.def.AeCatchDef;
import org.activebpel.rt.bpel.def.AeCompensationHandlerDef;
import org.activebpel.rt.bpel.def.AeCorrelationSetsDef;
import org.activebpel.rt.bpel.def.AeCorrelationsDef;
import org.activebpel.rt.bpel.def.AeEventHandlersDef;
import org.activebpel.rt.bpel.def.AeFaultHandlersDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeScopeDef;
import org.activebpel.rt.bpel.def.AeTerminationHandlerDef;
import org.activebpel.rt.bpel.def.activity.AeActivityAssignDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityEmptyDef;
import org.activebpel.rt.bpel.def.activity.AeActivityExitDef;
import org.activebpel.rt.bpel.def.activity.AeActivityFlowDef;
import org.activebpel.rt.bpel.def.activity.AeActivityForEachDef;
import org.activebpel.rt.bpel.def.activity.AeActivityIfDef;
import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityPickDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReplyDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef;
import org.activebpel.rt.bpel.def.activity.AeActivityThrowDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWaitDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWhileDef;
import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;
import org.activebpel.rt.bpel.def.validation.IAeValidationProblemCodes;
import org.activebpel.rt.bpel.def.validation.IAeValidationProblemReporter;

/**
 * Class which implements a visitor pattern used to validate that the current process
 * conforms to the BPEL constraint regarding Create Instance usage. This contraint stipulates
 * that if a process activity (Pick or Receive) is annotated with the Create Instance property,
 * that no other basic activities precede them. Furthermore, all or none of the activities which
 * start in parallel for the process must be specified as Create Instance.  In addition, 
 * 
 *
 * This visitor differs from our normal visitor pattern in that the traversal direction
 * navigates from a given node towards the top of the model. The intention is to assure that
 * we have no basic activities preceding us, and only containers are allowed as parents.
 * The basic validation is initiated with the list of nodes which are known to be instance
 * creators, and each node is traversed to assure it is the first basic activity. All invalid
 * points which are hit have been equipped with an error reporter to log with the process
 * validation log.
 *
 */
public class AeCheckStartActivityVisitor extends AeAbstractDefVisitor implements IAeDefVisitor
{
   /** The error reporter specified during creation. */
   private IAeValidationProblemReporter mErrorReporter ;
   /** The current activity being processed. */
   private AeActivityDef mCurrentActivity;
   /** Flag indicating current direction of traversal. */
   private boolean mAscending;
   /** Set of defs that are invalid */
   private Set mErrorDefs = new HashSet();

   /**
    * Constructor which requires an error reporter to be used during traversal.
    * @param aErrorReporter The required error reporter
    */
   public AeCheckStartActivityVisitor(IAeValidationProblemReporter aErrorReporter)
   {
      mErrorReporter = aErrorReporter;

      setTraversalVisitor(new AeTraversalVisitor(new AeDefTraverser(), this));
   }

   /**
    * Kicks off the validation process given the list of create instance activities.
    * @param aCreateInstanceActivities the list of activities which have been annotated with Create Instance
    */
   public void doValidation(List aCreateInstanceActivities)
   {
      mErrorDefs.clear();
      for (Iterator iter=aCreateInstanceActivities.iterator(); iter.hasNext();)
      {
         mCurrentActivity = (AeActivityDef)iter.next();
         mAscending = true;
         mCurrentActivity.getParent().accept(this);
      }
      
      for (Iterator iter = mErrorDefs.iterator(); iter.hasNext();)
      {
         AeBaseDef def = (AeBaseDef) iter.next();
         mErrorReporter.reportProblem(IAeValidationProblemCodes.BPEL_CHECK_START_ACTIVITY_CODE,
                                 AeMessages.getString("AeCheckStartActivityVisitor.ERROR_CREATE_INSTANCE_VALIDATION"), //$NON-NLS-1$
                                 new String [] {}, def); 
      }
   }

   /**
    * Override to handle special processing of Scope container.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeScopeDef)
    */
   public void visit(AeScopeDef aDef)
   {
      if (mAscending)
         aDef.getParent().accept(this);
      else
         aDef.getActivityDef().accept(this);
   }

   /**
    * Override to handle special processing of Flow container.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityFlowDef)
    */
   public void visit(AeActivityFlowDef aDef)
   {
      // Loop through all activities contained in this flow
      for (Iterator iter=aDef.getActivityDefs(); iter.hasNext();)
      {
         // If the activity is not a target for any other activity, then it is an
         // initial activity in the flow and must be evaluated.
         AeActivityDef activity = (AeActivityDef)iter.next();
         if (!activity.hasTargets())
         {
            // If the current activity is not the same as the activity we are processing
            // we will need to switch direction and descend into the activity to find it.
            if (activity != mCurrentActivity)
            {
               boolean currMode = mAscending;
               mAscending = false;
               activity.accept(this);
               mAscending = currMode;
            }
         }
      }

      // Only process if ascending
      if (mAscending)
      {
         mCurrentActivity = aDef;
         aDef.getParent().accept(this);
      }
   }

   /**
    * Override to handle special processing of Sequence container.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef)
    */
   public void visit(AeActivitySequenceDef aDef)
   {
      // If no activities in sequence, return since this error is flagged elsewhere
      if (! aDef.getActivityDefs().hasNext())
         return;

      // We only need to process the first activity of the sequence
      AeActivityDef firstActivity = (AeActivityDef)aDef.getActivityDefs().next();

      if (mAscending)
      {
         // If the current activity we are looking for is the first activity or we are dealing with
         // a nested sequence, this activity becomes current activity and we traverse to the parent.
         // Otherwise, this is an error.
         if(mCurrentActivity == firstActivity || firstActivity instanceof AeActivitySequenceDef)
         {
            mCurrentActivity = firstActivity;
            aDef.getParent().accept(this);
         }
         else
         {
            reportError(firstActivity);
         }
      }
      else
      {
         // Descend into the first activity
         firstActivity.accept(this);
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.AeCompensationHandlerDef)
    */
   public void visit(AeCompensationHandlerDef aDef)
   {
      reportError(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.AeTerminationHandlerDef)
    */
   public void visit(AeTerminationHandlerDef aDef)
   {
      reportError(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchAllDef)
    */
   public void visit(AeCatchAllDef aDef)
   {
      reportError(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchDef)
    */
   public void visit(AeCatchDef aDef)
   {
      reportError(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityForEachDef)
    */
   public void visit(AeActivityForEachDef aDef)
   {
      reportError(mCurrentActivity);
   }
   
   /**
    * Override to handle special processing of While container.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWhileDef)
    */
   public void visit(AeActivityWhileDef aDef)
   {
      reportError(mCurrentActivity);
   }

   /**
    * Overrides method to prevent traversal from performing the normal top down navigation.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef)
    */
   public void visit(AeActivityRepeatUntilDef aDef)
   {
      reportError(mCurrentActivity);
   }
   
   /**
    * Overrides method to prevent traversal from performing the normal top down navigation.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
    */
   public void visit(AeActivityScopeDef aDef)
   {
      if (mAscending)
      {
         mCurrentActivity = aDef;
         aDef.getParent().accept(this);
      }
      else if (aDef.getActivityDef() != null)
      {
         aDef.getActivityDef().accept(this);
      }
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityAssignDef)
    */
   public void visit(AeActivityAssignDef aDef)
   {
      reportError(aDef);
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef)
    */
   public void visit(AeActivityCompensateDef aDef)
   {
      reportError(aDef);
   }
   
   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef)
    */
   public void visit(AeActivityCompensateScopeDef aDef)
   {
      reportError(aDef);
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityEmptyDef)
    */
   public void visit(AeActivityEmptyDef aDef)
   {
      reportError(aDef);
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef)
    */
   public void visit(AeActivityInvokeDef aDef)
   {
      reportError(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef)
    */
   public void visit(AeChildExtensionActivityDef aDef)
   {
      reportError(aDef);
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityPickDef)
    */
   public void visit(AeActivityPickDef aDef)
   {
      if (! aDef.isCreateInstance())
         reportError(aDef);
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
    */
   public void visit(AeActivityReceiveDef aDef)
   {
      if (! aDef.isCreateInstance())
         reportError(aDef);
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReplyDef)
    */
   public void visit(AeActivityReplyDef aDef)
   {
      reportError(aDef);
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityIfDef)
    */
   public void visit(AeActivityIfDef aDef)
   {
      reportError(aDef);
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityExitDef)
    */
   public void visit(AeActivityExitDef aDef)
   {
      reportError(aDef);
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityThrowDef)
    */
   public void visit(AeActivityThrowDef aDef)
   {
      reportError(aDef);
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWaitDef)
    */
   public void visit(AeActivityWaitDef aDef)
   {
      reportError(aDef);
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationsDef)
    */
   public void visit(AeCorrelationsDef aDef)
   {
      reportError(aDef);
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetsDef)
    */
   public void visit(AeCorrelationSetsDef aDef)
   {
      reportError(aDef);
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeEventHandlersDef)
    */
   public void visit(AeEventHandlersDef aDef)
   {
      reportError(aDef);
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeFaultHandlersDef)
    */
   public void visit(AeFaultHandlersDef aDef)
   {
      reportError(aDef);
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
    */
   public void visit(AeOnAlarmDef aDef)
   {
      reportError(aDef);
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
    */
   public void visit(AeOnMessageDef aDef)
   {
      reportError(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      visit((AeOnMessageDef) aDef);
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseDef)
    */
   public void visit(AeElseDef aDef)
   {
      reportError(aDef);
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
    */
   public void visit(AeElseIfDef aDef)
   {
      reportError(aDef);
   }

   /**
    * Overrides method to report error during traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeIfDef)
    */
   public void visit(AeIfDef aDef)
   {
      reportError(aDef);
   }

   /**
    * Report error encountered during traversal.
    * @param aDef the activity which the error was detected while processing
    */
   private void reportError(AeBaseDef aDef)
   {
      mErrorDefs.add(aDef);
   }
}
