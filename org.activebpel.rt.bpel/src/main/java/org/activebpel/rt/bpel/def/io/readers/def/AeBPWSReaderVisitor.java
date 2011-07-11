// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/readers/def/AeBPWSReaderVisitor.java,v 1.7 2007/09/26 02:21:03 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.readers.def;

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeExtensionActivityDef;
import org.activebpel.rt.bpel.def.AePartnerLinksDef;
import org.activebpel.rt.bpel.def.AePartnersDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.activity.AeActivityBreakDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityContinueDef;
import org.activebpel.rt.bpel.def.activity.AeActivityFlowDef;
import org.activebpel.rt.bpel.def.activity.AeActivityIfDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWaitDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWhileDef;
import org.activebpel.rt.bpel.def.activity.support.AeConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeForDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeLinksDef;
import org.activebpel.rt.bpel.def.activity.support.AeLiteralDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.activity.support.AeQueryDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourceDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetDef;
import org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeUntilDef;
import org.activebpel.rt.bpel.def.activity.support.AeVarDef;
import org.activebpel.rt.bpel.def.io.AeBPWSUtil;
import org.activebpel.rt.bpel.def.io.IAeBpelLegacyConstants;
import org.activebpel.rt.util.AeUtil;
import org.w3c.dom.Element;

/**
 * A BPEL4WS 1.1 implementation of a reader visitor.
 */
public class AeBPWSReaderVisitor extends AeBpelReaderVisitor
{
   /**
    * Constructor.
    *
    * @param aParentDef child will be added to this
    * @param aElement current element to read from
    */
   public AeBPWSReaderVisitor( AeBaseDef aParentDef, Element aElement )
   {
      super(aParentDef, aElement);
   }

