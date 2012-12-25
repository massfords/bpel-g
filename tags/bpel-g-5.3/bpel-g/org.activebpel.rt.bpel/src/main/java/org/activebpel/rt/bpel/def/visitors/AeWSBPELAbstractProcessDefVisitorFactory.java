//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeWSBPELAbstractProcessDefVisitorFactory.java,v 1.3 2008/02/17 21:37:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.bpel.def.validation.AeWSBPELAbstractProcessDefToValidationVisitor;
import org.activebpel.rt.bpel.def.validation.IAeValidationContext;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.xml.def.IAePathSegmentBuilder;

/**
 * Factory for creating def visitors for WS-BPEL 2.0 abstract process defs.
 */
public class AeWSBPELAbstractProcessDefVisitorFactory extends AeWSBPELDefVisitorFactory
{ 
   /**
    * Default ctor.
    */
   public AeWSBPELAbstractProcessDefVisitorFactory()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createPathSegmentBuilder()
    */
   public IAePathSegmentBuilder createPathSegmentBuilder()
   {
      return new AeWSBPELDefPathSegmentVisitor();
   }
   
   /**
    * @param aProcess
    * @param aParent
    */
   public IAeDefToImplVisitor createImplVisitor(IAeBusinessProcessInternal aProcess, IAeBpelObject aParent)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createImplVisitor(long, org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal, org.activebpel.rt.bpel.impl.IAeProcessPlan)
    */
   public IAeDefToImplVisitor createImplVisitor(long aPid, IAeBusinessProcessEngineInternal aEngine, IAeProcessPlan aPlan)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createValidationVisitor(org.activebpel.rt.bpel.def.validation.IAeValidationContext)
    */
   public IAeDefVisitor createValidationVisitor(IAeValidationContext aContext)
   {
      return new AeWSBPELAbstractProcessDefToValidationVisitor(aContext);
   }
}
