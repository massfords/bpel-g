//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeCoordinationContainer.java,v 1.6 2006/06/05 20:40:22 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.coord.AeCoordinationException;
import org.activebpel.rt.bpel.coord.AeCoordinationFaultException;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;
import org.activebpel.rt.util.AeUtil;


/**
 * Support container to manage coordinated acitivites for a scope.
 */
public class AeCoordinationContainer extends AeAbstractBpelObject
{
   // State of each coordinated activity.  This state information will also be saved
   // during persistence and used during restoration.
   
   /** Indicates the activity is active. */
   public static final String COORD_STATE_ACTIVE            = "active" ; //$NON-NLS-1$
   /** Indicates the activity has completed. */
   public static final String COORD_STATE_COMPLETED         = "completed" ; //$NON-NLS-1$
   /** Indicates the activity has completed and compensation handler has been installed. */
   public static final String COORD_STATE_COMPENSATABLE     = "compensatable" ; //$NON-NLS-1$
   /** Indicates the activity has completed compensation. */
   public static final String COORD_STATE_COMPENSATED       = "compensated" ; //$NON-NLS-1$      
   /** Indicates the activity has faulted and is not eligible for compensation. */
   public static final String COORD_STATE_FAULTED           = "faulted" ; //$NON-NLS-1$
   /** Indicates that an 'active' activity has been sent a cancel signal due to fault/comp handler execution. */
   public static final String COORD_STATE_CANCELED           = "canceled" ; //$NON-NLS-1$   
   
   /** 
    * Contains a set of coordination ids of activities which are currently executing (i.e has not completed, or faulted).
    * A scope cannot complete until all coordinated activities have completed.
    */
   private Set mActiveCoordinations = null;

   /** 
    * Map containing a set of all registered coordination-ids (key) along with the state information (value).
    */
   private Map mRegsiteredCoordinations = null;   
   
   /**
    * Map containing a set of coordination-ids (key) to compensationHandler (value) of coordinated activities
    * which have a compensation handler associated with them. 
    **/
   private Map mCompCoordinationsMap = null;
   
   /**
    * Default constructor.
    * @param aEnclosingScope enclosing scope.
    */
   public AeCoordinationContainer(IAeBpelObject aEnclosingScope)
   {
     super(null, aEnclosingScope);
   }
   
   /**
    * Returns a colletion of all activities that have registered for coordination.
    * @return map containing all coordinations.
    */
   protected Map getRegisteredCoordinationsMap()
   {
      if (mRegsiteredCoordinations == null)
      {
         mRegsiteredCoordinations = new HashMap();
      }
      return mRegsiteredCoordinations;
   }
   
   /**
    * Returns the map containing coordinations which have compensation handlers installed.
    */
   protected Map getCompCoordinationMap()
   {
      if (mCompCoordinationsMap == null)
      {
         mCompCoordinationsMap = new HashMap();
      }
      return mCompCoordinationsMap;
   }
  
   /** 
    * @return Set containing ids of active coordinations.
    */
   protected Set getActiveCoordinations()
   {
      if (mActiveCoordinations == null)
      {
         mActiveCoordinations = new HashSet();
      }
      return mActiveCoordinations;
   }
   
   /**
    * Returns a set containing coordination-ids of all activities (including completed and faulted).
    * @return set of all coordinations.
    */
   public Set getRegisteredCoordinations()
   {
      return Collections.unmodifiableSet(getRegisteredCoordinationsMap().keySet());
   }   

   /**
    * Returns the state of the given coordination.
    * @param aCoordinationId
    * @return current state of the coordination.
    */
   public String getState(String aCoordinationId)
   {
      return (String)getRegisteredCoordinationsMap().get(aCoordinationId);
   }
   
