//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeOnEvent.java,v 1.13.16.1 2008/04/21 16:09:43 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support; 

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeActivity;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeDynamicScopeParent;
import org.activebpel.rt.bpel.impl.activity.AeActivityOnEventScopeImpl;
import org.activebpel.rt.bpel.impl.activity.AeDynamicScopeCreator;
import org.activebpel.rt.bpel.impl.activity.IAeEventParent;
import org.activebpel.rt.bpel.impl.activity.IAeMessageDispatcher;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.wsio.receive.IAeMessageContext;

/**
 * The onEvent for a scope provides support for concurrent handling of messages. 
 * The implementation here leverages the existing code for dynamic scopes that 
 * was added to support the parallel forEach. Dynamic scopes describes the 
 * situation where we have more than one implementation object that maps to a 
 * def object. In the case of the onEvent, we'll create a dynamic scope each 
 * time a message arrives for the onEvent.
 * 
 * The onEvent resolves its partnerLink and messageExchange values down to its 
 * child scope first and then up to its enclosing scopes. The partnerLink is 
 * required at the time of the onEvent's queuing and during dispatch of the 
 * message. The partnerLink instance info is required during queuing in order to 
 * detect conflictingReceives. The partnerLink instance info and messageExchange 
 * value are required during message dispatch in order to detect a 
 * conflictingRequest. 
 * 
 * One problem with the deferred creation of the child scopes is that the 
 * onEvent may require some resources from its child scope in order to execute. 
 * An example of this would be an onEvent that resolves its partnerLink to its 
 * child scope. This scope will not exist at the time the onEvent executes but 
 * the instance info for the partnerLink is required in order to test for 
 * conflictingReceive test.
 * 
 * In order to address this, the onEvent will maintain a separate scope which is
 * referred to as its queueing scope. This scope is used to resolve any 
 * partnerLinks at the time the onEvent gets queued. This scope is not used for 
 * anything else, nor is its state saved as part of the process. 
 * 
 */
// TODO (MF) change inheritance here, don't want to extend AeOnMessage
public class AeOnEvent extends AeOnMessage implements IAeDynamicScopeParent
{
   /** 
    * Scope used to queue the onEvent. This scope never executes. It exists 
    * simply to provide the partnerLink instance for onEvents that resolve their 
    * partnerLink to their child scope instance. 
    */
   private AeActivityOnEventScopeImpl mQueuingScope;
   
   /** 
    * Temporary reference to a scope that will receive an onMessage or onFault 
    * call from the process.
    */
   private AeActivityOnEventScopeImpl mCurrentScope;
   
   /** list of child scope instances created during execute routine */
   private List mChildren = new ArrayList();

   /** 
    * list of child scope instances that have been restored for compensation 
    * purposes 
    */
   private List mCompensatableChildren = new ArrayList();

   /** value for the next scope instance created for this onEvent */
   private int mInstanceValue = 1;
   
   /**
    * Ctor accepts def and parent
    * @param aDef
    * @param aParent
    */
   public AeOnEvent(AeOnEventDef aDef, IAeEventParent aParent)
   {
      super(aDef, aParent);
   }
   
   /**
    * Moves our list of previously executed children  to the compensatable list
    * and then clears the list prior to calling the super's execute.
    * 
    * @see org.activebpel.rt.bpel.impl.activity.support.AeOnMessage#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      if (isConcurrent())
      {
         for (Iterator iter = getChildren().iterator(); iter.hasNext();)
         {
            AeActivityOnEventScopeImpl scope = (AeActivityOnEventScopeImpl) iter.next();
            if (scope.isNormalCompletion())
            {
               getCompensatableChildren().add(scope);
            }
         }
         getChildren().clear();
      }
      super.execute();
   }

   /**
    * Overridden to try to resolve the variable to the child scope first.
    * 
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#findCorrelationSet(java.lang.String)
    */
   public AeCorrelationSet findCorrelationSet(String aName)
   {
      AeCorrelationSet cs = null;
      
      // If there is a current scope set, then we must be resolving the cs
      // as part of consuming the message. In this case, the cs may resolve to 
      // the current scope.  
      if (getCurrentScope()!= null)
         cs = getCurrentScope().getCorrelationSet(aName);
      
      // No current scope or it didn't find the cs. Resolve the cs using the
      // ancestors.
      if (cs == null)
         cs = super.findCorrelationSet(aName);
      return cs;
   }

   /**
    * Overridden to try to resolve the variable to the child scope first.
    * 
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#findVariable(java.lang.String)
    */
   public IAeVariable findVariable(String aName)
   {
      IAeVariable variable = null;

      // If there is a current scope set, then we must be resolving the variable
      // as part of consuming the message. In this case, the variable MUST 
      // resolve to the current scope. It cannot resolve to an ancestor scope. 
      if (getCurrentScope() != null)
         variable = getCurrentScope().getVariable(aName);
      
      // If there is no current scope and we didn't find the variable, then the
      // call to findVariable must be from some nested activity within the 
      // onEvent (i.e. an &lt;assign&gt;). These variables should be resolved to
      // an ancestor scope using the super.
      if (getCurrentScope() == null && variable == null)
         variable = super.findVariable(aName);
      
      return variable;
   }
   
