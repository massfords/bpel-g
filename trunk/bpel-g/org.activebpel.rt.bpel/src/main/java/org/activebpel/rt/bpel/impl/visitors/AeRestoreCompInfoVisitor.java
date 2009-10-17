// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/visitors/AeRestoreCompInfoVisitor.java,v 1.12 2007/11/27 02:49:47 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.visitors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.AeBusinessProcess;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.AeVariable;
import org.activebpel.rt.bpel.impl.IAeDynamicScopeParent;
import org.activebpel.rt.bpel.impl.activity.AeActivityForEachParallelImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.activity.support.AeCompInfo;
import org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeCoordinatorCompInfo;
import org.activebpel.rt.bpel.impl.activity.support.AeCoordinatorCompensationHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeCorrelationSet;
import org.activebpel.rt.bpel.impl.activity.support.AeImplicitCompensationHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeOnEvent;
import org.activebpel.rt.bpel.impl.activity.support.AeRepeatableOnAlarm;
import org.activebpel.rt.bpel.impl.activity.support.AeScopeSnapshot;
import org.activebpel.rt.bpel.impl.storage.AeRestoreImplState;
import org.activebpel.rt.util.AeUtil;
import org.w3c.dom.Element;

/**
 * Visits a tree of BPEL implementation objects to restore {@link AeCompInfo}
 * objects from an instance of <code>AeRestoreImplState</code>.
 */
public class AeRestoreCompInfoVisitor extends AeBaseRestoreVisitor
{
   /**
    * Constructor.
    *
    * @param aImplState The state object from which to restore state.
    */
   public AeRestoreCompInfoVisitor(AeRestoreImplState aImplState)
   {
      super(aImplState);
   }

   /**
    * Creates the compensation info object for the specified implementation
    * object, if the compensation info exists.
    *
    * @param aImpl
    * @return AeCompInfo
    * @throws AeBusinessProcessException
    */
   protected AeCompInfo createCompInfo(AeAbstractBpelObject aImpl) throws AeBusinessProcessException
   {
      Element implElement = getImplState().getElement(aImpl);
      String xpath = "./" + STATE_COMPINFO; //$NON-NLS-1$
      Element compInfoElement = (Element) selectOptionalNode(implElement, xpath, "Error restoring compensation info"); //$NON-NLS-1$

      return (compInfoElement == null) ? null : createCompInfo(compInfoElement);
   }
   
