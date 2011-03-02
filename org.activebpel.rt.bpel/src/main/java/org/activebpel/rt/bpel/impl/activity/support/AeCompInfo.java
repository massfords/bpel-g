// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeCompInfo.java,v 1.20 2006/10/05 22:39:02 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;

/**
 * An AeCompInfo object represents a single successful execution of a <code>scope</code>
 * activity. It contains a reference to the scope that completed, a variable snapshot
 * object to record the state of all of the variables, and a collection of any
 * child AeCompInfo objects that may have been created by enclosed scopes.
 * This information is reported back to the scope's enclosing scope in case the
 * scope needs to be compensated.
 */
public class AeCompInfo
{
   /** reference to the parent comp info object - used to resolve variables */
   private AeCompInfo mParent;
   /** The scope that successfully completed execution */
   private AeActivityScopeImpl mScope;
   /** Snapshot of the variable data at the time of completion */
   private AeScopeSnapshot mSnapshot;
   /** All completed enclosed scopes */
   private LinkedList mEnclosedScopes;
   /** Flag to prevent multiple invocations */
   private boolean mEnabled = true;

   /**
    * Creates AeCompInfo with all of its data. This is done during the successful
    * completion of a scope.
    * @param aScope - the scope that completed
    */
   public AeCompInfo(AeActivityScopeImpl aScope)
   {
      // TODO (KR) Don't save scope itself--save scope definition and location path/id
      mScope = aScope;
   }

   /**
    * Getter for the parent, may be null.
    */
   protected AeCompInfo getParent()
   {
      return mParent;
   }

   /**
    * Setter for the parent
    * @param aParent
    */
   public void setParent(AeCompInfo aParent)
   {
      mParent = aParent;
   }

   /**
    * Extracts matching info objects from the linked list of completed enclosed scopes.
    * @param aScopeName
    */
   public List getEnclosedInfoByScopeName(String aScopeName)
   {
      List matchingScopes = new LinkedList();

      for (Iterator i = getEnclosedScopes().iterator(); i.hasNext(); )
      {
         AeCompInfo enclosedScope = (AeCompInfo) i.next();
         if (aScopeName.equals(enclosedScope.getScopeName()))
         {
            matchingScopes.add(enclosedScope);
         }
      }

      return matchingScopes;
   }

   /**
    * Convenience method to pull the scope name from the scope's def object
    */
   private String getScopeName()
   {
      AeActivityScopeDef def = (AeActivityScopeDef) getScope().getDefinition();
      return def.getName();
   }

   /**
    * Gets the variable from the local snapshot. If not available in the snapshot
    * then the parent is consulted.
    * @param aName
    * @return IAeVariable or null if it wasn't available. A null means that the scope should resolve the variable.
    */
   public IAeVariable getVariable(String aName)
   {
      IAeVariable variable = getSnapshot() != null ? getSnapshot().getVariable(aName) : null;
      if (variable == null && getParent() != null)
         variable = getParent().getVariable(aName);
      return variable;
   }

   /**
    * Gets the correlation set from the local snapshot.  If not available in the snapshot, then the
    * parent is consulted.
    * 
    * @param aName
    */
   public AeCorrelationSet getCorrelationSet(String aName)
   {
      AeCorrelationSet corrSet = getSnapshot() != null ? getSnapshot().getCorrelationSet(aName) : null;
      if (corrSet == null && getParent() != null)
         corrSet = getParent().getCorrelationSet(aName);
      return corrSet;
   }

   /**
    * Gets the partner link from the local snapshot.  If not available in the snapshot, then the
    * parent is consulted.
    * 
    * @param aName
    */
   public AePartnerLink getPartnerLink(String aName)
   {
      AePartnerLink plink = getSnapshot() != null ? getSnapshot().getPartnerLink(aName) : null;
      if (plink == null && getParent() != null)
         plink = getParent().getPartnerLink(aName);
      return plink;
   }

   /**
    * Creates the snapshot.
    */
   public void recordSnapshot(AeActivityScopeImpl aScope)
   {
      // If the scope has an explicit compensation handler or a nested one, then take a
      // snapshot. Otherwise, skip the snapshot, because no one will ever look
      // at it.
      if (aScope.isRecordSnapshotEnabled())
      {
         setSnapshot(new AeScopeSnapshot());
         aScope.recordScopeSnapshot(getSnapshot());
      }
   }

   /**
    * Adds an enclosed scope's compensation information to our collection of
    * enclosed scopes.
    * @param aCompInfo
    */
   public void add(AeCompInfo aCompInfo, AeActivityScopeImpl aScope)
   {
      aCompInfo.setParent(this);
      aCompInfo.recordSnapshot(aScope);

      // TODO (KR) Don't save this compinfo if it is not coordinated, has no child compinfos, and has no snapshot.
      getEnclosedScopes().addFirst(aCompInfo);
   }