   /**
    * Overridden to try to resolve the plink to the child scope first.
    * 
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#findPartnerLink(java.lang.String)
    */
   public AePartnerLink findPartnerLink(String aName)
   {
      AePartnerLink plink = null;

      // If the onEvent is being queued then we should check with the queuing
      // scope to see if it resolves the plink
      if (getCurrentScope() == null)
         plink = getQueuingScope().getPartnerLink(aName);

      // Otherwise, the reference to the plink is resolved through our ancestors
      if (plink == null)
         plink = super.findPartnerLink(aName);
      
      return plink;
   }

   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.AeOnMessage#createDispatcher(org.activebpel.wsio.receive.IAeMessageContext)
    */
   public IAeMessageDispatcher createDispatcher(IAeMessageContext aMessageContext)
   {
      AeActivityOnEventScopeImpl scope = null;
      if (isConcurrent())
      {
         // get and increment our instance value
         int instance = getInstanceValue();
         setInstanceValue(getInstanceValue() + 1);
         
         // create a dynamic scope instance
         List scopes = AeDynamicScopeCreator.create(true, this, instance, instance);
         scope = (AeActivityOnEventScopeImpl) scopes.get(0);
         
         // add to our list of children
         getChildren().add(scope);
      }
      else
      {
         scope = getQueuingScope();
      }
      scope.setMessageContext(aMessageContext);
      return scope;
   }

   /**
    * If there are no more children executing then the event will notify the 
    * scope that it is quiescent and give the scope a chance to complete.
    * 
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#childComplete(org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   public void childComplete(IAeBpelObject aChild)
         throws AeBusinessProcessException
   {
      if (isConcurrent())
      {
         for (Iterator iter = getChildrenForStateChange(); iter.hasNext();)
         {
            AeActivityOnEventScopeImpl scope = (AeActivityOnEventScopeImpl) iter.next();
            if (!scope.getState().isFinal())
            {
               return;
            }
         }
         findEnclosingScope().eventCompleted(this);
      }
      else
      {
         super.childComplete(aChild);
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.AeOnMessage#extractMessageData(org.activebpel.rt.message.IAeMessageData)
    */
   public void extractMessageData(IAeMessageData aMessageData) throws AeBusinessProcessException
   {
      super.extractMessageData(aMessageData);
   }
   
   /**
    * Getter for the def
    */
   private AeOnEventDef getDef()
   {
      return (AeOnEventDef) getDefinition();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.AeOnMessage#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept(IAeImplVisitor aVisitor) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }

   /**
    * @return Returns the children.
    */
   public List getChildren()
   {
      return mChildren;
   }

   /**
    * @return Returns the compensatableChildren.
    */
   public List getCompensatableChildren()
   {
      return mCompensatableChildren;
   }

   /**
    * @param aCurrentScope The currentScope to set.
    */
   public void setCurrentScope(AeActivityOnEventScopeImpl aCurrentScope)
   {
      mCurrentScope = aCurrentScope;
   }
   
   public AeActivityOnEventScopeImpl getCurrentScope()
   {
      return mCurrentScope;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeDynamicScopeParent#getInstanceValue()
    */
   public int getInstanceValue()
   {
      return mInstanceValue;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeDynamicScopeParent#setInstanceValue(int)
    */
   public void setInstanceValue(int aInstanceValue)
   {
      mInstanceValue = aInstanceValue;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageReceiverActivity#isConcurrent()
    */
   public boolean isConcurrent()
   {
      return true;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeDynamicScopeParent#getChildScopeDef()
    */
   public AeActivityScopeDef getChildScopeDef()
   {
      return getDef().getChildScope();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.AeActivityParent#addActivity(org.activebpel.rt.bpel.IAeActivity)
    */
   public void addActivity(IAeActivity aActivity)
   {
      getChildren().add(aActivity);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.AeActivityParent#getActivity()
    */
   public IAeActivity getActivity()
   {
      // TODO (MF) remove after changing base class
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.AeActivityParent#setActivity(org.activebpel.rt.bpel.IAeActivity)
    */
   protected void setActivity(IAeActivity aActivity)
   {
      // TODO (MF) remove after changing base class
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.AeActivityParent#getChildrenForStateChange()
    */
   public Iterator getChildrenForStateChange()
   {
      return getChildren().iterator();
   }

   /**
    * @return Returns the queuingScope.
    */
   public AeActivityOnEventScopeImpl getQueuingScope()
   {
      return mQueuingScope;
   }

   /**
    * @param aQueuingScope The queuingScope to set.
    */
   public void setQueuingScope(AeActivityOnEventScopeImpl aQueuingScope)
   {
      mQueuingScope = aQueuingScope;
   }
}