   /**
    * Creates an instance of <code>AeCompInfo</code> from its serialization.
    *
    * @param aElement
    * @return AeCompInfo
    * @throws AeBusinessProcessException
    */
   protected AeCompInfo createCompInfo(Element aElement) throws AeBusinessProcessException
   {
      int id = getAttributeInt(aElement, STATE_ID);
      AeCompInfo compInfo = (AeCompInfo) getObject(id);

      if (compInfo == null)
      {
         // Haven't seen this object id yet. Create the compensation info
         // object.
         AeActivityScopeImpl scope = null;
         // Try to read the location first, then fallback to id if not found
         String scopeLocation = getAttribute(aElement, STATE_SCOPE_LOCATION);
         if (AeUtil.notNullOrEmpty(scopeLocation))
         {
            scope = (AeActivityScopeImpl) getImplState().getProcess().findBpelObject(scopeLocation);
         }
         if (scope == null)
         {
            int scopeLocationId = getAttributeInt(aElement, STATE_SCOPE);
            scope = (AeActivityScopeImpl) getImplState().getProcess().findBpelObject(scopeLocationId);
         }
         
         if (scope == null)
         {
         	// TODO (MF) should defer the restore of the scope until the compInfo executes. This requires reworking the restore of the scope snapshot data below.
         
            // The scope could be null if it references a previous iteration of
            // a forEach that is no longer accessible. Attempt to restore this
            // scope and have it parented by its forEach.
            scopeLocation = getAttribute(aElement, STATE_SCOPE_LOCATION);
            scope = restoreScopeInstance(scopeLocation, true);
         }
         
         boolean hasCoordination = getAttributeBoolean(aElement, STATE_HASCOORDINATIONS);

         if (hasCoordination)
         {
            String coordinationId = getAttribute(aElement, STATE_COORDINATION_ID);
            compInfo = new AeCoordinatorCompInfo(scope, coordinationId);
         }
         else
         {
            compInfo = new AeCompInfo(scope);
         }

         putIdObject(id, compInfo);

         boolean enabled = getAttributeBoolean(aElement, STATE_ENABLED);
         compInfo.setEnabled(enabled);

         if (enabled)
         {
            // This element contains the compensation info object's inner data.
            // Reconstruct the variables, correlation sets, partner links, and enclosed
            // compensation info objects.
            Map variables = createCompInfoVariables(aElement, scope);
            Map correlationSets = createCompInfoCorrelationSets(aElement, scope);
            Map partnerLinks = createCompInfoPartnerLinks(aElement, scope);
            AeScopeSnapshot snapshot = new AeScopeSnapshot(variables, correlationSets, partnerLinks);
            compInfo.setSnapshot(snapshot);

            String xpath = "./" + STATE_COMPINFO; //$NON-NLS-1$
            List enclosedScopeElements = selectNodes(aElement, xpath, "Error restoring enclosed compensation info"); //$NON-NLS-1$
            LinkedList enclosedScopes = new LinkedList();

            for (Iterator i = enclosedScopeElements.iterator(); i.hasNext(); )
            {
               Element element = (Element) i.next();
               AeCompInfo enclosedScope = createCompInfo(element);
               enclosedScope.setParent(compInfo);
               enclosedScopes.addLast(enclosedScope);
            }

            compInfo.setEnclosedScopes(enclosedScopes);
         }
      }

      return compInfo;
   }

   /**
    * Creates a <code>Map</code> of correlation sets from their serialization
    * in a compensation info serialization.
    *
    * @param aCompInfoElement
    * @param aScope
    * @return Map
    * @throws AeBusinessProcessException
    */
   protected Map createCompInfoCorrelationSets(Element aCompInfoElement, AeActivityScopeImpl aScope) throws AeBusinessProcessException
   {
      String xpath = "./" + STATE_CORRSET; //$NON-NLS-1$
      List elements = selectNodes(aCompInfoElement, xpath, "Error restoring compensation info correlation sets"); //$NON-NLS-1$
      Map map = new HashMap();

      for (Iterator i = elements.iterator(); i.hasNext(); )
      {
         Element element = (Element) i.next();
         String name = getAttribute(element, STATE_NAME);

         int versionNumber = getAttributeInt(element, STATE_VERSION);
         AeCorrelationSet correlationSet = (AeCorrelationSet) aScope.findCorrelationSet(name);

         if (versionNumber != correlationSet.getVersionNumber())
         {
            // Different version from the correlation set in the scope. Clone a
            // new correlation set from the scope correlation set.
            correlationSet = (AeCorrelationSet) correlationSet.clone();
            restoreCorrelationSet(element, correlationSet);
         }

         map.put(name, correlationSet);
      }

      return map;
   }

   /**
    * Creates a <code>Map</code> of variables from their serialization in a
    * compensation info serialization.
    *
    * @param aCompInfoElement
    * @param aScope
    * @return Map
    * @throws AeBusinessProcessException
    */
   protected Map createCompInfoVariables(Element aCompInfoElement, AeActivityScopeImpl aScope) throws AeBusinessProcessException
   {
      String xpath = "./" + STATE_VAR; //$NON-NLS-1$
      List elements = selectNodes(aCompInfoElement, xpath, "Error restoring compensation info variables"); //$NON-NLS-1$
      Map map = new HashMap();

      for (Iterator i = elements.iterator(); i.hasNext(); )
      {
         Element element = (Element) i.next();
         String name = getAttribute(element, STATE_NAME);

         int versionNumber = getAttributeInt(element, STATE_VERSION);
         AeVariable variable = (AeVariable) aScope.findVariable(name);

         if (versionNumber != variable.getVersionNumber())
         {
            // Different version from the variable in the scope. Clone a new
            // variable from the scope variable.
            variable = (AeVariable) variable.clone();
            restoreVariable(element, variable);
         }

         map.put(name, variable);
      }

      return map;
   }

