// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeDefVariableUsageVisitor.java,v 1.26 2008/01/25 21:01:18 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeCatchDef;
import org.activebpel.rt.bpel.def.AeCompensationHandlerDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.def.IAeQueryDef;
import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReplyDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityThrowDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWaitDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWhileDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourceDef;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.def.util.AeVariableData;
import org.activebpel.rt.bpel.def.util.AeVariableProperty;
import org.activebpel.rt.expr.def.AeExpressionAnalyzerContext;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzer;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.def.AeBaseDefNamespaceContext;
import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * Visitor that compiles a set of variables used for each activity. This set
 * will be stored on each activity's def object and is used as part of the 
 * variable locking process and the implementation of serializable scopes.
 */
public class AeDefVariableUsageVisitor extends AeAbstractDefVisitor
{
   /** Def objects are pushed and popped from the stack as we traverse the tree */
   private Stack mStack = new Stack();
   /** Collection of visited activities, useful in case we don't find a serializable scope and want to reset all of the used variable sets*/
   private Collection mVisitedActivities = new ArrayList();
   /** Set to true if come across a serializable scope which requires us to do resource locking */
   private boolean mResourceLockingRequired = false;
   /** The current compensation handler or <code>null</code> */
   private AeCompensationHandlerDef mCompensationHandler;
   /** The expression language factory. */
   private IAeExpressionLanguageFactory mExpressionLanguageFactory;

   /**
    * Creates the visitor with the default traversal implementation. 
    */
   protected AeDefVariableUsageVisitor(IAeExpressionLanguageFactory aExpressionLanguageFactory)
   {
      setExpressionLanguageFactory(aExpressionLanguageFactory);
      setTraversalVisitor(new AeTraversalVisitor(new AeDefTraverser(), this));
   }
   
   /**
    * Gets the visited activities.
    */
   protected Collection getVisitedActivities()
   {
      return mVisitedActivities;
   }

   /** 
    * Returns true variable locking is required, meaning we've discovered a 
    * serializable scope within the process 
    */
   protected boolean isResourceLockingRequired()
   {
      return mResourceLockingRequired;
   }
   
   /**
    * Gets called when the visitor discovers a serializable scope within the 
    * process which necessitates variable locking.
    * @param aBool
    */
   protected void setResourceLockingRequired(boolean aBool)
   {
      mResourceLockingRequired = aBool;
   }

   /**
    * An enumerated constant for read locks and write locks, not used outside of 
    * this class. 
    * 
    * Read locks are required when an activity (like an Invoke) reads data from 
    * a variable but does not update it. If these operations exist OUTSIDE the 
    * bounds of a serializable scope then we do not need to account for any locks. 
    * If these operations occur within the bounds of a serializable scope, then 
    * we have to account for the lock since all of the variable usage within a 
    * serializable scope is accounted for during the execution of a scope. 
    * 
    * Write locks are required when an activity (like an Assign) writes data
    * to a variable. Even activities that exist outside the serializable scope 
    * are required to use locks since the variable's data cannot change by an
    * activity external to the scope once the serializable scope begins execution.
    * This inlcudes other serializable scopes as well.
    */
   protected static class AeLockType
   {
      /** constant for a read lock */
      static final AeLockType Read = new AeLockType();
      /** constant for a write lock */
      static final AeLockType Write = new AeLockType();
      
      /** private ctor to prevent instantiation */
      private AeLockType() { }
   }

