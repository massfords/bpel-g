// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeBPWSDefPathSegmentVisitor.java,v 1.6 2008/02/17 21:37:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.bpel.def.AeExtensionActivityDef;
import org.activebpel.rt.bpel.def.AeMessageExchangeDef;
import org.activebpel.rt.bpel.def.AeMessageExchangesDef;
import org.activebpel.rt.bpel.def.AePartnerDef;
import org.activebpel.rt.bpel.def.AePartnersDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeForDef;
import org.activebpel.rt.bpel.def.activity.support.AeIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeLiteralDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeQueryDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourcesDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetsDef;
import org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeUntilDef;
import org.activebpel.rt.bpel.def.io.IAeBpelLegacyConstants;

/**
 * A def path visitor implementation for BPEL4WS 1.1
 */
public class AeBPWSDefPathSegmentVisitor extends AeAbstractDefPathSegmentVisitor
{
   /**
    * Default c'tor.
    */
   public AeBPWSDefPathSegmentVisitor()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourcesDef)
    */
   public void visit(AeSourcesDef aDef)
   {
      // Skip the implicit 'sources' construct - it isn't really there in bpws 1.1
      setPathSegment(null);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetsDef)
    */
   public void visit(AeTargetsDef aDef)
   {
      // Skip the implicit 'targets' construct - it isn't really there in bpws 1.1
      setPathSegment(null);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefPathSegmentVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef)
    */
   public void visit(AeTransitionConditionDef aDef)
   {
      // Skip the implicit 'transitionCondition' construct - it isn't really there in bpws 1.1
      setPathSegment(null);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefPathSegmentVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef)
    */
   public void visit(AeJoinConditionDef aDef)
   {
      // Skip the implicit 'joinCondition' construct - it isn't really there in bpws 1.1
      setPathSegment(null);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefPathSegmentVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForDef)
    */
   public void visit(AeForDef aDef)
   {
      // Skip the implicit 'for' construct - it isn't really there in bpws 1.1
      setPathSegment(null);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefPathSegmentVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeUntilDef)
    */
   public void visit(AeUntilDef aDef)
   {
      // Skip the implicit 'until' construct - it isn't really there in bpws 1.1
      setPathSegment(null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefPathSegmentVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeConditionDef)
    */
   public void visit(AeConditionDef aDef)
   {
      // Skip the implicit 'condition' construct - it isn't really there in bpws 1.1
      setPathSegment(null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefPathSegmentVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionActivityDef)
    */
   public void visit(AeExtensionActivityDef aDef)
   {
      setPathSegment(null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      // We model onMessage w/in eventHandlers as AeOnEvent even in 1.1.
      setPathSegment(IAeBPELConstants.TAG_ON_MESSAGE);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefPathSegmentVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityIfDef)
    */
   public void visit(AeActivityIfDef aDef)
   {
      // We model switch as if
      setPathSegment(IAeBpelLegacyConstants.TAG_SWITCH);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefPathSegmentVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeIfDef)
    */
   public void visit(AeIfDef aDef)
   {
      // We model the 1st case as an ifdef
      setPathSegment(IAeBpelLegacyConstants.TAG_CASE);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefPathSegmentVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
    */
   public void visit(AeElseIfDef aDef)
   {
      // We model the other cases as elseifs
      setPathSegment(IAeBpelLegacyConstants.TAG_CASE);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefPathSegmentVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseDef)
    */
   public void visit(AeElseDef aDef)
   {
      // We model the otherwise as an else
      setPathSegment(IAeBpelLegacyConstants.TAG_OTHERWISE);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefPathSegmentVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef)
    */
   public void visit(AeActivityCompensateScopeDef aDef)
   {
      // We model the <compensate scope="S1"> activity as a compensateScope def
      setPathSegment(IAeBPELConstants.TAG_COMPENSATE);
   }


   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerDef)
    */
   public void visit(AePartnerDef aDef)
   {
      setPathSegment(IAeBpelLegacyConstants.TAG_PARTNER);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnersDef)
    */
   public void visit(AePartnersDef aDef)
   {
      setPathSegment(IAeBpelLegacyConstants.TAG_PARTNERS);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefPathSegmentVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeQueryDef)
    */
   public void visit(AeQueryDef aDef)
   {
      // Skip the implicit 'query' construct - it isn't really there in bpws 1.1
      setPathSegment(null);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefPathSegmentVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLiteralDef)
    */
   public void visit(AeLiteralDef aDef)
   {
      // Skip the implicit 'literal' construct - it isn't really there in bpws 1.1
      setPathSegment(null);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangesDef)
    */
   public void visit(AeMessageExchangesDef aDef)
   {
      // Skip the implicit construct - since this does not exist in bpws 1.1 and therefore we do not
      // want to assign new location id/path to legacy documents and change the document's id structs.
      setPathSegment(null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangeDef)
    */
   public void visit(AeMessageExchangeDef aDef)
   {
      // Skip the implicit construct - since this does not exist in bpws 1.1 and therefore we do not
      // want to assign new location id/path to legacy documents and change the document's id structs.
      setPathSegment(null);
   }   
}