   /**
    * Creates a <code>Map</code> of partner links from their serialization in a
    * compensation info serialization.
    * 
    * @param aCompInfoElement
    * @param aScope
    * @throws AeBusinessProcessException
    */
   protected Map createCompInfoPartnerLinks(Element aCompInfoElement, AeActivityScopeImpl aScope) throws AeBusinessProcessException
   {
      String xpath = "./" + STATE_PLINK; //$NON-NLS-1$
      List elements = selectNodes(aCompInfoElement, xpath, "Error restoring compensation info partner links"); //$NON-NLS-1$
      Map map = new HashMap();

      for (Iterator i = elements.iterator(); i.hasNext(); )
      {
         Element element = (Element) i.next();
         String name = getAttribute(element, STATE_NAME);

         int versionNumber = getAttributeInt(element, STATE_VERSION);
         AePartnerLink plink = aScope.findPartnerLink(name);

         if (versionNumber != plink.getVersionNumber())
         {
            // Different version from the partner link in the scope. Clone a new
            // partner link from the scope partner link.
            plink = (AePartnerLink) plink.clone();
            restorePartnerLink(element, plink);
         }

         map.put(name, plink);
      }

      return map;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visitBase(org.activebpel.rt.bpel.impl.AeAbstractBpelObject)
    */
   protected void visitBase(AeAbstractBpelObject aImpl) throws AeBusinessProcessException
   {
      Element element = getImplState().getElement(aImpl);

      boolean skipChildren = getAttributeBoolean(element, STATE_SKIPCHILDREN);
      if (!skipChildren)
      {
         super.visitBase(aImpl);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeAbstractImplVisitor#visitScope(org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl)
    */
   protected void visitScope(AeActivityScopeImpl aImpl)
         throws AeBusinessProcessException
   {
      super.visitScope(aImpl);
      AeCompInfo info = createCompInfo(aImpl);
      aImpl.setCompInfo(info);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.AeBusinessProcess)
    */
   public void visit(AeBusinessProcess aImpl) throws AeBusinessProcessException
   {
      visit((AeActivityScopeImpl) aImpl);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler)
    */
   public void visit(AeCompensationHandler aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);

      AeCompInfo info = createCompInfo(aImpl);
      aImpl.setCompInfo(info);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityForEachParallelImpl)
    */
   public void visit(AeActivityForEachParallelImpl aImpl)
         throws AeBusinessProcessException
   {
      super.visit(aImpl);
      
      restoreCompensatableChildren(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeOnEvent)
    */
   public void visit(AeOnEvent aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);
      restoreCompensatableChildren(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeRepeatableOnAlarm)
    */
   public void visit(AeRepeatableOnAlarm aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);
      restoreCompensatableChildren(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeImplicitCompensationHandler)
    */
   public void visit(AeImplicitCompensationHandler aImpl) throws AeBusinessProcessException
   {
      visit((AeCompensationHandler) aImpl);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeCoordinatorCompensationHandler)
    */
   public void visit(AeCoordinatorCompensationHandler aImpl) throws AeBusinessProcessException
   {
      visit((AeCompensationHandler) aImpl);
   }      

   /**
    * Restores all of the compensatable scopes for dynamic scope parents
    * @param aImpl
    */
   protected void restoreCompensatableChildren(IAeDynamicScopeParent aImpl) throws AeBusinessProcessException
   {
      // now visit the compensatable scopes
      for (Iterator iter = aImpl.getCompensatableChildren().iterator(); iter.hasNext();)
      {
         AeActivityScopeImpl scope = (AeActivityScopeImpl) iter.next();
         scope.accept(this);
      }
   }
}