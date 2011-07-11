//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/AeWSBPELAbstractProcessDefToValidationVisitor.java,v 1.4 2007/09/26 02:21:03 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation;

import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef;
import org.activebpel.rt.bpel.def.validation.process.AeWSBPELAbstractProcessProcessValidator;

/**
 * WSBPEL 2.0 Abstract process validation visitor.O
 */
public class AeWSBPELAbstractProcessDefToValidationVisitor extends AeWSBPELDefToValidationVisitor
{

   /**
    * Ctor
    * @param aContext
    */
   public AeWSBPELAbstractProcessDefToValidationVisitor(IAeValidationContext aContext)
   {
      super(aContext);
   }

   /**
    * Initializes def to model map.
    */
   protected void initMap()
   {
      super.initMap();
      getDefToValidatorMap().put(AeProcessDef.class, AeWSBPELAbstractProcessProcessValidator.class);
      // todo (PJ) add Def.class-to-ValidatorImpl mappings such that:
      // 1) createInstance receive activity is optional
      // 2) if/while/repeat condition can have value of  ##opaque (or empty).
      // 3) plink and operation is optional i.e. attr value can be ##opaque or empty in receives/invoke/onmessage
   }

   /**
    * Overrides method to ignore validation.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef)
    */
   public void visit(AeActivityOpaqueDef aDef)
   {
      // no need to validate opaque activities.
      super.traverse(aDef);
   }
}
