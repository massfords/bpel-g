// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/readers/def/AeBpelReaderVisitor.java,v 1.6 2008/02/17 21:37:09 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.readers.def;



import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.*;
import org.activebpel.rt.bpel.def.activity.*;
import org.activebpel.rt.bpel.def.activity.support.*;
import org.activebpel.rt.bpel.def.io.AeCorrelationPatternIOFactory;
import org.activebpel.rt.bpel.def.io.IAeCorrelationPatternIO;
import org.activebpel.rt.bpel.def.io.writers.AeCorrelationSetUtil;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeDocumentationDef;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.activebpel.rt.xml.def.AeExtensionElementDef;
import org.activebpel.rt.xml.def.io.readers.AeAbstractReportingDefReader;
import org.activebpel.rt.xml.def.io.readers.IAeReportingDefReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.xml.XMLConstants;
import java.util.List;

/**
 * Responsible for setting all of the properties on the newly created AeDef objects 
 * from the current xml element and adding then def to their parent def.
 */
public abstract class AeBpelReaderVisitor extends AeAbstractReportingDefReader implements IAeReportingDefReader, IAeBPELConstants, IAeDefVisitor
{
   /**
    * Constructor
    * @param aParentDef child will be added to this
    * @param aElement current element to read from
    */
   public AeBpelReaderVisitor( AeBaseDef aParentDef, Element aElement )
   {
      super(aParentDef, aElement);
   }
   

