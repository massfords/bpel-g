//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeDynamicScopeCreator.java,v 1.3 2006/10/26 13:51:30 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity; 

import java.util.LinkedList;
import java.util.List;

import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.activity.AeActivityForEachDef;
import org.activebpel.rt.bpel.def.visitors.AeDefVisitorFactory;
import org.activebpel.rt.bpel.def.visitors.IAeDefToImplVisitor;
import org.activebpel.rt.bpel.impl.AeBusinessProcess;
import org.activebpel.rt.bpel.impl.IAeDynamicScopeParent;

/**
 * Creates one or more child scope instances for a parallel forEach, onEvent, or onAlarm.
 * 
 *  The scopes that get created will NOT automatically be added to the parent's collection of children.
 *  This allows some flexibility by the caller to decide where the scope instance(s) should go.
 */
public class AeDynamicScopeCreator
{
   /** true if the scopes are being created for execution as opposed to being restored */
   private boolean mCreateFlag;
   /** value of the start instance */
   private int mStartInstance;
   /** value of the final instance */
   private int mFinalInstance;
   /** list of scopes that were created */
   private List mScopes = new LinkedList();
   /** reference to the parent object */
   private IAeDynamicScopeParent mParent;
   
   /**
    * Protected ctor, use the static method
    * @param aCreateFlag
    * @param aParent
    * @param aStartInstance
    * @param aFinalInstance
    */
   protected AeDynamicScopeCreator(boolean aCreateFlag, IAeDynamicScopeParent aParent, int aStartInstance, int aFinalInstance)
   {
      setCreateFlag(aCreateFlag);
      setParent(aParent);
      setStartInstance(aStartInstance);
      setFinalInstance(aFinalInstance);
   }
   
   /**
    * Convenience method for creating a list of dynamic scopes. These scopes will not be part of the parent's
    * children.
    * @param aCreateFlag - true if we're creating for execution and the newly created objects and variables should be reported to the process. 
    * @param aParent - parent for the dynamic scopes
    * @param aStartInstance - instance value to use for the first scope
    * @param aFinalInstance - instance value to use for the final scope
    */
   public static List create(boolean aCreateFlag, IAeDynamicScopeParent aParent, int aStartInstance, int aFinalInstance)
   {
      AeDynamicScopeCreator creator = new AeDynamicScopeCreator(aCreateFlag, aParent, aStartInstance, aFinalInstance);
      creator.create();
      return creator.getScopes();
   }
   
   /**
    * Uses the IAeDefToImplVisitor to create all of the scopes needed. Each scope will receive custom location
    * paths that include its instance id in its path.
    */
   protected void create()
   {
      // our custom visitor for creating the impl's
      IAeDefToImplVisitor objectCreationVisitor = AeDefVisitorFactory.getInstance(getParent().getBPELNamespace()).createImplVisitor(getParent().getProcess(), getParent());
      
      // keep looping for all of the iterations 
      for( int i=getStartInstance(); i<= getFinalInstance(); i++)
      {
         // create the child scope impl and all of its descendents
         getParent().getChildScopeDef().accept(objectCreationVisitor);
         
         // remove the most recently added child scope
         AeActivityScopeImpl instanceScope = (AeActivityScopeImpl) getParent().getChildren().remove(getParent().getChildren().size()-1);
         getScopes().add(instanceScope);
         
         // visit that instance w/ an impl visitor to set the location paths and ids on all
         // of the activities and other implementation objects (like variables and correlation sets)
         AeLocationPathImplVisitor locationVisitor =
                  new AeLocationPathImplVisitor((AeBusinessProcess) getParent().getProcess(), 
                                       instanceScope, i, isCreateFlag());
         locationVisitor.startVisiting();

         // There is special logic for a forEach. Could replace the instanceof with a subclass or visitor if necessary
         if (getParent() instanceof AeActivityForEachParallelImpl)
         {
            AeActivityForEachParallelImpl forEach = ((AeActivityForEachParallelImpl)getParent());
            int counter = forEach.getCounterValue();
            AeActivityForEachDef def = forEach.getDef();
            IAeVariable variable = (IAeVariable) instanceScope.getVariable(def.getCounterName());
            int scopeCount = getScopes().size() - 1;
            variable.setTypeData(new Integer(counter+scopeCount));
         }
      }
      
      if (isCreateFlag())
      {
         // make the process aware of all of the variables we've created
         objectCreationVisitor.reportObjects();
      }
   }
   
   /**
    * @return Returns the createFlag.
    */
   protected boolean isCreateFlag()
   {
      return mCreateFlag;
   }

   /**
    * @param aCreateFlag The createFlag to set.
    */
   protected void setCreateFlag(boolean aCreateFlag)
   {
      mCreateFlag = aCreateFlag;
   }

   /**
    * @return Returns the finalInstance.
    */
   protected int getFinalInstance()
   {
      return mFinalInstance;
   }

   /**
    * @param aFinalInstance The finalInstance to set.
    */
   protected void setFinalInstance(int aFinalInstance)
   {
      mFinalInstance = aFinalInstance;
   }

   /**
    * @return Returns the parent.
    */
   protected IAeDynamicScopeParent getParent()
   {
      return mParent;
   }

   /**
    * @param aParent The parent to set.
    */
   protected void setParent(IAeDynamicScopeParent aParent)
   {
      mParent = aParent;
   }

   /**
    * @return Returns the scopes.
    */
   protected List getScopes()
   {
      return mScopes;
   }

   /**
    * @param aScopes The scopes to set.
    */
   protected void setScopes(List aScopes)
   {
      mScopes = aScopes;
   }

   /**
    * @return Returns the startInstance.
    */
   protected int getStartInstance()
   {
      return mStartInstance;
   }

   /**
    * @param aStartInstance The startInstance to set.
    */
   protected void setStartInstance(int aStartInstance)
   {
      mStartInstance = aStartInstance;
   }
}
 