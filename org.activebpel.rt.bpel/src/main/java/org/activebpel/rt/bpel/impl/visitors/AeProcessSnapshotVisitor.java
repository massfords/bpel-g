// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/visitors/AeProcessSnapshotVisitor.java,v 1.11.4.1 2008/04/21 16:09:43 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.visitors;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.AeCorrelationSetDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeScopeDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.impl.AeBusinessProcess;
import org.activebpel.rt.bpel.impl.activity.AeActivityInvokeImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.activity.support.AeCompInfo;
import org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeCorrelationSet;
import org.activebpel.rt.bpel.impl.activity.support.AeFaultHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeScopeSnapshot;
import org.activebpel.rt.bpel.impl.activity.support.AeWSBPELFaultHandler;

/**
 * Visits a tree of BPEL implementation objects to collect all versions of all
 * variables and correlation sets that are still live for normal or
 * compensation processing.
 */
public class AeProcessSnapshotVisitor extends AeImplTraversingVisitor
{
   /** The <code>Set</code> of <code>AeCorrelationSet</code> instances. */
   private final Set mCorrelationSets = new HashSet();

   /** The <code>Set</code> of <code>IAeVariable</code> instances. */
   private final Set mVariables = new HashSet();
   
   /** The <code>Set</code> of <code>AePartnerLink</code> instances. */
   private final Set mPartnerLinks = new HashSet();

   /** The <code>Set</code> of pending <code>AeActivityInvokeImpl</code> instances. */
   private final Set mPendingInvokes = new HashSet();

   /**
    * Adds all live variable and correlation set instances reachable from the
    * specified compensation info object.
    *
    * @param aCompInfo
    */
   protected void addCompInfo(AeCompInfo aCompInfo)
   {
      AeScopeSnapshot snapshot = aCompInfo.getSnapshot();
      if (snapshot != null)
      {
         Set correlationSets = snapshot.getCorrelationSets();
         Set variables = snapshot.getVariables();
         Set partnerLinks = snapshot.getPartnerLinks();
   
         addCorrelationSets(correlationSets);
         addVariables(variables);
         addPartnerLinks(partnerLinks);
      }

      // Add variables and correlation sets from enclosed scopes' compensation
      // infos.
      for (Iterator i = aCompInfo.getEnclosedScopes().iterator(); i.hasNext(); )
      {
         AeCompInfo enclosedScope = (AeCompInfo) i.next();
         addCompInfo(enclosedScope);
      }
   }

   /**
    * Adds the specified correlation set instance to the set that will be
    * returned by <code>getCorrelationSets</code>.
    *
    * @param aCorrelationSet
    */
   protected void addCorrelationSet(AeCorrelationSet aCorrelationSet)
   {
      getCorrelationSets().add(aCorrelationSet);
   }

   /**
    * Adds the specified set of correlation set instances to the set that will
    * be returned by <code>getCorrelationSets</code>.
    *
    * @param aCorrelationSets
    */
   protected void addCorrelationSets(Set aCorrelationSets)
   {
      getCorrelationSets().addAll(aCorrelationSets);
   }

   /**
    * Adds the specified invoke instance to the set of pending invokes that
    * will be returned by <code>getPendingInvokes</code>.
    *
    * @param aInvoke
    */
   protected void addPendingInvoke(AeActivityInvokeImpl aInvoke)
   {
      getPendingInvokes().add(aInvoke);
   }

   /**
    * Adds all live variable and correlation set instances reachable from the
    * specified activity scope implementation object.
    *
    * @param aImpl
    * @param aScopeDef
    */
   protected void addScope(AeActivityScopeImpl aImpl, AeScopeDef aScopeDef) throws AeBusinessProcessException
   {
      // Add the scope's variables.
      for (Iterator i = aScopeDef.getVariableDefs(); i.hasNext(); )
      {
         AeVariableDef variableDef = (AeVariableDef) i.next();
         IAeVariable variable = aImpl.findVariable(variableDef.getName());

         addVariable(variable);
      }

      // Add the scope's correlation sets.
      for (Iterator i = aScopeDef.getCorrelationSetDefs(); i.hasNext(); )
      {
         AeCorrelationSetDef correlationSetDef = (AeCorrelationSetDef) i.next();
         AeCorrelationSet correlationSet = aImpl.findCorrelationSet(correlationSetDef.getName());

         addCorrelationSet(correlationSet);
      }

      if (aImpl.hasCompInfo())
      {
         // Add variables and correlation sets from the scope's compensation
         // info.
         addCompInfo(aImpl.getCompInfo());
      }
   }

   /**
    * Adds the fault variable.
    * @param aImpl
    * @throws AeBusinessProcessException
    */
   protected void addFaultVariable(AeFaultHandler aImpl) throws AeBusinessProcessException
   {
      if ( aImpl.hasFaultVariable() )
      {
         addVariable( aImpl.getFaultVariable() );
      }
   }
      
   
   /**
    * Adds the specified variable instance to the set that will be returned by
    * <code>getVariables</code>.
    *
    * @param aVariable
    */
   protected void addVariable(IAeVariable aVariable)
   {
      getVariables().add(aVariable);
   }

   /**
    * Adds the specified set of variable instances to the set that will be
    * returned by <code>getVariables</code>.
    *
    * @param aVariables
    */
   protected void addVariables(Set aVariables)
   {
      getVariables().addAll(aVariables);
   }
   
   /**
    * Adds the specified set of partner link instances to the set that will be 
    * returned by <code>getPartnerLinks</code>
    * 
    * @param aPartnerLinks
    */
   protected void addPartnerLinks(Set aPartnerLinks)
   {
      getPartnerLinks().addAll(aPartnerLinks);
   }

   /**
    * Returns the <code>Set</code> of <code>AeCorrelationSet</code> instances.
    */
   public Set getCorrelationSets()
   {
      return mCorrelationSets;
   }

   /**
    * Returns the <code>Set</code> of pending <code>AeActivityInvokeImpl</code> instances.
    */
   public Set getPendingInvokes()
   {
      return mPendingInvokes;
   }

   /**
    * Returns the <code>Set</code> of <code>IAeVariable</code> instances.
    */
   public Set getVariables()
   {
      return mVariables;
   }
   
   /**
    * Returns the set of AePartnerLink instances.
    */
   public Set getPartnerLinks()
   {
      return mPartnerLinks;
   }
   
   /** 
    * Overrides method to add fault variables. 
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeWSBPELFaultHandler)
    */
   public void visit(AeWSBPELFaultHandler aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl); 
      addFaultVariable( (AeFaultHandler) aImpl);
   }   

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityInvokeImpl)
    */
   public void visit(AeActivityInvokeImpl aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);   

      if (aImpl.isPending())
      {
         addPendingInvoke(aImpl);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeAbstractImplVisitor#visitScope(org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl)
    */
   protected void visitScope(AeActivityScopeImpl aImpl)
         throws AeBusinessProcessException
   {
      super.visitScope(aImpl);
      AeActivityScopeDef activityScopeDef = (AeActivityScopeDef) aImpl.getDefinition();
      AeScopeDef scopeDef = activityScopeDef.getScopeDef();
      addScope(aImpl, scopeDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.AeBusinessProcess)
    */
   public void visit(AeBusinessProcess aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);

      AeProcessDef processDef = (AeProcessDef) aImpl.getDefinition();
      addScope(aImpl, processDef);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler)
    */
   public void visit(AeCompensationHandler aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);

      AeCompInfo compInfo = aImpl.getCompInfo();
      if (compInfo != null)
      {
         addCompInfo(compInfo);
      }
   }
}
