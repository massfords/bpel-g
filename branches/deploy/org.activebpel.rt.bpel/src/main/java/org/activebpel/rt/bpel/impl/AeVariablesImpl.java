//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeVariablesImpl.java,v 1.4 2008/02/17 21:37:07 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl; 

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.AeVariablesDef;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.activity.IAeVariableContainer;
import org.activebpel.rt.bpel.impl.activity.assign.AeCopyOperationContext;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext;

/**
 * impl for the variables object that contains a map of the variables and behavior for variable initialization
 */
public class AeVariablesImpl implements IAeVariableContainer
{
   /** map of variable name to variable object */
   private Map mMap = new HashMap();
   /** def object */
   private AeVariablesDef mVariablesDef;
   /** scope parent */
   private AeActivityScopeImpl mScope;
   /** virtual copy operations that contain variable initializations */
   private Collection mCopyOperations;
   /** context used to initialize our variables */
   private IAeCopyOperationContext mContext;
   
   /**
    * Ctor accepts the def and scope parent
    * @param aDef
    * @param aScopeParent
    */
   public AeVariablesImpl(AeVariablesDef aDef, AeActivityScopeImpl aScopeParent)
   {
      mVariablesDef = aDef;
      mScope = aScopeParent;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeVariableContainer#iterator()
    */
   public Iterator iterator()
   {
      return getMap().values().iterator();
   }

   /**
    * Getter for the variable by name
    * @param aVariableName
    */
   public IAeVariable findVariable(String aVariableName)
   {
      return (IAeVariable) getMap().get(aVariableName);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeVariableContainer#addVariable(org.activebpel.rt.bpel.IAeVariable)
    */
   public void addVariable(IAeVariable aVariable)
   {
      getMap().put(aVariable.getDefinition().getName(), aVariable);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeVariableContainer#getParent()
    */
   public IAeBpelObject getParent()
   {
      return getScope();
   }

   /**
    * Getter for the variables map
    */
   public Map getMap()
   {
      return mMap;
   }
   
   /**
    * Getter for the def
    */
   protected AeVariablesDef getDef()
   {
      return mVariablesDef;
   }
   
   /**
    * Getter for the scope
    */
   protected AeActivityScopeImpl getScope()
   {
      return mScope; 
   }

   /**
    * Variables get re-initialized each time the scope executes.
    */
   public void clearVariableState(boolean aCloneFlag)
   {
      for (Iterator iter = getMap().entrySet().iterator(); iter.hasNext();)
      {
         Map.Entry entry = (Entry) iter.next();
         AeVariable var = (AeVariable) entry.getValue();
         if (aCloneFlag)
         {
            var = (AeVariable) var.clone();
            entry.setValue(var);
         }
         if (!var.getDefinition().isImplicit())
         {
            var.clear();
         }
      }
   }
   
   /**
    * Getter for the copy operations collection.
    */
   protected Collection getCopyOperationsCollection()
   {
      if (mCopyOperations == null)
         mCopyOperations = new ArrayList();
      
      return mCopyOperations;
   }

   /**
    * Add a virtual copy operation to the scope.
    * @param aCopyOp
    */
   public void addCopyOperation(IAeCopyOperation aCopyOp)
   {
      getCopyOperationsCollection().add(aCopyOp);
   }

   /**
    * Initialize variable values which specify a from-spec.
    */
   public void initialize() throws AeBpelException
   {
      try
      {
         for (Iterator iter=getCopyOperationsCollection().iterator(); iter.hasNext(); )
         {
            IAeCopyOperation copyOp = (IAeCopyOperation) iter.next();
            copyOp.setContext(getContext());
            copyOp.execute();
         }
      }
      catch (Exception e)
      {
         AeException.logError(e);
         IAeFault fault = getScope().getFaultFactory().getScopeInitializationFailure();
         throw new AeBpelException(AeMessages.getString("AeVariablesImpl.InitializationError"), fault); //$NON-NLS-1$
      }
   }
  
   /**
    * Getter for the context, lazily creating if null
    */
   protected IAeCopyOperationContext getContext()
   {
      if (mContext == null)
      {
         mContext = new AeCopyOperationContext(getScope());
      }
      return mContext;
   }
}
 