   /**
    * Getter for the scope.
    */
   public AeActivityScopeImpl getScope()
   {
      // TODO (KR) Lookup scope by location path/id or construct scope impl from scope definition and location path/id 
      return mScope;
   }

   /**
    * Getter for the snapshot.
    */
   public AeScopeSnapshot getSnapshot()
   {
      return mSnapshot;
   }

   /**
    * Setter for the snapshot.
    */
   public void setSnapshot(AeScopeSnapshot aSnapshot)
   {
      mSnapshot = aSnapshot;
   }

   /**
    * Getter for the enclosed scopes list
    */
   public LinkedList getEnclosedScopes()
   {
      if (mEnclosedScopes == null)
      {
         mEnclosedScopes = new LinkedList();
      }
      return mEnclosedScopes;
   }

   /**
    * Getter for the coordinated enclosed scopes list
    */
   public List getCoordinatedEnclosedScopes()
   {
      List matchingScopes = new LinkedList();
      for (Iterator childIter = getEnclosedScopes().iterator(); childIter.hasNext(); )
      {
         AeCompInfo childCompInfo = (AeCompInfo) childIter.next();
         if (childCompInfo instanceof AeCoordinatorCompInfo)
         {
            matchingScopes.add(childCompInfo);
         }
      }
      return matchingScopes;
   }

   /**
    * Returns true if any of the enclosed (child) AeCoordinatorCompInfo objects are enabled.
    * A CompoInfo object is initially enabled and disabled after being executed (compensated).
    * A disabled comp info should not be used to avoid REPEATED_COMPENSATION faults.
    *
    * @return true if any of the comp info objects have not been run already.
    */
   public boolean hasEnabledCoordinatedChildren()
   {
      boolean rVal = false;
      for (Iterator childIter = getCoordinatedEnclosedScopes().iterator(); childIter.hasNext(); )
      {
         AeCompInfo childCompInfo = (AeCompInfo) childIter.next();
         if (childCompInfo.isEnabled())
         {
            rVal = true;
            break;
         }
      }
      return rVal;
   }

   /**
    * Setter for the enclosed scopes list
    */
   public void setEnclosedScopes(List aEnclosedScopes)
   {
      mEnclosedScopes = new LinkedList(aEnclosedScopes);
   }

   /**
    * Sets the compensation info on the scope and prepares the compensation handler
    * for execution.
    */
   public void prepareForCompensation(IAeCompensationCallback aCallback) throws AeBusinessProcessException
   {
      AeCompensationHandler ch = getCompensationHandler();

      getScope().setCompInfo(this);
      ch.setCompInfo(this);
      ch.setCallback(aCallback);
      ch.setState(AeBpelState.INACTIVE);
   }

   /**
    * Removes the compensation info from the scope and compensation handler after
    * execution. After the comp info is removed it is disabled to prevent further
    * compensation with this same instance data.
    */
   public void compensationComplete()
   {
      getScope().setCompInfo(null);
      getCompensationHandler().setCallback(null);
      getCompensationHandler().setCompInfo(null);
      setEnabled(false);
   }

   /**
    * Returns true if the comp info object is enabled.
    */
   public boolean isEnabled()
   {
      return mEnabled;
   }

   /**
    * Sets the enabled flag for the comp info.
    * @param aB
    */
   public void setEnabled(boolean aB)
   {
      mEnabled = aB;

      if (!isEnabled())
      {
         removeCompensatedScopes();
         setSnapshot(null);
         mEnclosedScopes = null;
      }
   }

   /**
    * Convenience method for getting the compensation handler.
    */
   public AeCompensationHandler getCompensationHandler()
   {
      return getScope().getCompensationHandler();
   }

   /**
    * Returns true if this is a coordinated comp info. This is normally used by
    * during saving and restoring state information.
    * @return true if this is a coordinated comp info.
    */
   public boolean isCoordinated()
   {
      return false;
   }

   /**
    * Returns the coordinationId if this is a coordinated comp info.
    * This information is normally used during state save/restore procedures.
    * @return the coordinationId if this is a coordinated comp info.
    */
   public String getCoordinationId()
   {
      return ""; //$NON-NLS-1$
   }

   /**
    * Removes any enclosed comp info references that have been compensated. This
    * method should be called when the references are no longer reachable, which
    * would be at the end of a compensationHandler's execution.
    */
   protected void removeCompensatedScopes()
   {
      // There is no point in holding onto these comp info's once they've been disabled
      for (Iterator iter = getEnclosedScopes().iterator(); iter.hasNext();)
      {
         AeCompInfo compInfo = (AeCompInfo) iter.next();
         if (!compInfo.isEnabled())
         {
            iter.remove();
         }
      }
   }
}