   /**
    * Adds the given coordination and its state to the overall master collection of coordinations.
    * @param aCoordinationId
    * @param aState
    */
   protected void registerCoordination(String aCoordinationId, String aState)
   {
      getRegisteredCoordinationsMap().put(aCoordinationId, aState);
      AeActivityScopeImpl scope = (AeActivityScopeImpl) getParent();
      // update the business process properties to indicate that the process is part of a coordinated activity.
      scope.getProcess().setCoordinator(true);      
   }
   
   /**
    * Restores the coordination and its state. This method is normally used by the
    * state restoration process.
    * @param aCoordinationId coordination id
    * @param aState state of the coordination.
    */
   public void restoreCoordination(String aCoordinationId, String aState)
   {
      // add to main collection
      registerCoordination(aCoordinationId, aState);      
      if ( COORD_STATE_ACTIVE.equals(aState) )
      {
         // and add to the active collection
         getActiveCoordinations().add(aCoordinationId);         
      }
      else if ( COORD_STATE_COMPENSATABLE.equals(aState) )
      {
         AeActivityScopeImpl scope = (AeActivityScopeImpl) getParent();
         AeCoordinatorCompensationHandler ch = new AeCoordinatorCompensationHandler(scope, aCoordinationId);
         getCompCoordinationMap().put(aCoordinationId, ch);
      }      
   }
   
   /**
    * Returns true if there are any activities remaining under  active coordination.
    * Coordinations that have either completed or faulted are not considered active.
    * @return True if there are any activities remaining under  active coordination 
    */
   public boolean hasActiveCoordinations()
   {
      return getActiveCoordinations().size() > 0;
   }  
      
   /**
    * Returns true if there any coordinated activities (either active, completed or faulted).
    * @return True if there any coordinated activities.
    */
   public boolean hasCoordinations()
   {
      return getRegisteredCoordinations().size() > 0;
   } 
   
   /**
    * Returns true if the given coordination id is part of this scope.
    * @param aCoordinationId
    */   
   public boolean hasCoordinationId(String aCoordinationId)
   {
      boolean rVal = false;
      if (AeUtil.notNullOrEmpty(aCoordinationId))
      {
         rVal = getRegisteredCoordinationsMap().containsKey(aCoordinationId);
      }
      return rVal;
   }      
   
   /**
    * Adds the coordination-id to the currently 'active' set.
    * The id is added when a participant registers for coordination.
    * @param aCoordinationId
    */
   public synchronized void registerCoordinationId(String aCoordinationId) throws AeCoordinationException
   {
      if ( hasCoordinationId(aCoordinationId) )
      {
         throw new AeCoordinationFaultException(AeCoordinationFaultException.ALREADY_REGISTERED, aCoordinationId);
      }
      else if ( AeUtil.notNullOrEmpty(aCoordinationId) )
      {   
         
         // add to the master collection
         registerCoordination(aCoordinationId, COORD_STATE_ACTIVE);
         // add to the active collection
         getActiveCoordinations().add(aCoordinationId);         
      }
   }
   
   /**
    * Removes the coordination-id from either the active or completed set..
    * The id is removed when a participant has reached the ENDED state.
    * @param aCoordinationId
    */
   
   public synchronized void deregisterCoordinationId(String aCoordinationId)
   {
      if (hasCoordinationId(aCoordinationId)) 
      {
         // remove from all collections
         getRegisteredCoordinationsMap().remove(aCoordinationId);
         getActiveCoordinations().remove(aCoordinationId);
         getCompCoordinationMap().remove(aCoordinationId);
         checkForCompletion();
      }// if has coord
   }
   