   /**
    * Overrides to parse the 'abstractProcess' attribute.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      super.visit(aDef);
      
      aDef.setAbstractProcess(getAttributeBoolean(TAG_ABSTRACT_PROCESS));

      // Note: for legacy reasons, some container defs must always be created.  Old 
      // versions of the engine created these containers as part of the ProcessDef
      // constructor.  These containers are needed so that the same set of location 
      // paths are created in 3.0 as 2.1.  If a different set of paths are created
      // then the location path IDs will not be correct, and we will have problems
      // in persistence.
      aDef.setPartnersDef(new AePartnersDef());
      aDef.setPartnerLinksDef(new AePartnerLinksDef());
   }   
   
   /**
    * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#readAssignVarDefAttributes(org.activebpel.rt.bpel.def.activity.support.AeVarDef)
    */
   protected void readAssignVarDefAttributes(AeVarDef aVarDef)
   {
      super.readAssignVarDefAttributes(aVarDef);

      String query = getAttribute(TAG_QUERY);
      if (AeUtil.notNullOrEmpty(query))
      {
         AeQueryDef queryDef = new AeQueryDef();
         queryDef.setQuery(query);
         aVarDef.setQueryDef(queryDef);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#readAttributes(org.activebpel.rt.bpel.def.activity.support.AeFromDef)
    */
   protected void readAttributes(AeFromDef aFromDef)
   {
      super.readAttributes(aFromDef);

      aFromDef.setOpaque(getAttributeBoolean(TAG_OPAQUE_ATTR));
      aFromDef.setExpression(getAttribute(TAG_EXPRESSION));
      checkForLiteral(aFromDef);
   }

   /**
    * Custom readChildren impl to account for literal value.
    *
    * @param aDef the var def impl
    */
   protected void checkForLiteral(AeFromDef aDef)
   {
      AeLiteralDef literalDef = new AeLiteralDef();
      if (addChildrenToLiteral(getCurrentElement(), literalDef))
      {
         aDef.setLiteralDef(literalDef);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#readActivityAttributes(org.activebpel.rt.bpel.def.AeActivityDef)
    */
   protected void readActivityAttributes(AeActivityDef aCurrentDef)
   {
      super.readActivityAttributes(aCurrentDef);

      AeBPWSUtil.setJoinConditionOnActivity(getAttribute(TAG_JOIN_CONDITION), null, aCurrentDef);
   }

   /**
    * Reads the namespace qualified attribute for message exchange.
    */
   protected String getMessageExchangeValue()
   {
      String value = getAttributeNS(IAeBPELConstants.ABX_2_0_NAMESPACE_URI, TAG_MESSAGE_EXCHANGE);
      if (value.equals("")) //$NON-NLS-1$
      {
         // sadly, try w/o the namespace to handle legacy case from 2.0 beta where the messageExchange attribute wasn't NS qualified
         value = getAttribute(TAG_MESSAGE_EXCHANGE);
      }
      return value;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityFlowDef)
    */
   public void visit(AeActivityFlowDef aDef)
   {
      super.visit(aDef);
      
      // Note: for legacy reasons, some container defs must always be created. Old
      // versions of the engine always created these containers These containers
      // are needed so that the same set of location paths are created in 3.0 as
      // 2.1. If a different set of paths are created then the location path IDs
      // will not be correct, and we will have problems in persistence.
      aDef.setLinksDef(new AeLinksDef());
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourceDef)
    */
   public void visit(AeSourceDef aDef)
   {
      super.visit(aDef);
      
      String transCondExpression = getAttribute(TAG_TRANSITION_CONDITION);
      if (AeUtil.notNullOrEmpty(transCondExpression))
      {
         AeTransitionConditionDef transCondDef = new AeTransitionConditionDef();
         transCondDef.setExpression(transCondExpression);
         aDef.setTransitionConditionDef(transCondDef);
      }

      AeBPWSUtil.addSourceToActivity(aDef, (AeActivityDef) getParentDef());
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetDef)
    */
   public void visit(AeTargetDef aDef)
   {
      super.visit(aDef);
      
      AeBPWSUtil.addTargetToActivity(aDef, (AeActivityDef) getParentDef());
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWhileDef)
    */
   public void visit(AeActivityWhileDef aDef)
   {
      super.visit(aDef);
      
      String condition = getAttribute(TAG_CONDITION);
      if (AeUtil.notNullOrEmpty(condition))
      {
         AeConditionDef condDef = new AeConditionDef();
         condDef.setExpression(condition);
         aDef.setConditionDef(condDef);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
    */
   public void visit(AeActivityScopeDef aDef)
   {
      super.visit(aDef);
      
      aDef.setVariableAccessSerializable(getAttributeBoolean(IAeBpelLegacyConstants.TAG_VARIABLE_ACCESS_SERIALIZABLE));
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
    */
   public void visit(AeOnAlarmDef aDef)
   {
      super.visit(aDef);
      
      String forExpression = getAttribute(TAG_FOR);
      if (AeUtil.notNullOrEmpty(forExpression))
      {
         AeForDef forDef = new AeForDef();
         forDef.setExpression(forExpression);
         aDef.setForDef(forDef);
      }

      String untilExpression = getAttribute(TAG_UNTIL);
      if (AeUtil.notNullOrEmpty(untilExpression))
      {
         AeUntilDef untilDef = new AeUntilDef();
         untilDef.setExpression(untilExpression);
         aDef.setUntilDef(untilDef);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWaitDef)
    */
   public void visit(AeActivityWaitDef aDef)
   {
      super.visit(aDef);
      
      readAttributes(aDef);
      String forExpression = getAttribute(TAG_FOR);
      if (AeUtil.notNullOrEmpty(forExpression))
      {
         AeForDef forDef = new AeForDef();
         forDef.setExpression(forExpression);
         aDef.setForDef(forDef);
      }
      String untilExpression = getAttribute(TAG_UNTIL);
      if (AeUtil.notNullOrEmpty(untilExpression))
      {
         AeUntilDef untilDef = new AeUntilDef();
         untilDef.setExpression(untilExpression);
         aDef.setUntilDef(untilDef);
      }
   }

   /**
    * Note that the if activity is simply a convenient way to model a bpel 1.1 switch activity.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityIfDef)
    */
   public void visit(AeActivityIfDef aDef)
   {
      readAttributes(aDef);
      addActivityToParent(aDef);
   }

   /**
    * Reads in the condition attribute for an elseIf def.  Note that the else if def, in this case,
    * is really a switchCase def.  We simply model the switchCase as an elseIf.  In addition, this
    * method gets called for both ifDef and elseIfDef.  The reason is that the first switchCase is
    * modelled as an ifDef, and the rest of the switchCases are modelled as elseIfs.
    * 
    * @param aDef
    */
   protected void readElseIfCondition(AeElseIfDef aDef)
   {
      String conditionExpression = getAttribute(TAG_CONDITION);
      if (AeUtil.notNullOrEmpty(conditionExpression))
      {
         AeConditionDef conditionDef = new AeConditionDef();
         conditionDef.setExpression(conditionExpression);
         aDef.setConditionDef(conditionDef);
      }
   }
   
   /**
    * Note that this is really just the first switchCase, which we are modelling as an if.
    * 
    * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeIfDef)
    */
   public void visit(AeIfDef aDef)
   {
      readAttributes(aDef);
      readElseIfCondition(aDef);
      ((AeActivityIfDef) getParentDef()).setIfDef(aDef);
   }

   /**
    * Note that this is really a switchCase that gets modelled as an elseIf.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
    */
   public void visit(AeElseIfDef aDef)
   {
      readAttributes(aDef);
      readElseIfCondition(aDef);
      ((AeActivityIfDef) getParentDef()).addElseIfDef(aDef);
   }

   /**
    * Note that this is really a switchOtherwise that we are modelling as an else.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseDef)
    */
   public void visit(AeElseDef aDef)
   {
      readAttributes(aDef);
      ((AeActivityIfDef)getParentDef()).setElseDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef)
    */
   public void visit(AeForEachBranchesDef aDef)
   {
      super.visit(aDef);
      
      aDef.setCountCompletedBranchesOnly(getAttributeBoolean(IAeBpelLegacyConstants.COUNT_COMPLETED_BRANCHES_ONLY));
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef)
    */
   public void visit(AeActivityCompensateScopeDef aDef)
   {
      readAttributes(aDef);
      aDef.setTarget(getAttribute(TAG_SCOPE));
      addActivityToParent(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityBreakDef)
    */
   public void visit(AeActivityBreakDef aDef)
   {
      readAeExtensionActivity(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityContinueDef)
    */
   public void visit(AeActivityContinueDef aDef)
   {
      readAeExtensionActivity(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef)
    */
   public void visit(AeActivitySuspendDef aDef)
   {
      readAeExtensionActivity(aDef);
   }

   /**
    * Reads an active-endpoints extension activity.
    * 
    * @param aDef
    */
   protected void readAeExtensionActivity(AeActivityDef aDef)
   {
      readAttributes(aDef);
      AeExtensionActivityDef extensionActivityDef = new AeExtensionActivityDef();
      extensionActivityDef.setActivityDef(aDef);
      addActivityToParent(extensionActivityDef);
   }
}
