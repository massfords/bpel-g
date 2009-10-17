// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityCompensateImpl.java,v 1.35 2008/02/17 21:37:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeFaultFactory;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeFCTHandler;
import org.activebpel.rt.bpel.impl.IAeFaultFactory;
import org.activebpel.rt.bpel.impl.activity.support.AeCompInfo;
import org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeSupportActivityLocationPathSuffix;
import org.activebpel.rt.bpel.impl.activity.support.IAeCompensationCallback;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;

/**
 * The compensate activity gets called from a fault handler or from within an
 * already executing compensation handler. The role of the compensation activity
 * is to identify the scope or scopes that are getting compensated and then to
 * queue the identified scope's compensation handlers.
 */
public class AeActivityCompensateImpl extends AeActivityImpl implements IAeCompensationCallback
{
   /** The <code>List</code> of matching <code>AeCompInfo</code> objects for iteration. */
   private List mIterationScopes;

   /** The next index in the list of matching <code>AeCompInfo</code> objects. */
   private int mNextIndex = 0;
   
   /** If true, the compensate activity should run only the coordinated <code>AeCompInfo</code> objects. */
   private boolean mMatchCoordinated; 

   /** 
    * default constructor for activity which compensates all compInfo objects  
    */
   public AeActivityCompensateImpl(AeActivityCompensateDef aActivityDef, IAeActivityParent aParent)
   {
      this(aActivityDef, aParent, false);
   }

   /**
    * Constructor for activity.
    * @param aActivityDef activity definition
    * @param aParent enclosing scope or fault handler
    */
   public AeActivityCompensateImpl(AeActivityCompensateDef aActivityDef, IAeActivityParent aParent,
         boolean aMatchCoordinated)
   {
      super(aActivityDef, aParent);
      mMatchCoordinated = aMatchCoordinated;
   }
   
   /**
    * @return Returns the matchCoordinated.
    */
   protected boolean isMatchCoordinated()
   {
      return mMatchCoordinated;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept( IAeImplVisitor aVisitor ) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }

   /**
    * factory method that creates the implicit compensation activity for use
    * by the implicit fault handler and the implicit compensation handler
    * @param aParent
    * @param aDef
    */
   public static AeActivityCompensateImpl createImplicitCompensation(IAeActivityParent aParent, AeBaseDef aDef)
   {
      return createImplicitCompensation(aParent, aDef, false);
   }
   
   /**
    * Factory method that creates the implicit compensation activity for use
    * by the implicit fault handler and the implicit compensation handler. 
    * Setting aMatchCoordinated to true compensates only the coordinated activities.
    *  
    * @param aParent enclosing scope.
    * @param aDef
    * @param aMatchCoordinated filters the list of CompInfo objects by matching only the coordinated activities.
    */
   public static AeActivityCompensateImpl createImplicitCompensation(IAeActivityParent aParent, final AeBaseDef aDef, boolean aMatchCoordinated)
   { 
      AeActivityCompensateImpl implicitComp = new AeActivityCompensateImpl(null, aParent, aMatchCoordinated)
      {
         /**
          * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#getDefinition()
          */
         public AeBaseDef getDefinition()
         {
            return aDef; 
         }

         /**
          * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#getLocationPath()
          */
         public String getLocationPath()
         {
            IAeBpelObject parent = getParent();
            StringBuffer buffer = new StringBuffer(parent.getLocationPath());
            if (isMatchCoordinated())
            {
               buffer.append(AeSupportActivityLocationPathSuffix.IMPLICIT_CC_COMPENSATE_ACTIVITY);
            }
            else
            {
               buffer.append(AeSupportActivityLocationPathSuffix.IMPLICIT_COMPENSATE_ACTIVITY); 
            }
            return buffer.toString();
         }

         /**
          * @see org.activebpel.rt.bpel.IAeLocatableObject#hasLocationId()
          */
         public boolean hasLocationId()
         {
            // There is no definition object to give us a location id.
            return false;
         }
      };

      implicitComp.getProcess().addBpelObject(implicitComp.getLocationPath(), implicitComp);
      return implicitComp;
   }

