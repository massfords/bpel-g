// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/writers/def/AeBPWSWriterVisitor.java,v 1.6 2008/01/11 01:50:47 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.io.writers.def;

import java.util.Collections;
import java.util.Map;

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityIfDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWaitDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWhileDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourceDef;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.def.activity.support.AeVarDef;
import org.activebpel.rt.bpel.def.io.IAeBpelLegacyConstants;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.w3c.dom.Element;

/**
 * An implementation of a writer visitor for bpel4ws 1.1.
 */
public class AeBPWSWriterVisitor extends AeWriterVisitor
{
   /** mapping of namespaces to preferred prefixes */
   private static final Map sPreferredPrefixes = Collections.singletonMap(IAeBPELConstants.BPWS_NAMESPACE_URI, "bpws"); //$NON-NLS-1$
   
   /**
    * Constructs a bpel4ws writer visitor.
    *
    * @param aDef
    * @param aParentElement
    * @param aNamespace
    * @param aTagName
    */
   public AeBPWSWriterVisitor(AeBaseXmlDef aDef, Element aParentElement, String aNamespace, String aTagName)
   {
      super(aDef, aParentElement, aNamespace, aTagName, sPreferredPrefixes);
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#writeAssignVarAttributes(org.activebpel.rt.bpel.def.activity.support.AeVarDef)
    */
   protected void writeAssignVarAttributes(AeVarDef aDef)
   {
      super.writeAssignVarAttributes(aDef);

      setAttribute(TAG_QUERY, aDef.getQuery());
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#writeAssignFromAttributes(org.activebpel.rt.bpel.def.activity.support.AeFromDef)
    */
   protected void writeAssignFromAttributes(AeFromDef aDef)
   {
      super.writeAssignFromAttributes(aDef);

      setAttribute(TAG_EXPRESSION, aDef.getExpression());
      setAttribute(TAG_OPAQUE_ATTR, aDef.isOpaque(), false);
   }

   /**
    * Write attributes to the Element.
    * @param aDef
    */
   protected void writeActivityAttributes( AeActivityDef aDef )
   {
      super.writeActivityAttributes(aDef);

      AeJoinConditionDef joinConditionDef = aDef.getJoinConditionDef();
      if (joinConditionDef != null)
         setAttribute(TAG_JOIN_CONDITION, joinConditionDef.getExpression());
   }

   /**
    * Writes the message exchange value if not empty or null.  Overrides the base in order
    * to put the message exchange attribute in the abx namespace.
    *
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#writeMessageExchange(java.lang.String)
    */
   protected void writeMessageExchange(String aMessageExchangeValue)
   {
      if (AeUtil.notNullOrEmpty(aMessageExchangeValue))
      {
         String prefix = AeXmlUtil.getOrCreatePrefix(getElement(), IAeBPELConstants.ABX_2_0_NAMESPACE_URI);
         getElement().setAttributeNS(IAeBPELConstants.ABX_2_0_NAMESPACE_URI,
                                     prefix + ":" + TAG_MESSAGE_EXCHANGE,  //$NON-NLS-1$
                                     aMessageExchangeValue);
      }
   }
   
   /**
    * Overrides to append the abstractProcess boolean attribute.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      super.visit(aDef);
      setAttribute(TAG_ABSTRACT_PROCESS, aDef.isAbstractProcess(), false);
   }      

   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef)
    */
   public void visit(AeForEachBranchesDef aDef)
   {
      super.visit(aDef);
      setAttribute(IAeBpelLegacyConstants.COUNT_COMPLETED_BRANCHES_ONLY, aDef.isCountCompletedBranchesOnly(), false);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourceDef)
    */
   public void visit(AeSourceDef aDef)
   {
      super.visit(aDef);

      setAttribute(TAG_TRANSITION_CONDITION, aDef.getTransitionCondition());
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWaitDef)
    */
   public void visit(AeActivityWaitDef aDef)
   {
      super.visit(aDef);

      setAttribute(TAG_FOR, aDef.getFor());
      setAttribute(TAG_UNTIL, aDef.getUntil());
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
    */
   public void visit(AeOnAlarmDef aDef)
   {
      super.visit(aDef);

      setAttribute(TAG_FOR, aDef.getFor());
      setAttribute(TAG_UNTIL, aDef.getUntil());
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWhileDef)
    */
   public void visit(AeActivityWhileDef aDef)
   {
      super.visit(aDef);

      setAttribute(TAG_CONDITION, aDef.getConditionDef().getExpression());
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
    */
   public void visit(AeActivityScopeDef aDef)
   {
      super.visit(aDef);

      setAttribute(IAeBpelLegacyConstants.TAG_VARIABLE_ACCESS_SERIALIZABLE, aDef.isIsolated(), false);
   }

   /**
    * Visit the if activity.  Note that in 1.1, the if activity is really a switch activity.  We
    * model it this way in order to have a single model for both 2.0 and 1.1.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityIfDef)
    */
   public void visit(AeActivityIfDef aDef)
   {
      writeAttributes(aDef);
   }

   /**
    * Visits the ifDef to write out its state.  Note that the ifDef in bpel 1.1 is really the first
    * case in a switch.  We model it as an ifDef in order to have a single model for both 1.1 and 2.0.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeIfDef)
    */
   public void visit(AeIfDef aDef)
   {
      visit((AeElseIfDef) aDef);
   }

   /**
    * Visits the elseIfDef to write out its state.  Note that the elseIfDef in bpel 1.1 is really 
    * a case def.  We model it as an elseIfDef in order to have a single model for both 1.1 and 2.0.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
    */
   public void visit(AeElseIfDef aDef)
   {
      writeStandardAttributes(aDef);
      if (aDef.getConditionDef() != null)
         setAttribute(TAG_CONDITION, aDef.getConditionDef().getExpression());
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseDef)
    */
   public void visit(AeElseDef aDef)
   {
      writeStandardAttributes(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef)
    */
   public void visit(AeActivityCompensateScopeDef aDef)
   {
      // Even though this construct doesn't exist in bpel 1.1, we model the <compensate scope="S1" form of the
      // bpel 1.1 activity by using the bpel 2.0 compensateScope def.
      writeAttributes(aDef);
      
      setAttribute(TAG_SCOPE, aDef.getTarget());
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToDef)
    */
   public void visit(AeToDef aDef)
   {
      super.visit(aDef);

      setAttribute(TAG_QUERY, aDef.getQuery());
   }
}
