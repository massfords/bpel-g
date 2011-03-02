// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeWSBPELDefVariableUsageVisitor.java,v 1.6 2006/11/10 04:02:08 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.AeCompensationHandlerDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AeScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartDef;
import org.activebpel.rt.bpel.def.io.readers.def.AeExpressionSpecStrategyKey;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.util.AeUtil;

/**
 * Def visitor that records resources that need to be locked for WS-BPEL 2.0 rules.
 * 
 * Note that the visitor that assigns To Strategies to the ToDef must be run before
 * this visitor runs.
 */
public class AeWSBPELDefVariableUsageVisitor extends AeDefVariableUsageVisitor
{
   /**
    * The scope that owns the compensation handler for the current isolation
    * domain or <code>null</code> if we are not traversing an isolated domain
    * for a compensation handler.
    */
   private AeActivityScopeDef mIsolatedCompensationHandlerScope;

   /**
    * Constructs a BPEL 2.0 def variable visitor.
    * 
    * @param aExpressionLanguageFactory
    */
   protected AeWSBPELDefVariableUsageVisitor(IAeExpressionLanguageFactory aExpressionLanguageFactory)
   {
      super(aExpressionLanguageFactory);
   }

   /**
    * Returns the scope that owns the compensation handler for the current
    * isolation domain or <code>null</code> if we are not traversing an isolated
    * domain for a compensation handler.
    */
   protected AeActivityScopeDef getIsolatedCompensationHandlerScope()
   {
      return mIsolatedCompensationHandlerScope;
   }

   /**
    * Returns <code>true</code> if and only if we are traversing an isolated
    * domain for a compensation handler.
    */
   protected boolean isInIsolatedCompensationHandler()
   {
      return getIsolatedCompensationHandlerScope() != null;
   }

   /**
    * Sets the scope that owns the compensation handler for the current
    * isolation domain.
    */
   protected void setIsolatedCompensationHandlerScope(AeActivityScopeDef aIsolatedCompensationHandlerScope)
   {
      mIsolatedCompensationHandlerScope = aIsolatedCompensationHandlerScope;
   }