   /**
    * A <code>compensate</code> Activity calls compensate on all of
    * the enclosed scopes. This method will set the <code>iterator</code>
    * field with all of the instances that need compensation.  We might have
    * multiple compensation calls to make since the scopes could have executed 
    * in a loop multiple times.
    * 
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      super.execute();
      // clearing the scopes here since we could exist within a loop and get 
      // executed multiple times. Want to force the scopes to get rebuilt each time
      mIterationScopes = null;
      
      // Set iterator to start at index 0.
      setNextIndex(0);

      compScope();
   }

   /**
    * Executes the compensation handler for the next compensatable scope instance
    * from the Iterator. If the iterator is empty, then we're done.
    * 
    * @throws AeBusinessProcessException
    */
   private void compScope() throws AeBusinessProcessException
   {
      // find the first compInfo to compensate
      while(hasMoreCompInfos())
      {
         AeCompInfo compInfo = getNextCompInfo();
         
         if (!compInfo.isEnabled())
         {
            IAeFaultFactory factory = AeFaultFactory.getFactory(getBPELNamespace());
            IAeFault fault = factory.getRepeatedCompensation();
            if (fault != null)
            {
               throw new AeBpelException(AeMessages.getString("AeCompInfo.ERROR_0"), fault); //$NON-NLS-1$
            }
         }
         else
         {
            compInfo.prepareForCompensation(this);
            getProcess().queueObjectToExecute(compInfo.getCompensationHandler());
            return;
         }
      }
      
      // if we get here then there were no enabled comp infos
      objectCompleted();
   }
   
   /**
    * Gets the next comp info object for compensation
    */
   private AeCompInfo getNextCompInfo() throws AeBusinessProcessException
   {
      int i = getNextIndex();
      setNextIndex(i + 1);

      return (AeCompInfo) getIterationScopes().get(i);
   }

   /**
    * returns true if there are more comp info objects that need executing
    */
   private boolean hasMoreCompInfos() throws AeBusinessProcessException
   {
      return getNextIndex() < getIterationScopes().size();
   }

   /**
    * Convenience method to pull the comp info object from the enclosed scope
    */
   protected AeCompInfo getCompInfo()
   {
      return findRootScopeForCompensation().getCompInfo();
   }

   /**
    * Walks up the parent hiearchy until it comes across a catch/catchAll or compensationHandler
    * and then it returns that object's enclosing scope.
    */
   private AeActivityScopeImpl findRootScopeForCompensation()
   {
      boolean foundFCTHandler = false;
      for (IAeBpelObject parent = getParent(); parent != null; parent = parent.getParent())
      {
         // skip over scopes til u find a catch/catchAll or compensation handler
         if (foundFCTHandler && parent instanceof AeActivityScopeImpl)
         {
            return (AeActivityScopeImpl) parent;
         }
         // we found a fault, compensation, or termination handler
         else if (parent instanceof IAeFCTHandler)
         {
            foundFCTHandler = true;
         }
      }
      return null;
   }

   /**
    * A fault during compensation gets propagated to the parent like any other fault.
    * We'll remove any installed compensation information here before propagating
    * the fault.
    * @see org.activebpel.rt.bpel.impl.activity.support.IAeCompensationCallback#compensationCompleteWithFault(org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler, org.activebpel.rt.bpel.IAeFault)
    */
   public void compensationCompleteWithFault(AeCompensationHandler aHandler, IAeFault aFault) throws AeBusinessProcessException
   {
      aHandler.getCompInfo().compensationComplete();
      objectCompletedWithFault(aFault);
   }
   
   /**
    * Callback method from the compensation handler to indicate that it has
    * successfully completed without a fault. We'll remove the installed compensation
    * information and then move onto the next instance that needs compensation.
    * @see org.activebpel.rt.bpel.impl.activity.support.IAeCompensationCallback#compensationComplete(org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler)
    */
   public void compensationComplete(AeCompensationHandler aHandler) throws AeBusinessProcessException
   {
      AeCompInfo ci = aHandler.getCompInfo(); 
      ci.compensationComplete();
      compScope();
   }
   
