// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeBpelObject.java,v 1.23 2008/01/11 16:37:21 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.util.Collection;
import java.util.Iterator;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAeLocatableObject;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.impl.activity.IAeExtensionLifecycleAdapter;
import org.activebpel.rt.bpel.impl.activity.support.AeCorrelationSet;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;

/**
 * Base interface for Bpel object implementations. 
 */
public interface IAeBpelObject extends IAeLocatableObject
{
   /**
    * Returns the current state
    */
   public AeBpelState getState();
   
   /**
    * All implementation objects except the process will have a parent activity.
    */
   public IAeBpelObject getParent();
   
   /**
    * Returns an Iterator for the bpel object's child objects which participate
    * in the state change propagation. For structured activities, this merely
    * returns an Iterator for the child activity or activities. In some cases
    * though, like Scope, we're returning objects in addition to the 
    * single child activity. This is because scope needs to propagate the 
    *  
    * @return Iterator containing IAeBpelObjets
    */
   public Iterator getChildrenForStateChange();
   
   /**
    * Gets a variable by name. If the object implementing this interface is a scope
    * or process then it'll check to see if it owns the variable, otherwise the
    * request should propogate up to the parent activity.
    */
   public IAeVariable findVariable(String aName);
   
   /**
    * Gets the <code>partnerLink</code> by name
    */
   public AePartnerLink findPartnerLink(String aName);
   
   /**
    * Getter for the owning process.
    */
   public IAeBusinessProcessInternal getProcess();
   
   /**
    * Finds the correlation set by name. If the activity does not define this correlation set then it will
    * keep walking up the parent hierarchy until it is resolved.
    */
   public AeCorrelationSet findCorrelationSet(String aName);

   /**
    * Returns the suppress join condition attribute for this bpel object. This 
    * attribute is configurable at the activity level and inherited from the parent
    * object unless it is overridden.
    */
   public boolean isSuppressJoinConditionFailure();
   
   /**
    * Returns the namespace associated with the prefix from the associated model.
    */
   public String translateNamespacePrefixToUri(String aPrefix);
   
   /**
    * All implementions should return true. Only activities
    * may not be ready as a result of links not being ready.
    */
   public boolean isReadyToExecute();
   
   /**
    * All non-activity implementions should return true. Only activities
    * may not execute as a result of link status or join conditions being false.
    */
   public boolean isNotDeadPath() throws AeBusinessProcessException;
   
   /**
    * Setter for the state.
    * @param aState
    */
   public void setState(AeBpelState aState) throws AeBusinessProcessException;
   
   /**
    * Signals the object that it has faulted with the fault passed in.
    * @param aFault
    */
   public void setFaultedState(IAeFault aFault) throws AeBusinessProcessException;

   /**
    * Returns true if the child is in a state that can be terminated.
    */
   public boolean isTerminatable();

   /**
    * Returns true if the object is currently in the process of terminating.
    */
   public boolean isTerminating();
   
   /**
    * Returns true if the object is currently faulting or in the process of being retried
    */
   public boolean isFaultingOrRetrying();
   
   /**
    * Getter for the BPEL namespace
    */
   public String getBPELNamespace();
   
   /** 
    * @return Returns true if the object can be completed.
    */
   public boolean isCompletable();
   
   /**
    * Gets a collection of {@link IAeExtensionLifecycleAdapter} objects for this 
    * object.
    */
   public Collection getExtensions();

   /**
    * Gets this bpel object's definition.
    */
   public AeBaseDef getDefinition();

   /**
    * Accept a visit from an IAeImplVisitor, e.g, a validator, etc.
    * 
    * @param aVisitor The visitor to accept.
    */
   public void accept( IAeImplVisitor aVisitor ) throws AeBusinessProcessException;
}