   /**
    * No variable locks necessary for the onEvent since its variables will be private.
    *  
    * @see org.activebpel.rt.bpel.def.visitors.AeDefVariableUsageVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      traverse(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromDef)
    */
   public void visit(AeFromDef aDef)
   {
      addPartnerLinkLock(findParentActivityDef(), aDef.getPartnerLink(), AeLockType.Read);
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeDefVariableUsageVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToDef)
    */
   public void visit(AeToDef aDef)
   {
      if (aDef.getStrategyKey() instanceof AeExpressionSpecStrategyKey)
      {
         AeExpressionSpecStrategyKey strategy = (AeExpressionSpecStrategyKey) aDef.getStrategyKey();
         AeActivityDef parent = findParentActivityDef();
         addVariableLock(parent, strategy.getVariableName(), AeLockType.Write);
      }

      addPartnerLinkLock(findParentActivityDef(), aDef.getPartnerLink(), AeLockType.Write);

      // Note: super.visit will parse the query and acquire read locks for each variable it finds.
      // We have already acquired a Write lock for one of the variables, so the attempt to acquire a 
      // Read lock for that same variable will be a no-op.
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartDef)
    */
   public void visit(AeFromPartDef aDef)
   {
      addVariableLock(findParentActivityDef(), aDef.getToVariable(), AeLockType.Write);

      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef)
    */
   public void visit(AeFromPartsDef aDef)
   {
      // If this is a fromParts element for an onEvent, then we don't need to
      // visit the individual fromPart elements, because onEvent variables are
      // private to the onEvent's scope.
      if (!(aDef.getParent() instanceof AeOnEventDef))
      {
         super.visit(aDef);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartDef)
    */
   public void visit(AeToPartDef aDef)
   {
      addVariableLock(findParentActivityDef(), aDef.getFromVariable(), AeLockType.Read);

      super.visit(aDef);
   }

   /**
    * Walks up the stack looking for an AeScopeDef that defines the partner link passed in.
    * We should ALWAYS find one or we have a bad bpel file.
    * @param aPartnerLinkName
    */
   private String getPathForPartnerLink(AeActivityDef aDef, String aPartnerLinkName)
   {
      AePartnerLinkDef plDef = AeDefUtil.findPartnerLinkDef(aDef, aPartnerLinkName);
      if (plDef == null)
         throw new RuntimeException( AeMessages.format( "AeWSBPELDefVariableUsageVisitor.ERROR_0", new Object[] {aPartnerLinkName, getCurrentLocationPath()} ) ); //$NON-NLS-1$

      return plDef.getLocationPath();
   }

   /**
    * We'll record the usage of the partner link based on the lock type and whether or not the activity 
    * is nested within a serializable scope. In all cases, we record a lock if it's a write lock. This 
    * is because the rules for a serializable scope say that a variable should not change once the scope
    * is entered. If an activity outside of the serializable scope wants to write to a partner link then 
    * it must obtain a write lock and as such, we're recording that requirement here. If we're in a 
    * serializable scope however, we acquire a lock irrespective of the lock flag since the serializable 
    * scope acquires locks on all of the partner links its using to prevent another serializable scope 
    * from accessing these same partner links until the locks are released. This prevents the issue
    * of dirty reads and non-repeatable reads.
    *   
    * @param aDef - the def that uses the variable
    * @param aPartnerLinkName - the name of the partner link
    * @param aLockType - true if we're adding a write lock
    */
   protected void addPartnerLinkLock(AeActivityDef aDef, String aPartnerLinkName, AeLockType aLockType)
   {
      // if def is null we must be a child of the main process, which
      // doesn't lock its event handlers or fault handler variables.
      if (aDef != null && (! AeUtil.isNullOrEmpty(aPartnerLinkName)) )
      {
         if (aLockType == AeLockType.Write || isInIsolatedScope(aDef))
         {
            String plLocationPath = getPathForPartnerLink(aDef, aPartnerLinkName);
            AeActivityDef targetForLock = getTargetForLock(aDef);
            
            // if the partner link in question is defined locally within the activity, then there
            // is no need to acquire a lock since the partner link is not accessible to other scopes.
            if (! plLocationPath.startsWith(targetForLock.getLocationPath()))
               getResourcesUsedSet(targetForLock).add(plLocationPath);
         }
      }
   }

   /**
    * Overrides method to return the resources used by the compensation handler
    * for the current isolation domain if we are traversing an isolated domain
    * for a compensation handler. Otherwise, returns the resources used by the
    * given activity.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.AeDefVariableUsageVisitor#getResourcesUsedSet(org.activebpel.rt.bpel.def.AeActivityDef)
    */
   protected Set getResourcesUsedSet(AeActivityDef aActivityDef)
   {
      Set resourcesUsed;

      // If we are traversing an isolated domain for a compensation handler,
      // then return the set stored for the compensation handler in its scope.
      if (isInIsolatedCompensationHandler())
      {
         AeActivityScopeDef scope = (AeActivityScopeDef) aActivityDef;

         resourcesUsed = scope.getResourcesUsedByCompensationHandler();
         if (resourcesUsed == null)
         {
            resourcesUsed = new HashSet();
            scope.setResourcesUsedByCompensationHandler(resourcesUsed);
         }
      }
      // Otherwise, delegate to the base class implementation.
      else
      {
         resourcesUsed = super.getResourcesUsedSet(aActivityDef);
      }

      return resourcesUsed;
   }
   
   /**
    * Overrides method to return the scope that owns the compensation handler of
    * the current isolation domain if we are traversing the isolation domain for
    * a compensation handler. In WS-BPEL, the compensation handler of an
    * isolated scope forms an isolation domain separate from the isolation
    * domain of the rest of the scope.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.AeDefVariableUsageVisitor#getTargetForLock(org.activebpel.rt.bpel.def.AeActivityDef)
    */
   protected AeActivityDef getTargetForLock(AeActivityDef aDef)
   {
      AeActivityDef target = aDef;

      if (isInIsolatedCompensationHandler())
      {
         target = getIsolatedCompensationHandlerScope();
      }
      else if (isInIsolatedScope(aDef))
      {
         target = aDef.getIsolatedScope();
      }

      return target;
   }

   /**
    * Overrides method to return <code>true</code> if we are in an isolated
    * scope whether or not we are in a compensation handler.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.AeDefVariableUsageVisitor#isInIsolatedScope(org.activebpel.rt.bpel.def.AeActivityDef)
    */
   protected boolean isInIsolatedScope(AeActivityDef aDef)
   {
      return aDef.getIsolatedScope() != null;      
   }

   /**
    * Traverses the compensation handlers for all of the child scopes of the
    * given scope.
    *
    * @param aScope
    */
   protected void traverseChildScopeCompensationHandlers(AeScopeDef aScope)
   {
      Set childScopes = AeChildScopesVisitor.findChildScopes(aScope);
      
      for (Iterator i = childScopes.iterator(); i.hasNext(); )
      {
         AeActivityScopeDef childScope = (AeActivityScopeDef) i.next();
         traverseCompensationHandler(childScope);
      }
   }

   /**
    * Traverses the compensation handler of the given scope.
    *
    * @param aScope
    */
   protected void traverseCompensationHandler(AeActivityScopeDef aScope)
   {
      AeCompensationHandlerDef compensationHandler = aScope.getCompensationHandlerDef();

      if (aScope.isIsolated())
      {
         setIsolatedCompensationHandlerScope(aScope);
      }

      if (compensationHandler != null)
      {
         // The scope has an explicit compensation handler. Visit it.
         visit(compensationHandler);
      }
      else
      {
         // The scope has a default compensation handler. Traverse the
         // compensation handlers for all of the child scopes.
         traverseChildScopeCompensationHandlers(aScope.getScopeDef());
      }

      if (aScope.isIsolated())
      {
         setIsolatedCompensationHandlerScope(null);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef)
    */
   public void visit(AeActivityCompensateDef aDef)
   {
      AeScopeDef rootScope = aDef.findRootScopeForCompensation();
      traverseChildScopeCompensationHandlers(rootScope);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef)
    */
   public void visit(AeActivityCompensateScopeDef aDef)
   {
      AeScopeDef rootScope = aDef.findRootScopeForCompensation();
      String targetName = aDef.getTarget();
      AeActivityScopeDef targetScope = AeChildScopeByNameVisitor.findChildScopeByName(rootScope, targetName);
      traverseCompensationHandler(targetScope);
   }
}