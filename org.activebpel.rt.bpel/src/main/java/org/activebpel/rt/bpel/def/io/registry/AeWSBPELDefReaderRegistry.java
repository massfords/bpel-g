// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/registry/AeWSBPELDefReaderRegistry.java,v 1.16 2008/02/29 18:20:43 dvilaverde Exp $
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.io.registry;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeExtensionActivityDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef;
import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeQueryDef;
import org.activebpel.rt.bpel.def.io.readers.def.AeWSBPELReaderVisitor;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.io.readers.IAeDefReader;
import org.activebpel.rt.xml.def.io.readers.IAeReaderFactory;
import org.activebpel.rt.xml.def.io.readers.IAeReportingDefReader;
import org.w3c.dom.Element;

/**
 * An implementation of the Def Reader Registry for WS-BPEL 2.0. This implementation extends the BPEL4WS 1.1
 * implementation and then adds and/or removes mappings as needed to account for the differences between the
 * two variants of BPEL.
 */
public class AeWSBPELDefReaderRegistry extends AeAbstractBpelReaderRegistry
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
            return new AeWSBPELReaderVisitor((AeBaseDef)aParentDef, aElement);
         }
      };

   /** A reader to use when we don't recognize the child of an extensionActivity. */
   private IAeDefReader mChildExtensionActivityReader;
   
   /**
    * Default c'tor.
    */
   public AeWSBPELDefReaderRegistry()
   {
      super(IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI, sFactory);
   }
   
   /**
    * Ctor
    * @param aDefaultNamespace
    * @param aFactory
    */
   protected AeWSBPELDefReaderRegistry(String aDefaultNamespace, IAeReaderFactory aFactory)
   {
      super(aDefaultNamespace, aFactory);
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.registry.AeBPWSDefReaderRegistry#initParentRegistry()
    */
   protected void initParentRegistry()
   {
      super.initParentRegistry();

      registerReader(ACTIVITY_SCOPE_CLASS,  TAG_PARTNER_LINKS,createReader(PARTNER_LINKS_CLASS));

      registerReader(PROCESS_CLASS, TAG_IMPORT, createReader(IMPORT_CLASS));
      registerReader(ACTIVITY_ASSIGN_CLASS, TAG_EXTENSION_ASSIGN_OPERATION, createReader(ASSIGN_EXTENSIBLE_ASSIGN_CLASS));
      registerReader(VARIABLE_CLASS, TAG_FROM, createReader(ASSIGN_FROM_CLASS));
      registerReader(PROCESS_CLASS, TAG_EXTENSIONS, createReader(EXTENSIONS_CLASS));
      registerReader(EXTENSIONS_CLASS, TAG_EXTENSION, createReader(EXTENSION_CLASS));

      registerReader(ACTIVITY_INVOKE_CLASS, TAG_FROM_PARTS, createReader(FROM_PARTS_CLASS));
      registerReader(ACTIVITY_RECEIVE_CLASS, TAG_FROM_PARTS, createReader(FROM_PARTS_CLASS));
      registerReader(ON_MESSAGE_CLASS, TAG_FROM_PARTS, createReader(FROM_PARTS_CLASS));
      registerReader(ON_EVENT_CLASS, TAG_FROM_PARTS, createReader(FROM_PARTS_CLASS));

      registerReader(ACTIVITY_INVOKE_CLASS, TAG_TO_PARTS, createReader(TO_PARTS_CLASS));
      registerReader(ACTIVITY_REPLY_CLASS, TAG_TO_PARTS, createReader(TO_PARTS_CLASS));

      registerReader(TO_PARTS_CLASS, TAG_TO_PART, createReader(TO_PART_CLASS));
      registerReader(FROM_PARTS_CLASS, TAG_FROM_PART, createReader(FROM_PART_CLASS));

      // 'target' and 'source' constructs will be children of the 'targets' and 'sources' constructs, respectively.
      registerReader(TARGETS_CLASS, TAG_TARGET, createReader(TARGET_CLASS));
      registerReader(SOURCES_CLASS, TAG_SOURCE, createReader(SOURCE_CLASS));

      registerReader(TARGETS_CLASS, TAG_JOIN_CONDITION, createReader(JOIN_CONDITION_CLASS));
      registerReader(SOURCE_CLASS, TAG_TRANSITION_CONDITION, createReader(TRANSITION_CONDITION_CLASS));
      
      // Add the ws-bpel 2.0 messageExchange mappings
      registerReader(PROCESS_CLASS, TAG_MESSAGE_EXCHANGES, createReader(MESSAGE_EXCHANGES_CLASS));
      registerReader(ACTIVITY_SCOPE_CLASS, TAG_MESSAGE_EXCHANGES, createReader(MESSAGE_EXCHANGES_CLASS));
      registerReader(MESSAGE_EXCHANGES_CLASS, TAG_MESSAGE_EXCHANGE, createReader(MESSAGE_EXCHANGE_CLASS));

      registerReader(ACTIVITY_WAIT_CLASS, TAG_FOR, createReader(FOR_CLASS));
      registerReader(ACTIVITY_WAIT_CLASS, TAG_UNTIL, createReader(UNTIL_CLASS));
      registerReader(ON_ALARM_CLASS, TAG_FOR, createReader(FOR_CLASS));
      registerReader(ON_ALARM_CLASS, TAG_UNTIL, createReader(UNTIL_CLASS));

      registerReader(ACTIVITY_IF_CLASS, TAG_CONDITION, createReader(CONDITION_CLASS));
      registerReader(ACTIVITY_IF_CLASS, TAG_ELSEIF, createReader(ELSEIF_CLASS));
      registerReader(ACTIVITY_IF_CLASS, TAG_ELSE, createReader(ELSE_CLASS));
      registerReader(ELSEIF_CLASS, TAG_CONDITION, createReader(CONDITION_CLASS));

      registerReader(ACTIVITY_WHILE_CLASS, TAG_CONDITION, createReader(CONDITION_CLASS));
      registerReader(ACTIVITY_REPEAT_UNTIL_CLASS, TAG_CONDITION, createReader(CONDITION_CLASS));

      registerReader(ON_ALARM_CLASS, TAG_REPEAT_EVERY, createReader(REPEAT_EVERY_CLASS));

      registerReader(EVENT_HANDLERS_CLASS, TAG_ON_EVENT, createReader(ON_EVENT_CLASS));

      registerReader(ACTIVITY_SCOPE_CLASS, TAG_TERMINATION_HANDLER, createReader(TERMINATION_HANDLER_CLASS));

      registerReader(ASSIGN_FROM_CLASS, TAG_LITERAL, createReader(LITERAL_CLASS));

      // Register BPEL 2.0 entries for foreach elements
      registerReader(ACTIVITY_FOREACH_CLASS, TAG_FOREACH_STARTCOUNTER, createReader(ACTIVITY_FOREACH_START));
      registerReader(ACTIVITY_FOREACH_CLASS, TAG_FOREACH_FINALCOUNTER, createReader(ACTIVITY_FOREACH_FINAL));
      registerReader(ACTIVITY_FOREACH_CLASS, TAG_FOREACH_COMPLETION_CONDITION, createReader(ACTIVITY_FOREACH_COMPLETION_CONDITION));
      registerReader(ACTIVITY_FOREACH_COMPLETION_CONDITION, TAG_FOREACH_BRANCHES, createReader(ACTIVITY_FOREACH_BRANCHES));
      
      registerReader(ASSIGN_FROM_CLASS, TAG_QUERY, createReader(AeQueryDef.class));
      registerReader(ASSIGN_TO_CLASS, TAG_QUERY, createReader(AeQueryDef.class));

      // Process level compensation handler and termination handling
      registerReader(PROCESS_CLASS, new QName(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ALLOW_PROCESS_COORDINATION, TAG_COMPENSATION_HANDLER), createReader(COMPENSATION_HANDLER_CLASS));
      registerReader(PROCESS_CLASS, new QName(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ALLOW_PROCESS_COORDINATION, TAG_TERMINATION_HANDLER), createReader(TERMINATION_HANDLER_CLASS));

      // opaqueFrom activity for bpel 2.0 abstract processes.
      // (note: this reader is registered in this 'bpel executable process' namespace to allow users to load executable processes that may have opaque activities (from AbxProcess->Save-As)
      registerReader(AeAssignCopyDef.class, new QName(IAeBPELConstants.WSBPEL_2_0_ABSTRACT_NAMESPACE_URI, TAG_OPAQUE_FROM), createReader(AeFromDef.class));

      // map readers for children of understood element
      registerReader(ACTIVITY_CHILDEXTENSION_CLASS, TAG_FROM,         createReader(ASSIGN_FROM_CLASS));
      registerReader(ACTIVITY_CHILDEXTENSION_CLASS, TAG_FROM_PARTS,   createReader(FROM_PARTS_CLASS));
      registerReader(ACTIVITY_CHILDEXTENSION_CLASS, TAG_TO_PARTS,     createReader(TO_PARTS_CLASS));

   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.registry.AeAbstractBpelReaderRegistry#getActivityContainers()
    */
   protected List getActivityContainers()
   {
      List list = new ArrayList(super.getActivityContainers());
      list.add(TERMINATION_HANDLER_CLASS);
      list.add(ACTIVITY_REPEAT_UNTIL_CLASS);
      list.add(ACTIVITY_IF_CLASS);
      list.add(EXTENSION_ACTIVITY_CLASS);
      return list;
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.registry.AeAbstractBpelReaderRegistry#getBpelActivityMappings()
    */
   protected List getBpelActivityMappings()
   {
      List list = super.getBpelActivityMappings();
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_IF), createReader(ACTIVITY_IF_CLASS) ));
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_FOREACH), createReader(ACTIVITY_FOREACH_CLASS) ));
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_REPEAT_UNTIL), createReader(ACTIVITY_REPEAT_UNTIL_CLASS) ));
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_VALIDATE), createReader(ACTIVITY_VALIDATE_CLASS) ));
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_COMPENSATE), createReader(ACTIVITY_COMPENSATE_CLASS) ));
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_COMPENSATE_SCOPE), createReader(ACTIVITY_COMPENSATE_SCOPE_CLASS) ));
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_RETHROW), createReader(ACTIVITY_RETHROW_CLASS) ));
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_EXTENSION_ACTIVITY), createReader(EXTENSION_ACTIVITY_CLASS) ));
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_EXIT), createReader(ACTIVITY_EXIT_CLASS) ));
      list.add(new AeRegistryMapping(new QName(IAeBPELConstants.WSBPEL_2_0_ABSTRACT_NAMESPACE_URI, TAG_OPAQUE_ACTIVITY), createReader(AeActivityOpaqueDef.class) ));
      // Add some 2.0 mappings.
      list.add(new AeRegistryMapping(new QName(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_BREAK), createReader(ACTIVITY_BREAK_CLASS) ));
      list.add(new AeRegistryMapping(new QName(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_CONTINUE), createReader(ACTIVITY_CONTINUE_CLASS) ));
      list.add(new AeRegistryMapping(new QName(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_SUSPEND), createReader(ACTIVITY_SUSPEND_CLASS) ));
      return list;
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.registry.AeBPWSDefReaderRegistry#initCommonActivityChildrenElements()
    */
   protected void initCommonActivityChildrenElements()
   {
      super.initCommonActivityChildrenElements();

      IAeDefReader targetReader = createReader(TARGETS_CLASS);
      IAeDefReader sourceReader = createReader(SOURCES_CLASS);

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
            ACTIVITY_OPAQUE_CLASS,
            ACTIVITY_WAIT_CLASS,
            ACTIVITY_WHILE_CLASS,
            ACTIVITY_REPEAT_UNTIL_CLASS,
            ACTIVITY_VALIDATE_CLASS,
            ACTIVITY_CHILDEXTENSION_CLASS
      };
      
      for (int i = 0; i < activities.length; i++)
      {
         registerReader(activities[i],  TAG_TARGETS, targetReader);
         registerReader(activities[i],  TAG_SOURCES, sourceReader);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.registry.AeBPWSDefReaderRegistry#initGenericElementRegistry()
    */
   protected void initGenericElementRegistry()
   {
      super.initGenericElementRegistry();

      getGenericReadersMap().put(makeDefaultQName(TAG_DOCUMENTATION), createReader(DOCUMENTATION_CLASS));
   }

   /**
    * Populates the extension activity registry with entries.
    */
   protected void initExtensionActivityRegistry()
   {
      super.initExtensionActivityRegistry();
      
      setChildExtensionActivityReader(createReader(AeChildExtensionActivityDef.class));

      
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.registry.AeBPWSDefReaderRegistry#getReader(AeBaseXmlDef, javax.xml.namespace.QName)
    */
   public IAeDefReader getReader(AeBaseXmlDef aParentDef, QName aElementQName) throws UnsupportedOperationException
   {
      IAeDefReader reader = super.getReader(aParentDef, aElementQName);
      
      if (reader == null && aParentDef instanceof AeExtensionActivityDef)
      {
         return getChildExtensionActivityReader();
      }
      
      return reader;
   }
   
   /**
    * @return Returns the Reader for notunderstood child element of extensionActivity.
    */
   public IAeDefReader getChildExtensionActivityReader()
   {
      return mChildExtensionActivityReader;
   }

   /**
    * @param aExtensionActivityNotUnderstoodReader The extensionActivityNotUnderstoodReader to set.
    */
   public void setChildExtensionActivityReader(IAeDefReader aExtensionActivityNotUnderstoodReader)
   {
      mChildExtensionActivityReader = aExtensionActivityNotUnderstoodReader;
   }

   
}