   /**
    * Called by all of the visit methods in order to traverse the tree. Serves
    * as a good place to record the def if it's an activity because we'll be 
    * revisiting all of the def objects to install empty sets if there are no
    * serialized scopes within this process def.
    * @param aDef
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#traverse(org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   protected void traverse(AeBaseXmlDef aDef)
   {
      // keep track of the activities since we'll revisit them in the event
      // that we don't discover a serializable scope so we can set their 
      // used variable set to Collections.EMPTY_SET
      if (aDef instanceof AeActivityDef)
         getVisitedActivities().add(aDef);

      // normal traverse code      
      getStack().push(aDef);
      aDef.accept(getTraversalVisitor());
      getStack().pop();
   }

   /**
    * Uses the query parser to look for any signs of variable usage within
    * the string passed in.  This includes the built in bpel functions getVariableProperty
    * and getVariableData.  If these are found then a shared lock is added since
    * we're only reading the variable and not updating it.
    * 
    * TODO (EPW) currently this assumes that the query is an XPath query.  If we want to support other query languages, his will need to change.
    * 
    * @param aDef
    * @param aQueryDef
    */
   protected void parseForVariables(AeActivityDef aDef, IAeQueryDef aQueryDef)
   {
      // we don't parse for variables when the query is empty or if
      // the enclosing activity is null. In the latter case, it indicates
      // that the parent was the process and we don't acquire locks on variables
      // at this level.
      if (aQueryDef == null || AeUtil.isNullOrEmpty(aQueryDef.getQuery()) || aDef == null)
         return;

      try
      {
         // Note: Currently we are using the expression language analyzer, since we don't have a framework for query language analyzers.
         String language = getExpressionLanguageFactory().getBpelDefaultLanguage(aQueryDef.getBpelNamespace());
         IAeExpressionAnalyzer analyzer = getExpressionLanguageFactory().createExpressionAnalyzer(aQueryDef.getBpelNamespace(), language);
         IAeExpressionAnalyzerContext ctx = new AeExpressionAnalyzerContext(new AeBaseDefNamespaceContext(aDef));

         for (Iterator it = analyzer.getVarPropertyList(ctx, aQueryDef.getQuery()).iterator(); it.hasNext();)
         {
            AeVariableProperty varProp = (AeVariableProperty) it.next();
            addVariableLock(aDef, varProp.getVarName(), AeLockType.Read);
         }

         for (Iterator it = analyzer.getVarDataList(ctx, aQueryDef.getQuery()).iterator(); it.hasNext();)
         {
            AeVariableData varData = (AeVariableData) it.next();
            addVariableLock(aDef, varData.getVarName(), AeLockType.Read);
         }

      }
      catch (AeException e)
      {
         AeException.logError(e, e.getLocalizedMessage());
      }
   }
   
   /**
    * Uses the expression parser to look for any signs of variable usage within
    * the string passed in. This includes the built in bpel functions getVariableProperty
    * and getVariableData. If these are found then a shared lock is added since
    * we're only reading the variable and not updating it.
    * 
    * @param aDef
    * @param aExpressionDef
    */
   protected void parseForVariables(AeActivityDef aDef, IAeExpressionDef aExpressionDef)
   {
      // we don't parse for variables when the expression is empty or if
      // the enclosing activity is null. In the latter case, it indicates
      // that the parent was the process and we don't acquire locks on variables
      // at this level. With BPEL 2.0, the variables for eventHandlers and faultHandlers
      // will be implicitly defined.
      if (aExpressionDef == null || AeUtil.isNullOrEmpty(aExpressionDef.getExpression()) || aDef == null)
         return;

      try
      {
         AeProcessDef processDef = AeDefUtil.getProcessDef(aDef);
         String language = AeDefUtil.getExpressionLanguage(aExpressionDef, processDef);
         IAeExpressionAnalyzer analyzer = getExpressionLanguageFactory().createExpressionAnalyzer(aExpressionDef.getBpelNamespace(), language);
         IAeExpressionAnalyzerContext ctx = new AeExpressionAnalyzerContext(new AeBaseDefNamespaceContext(aDef));

         for (Iterator it = analyzer.getVarPropertyList(ctx, aExpressionDef.getExpression()).iterator(); it.hasNext();)
         {
            AeVariableProperty varProp = (AeVariableProperty) it.next();
            addVariableLock(aDef, varProp.getVarName(), AeLockType.Read);
         }

         for (Iterator it = analyzer.getVarDataList(ctx, aExpressionDef.getExpression()).iterator(); it.hasNext();)
         {
            AeVariableData varData = (AeVariableData) it.next();
            addVariableLock(aDef, varData.getVarName(), AeLockType.Read);
         }

      }
      catch (AeException e)
      {
         AeException.logError(e, e.getLocalizedMessage());
      }
   }
   