   /**
    * Signals active coordinations to cancel or compensate (if the participant has completed).
    * The state of these coordinations will be transistioned to canceled.
    */
   public void cancelActiveCoordinations()
   {
      // This method is called by the fault or compensation handlers to signal
      // the participants to either cancel (if they are active) or compensate (if they have completed).
      // We need to do this due to race conditions that may occur. For example, a participant may have
      // completed and indicated this (asynchronously) to the coordinator (and hence the enclosing scope).
      // But due to certain race conditions, the COMPLETED signal may never get this scope and hence
      // the scope will assume such activity is still active and not eligible for compensation.
      // (race condition can occur when the coordinator tries to get a lock on this process, but 
      // will never aquire on time since the scope's fault/comp handler is already executing.)
 
      if (hasActiveCoordinations())
      {
         Set activeSet = new HashSet(getActiveCoordinations());
         Iterator  coordIdIter = activeSet.iterator();
         while (coordIdIter.hasNext())
         {
            cancelActiveCoordination( (String) coordIdIter.next() );
         }
      }
   }
   
   /**
    * Signals the participant to cancel or compensate. 
    * @param aCoordinationId
    */
   protected void cancelActiveCoordination(String aCoordinationId)
   {
      if (getActiveCoordinations().contains(aCoordinationId))
      {
         // remove coordination id from active collection.
         getActiveCoordinations().remove(aCoordinationId);
         // update it's current state
         getRegisteredCoordinationsMap().put(aCoordinationId, COORD_STATE_CANCELED);
         // fire evt to coordination manager.
         AeActivityScopeImpl scope = (AeActivityScopeImpl) getParent();
         scope.getProcess().getEngine().getCoordinationManager().compensateOrCancel(aCoordinationId);
         checkForCompletion();
      }
   }   
   
   /**
    * Activity has completed and is available for compensation.
    * @param aCoordinationId
    */
   public void coordinationCompleted(String aCoordinationId)
   {
      if (getActiveCoordinations().contains(aCoordinationId))
      {
         // remove coordination id from active collection.
         getActiveCoordinations().remove(aCoordinationId);
         // update it's current state
         getRegisteredCoordinationsMap().put(aCoordinationId, COORD_STATE_COMPLETED);
         checkForCompletion();
      }
   }
   
   /**
    * Activity has faulted and is not available for compensation.
    * @param aCoordinationId
    */
   public void coordinationCompletedWithFault(String aCoordinationId)
   {
      if (getActiveCoordinations().contains(aCoordinationId))
      {
         // remove coordination id from active collection.
         getActiveCoordinations().remove(aCoordinationId);
         // update state
         getRegisteredCoordinationsMap().put(aCoordinationId, COORD_STATE_FAULTED);
         // does the comp handler need to be removed?
         checkForCompletion();
      }      
   }
   
   /**
    * Getter for the coordinated compensation handler. 
    * @return compensation handler if found or null otherwise.
    */
   public synchronized AeCompensationHandler getCompensationHandler(String aCoordinationId)
   {
      AeCompensationHandler handler = null;
      handler = (AeCompensationHandler) getCompCoordinationMap().get(aCoordinationId);      
      return handler;
   }
   
   /**
    * Setter for the coordinated compensation handler and changes the state of
    * the coordination to COORD_STATE_COMPENSATABLE.
    * 
    * @param aCompensationHandler a AeCoordinatorCompensationHandler instance.
    */
   public synchronized void addCompensationHandler(AeCompensationHandler aCompensationHandler)
   {
      if (aCompensationHandler instanceof AeCoordinatorCompensationHandler)
      {
         String cId =((AeCoordinatorCompensationHandler) aCompensationHandler).getCoordinationId();
         if (COORD_STATE_COMPLETED.equals( getRegisteredCoordinationsMap().get(cId) ) )
         {
            getCompCoordinationMap().put(cId, aCompensationHandler);
            // update state
            getRegisteredCoordinationsMap().put(cId, COORD_STATE_COMPENSATABLE);
         }
      }
      else 
      {
         AeException.logWarning(AeMessages.format("AeCoordinationContainer.UNSUPPORTED_COMPHANDLER",String.valueOf(aCompensationHandler) )); //$NON-NLS-1$
      }
   }
   
