//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeDefToBPWSImplVisitor.java,v 1.3 2006/10/26 13:36:12 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import org.activebpel.rt.bpel.def.AeCorrelationSetDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.faults.AeFaultMatchingStrategyFactory;
import org.activebpel.rt.bpel.impl.AeBPELMessageDataValidator;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.bpel.impl.IAeMessageValidator;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.impl.activity.IAeScopeTerminationStrategy;
import org.activebpel.rt.bpel.impl.activity.support.AeCorrelationSet;
import org.activebpel.rt.bpel.impl.activity.support.AeIMACorrelations;
import org.activebpel.rt.bpel.impl.activity.support.AeIMACorrelations.IAeCorrelationSetFilter;

/**
 * Creates impl objects for BPEL4WS 1.1 
 */
public class AeDefToBPWSImplVisitor extends AeDefToImplVisitor
{
   /** strategy for terminating a 1.1 scope */
   private static final IAeScopeTerminationStrategy BPEL4WS_ScopeTerminationStrategy = new AeBPWSScopeTerminationStrategy();

   /** message validator allows empty parts */
   private static final IAeMessageValidator MESSAGE_VALIDATOR = new AeBPELMessageDataValidator(true);

   /**
    * Constructor - inits the visitor
    * @param aPid - id of the process you want to create
    * @param aEngine - engine that owns the process
    * @param aPlan - plan for the process deployment
    */
   public AeDefToBPWSImplVisitor(long aPid,
         IAeBusinessProcessEngineInternal aEngine, IAeProcessPlan aPlan)
   {
      super(aPid, aEngine, aPlan);
      init();
   }

   /**
    * Constructor - inits the visitor
    * @param aProcess - the process that will own the newly created objects
    * @param aParent - the parent object for any newly created activities
    */
   public AeDefToBPWSImplVisitor(IAeBusinessProcessInternal aProcess,
         IAeBpelObject aParent)
   {
      super(aProcess, aParent);
      init();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeDefToImplVisitor#createTraverser()
    */
   protected IAeDefVisitor createTraverser()
   {
      return new AeTraversalVisitor(new AeImplementationTraverser(), this);
   }

   /**
    * sets the strategies used for the scope
    */
   protected void init()
   {
      setScopeTerminationStrategy(BPEL4WS_ScopeTerminationStrategy);
      setFaultMatchingStrategy(AeFaultMatchingStrategyFactory.getInstance(IAeBPELConstants.BPWS_NAMESPACE_URI));
      setMessageValidator(MESSAGE_VALIDATOR);
   }

   /**
    * Create the correlation set implementation object and add it to the scope.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetDef)
    */
   public void visit(AeCorrelationSetDef aDef)
   {
      AeCorrelationSet set = new AeCorrelationSet(aDef, getScope());
      getScope().addCorrelationSet(set);
      traverse(aDef, set);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeDefToImplVisitor#getCorrelationsFilter()
    */
   protected IAeCorrelationSetFilter getCorrelationsFilter()
   {
      return AeIMACorrelations.ALL;
   }
}
 