   /**
    * We'll record the usage of the variable based on the lock type and
    * whether or not the activity is nested within a serializable scope. In all
    * cases, we record a lock if it's a write lock. This is because the rules for
    * a serializable scope say that a variable should not change once the scope
    * is entered. If an activity outside of the serializable scope wants to write to
    * a variable then it must obtain a write lock and as such, we're recording that
    * requirement here.
    * If we're in a serializable scope however, we acquire a lock irrespective of 
    * the lock flag since the serializable scope acquires locks on all of the 
    * variables its using to prevent another serializable scope from accessing 
    * these same variables until the locks are released. This prevents the issue
    * of dirty reads and non-repeatable reads.  
    * @param aDef - the def that uses the variable
    * @param aVarName - the name of the variable
    * @param aLockType - true if we're adding a write lock
    */
   protected void addVariableLock(AeActivityDef aDef, String aVarName, AeLockType aLockType)
   {
      // if def is null we must be a child of the main process, which
      // doesn't lock its event handlers or fault handler variables.
      if (aDef != null && (! AeUtil.isNullOrEmpty(aVarName)) )
      {
         if (aLockType == AeLockType.Write || isInIsolatedScope(aDef))
         {
            String variableLocationPath = getPathForVariable(aVarName);
            AeActivityDef targetForLock = getTargetForLock(aDef);
            // if the variable in question is defined locally within the activity
            // then there is no need to acquire a lock since the variable is not
            // accessible to other scopes.
            if (! variableLocationPath.startsWith(targetForLock.getLocationPath()))
               getResourcesUsedSet(targetForLock).add(variableLocationPath);
         }
      }
   }
   
   /**
    * Gets or creates the the set that stores the variables used for this def.
    * @param aActivityDef
    */
   protected Set getResourcesUsedSet(AeActivityDef aActivityDef)
   {
      Set resourcesUsed = aActivityDef.getResourcesUsed();
      if (resourcesUsed == null)
      {
         resourcesUsed = new HashSet();
         aActivityDef.setResourcesUsed(resourcesUsed);
      }
      return resourcesUsed;
   }
   
   /**
    * Walks up the stack looking for an AeScopeDef that defines the variable passed in.
    * We should ALWAYS find one or we have a bad bpel file.
    * @param aVarName
    */
   private String getPathForVariable(String aVarName)
   {
      AeVariableDef varDef = null;
      
      if (!getStack().isEmpty())
      {
         AeBaseDef def = (AeBaseDef) getStack().peek();
         varDef = AeDefUtil.getVariableByName(aVarName, def);
      }
      
      if (varDef == null)
         throw new RuntimeException( AeMessages.format( "AeDefVariableUsageVisitor.ERROR_0", new Object[] {aVarName, getCurrentLocationPath()} ) ); //$NON-NLS-1$

      return varDef.getLocationPath();
   }

   /**
    * Gets the location path of the item on the stack
    */
   protected String getCurrentLocationPath()
   {
      AeBaseDef def = (AeBaseDef) getStack().peek();
      return def.getLocationPath(); 
   }

   /**
    * Returns true if we're within a serializable scope. This also takes into
    * consideration the possibility of being within the serializable scope's 
    * compensation handler which is technically not part of the serializable
    * requirement. 
    */
   protected boolean isInIsolatedScope(AeActivityDef aDef)
   {
      return (aDef.getIsolatedScope() != null) && (getCompensationHandler() == null);      
   }
   
   /**
    * The target for the lock is either going to be the def passed in or it'll
    * be the serializable scope if we're within a serializable scope. The latter
    * is because the serializable scope will acquire the locks for all of its nested
    * activities prior to execution. This ensures that we won't have any deadlocks
    * and also frees the individual child activities from having to store any 
    * lock information.
    * @param aDef
    */
   protected AeActivityDef getTargetForLock(AeActivityDef aDef)
   {
      if (isInIsolatedScope(aDef))
      {
         return aDef.getIsolatedScope();
      }
      return aDef;
   }

   /**
    * Walks the stack from the current position up til it finds an instanceof
    * AeActivityDef to return. 
    */
   protected AeActivityDef findParentActivityDef()
   {
      for(int i=getStack().size()-1; i!=0; i--)
      {
         if (getStack().get(i) instanceof AeActivityDef)
            return (AeActivityDef) getStack().get(i);
      }
      return null;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCompensationHandlerDef)
    */
   public void visit(AeCompensationHandlerDef aDef)
   {
      // Save (and restore) the current compensation handler in case we have a
      // nested compensation handler.
      AeCompensationHandlerDef oldCompensationHandler = getCompensationHandler();

      // Set the current compensation handler.
      setCompensationHandler(aDef);

      super.visit(aDef);

      setCompensationHandler(oldCompensationHandler);
   }

   /**
    * Sets the current compensation handler. As stated in the spec, serializable
    * scopes do not need to acquire locks for activities within their compensation 
    * handler. This is because the execution of a compensation handler occurs 
    * after the normal execution of the scope and is therefore outside of the
    * bounds of the scope's serializable requirements.
    * @param aFlag
    */
   private void setCompensationHandler(AeCompensationHandlerDef aCompensationHandler)
   {
      mCompensationHandler = aCompensationHandler;
   }