   /**
    * @see org.activebpel.rt.xml.def.io.readers.IAeReportingDefReader#read(org.activebpel.rt.xml.def.AeBaseXmlDef, org.w3c.dom.Element)
    */
   public void read(AeBaseXmlDef aDef, Element aElement)
   {
      aDef.accept(this);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityAssignDef)
    */
   public void visit(AeActivityAssignDef def)
   {
      readAttributes(def);
      addActivityToParent(def);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef)
    */
   public void visit(AeActivityCompensateDef def)
   {
      readAttributes(def);
      addActivityToParent(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef)
    */
   public void visit(AeActivityCompensateScopeDef def)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityEmptyDef)
    */
   public void visit(AeActivityEmptyDef def)
   {
      readAttributes(def);
      addActivityToParent(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityContinueDef)
    */
   public void visit(AeActivityContinueDef def)
   {
      readAttributes(def);
      addActivityToParent(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityBreakDef)
    */
   public void visit(AeActivityBreakDef def)
   {
      readAttributes(def);
      addActivityToParent(def);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityFlowDef)
    */
   public void visit(AeActivityFlowDef def)
   {
      readAttributes(def);
      addActivityToParent(def);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef)
    */
   public void visit(AeActivityInvokeDef def)
   {
      readAttributes(def);
      def.setInputVariable(getAttribute(TAG_INPUT_VARIABLE));
      def.setOutputVariable(getAttribute(TAG_OUTPUT_VARIABLE));

      addActivityToParent(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityPickDef)
    */
   public void visit(AeActivityPickDef def)
   {
      readAttributes(def);
      def.setCreateInstance(getAttributeBoolean(TAG_CREATE_INSTANCE));
      addActivityToParent(def);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
    */
   public void visit(AeActivityReceiveDef def)
   {
      readAttributes(def);
      def.setVariable(getAttribute(TAG_VARIABLE));
      def.setCreateInstance(getAttributeBoolean(TAG_CREATE_INSTANCE));
      def.setMessageExchange(getMessageExchangeValue());
      addActivityToParent(def);
   }

   /**
    * Reads the namespace qualified attribute for message exchange.
    */
   protected abstract String getMessageExchangeValue();

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReplyDef)
    */
   public void visit(AeActivityReplyDef def)
   {
      readAttributes(def);
      def.setVariable(getAttribute(TAG_VARIABLE));
      def.setFaultName(getAttributeQName(TAG_FAULT_NAME));
      def.setMessageExchange(getMessageExchangeValue());
      addActivityToParent(def);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef)
    */
   public void visit(AeActivitySuspendDef def)
   {
      readAttributes(def);
      def.setVariable(getAttribute(TAG_VARIABLE));
      addActivityToParent(def);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
    */
   public void visit(AeActivityScopeDef def)
   {
      readAttributes(def);
      addActivityToParent(def);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef)
    */
   public void visit(AeActivitySequenceDef aDef)
   {
      readAttributes(aDef);
      addActivityToParent(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityExitDef)
    */
   public void visit(AeActivityExitDef aDef)
   {
      readAttributes(aDef);
      addActivityToParent(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityThrowDef)
    */
   public void visit(AeActivityThrowDef aDef)
   {
      readAttributes(aDef);
      aDef.setFaultName(getAttributeQName(TAG_FAULT_NAME));
      aDef.setFaultVariable(getAttribute(TAG_FAULT_VARIABLE));
      addActivityToParent(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWaitDef)
    */
   public void visit(AeActivityWaitDef aDef)
   {
      readAttributes(aDef);

      addActivityToParent(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWhileDef)
    */
   public void visit(AeActivityWhileDef aDef)
   {
      readAttributes(aDef);
      addActivityToParent(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef)
    */
   public void visit(AeActivityRepeatUntilDef aDef)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityForEachDef)
    */
   public void visit(AeActivityForEachDef aDef)
   {
      readAttributes(aDef);

      aDef.setCounterName(getAttribute(TAG_FOREACH_COUNTERNAME));
      aDef.setParallel(getAttributeBoolean(TAG_FOREACH_PARALLEL));
      
      addActivityToParent(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachCompletionConditionDef)
    */
   public void visit(AeForEachCompletionConditionDef aDef)
   {
      readAttributes(aDef);
      ((AeActivityForEachDef)getParentDef()).setCompletionCondition(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachFinalDef)
    */
   public void visit(AeForEachFinalDef aDef)
   {
      readAttributes(aDef);
      readExpressionDef(aDef);
      ((AeActivityForEachDef)getParentDef()).setFinalDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachStartDef)
    */
   public void visit(AeForEachStartDef aDef)
   {
      readAttributes(aDef);
      readExpressionDef(aDef);
      ((AeActivityForEachDef)getParentDef()).setStartDef(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef)
    */
   public void visit(AeForEachBranchesDef aDef)
   {
      readAttributes(aDef);
      readExpressionDef(aDef);
      ((AeForEachCompletionConditionDef)getParentDef()).setBranches(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef)
    */
   public void visit(AeAssignCopyDef aDef)
   {
      readAttributes(aDef);            
      ((AeActivityAssignDef)getParentDef()).addCopyDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromDef)
    */
   public void visit(AeFromDef aDef)
   {
      readAttributes(aDef);
      ((IAeFromParentDef)getParentDef()).setFromDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToDef)
    */
   public void visit(AeToDef aDef)
   {
      readAttributes(aDef);
      ((AeAssignCopyDef)getParentDef()).setToDef(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeQueryDef)
    */
   public void visit(AeQueryDef aDef)
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationsDef)
    */
   public void visit(AeCorrelationsDef aDef)
   {
      readAttributes(aDef);
      ((IAeCorrelationsParentDef) getParentDef()).setCorrelationsDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef)
    */
   public void visit(AeCorrelationDef aDef)
   {
      readAttributes(aDef);
      aDef.setCorrelationSetName(getAttribute(TAG_SET));
      aDef.setInitiate(getAttribute(TAG_INITIATE));
      String pattern = getAttribute(TAG_PATTERN);
      if (AeUtil.notNullOrEmpty(pattern))
      {
         IAeCorrelationPatternIO patternIO = AeCorrelationPatternIOFactory.getInstance(getCurrentElement().getNamespaceURI());
         aDef.setPattern(patternIO.fromString(pattern));
      }
      ((AeCorrelationsDef)getParentDef()).addCorrelationDef(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetsDef)
    */
   public void visit(AeCorrelationSetsDef aDef)
   {
      readAttributes(aDef);
      
      ((IAeCorrelationSetsParentDef) getParentDef()).setCorrelationSetsDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetDef)
    */
   public void visit(AeCorrelationSetDef aDef)
   {
      readAttributes(aDef);
      String rawProperties = getAttribute(TAG_PROPERTIES);
      AeCorrelationSetUtil.addProperties(aDef, rawProperties, getCurrentElement() );
      ((AeCorrelationSetsDef)getParentDef()).addCorrelationSetDef(aDef);
   }
   

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchAllDef)
    */
   public void visit(AeCatchAllDef aDef)
   {
      readAttributes(aDef);
      ((IAeCatchParentDef)getParentDef()).setCatchAllDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeEventHandlersDef)
    */
   public void visit(AeEventHandlersDef aDef)
   {
      readAttributes(aDef);
      ((IAeEventHandlersParentDef) getParentDef()).setEventHandlers(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchDef)
    */
   public void visit(AeCatchDef aDef)
   {
      readAttributes(aDef);
      aDef.setFaultName(getAttributeQName(TAG_FAULT_NAME));
      aDef.setFaultVariable(getAttribute(TAG_FAULT_VARIABLE));

      // could be adding fault handler to an invoke activity or
      // directly to a fault handler container element
      ((IAeCatchParentDef)getParentDef()).addCatchDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeFaultHandlersDef)
    */
   public void visit(AeFaultHandlersDef aDef)
   {
      readAttributes(aDef);
      
      ((IAeFaultHandlersParentDef) getParentDef()).setFaultHandlersDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLinksDef)
    */
   public void visit(AeLinksDef aDef)
   {
      readAttributes(aDef);
      
      ((AeActivityFlowDef) getParentDef()).setLinksDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLinkDef)
    */
   public void visit(AeLinkDef aDef)
   {
      readAttributes( aDef );
      ((AeLinksDef)getParentDef()).addLinkDef(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangesDef)
    */
   public void visit(AeMessageExchangesDef aDef)
   {
      readAttributes( aDef );

      ((IAeMessageExchangesParentDef)getParentDef()).setMessageExchangesDef(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangeDef)
    */
   public void visit(AeMessageExchangeDef aDef)
   {
      readAttributes( aDef );

      aDef.setName(getAttribute(TAG_NAME));
      ((AeMessageExchangesDef) getParentDef()).add(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
    */
   public void visit(AeOnAlarmDef aDef)
   {
      readAttributes(aDef);

      // could be pick activity or event handler
      ((IAeAlarmParentDef)getParentDef()).addAlarmDef(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
    */
   public void visit(AeOnMessageDef aDef)
   {
      readAttributes(aDef);
      readOnMessageOrOnEventAttributes(aDef);

      ((IAeMessageContainerDef) getParentDef()).addOnMessageDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      readAttributes(aDef);
      readOnMessageOrOnEventAttributes(aDef);
      
      aDef.setMessageType(getAttributeQName(TAG_MESSAGE_TYPE));
      aDef.setElement(getAttributeQName(TAG_ELEMENT));

      ((IAeEventContainerDef) getParentDef()).addOnEventDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerDef)
    */
   public void visit(AePartnerDef aDef)
   {
      readAttributes(aDef);

      ((AePartnersDef)getParentDef()).addPartnerDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinkDef)
    */
   public void visit(AePartnerLinkDef aDef)
   {
      readAttributes(aDef);
      aDef.setPartnerLinkTypeName(getAttributeQName(TAG_PARTNER_LINK_TYPE));
      aDef.setMyRole(getAttribute(TAG_MY_ROLE));
      aDef.setPartnerRole(getAttribute(TAG_PARTNER_ROLE));

      // parent could be partnerLinks container def or partner def
      ((IAePartnerLinkParentDef)getParentDef()).addPartnerLinkDef( aDef );
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinksDef)
    */
   public void visit(AePartnerLinksDef aDef)
   {
      readAttributes(aDef);
      
      ((IAePartnerLinksParentDef) getParentDef()).setPartnerLinksDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnersDef)
    */
   public void visit(AePartnersDef aDef)
   {
      readAttributes(aDef);
      
      ((AeProcessDef) getParentDef()).setPartnersDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef def)
   {
      readAttributes(def);
      def.setNamespace(getCurrentElement().getNamespaceURI());
      def.setTargetNamespace(getAttribute(TAG_TARGET_NAMESPACE));
      def.setQueryLanguage(getAttribute(TAG_QUERY_LANGUAGE));
      def.setExpressionLanguage(getAttribute(TAG_EXPRESSION_LANGUAGE));
      def.setSuppressJoinFailure(getAttributeBoolean(TAG_SUPPRESS_JOIN_FAILURE));
      def.setEnableInstanceCompensation(getAttributeBoolean(TAG_ENABLE_INSTANCE_COMPENSATION));
      
      // process def doesn't need to be added to anyone
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCompensationHandlerDef)
    */
   public void visit(AeCompensationHandlerDef aDef)
   {
      readAttributes(aDef);
      
      ((IAeCompensationHandlerParentDef) getParentDef()).setCompensationHandlerDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeScopeDef)
    */
   public void visit(AeScopeDef aDef)
   {
      throw new UnsupportedOperationException(AeMessages.getString("AeReaderVisitor.ERROR_0")); //$NON-NLS-1$
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourcesDef)
    */
   public void visit(AeSourcesDef aDef)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourceDef)
    */
   public void visit(AeSourceDef def)
   {
      readAttributes(def);
      def.setLinkName(getAttribute(TAG_LINK_NAME));
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetsDef)
    */
   public void visit(AeTargetsDef aDef)
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetDef)
    */
   public void visit(AeTargetDef aDef)
   {
      readAttributes(aDef);
      aDef.setLinkName(getAttribute(TAG_LINK_NAME));
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariableDef)
    */
   public void visit(AeVariableDef aDef)
   {
      readAttributes(aDef);
      aDef.setMessageType(getAttributeQName(TAG_MESSAGE_TYPE));
      aDef.setType(getAttributeQName(TAG_TYPE));
      aDef.setElement(getAttributeQName(TAG_ELEMENT));
      ((AeVariablesDef)getParentDef()).addVariableDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariablesDef)
    */
   public void visit(AeVariablesDef aDef)
   {
      readAttributes(aDef);
      
      ((IAeVariablesParentDef) getParentDef()).setVariablesDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityValidateDef)
    */
   public void visit(AeActivityValidateDef aDef)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeImportDef)
    */
   public void visit(AeImportDef aDef)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeDocumentationDef)
    */
   public void visit(AeDocumentationDef aDef)
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeExtensibleAssignDef)
    */
   public void visit(AeExtensibleAssignDef aDef)
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionDef)
    */
   public void visit(AeExtensionDef aDef)
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionsDef)
    */
   public void visit(AeExtensionsDef aDef)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef)
    */
   public void visit(AeFromPartsDef aDef)
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartsDef)
    */
   public void visit(AeToPartsDef aDef)
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartDef)
    */
   public void visit(AeFromPartDef aDef)
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartDef)
    */
   public void visit(AeToPartDef aDef)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef)
    */
   public void visit(AeJoinConditionDef aDef)
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef)
    */
   public void visit(AeTransitionConditionDef aDef)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForDef)
    */
   public void visit(AeForDef aDef)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeUntilDef)
    */
   public void visit(AeUntilDef aDef)
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef)
    */
   public void visit(AeChildExtensionActivityDef aDef)
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionActivityDef)
    */
   public void visit(AeExtensionActivityDef aDef)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityIfDef)
    */
   public void visit(AeActivityIfDef aDef)
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeConditionDef)
    */
   public void visit(AeConditionDef aDef)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseDef)
    */
   public void visit(AeElseDef aDef)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
    */
   public void visit(AeElseIfDef aDef)
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeIfDef)
    */
   public void visit(AeIfDef aDef)
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef)
    */
   public void visit(AeRepeatEveryDef aDef)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef)
    */
   public void visit(AeActivityRethrowDef aDef)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeTerminationHandlerDef)
    */
   public void visit(AeTerminationHandlerDef aDef)
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLiteralDef)
    */
   public void visit(AeLiteralDef aDef)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef)
    */
   public void visit(AeActivityOpaqueDef aDef)
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionAttributeDef)
    */
   public void visit(AeExtensionAttributeDef aDef)
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionElementDef)
    */
   public void visit(AeExtensionElementDef aDef)
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * Set properties on the def object according to its type.
    * @param aCurrentDef
    */
   protected void readAttributes(AeActivityPartnerLinkBaseDef aCurrentDef)
   {
      readCommonAttributes(aCurrentDef);
      readNameAttributes(aCurrentDef);
      readActivityAttributes(aCurrentDef);

      aCurrentDef.setPartnerLink(getAttribute(TAG_PARTNER_LINK));
      aCurrentDef.setPortType(getAttributeQName(TAG_PORT_TYPE));
      aCurrentDef.setOperation(getAttribute(TAG_OPERATION));
   }

   /**
    * Set properties on the def object according to its type.
    * @param aCurrentDef
    */
   protected void readAttributes( AeActivityDef aCurrentDef )
   {
      readCommonAttributes(aCurrentDef);
      readNameAttributes(aCurrentDef);
      readActivityAttributes(aCurrentDef);
   }

   /**
    * Set properties on the def object according to its type.
    * @param aCurrentDef
    */
   protected void readAttributes( AeNamedDef aCurrentDef )
   {
      readCommonAttributes(aCurrentDef);
      readNameAttributes(aCurrentDef);
   }

   /**
    * Sets properties on the assign var def.
    * 
    * @param aVarDef
    */
   protected void readAssignVarDefAttributes(AeVarDef aVarDef)
   {
      readCommonAttributes(aVarDef);
      aVarDef.setVariable(getAttribute(TAG_VARIABLE));
      aVarDef.setProperty(getAttributeQName(TAG_PROPERTY));
      aVarDef.setPart(getAttribute(TAG_PART));
      aVarDef.setPartnerLink(getAttribute(TAG_PARTNER_LINK));
   }

   /**
    * Set properties on the def object according to its type.
    * 
    * @param aToDef
    */
   protected void readAttributes( AeToDef aToDef )
   {
      readAssignVarDefAttributes(aToDef);
   }

   /**
    * Set properties on the def object according to its type.
    * 
    * @param aFromDef
    */
   protected void readAttributes( AeFromDef aFromDef )
   {
      readAssignVarDefAttributes(aFromDef);
      aFromDef.setEndpointReference(getAttribute(TAG_ENDPOINT_REFERENCE));
   }

   /**
    * Clones the element passed in. We don't want to use the standard <code>clone()</code>
    * method since the resulting document literal element would have the bpel namespace.
    * As such, this method needs to make an exact copy of the element but using the null
    * namespace.  In order to do this, we'll make a custom "deep" clone of the element by
    * walking all of its children and recreating them in a new Document.
    * 
    * @param aElem
    */
   protected Element cloneElement(Element aElem)
   {
      try
      {
         Document doc = AeXmlUtil.getDocumentBuilder(true, false).newDocument();
         Element rootElem = (Element) doc.importNode(aElem, false);
         String prefix = aElem.getPrefix();
         if (!AeUtil.isNullOrEmpty(prefix))
         {
            String ns = AeXmlUtil.getNamespaceForPrefix(aElem, prefix);
            rootElem.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:" + prefix, ns); //$NON-NLS-1$
         }
         AeXmlUtil.copyNodeContents(aElem, rootElem);
         return rootElem;
      }
      catch (Exception e)
      {
         /*
          * There shouldn't be any exception thrown here since the element is obviously
          * already well formed (or it wouldn't be in a DOM) so there shouldn't be any
          * issue reading it back in from a string.
          */
         throw new InternalError(AeMessages.getString("AeReaderVisitor.ERROR_3")); //$NON-NLS-1$
      }
   }

   /**
    * Reads the attributes of an onMessage or an onEvent.
    * 
    * @param aDef
    */
   protected void readOnMessageOrOnEventAttributes(AeOnMessageDef aDef)
   {
      aDef.setPartnerLink(getAttribute(TAG_PARTNER_LINK));
      aDef.setPortType(getAttributeQName(TAG_PORT_TYPE));
      aDef.setOperation(getAttribute(TAG_OPERATION));
      aDef.setVariable(getAttribute(TAG_VARIABLE));
      aDef.setMessageExchange(getMessageExchangeValue());
   }

   /**
    * Read in attributes for activity def.
    * @param aCurrentDef the activity def object
    */
   protected void readActivityAttributes(AeActivityDef aCurrentDef)
   {
      aCurrentDef.setSuppressFailure(getAttributeBoolOptional(TAG_SUPPRESS_FAILURE));
   }

   /**
    * Read in attributes for names def.
    * @param aCurrentDef the named def object
    */
   protected void readNameAttributes(AeNamedDef aCurrentDef)
   {
      aCurrentDef.setName( getAttribute(TAG_NAME) );
   }

   /**
    * Adds the activity def to the context parent def object.
    * @param aChildDef
    */
   protected void addActivityToParent(AeActivityDef aChildDef)
   {
       if( getParentDef() instanceof IAeMultipleActivityContainerDef )
       {
          ((IAeMultipleActivityContainerDef)getParentDef()).addActivityDef(aChildDef);   
       }
       else if( getParentDef() instanceof IAeSingleActivityContainerDef )
       {
          IAeSingleActivityContainerDef parent = (IAeSingleActivityContainerDef)getParentDef();
          if (parent.getActivityDef() == null)
          {
             parent.setActivityDef(aChildDef);
          }
          else
          {
             getErrors().add(AeMessages.format("AeReaderVisitor.ErrorAddingChild", AeXMLParserBase.documentToString(getCurrentElement()))); //$NON-NLS-1$
          }
       }
       else
       {
          getErrors().add(AeMessages.format("AeReaderVisitor.ErrorInRegistry", AeXMLParserBase.documentToString(getCurrentElement()))); //$NON-NLS-1$
       }
    }

   /**
    * Convenience method for accessing the parent def object.
    * @return parent def object
    */
   protected AeBaseDef getParentDef()
   {
      return (AeBaseDef) getParentXmlDef();
   }
   
   /**
    * Visits an expression base def in order to read the expression language and expression value.
    * 
    * @param aDef
    */
   protected void readExpressionDef(AeExpressionBaseDef aDef)
   {
      aDef.setExpressionLanguage(getAttribute(TAG_EXPRESSION_LANGUAGE));
      aDef.setExpression(AeXmlUtil.getText(getCurrentElement()));
   }

   /**
    * Adds the children of the given Element to the literal def.  Returns true if any children
    * were added to the literal def.
    * 
    * @param aElement
    * @param aLiteralDef
    */
   protected boolean addChildrenToLiteral(Element aElement, AeLiteralDef aLiteralDef)
   {
      List childNodes = getChildrenForLiteral(aElement);
      if (childNodes.isEmpty())
      {
         Text node = aElement.getOwnerDocument().createTextNode(""); //$NON-NLS-1$
         aLiteralDef.addChildNode(node);
      }
      else
      {
          for (Object childNode : childNodes) {
              Node node = (Node) childNode;
              aLiteralDef.addChildNode(node);
          }
      }
      return AeUtil.notNullOrEmpty(childNodes);
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor#visit(org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   public void visit(AeBaseXmlDef aDef)
   {
      throw new UnsupportedOperationException();
   }
}