   /** 
    * Overrides method to call this activity's childTerminated.
    * @see org.activebpel.rt.bpel.impl.activity.support.IAeCompensationCallback#compensationTerminated(org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler)
    */
   public void compensationTerminated(AeCompensationHandler aHandler) throws AeBusinessProcessException
   {
      // reset comp info
      aHandler.getCompInfo().compensationComplete();      
      childTerminated(aHandler);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityImpl#acquireResourceLocks()
    */
   protected boolean acquireResourceLocks()
   {
      return true;
   }

   /**
    * Returns the list of matching scopes for iteration.
    */
   protected List getIterationScopes() 
   {
      if (mIterationScopes == null)
      {
         List matchingScopes;

         if (isMatchCoordinated())
         {
            // get all effected comp info objects which are coordinated.
            matchingScopes = getCompInfo().getCoordinatedEnclosedScopes();
         }
         else
         {
            matchingScopes = getMatchingScopes();
         }

         mIterationScopes = new ArrayList(matchingScopes);
      }

      return mIterationScopes;
   }

   /**
    * Gets the list of matching scopes for compensation.
    * 
    */
   protected List getMatchingScopes()
   {
      return getCompInfo().getEnclosedScopes();
   }

   /**
    * Returns the next index in the list of matching scopes.
    */
   public int getNextIndex()
   {
      return mNextIndex;
   }

   /**
    * Sets the next index for iteration.
    */
   public void setNextIndex(int aNextIndex)
   {
      mNextIndex = aNextIndex;
   }
   
   /** 
    * Overrides method to 
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#getChildrenForCompletion()
    */
   protected Iterator getChildrenForCompletion()
   {
      try
      {
         return getExecutingCompHandlerIterator();
      }
      catch(Throwable t)
      {
         AeException.logError(t);
         return Collections.EMPTY_LIST.iterator();
      }
   }
   
   /**
    * Includes the compensationHandler that is currently executing.
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#getChildrenForTermination()
    */
   public Iterator getChildrenForTermination()
   {
      return getExecutingCompHandlerIterator();
   }
   
   /** 
    * @return iterator to currently executing compensation handler.
    */
   protected Iterator getExecutingCompHandlerIterator() 
   {
      Iterator childrenIterator = null;
      if (mNextIndex > 0)
      {
         // we need to terminate the active compensationHandler
         AeCompensationHandler compHandler = getExecutingCompensationHandlerForTermination();
         if (compHandler != null)
         {
            childrenIterator = Collections.singleton(compHandler).iterator();
         }
      }
      if (childrenIterator == null)
      {
         return Collections.EMPTY_LIST.iterator();
      }
      else
      {
         return childrenIterator;
      }
   }
   
   /**
    * Gets the executing compensation handler to terminate.
    */
   protected AeCompensationHandler getExecutingCompensationHandlerForTermination() 
   {
      AeCompensationHandler compHandler = null;
      int offset = mNextIndex - 1;
      // precautionary range check - the offset should never be out of range
      // since that would mean that we're being asked to terminate but we have
      // no compensation handlers running. 
      if (offset < getIterationScopes().size())
      {
         AeCompInfo compInfo = (AeCompInfo) getIterationScopes().get(offset);
         if (compInfo.getCompensationHandler() != null && !compInfo.getCompensationHandler().getState().isFinal())
         {
            compHandler = compInfo.getCompensationHandler();
         }
      }
      return compHandler;
   }

   /** 
    * Returns true if this IAeCompensationCallback is a coordinated. This is normally used by
    * during saving and restoring state information.
    * @return true if this is a coordinated comp info.
    */      
   public boolean isCoordinated()
   { 
      return false;
   }
   
   /**
    * Returns the coordinationId for this IAeCompensationCallback.
    * This information is normally used during state save/restore procedures.
    * @return the coordinationId if this is a coordinated comp info.
    */
   public String getCoordinationId()
   {   
      return ""; //$NON-NLS-1$
   }
}