   /**
    * Returns the current compensation handler or <code>null</code>.
    */
   protected AeCompensationHandlerDef getCompensationHandler()
   {
      return mCompensationHandler;
   }

   /**
    * If there are no serializable scopes within
    * the process def then the visitor will install Collections.EMPTY_SET for
    * each of the activity defs since no variable locking is required.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      super.visit(aDef);

      aDef.setContainsSerializableScopes(isResourceLockingRequired());
      
      if (!isResourceLockingRequired())
      {
         for (Iterator iter = getVisitedActivities().iterator(); iter.hasNext();)
         {
            AeActivityDef def = (AeActivityDef) iter.next();
            def.setResourcesUsed(Collections.EMPTY_SET);
         }
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef)
    */
   public void visit(AeActivityInvokeDef aDef)
   {
      addVariableLock(aDef, aDef.getInputVariable(), AeLockType.Read);
      addVariableLock(aDef, aDef.getOutputVariable(), AeLockType.Write);
      traverse(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
    */
   public void visit(AeActivityReceiveDef aDef)
   {
      addVariableLock(aDef, aDef.getVariable(), AeLockType.Write);
      traverse(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
    */
   public void visit(AeActivityScopeDef aDef)
   {
      if (aDef.isIsolated())
      {
         setResourceLockingRequired(true);
      }
      
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchDef)
    */
   public void visit(AeCatchDef aDef)
   {
      addVariableLock(findParentActivityDef(), aDef.getFaultVariable(), AeLockType.Read);
      traverse(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
    */
   public void visit(AeOnMessageDef aDef)
   {
      // TODO (MF) the onMessage should release its lock as soon as the data arrives. This is big change since there is currently no notion of ref counts for variable locking and it's possible that some other nested activity within the onMessage may request a lock on that variable 
      addVariableLock(findParentActivityDef(), aDef.getVariable(), AeLockType.Write);
      traverse(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      visit((AeOnMessageDef) aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
    */
   public void visit(AeOnAlarmDef aDef)
   {
      AeActivityDef parent = findParentActivityDef();
      parseForVariables(parent, aDef.getForDef());
      parseForVariables(parent, aDef.getUntilDef());
      traverse(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityThrowDef)
    */
   public void visit(AeActivityThrowDef aDef)
   {
      addVariableLock(aDef, aDef.getFaultVariable(), AeLockType.Read);
      traverse(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWaitDef)
    */
   public void visit(AeActivityWaitDef aDef)
   {
      parseForVariables(aDef, aDef.getForDef());
      parseForVariables(aDef, aDef.getUntilDef());
      traverse(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWhileDef)
    */
   public void visit(AeActivityWhileDef aDef)
   {
      parseForVariables(aDef, aDef.getConditionDef());
      traverse(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
    */
   public void visit(AeElseIfDef aDef)
   {
      parseForVariables(findParentActivityDef(), aDef.getConditionDef());
      traverse(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeIfDef)
    */
   public void visit(AeIfDef aDef)
   {
      visit((AeElseIfDef) aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourceDef)
    */
   public void visit(AeSourceDef aDef)
   {
      parseForVariables(findParentActivityDef(), aDef.getTransitionConditionDef());
      traverse(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromDef)
    */
   public void visit(AeFromDef aDef)
   { 
      AeActivityDef parent = findParentActivityDef();
      addVariableLock(parent, aDef.getVariable(), AeLockType.Read);
      parseForVariables(parent, aDef);
      
      traverse(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToDef)
    */
   public void visit(AeToDef aDef)
   {
      AeActivityDef parent = findParentActivityDef();
      addVariableLock(parent, aDef.getVariable(), AeLockType.Write);
      parseForVariables(parent, aDef);
      
      traverse(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReplyDef)
    */
   public void visit(AeActivityReplyDef aDef)
   {
      addVariableLock(aDef, aDef.getVariable(), AeLockType.Read);
      traverse(aDef);
   }

   /**
    * Getter for the stack
    */
   protected Stack getStack()
   {
      return mStack;
   }

   /**
    * @return Returns the expressionLanguageFactory.
    */
   protected IAeExpressionLanguageFactory getExpressionLanguageFactory()
   {
      return mExpressionLanguageFactory;
   }
   
   /**
    * @param aExpressionLanguageFactory The expressionLanguageFactory to set.
    */
   protected void setExpressionLanguageFactory(IAeExpressionLanguageFactory aExpressionLanguageFactory)
   {
      mExpressionLanguageFactory = aExpressionLanguageFactory;
   }
}