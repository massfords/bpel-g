// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/registry/AeBPWSDefReaderRegistry.java,v 1.5 2008/02/17 21:37:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.registry;

import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeMessageExchangeDef;
import org.activebpel.rt.bpel.def.AeMessageExchangesDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.activity.AeActivityIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachCompletionConditionDef;
import org.activebpel.rt.bpel.def.io.IAeBpelLegacyConstants;
import org.activebpel.rt.bpel.def.io.readers.def.AeBPWSReaderVisitor;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.io.readers.AeDelegatingDefReader;
import org.activebpel.rt.xml.def.io.readers.IAeDefReader;
import org.activebpel.rt.xml.def.io.readers.IAeReaderFactory;
import org.activebpel.rt.xml.def.io.readers.IAeReportingDefReader;
import org.w3c.dom.Element;

/**
 * BPEL4WS registry for mapping BPEL elements to the
 * appropriate deserializer.
 * <br />
 * All readers, except activities and the standard
 * activity elements (target/source) are mapped to 
 * reader instances by parent def object and
 * QName.  This allows potential for registering
 * different readers for the same QName based on
 * parenting.
 * <br />
 * In this impl activities and standard elements
 * are mapped directly via QName, however this behavior
 * can be overridden by installing a custom reader 
 * (mapped via the parent class to QName to reader)
 * in the main registry (specifically by overriding
 * the <code>initMainRegistry</code> method).
 */
public class AeBPWSDefReaderRegistry extends AeAbstractBpelReaderRegistry implements IAeBpelLegacyConstants
{
   /**
    * Creates the reader def visitor factory that the dispatch reader will use.
    */
   private static final IAeReaderFactory sFactory = new IAeReaderFactory()
      {
         /**
          * @see org.activebpel.rt.xml.def.io.readers.IAeReaderFactory#createReportingDefReader(org.activebpel.rt.xml.def.AeBaseXmlDef, org.activebpel.rt.xml.def.AeBaseXmlDef, org.w3c.dom.Element)
          */
         public IAeReportingDefReader createReportingDefReader(AeBaseXmlDef aParentDef, AeBaseXmlDef aNewDef, Element aElement)
         {
            return new AeBPWSReaderVisitor((AeBaseDef) aParentDef, aElement);
         }
      };

   /**
    * Default constructor.
    */
   public AeBPWSDefReaderRegistry()
   {
      super(IAeBPELConstants.BPWS_NAMESPACE_URI, sFactory);
   }
   
