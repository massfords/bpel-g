// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeLocationPathImplVisitor.java,v 1.8 2008/02/17 21:37:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity;

import java.util.Iterator;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.visitors.AeDefVisitorFactory;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.AeBusinessProcess;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.AeVariable;
import org.activebpel.rt.bpel.impl.activity.support.AeCorrelationSet;
import org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor;
import org.activebpel.rt.xml.def.IAePathSegmentBuilder;

/**
 * Visits each of the implementation objects and sets a custom location path
 * and id on each object. 
 */
public class AeLocationPathImplVisitor extends AeImplTraversingVisitor
{
   /** reference to the process since we might be asked to report all of the mappings */
   protected AeBusinessProcess mProcess;
   /** reference to the scope instance from the forEach */
   protected AeActivityScopeImpl mScope;
   /** builds paths for the dynamic scopes */
   private AeImplLocationPathBuilder mPathBuilder;
   
   /**
    * Creates the visitor and starts the process
    * 
    * @param aProcess
    * @param aImpl
    * @param aInstanceValue
    * @param aCreateMode
    */
   public AeLocationPathImplVisitor(AeBusinessProcess aProcess, AeActivityScopeImpl aImpl, int aInstanceValue, boolean aCreateMode)
   {
      mProcess = aProcess;
      IAePathSegmentBuilder segmentBuilder = AeDefVisitorFactory.getInstance(aProcess.getBPELNamespace()).createPathSegmentBuilder();
      mPathBuilder = new AeImplLocationPathBuilder(segmentBuilder, aImpl.getDefinition(), aCreateMode);
      mPathBuilder.setPath(aImpl.getParent().getLocationPath());
      mPathBuilder.setNextLocationId(aProcess.getMaxLocationId() + 1);
      mPathBuilder.setInstanceValue(aInstanceValue);
      mScope = aImpl;
   }
   
   /**
    * Visits the scope's def with the path generator and then the impl visitor
    * sets the newly generated paths.
    */
   public void startVisiting()
   {
      mScope.getDefinition().accept(mPathBuilder);
      try
      {
         mScope.accept(this);
      }
      catch (AeBusinessProcessException e)
      {
         // the impl visitors all throw AeBusinessProcessException. 
         // There shouldn't be any errors in setting the paths. The exception is related more to the I/O layer which 
         // reads and writes the impl state to a DOM.
         e.logError();
      }
      if (mPathBuilder.isCreateMode())
      {
         // after visiting all defs, update the process's maxLocationId with the next id - 1
         mProcess.setMaxLocationId(mPathBuilder.getNextLocationId() -1);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeAbstractImplVisitor#visitScope(org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl)
    */
   protected void visitScope(AeActivityScopeImpl aImpl) throws AeBusinessProcessException
   {
      super.visitScope(aImpl);
      AeActivityScopeDef def = (AeActivityScopeDef) aImpl.getDefinition();
      if (def.getScopeDef().hasVariables())
      {
         for (Iterator iter = aImpl.getVariableContainer().iterator(); iter.hasNext();)
         {
            AeVariable v = (AeVariable) iter.next();
            setVariablePath(v);
         }
      }
      
      if (def.getScopeDef().hasCorrelationSets())
      {
         for (Iterator iter = aImpl.getCorrelationSetMap().values().iterator(); iter.hasNext();)
         {
            AeCorrelationSet corrSet = (AeCorrelationSet) iter.next();
            setCorrelationSetPath(corrSet);
         }
      }

      for (Iterator iter = aImpl.getPartnerLinks().values().iterator(); iter.hasNext(); )
      {
         AePartnerLink plink = (AePartnerLink) iter.next();
         setPartnerLinkPath(plink);
      }
   }
   
   
   /**
    * Sets the variable's location path
    * @param aVariable
    */
   protected void setVariablePath(AeVariable aVariable)
   {
      String path = mPathBuilder.getLocationPath(aVariable.getDefinition());
      aVariable.setLocationPath(path);
   }

   /**
    * Sets the correlation set's location path.
    */
   protected void setCorrelationSetPath(AeCorrelationSet aCorrelationSet)
   {
      String path = mPathBuilder.getLocationPath(aCorrelationSet.getDefinition());
      aCorrelationSet.setLocationPath(path);
   }

   /**
    * Sets the partner link's location path
    * 
    * @param aPartnerLink
    */
   protected void setPartnerLinkPath(AePartnerLink aPartnerLink)
   {
      String path = mPathBuilder.getLocationPath(aPartnerLink.getDefinition());
      aPartnerLink.setLocationPath(path);
   }
   
   /**
    * Sets the bpel object's location path and id and then traverses.
    * 
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visitBase(org.activebpel.rt.bpel.impl.AeAbstractBpelObject)
    */
   protected void visitBase(AeAbstractBpelObject aImpl)
         throws AeBusinessProcessException
   {
      String path = mPathBuilder.getLocationPath(aImpl.getDefinition());
      aImpl.setLocationPath(path);
      int id = mPathBuilder.getLocationId(path);
      aImpl.setLocationId(id);

      super.visitBase(aImpl);
   }
}