   /**
    * Removes coordinated compensation handler and changes the state of the coordination
    * to COORD_STATE_COMPENSATED.
    * 
    * @param aCompensationHandler a AeCoordinatorCompensationHandler instance.
    */
   public synchronized void removeCompensationHandler(AeCompensationHandler aCompensationHandler)
   {
      if (aCompensationHandler instanceof AeCoordinatorCompensationHandler)
      {
         String cId =((AeCoordinatorCompensationHandler) aCompensationHandler).getCoordinationId();
         if (getCompCoordinationMap().containsKey(cId))
         {
            getCompCoordinationMap().remove(cId);
            getRegisteredCoordinationsMap().put(cId, COORD_STATE_COMPENSATED);
         }
      }
      else 
      {
         AeException.logWarning(AeMessages.format("AeCoordinationContainer.UNSUPPORTED_COMPHANDLER",String.valueOf(aCompensationHandler) )); //$NON-NLS-1$
      }
      
   }   
   
   /**
    * Returns true if the given coordination id has a compensation handler associated with it.
    * @param aCoordinationId
    */
   public boolean hasCompensationHandler(String aCoordinationId)
   {
      return getCompensationHandler(aCoordinationId) != null;
   }

   /**
    * Returns a list of compensation handlers
    */
   protected List getCompensationHandlers()
   {
      List list = new ArrayList();
      Iterator it = getCompCoordinationMap().values().iterator();
      while (it.hasNext())
      {
         Object handler = it.next();
         if (handler != null)
         {
            list.add(handler);
         }
      }
      return list;
   }
  
   /**
    * Checks to see if there are any remaining activities which have not completed or faulted.
    * If all of the activities have completed, this method calls back on the Scope::coordinationCompleted()
    * method.
    */
   private void checkForCompletion()
   {

      if (! hasActiveCoordinations())
      {
         // scope will no longer be waiting.
         try
         {
            // scope should be notified that the all coordinations have completed.
            AeActivityScopeImpl scope = (AeActivityScopeImpl) getParent();
            scope.coordinationCompleted();
         }
         catch(Exception e)
         {
            AeException.logError(e, e.getMessage());
         }
      }      
   }
   
   /**
    * Invoked by the scope to indicate that it has finished its activity and retuires a callback
    * when all active coordinations have completed.
    */
   public void callbackOnCompletion()
   {
      checkForCompletion();
   }
   
   /**
    * Accept a visit from an IAeImplVisitor, e.g, a validator, etc.
    * 
    * @param aVisitor The visitor to accept.
    */
   public void accept(IAeImplVisitor aVisitor ) throws AeBusinessProcessException
   {   
      aVisitor.visit(this);
   }
   
   /**
    * Installs event objects with the engine's event and alarm services.
    */
   public void execute() throws AeBusinessProcessException
   {
      // nothing happens in the execute.
   }   
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getChildrenForStateChange()
    */
   public Iterator getChildrenForStateChange()
   {
      return getCompensationHandlers().iterator();      
   }
   
   public void childComplete(IAeBpelObject aChild) throws AeBusinessProcessException
   {
      // no op
   }   
   
   /**
    * This is overridden because we don't have a def object. Our location path
    * is equal to the parent's path plus _CoordinationContainer
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getLocationPath()
    */
   public String getLocationPath()
   {
      IAeBpelObject parent = getParent();
      StringBuffer buffer = new StringBuffer(parent.getLocationPath());
      return buffer.append(AeSupportActivityLocationPathSuffix.COORDINATION_CONTAINER).toString(); 
   }

   /**
    * This object gets created because there was no compensation handler defined for the scope.
    * As such, there is no definition object and calling this method results in an exception.
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#getDefinition()
    */
   public AeBaseDef getDefinition()
   {
      throw new UnsupportedOperationException(AeMessages.getString("AeCoordinationContainer.DEF_NOT_AVAILABLE")); //$NON-NLS-1$
   }

   /**
    * Overrides method to return <code>false</code>, since {@link
    * #getDefinition()} will fail. 
    * 
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#hasLocationId()
    */
   public boolean hasLocationId()
   {
      return false;
   }   
}