   /**
    * Maps the parent AeDef class to its child readers based
    * on the child element QNames.
    */
   protected void initParentRegistry()
   {
      super.initParentRegistry();
      
      // maps readers for children of process element
      // exclusive of activity (which is handled sep)
      registerReader(PROCESS_CLASS,  TAG_PARTNERS,             createReader(PARTNERS_CLASS));
      registerReader(PROCESS_CLASS,  TAG_COMPENSATION_HANDLER, createReader(COMPENSATION_HANDLER_CLASS));
      registerReader(PROCESS_CLASS,  TAG_CORRELATION_SETS,     createReader(CORRELATION_SETS_CLASS));

      // maps readers for children of switch activity
      // exclusive of standard readers (source/target)
      registerReader(ACTIVITY_IF_CLASS, TAG_CASE,      new AeSwitchCaseReader(getReaderFactory()));
      registerReader(ACTIVITY_IF_CLASS, TAG_OTHERWISE, createReader(ELSE_CLASS));
      
      registerReader(PARTNERS_CLASS,         TAG_PARTNER,             createReader(PARTNER_CLASS));
      registerReader(PARTNER_CLASS,          TAG_PARTNER_LINK,        createReader(PARTNER_LINK_CLASS));

      // forEach extensions
      registerReader(ACTIVITY_FOREACH_CLASS, new QName(IAeBPELConstants.ABX_2_0_NAMESPACE_URI, TAG_FOREACH_STARTCOUNTER), createReader(ACTIVITY_FOREACH_START));
      registerReader(ACTIVITY_FOREACH_CLASS, new QName(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_FOREACH_STARTCOUNTER), createReader(ACTIVITY_FOREACH_START));
      registerReader(ACTIVITY_FOREACH_CLASS, new QName(IAeBPELConstants.ABX_2_0_NAMESPACE_URI, TAG_FOREACH_FINALCOUNTER), createReader(ACTIVITY_FOREACH_FINAL));
      registerReader(ACTIVITY_FOREACH_CLASS, new QName(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_FOREACH_FINALCOUNTER), createReader(ACTIVITY_FOREACH_FINAL));
      registerReader(ACTIVITY_FOREACH_CLASS, new QName(IAeBPELConstants.ABX_2_0_NAMESPACE_URI, TAG_FOREACH_COMPLETION_CONDITION), createReader(AeForEachCompletionConditionDef.class));
      registerReader(ACTIVITY_FOREACH_CLASS, new QName(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_FOREACH_COMPLETION_CONDITION), createReader(AeForEachCompletionConditionDef.class));
      registerReader(ACTIVITY_FOREACH_COMPLETION_CONDITION, new QName(IAeBPELConstants.ABX_2_0_NAMESPACE_URI, TAG_FOREACH_BRANCHES), createReader(AeForEachBranchesDef.class));
      registerReader(ACTIVITY_FOREACH_COMPLETION_CONDITION, new QName(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_FOREACH_BRANCHES), createReader(AeForEachBranchesDef.class));

      // messageExchange extension
      registerReader(PROCESS_CLASS,           new QName(IAeBPELConstants.ABX_2_0_NAMESPACE_URI, TAG_MESSAGE_EXCHANGES), createReader(AeMessageExchangesDef.class));
      registerReader(ACTIVITY_SCOPE_CLASS,    new QName(IAeBPELConstants.ABX_2_0_NAMESPACE_URI, TAG_MESSAGE_EXCHANGES), createReader(AeMessageExchangesDef.class));
      registerReader(MESSAGE_EXCHANGES_CLASS, new QName(IAeBPELConstants.ABX_2_0_NAMESPACE_URI, TAG_MESSAGE_EXCHANGE), createReader(AeMessageExchangeDef.class));

      // legacy registration for the 2.0 beta that went out w/o a version qualifier in the namespace
      registerReader(PROCESS_CLASS,           new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, TAG_MESSAGE_EXCHANGES), createReader(AeMessageExchangesDef.class));
      registerReader(ACTIVITY_SCOPE_CLASS,    new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, TAG_MESSAGE_EXCHANGES), createReader(AeMessageExchangesDef.class));
      registerReader(MESSAGE_EXCHANGES_CLASS, new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, TAG_MESSAGE_EXCHANGE), createReader(AeMessageExchangeDef.class));
      registerReader(ACTIVITY_FOREACH_CLASS,  new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, TAG_FOREACH_STARTCOUNTER), createReader(ACTIVITY_FOREACH_START));
      registerReader(ACTIVITY_FOREACH_CLASS,  new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, TAG_FOREACH_FINALCOUNTER), createReader(ACTIVITY_FOREACH_FINAL));
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.registry.AeAbstractBpelReaderRegistry#getBpelActivityMappings()
    */
   protected List getBpelActivityMappings()
   {
      List list = super.getBpelActivityMappings();
      list.add(new AeRegistryMapping( makeDefaultQName(TAG_COMPENSATE), new AeCompensateActivityReader(getReaderFactory())));
      list.add(new AeRegistryMapping( makeDefaultQName(IAeBpelLegacyConstants.TAG_TERMINATE), createReader(ACTIVITY_TERMINATE_CLASS) ));
      list.add(new AeRegistryMapping( makeDefaultQName(IAeBpelLegacyConstants.TAG_SWITCH), createReader(ACTIVITY_IF_CLASS) ));
      list.add(new AeRegistryMapping( new QName(IAeBPELConstants.ABX_2_0_NAMESPACE_URI, TAG_FOREACH), createReader(ACTIVITY_FOREACH_CLASS) ));
      list.add(new AeRegistryMapping( new QName(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_FOREACH), createReader(ACTIVITY_FOREACH_CLASS) ));
      // legacy registration for the 2.0 beta that went out w/o a version qualifier in the namespace
      list.add(new AeRegistryMapping( new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, TAG_FOREACH), createReader(ACTIVITY_FOREACH_CLASS) ));
      list.add(new AeRegistryMapping( new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, TAG_BREAK), createReader(ACTIVITY_BREAK_CLASS) ));
      list.add(new AeRegistryMapping( new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, TAG_CONTINUE), createReader(ACTIVITY_CONTINUE_CLASS) ));
      list.add(new AeRegistryMapping( new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, TAG_SUSPEND), createReader(ACTIVITY_SUSPEND_CLASS) ));
      
      return list;
   }

   /**
    * Populates the <code>mStandardReadersMap</code> with the
    * target and source element readers.
    */
   protected void initCommonActivityChildrenElements()
   {
      IAeDefReader targetReader = createReader(TARGET_CLASS);
      IAeDefReader sourceReader = createReader(SOURCE_CLASS);

      Class[] activities = 
      {
            ACTIVITY_ASSIGN_CLASS,
            ACTIVITY_BREAK_CLASS,
            ACTIVITY_COMPENSATE_CLASS,
            ACTIVITY_COMPENSATE_SCOPE_CLASS,
            ACTIVITY_CONTINUE_CLASS,
            ACTIVITY_EMPTY_CLASS,
            ACTIVITY_EXIT_CLASS,
            ACTIVITY_FLOW_CLASS,
            ACTIVITY_FOREACH_CLASS,
            ACTIVITY_IF_CLASS,
            ACTIVITY_INVOKE_CLASS,
            ACTIVITY_PICK_CLASS,
            ACTIVITY_RECEIVE_CLASS,
            ACTIVITY_REPLY_CLASS,
            ACTIVITY_REPLY_CLASS,
            ACTIVITY_RETHROW_CLASS,
            ACTIVITY_SCOPE_CLASS,
            ACTIVITY_SEQUENCE_CLASS,
            ACTIVITY_SUSPEND_CLASS,
            ACTIVITY_THROW_CLASS,
            ACTIVITY_WAIT_CLASS,
            ACTIVITY_WHILE_CLASS,
      };
      
      for (int i = 0; i < activities.length; i++)
      {
         registerReader(activities[i],  TAG_TARGET, targetReader);
         registerReader(activities[i],  TAG_SOURCE, sourceReader);
      }
   }

   /**
    * A specialty reader for reading in the 'case' child of a switch activity.  This is needed
    * because we create a different def for the first case in the list.
    */
   protected class AeSwitchCaseReader extends AeDelegatingDefReader
   {
      /**
       * ctor
       * @param aReaderFactory
       */
      public AeSwitchCaseReader(IAeReaderFactory aReaderFactory)
      {
         super(null, aReaderFactory);
      }

      /**
       * @see org.activebpel.rt.xml.def.io.readers.AeDelegatingDefReader#createChild(org.activebpel.rt.xml.def.AeBaseXmlDef, org.w3c.dom.Element)
       */
      protected AeBaseXmlDef createChild(AeBaseXmlDef aParent, Element aElement) throws AeException
      {
         // If this is the first case child - make it the 'if'.  Otherwise go with an 'elseif'.
         AeActivityIfDef ifActivityDef = (AeActivityIfDef) aParent;
         if (ifActivityDef.getIfDef() == null)
         {
            setChildClass(IF_CLASS);
         }
         else
         {
            setChildClass(ELSEIF_CLASS);
         }
         return super.createChild(aParent, aElement);
      }
   }
   /**
    * Special reader for the compensate activity.  If the compensate activity has a 'scope' attribute,
    * then we will model it using the BPEL 2.0 compensateScope def.
    */
   protected static class AeCompensateActivityReader extends AeDelegatingDefReader
   {
      /**
       * ctor.
       * @param aReaderFactory
       */
      public AeCompensateActivityReader(IAeReaderFactory aReaderFactory)
      {
         super(null, aReaderFactory);
      }
      
      /**
       * @see org.activebpel.rt.xml.def.io.readers.AeDelegatingDefReader#createChild(org.activebpel.rt.xml.def.AeBaseXmlDef, org.w3c.dom.Element)
       */
      protected AeBaseXmlDef createChild(AeBaseXmlDef aParent, Element aElement) throws AeException
      {
         // If the compensate activity has a 'scope' attribute, then model it as a compensateScope
         // def...otherwise use the compensate def.
         if (aElement.hasAttribute(TAG_SCOPE))
         {
            setChildClass(ACTIVITY_COMPENSATE_SCOPE_CLASS);
         }
         else
         {
            setChildClass(ACTIVITY_COMPENSATE_CLASS);
         }

         return super.createChild(aParent, aElement);
      }
   }
}
