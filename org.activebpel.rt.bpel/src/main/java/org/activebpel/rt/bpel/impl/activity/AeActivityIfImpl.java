// $Header$
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.activity.AeActivityIfDef;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.activity.support.AeElse;
import org.activebpel.rt.bpel.impl.activity.support.AeElseIf;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;
import org.activebpel.rt.util.AeUtil;

/**
 * Implementation of the bpel 2.0 if activity.
 */
public class AeActivityIfImpl extends AeActivityImpl
{
   /** The list of 'else' children. */
   private List mElseIfs = new ArrayList();
   /** The optional 'else' child. */
   private AeElse mElse;

   /**
    * Constructs the activity if impl.
    *
    * @param aIfDef
    * @param aParent
    */
   public AeActivityIfImpl(AeActivityIfDef aIfDef, IAeActivityParent aParent)
   {
      super(aIfDef, aParent);
   }

   /**
    * Adds an 'elseif' child to the list.
    *
    * @param aElseIf
    */
   public void addElseIf(AeElseIf aElseIf)
   {
      getElseIfs().add(aElseIf);
   }

   /**
    * Sets the optional 'else' child.
    *
    * @param aElse
    */
   public void setElse(AeElse aElse)
   {
      mElse = aElse;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getChildrenForStateChange()
    */
   public Iterator getChildrenForStateChange()
   {
      return AeUtil.join(getElseIfs().iterator(), getElse());
   }

   /**
    * Switch activity checks all cases and executes the applicable case container or default if none are true.
    *
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      super.execute();
      IAeBpelObject child = findTrueClause();

      // note if child is null then calling this will complete execution
      // as all cases will be dead paths
      setAllOtherChildrenToDeadPath(child);

      if (child != null)
      {
         getProcess().queueObjectToExecute(child);
      }
   }

   /**
    * Sets all of the other child objects to dead path except for the one passed in which will be executed.
    *
    * @param aChild can be null
    */
   private void setAllOtherChildrenToDeadPath(IAeBpelObject aChild) throws AeBusinessProcessException
   {
      setAllOtherToDeadPath(aChild, getChildrenForStateChange());
   }

   /**
    * Walks the elseif list looking for a case that evaluates to true. If a case evaluates to false then it
    * becomes a dead path. If it evaluates to true, we'll return it. If none of the cases eval to true, then
    * we'll fall back on the else.  If there is no else, then we'll return null.
    *
    * @return IAeBpelObject a case that eval'd to true, the else, or null if none eval'd to true.
    * @throws AeBusinessProcessException
    */
   private IAeBpelObject findTrueClause() throws AeBusinessProcessException
   {
      IAeBpelObject foundClause = null;
      for (Iterator iter = getElseIfs().iterator(); iter.hasNext();)
      {
         AeElseIf elseIf = (AeElseIf) iter.next();
         if (elseIf.isEvalTrue())
         {
            foundClause = elseIf;
            break;
         }
      }

      // If nothing evaluated to true, use the else.
      if (foundClause == null)
         foundClause = getElse();

      return foundClause;
   }

   /**
    * Handles a child completion by completing switch activity.
    *
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#childComplete(org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   public void childComplete(IAeBpelObject aChild) throws AeBusinessProcessException
   {
      if (childrenAreDone())
      {
         objectCompleted();
      }
   }

   /**
    * @return Returns the elseIfs.
    */
   protected List getElseIfs()
   {
      return mElseIfs;
   }

   /**
    * @param aElseIfs The elseIfs to set.
    */
   protected void setElseIfs(List aElseIfs)
   {
      mElseIfs = aElseIfs;
   }

   /**
    * @return Returns the else.
    */
   protected AeElse getElse()
   {
      return mElse;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept(IAeImplVisitor aVisitor) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }
}
