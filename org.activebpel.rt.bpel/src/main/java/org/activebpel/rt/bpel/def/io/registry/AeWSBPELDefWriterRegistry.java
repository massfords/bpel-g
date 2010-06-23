// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/registry/AeWSBPELDefWriterRegistry.java,v 1.16.2.1 2008/04/21 16:09:44 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.io.registry;

import java.util.Map;

import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeCompensationHandlerDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeTerminationHandlerDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef;
import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeQueryDef;
import org.activebpel.rt.bpel.def.io.IAeBpelFactory;
import org.activebpel.rt.bpel.def.io.writers.def.AeWSBPELWriterVisitor;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.io.writers.IAeDefWriter;
import org.activebpel.rt.xml.def.io.writers.IAeDefWriterFactory;
import org.w3c.dom.Element;

/**
 * A WSBPEL 2.0 implementation of a Def Writer Registry.
 */
public class AeWSBPELDefWriterRegistry extends AeAbstractBpelWriterRegistry
{

   /**
    * Default c'tor.
    */
   public AeWSBPELDefWriterRegistry(Map aFeatures)
   {
      super(IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI, new AeFeatureBasedDefWriterFactory(aFeatures));
   }
   
   /**
    * Inner class which provides a WSBPEL Def writer that supports a features set.
    */
   protected static class AeFeatureBasedDefWriterFactory implements IAeDefWriterFactory
   {
      /** Factory features map. */
      private Map mFeatures;
      
      /**
       * Constructor
       * 
       * @param aFeatures factory features map.
       */
      public AeFeatureBasedDefWriterFactory(Map aFeatures)
      {
         mFeatures = aFeatures;
      }
      
      /**
       * @see org.activebpel.rt.xml.def.io.writers.IAeDefWriterFactory#createDefWriter(org.activebpel.rt.xml.def.AeBaseXmlDef, org.w3c.dom.Element, java.lang.String, java.lang.String)
       */
      public IAeDefWriter createDefWriter(AeBaseXmlDef aDef, Element aParentElement, String aNamespaceUri, String aTagName)
      {
         boolean writePortType = Boolean.TRUE.equals(mFeatures.get(IAeBpelFactory.FEATURE_ID_WRITE_PORTTYPE));
         return new AeWSBPELWriterVisitor(aDef, aParentElement, aNamespaceUri, aTagName, writePortType);
      }
   }
   
   /**
    * Ctor for subclasses
    * @param aNamespace
    * @param aFactory
    */
   protected AeWSBPELDefWriterRegistry(String aNamespace, IAeDefWriterFactory aFactory)
   {
      super(aNamespace, aFactory);
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.registry.AeBPWSDefWriterRegistry#init()
    */
   protected void init()
   {
      super.init();

      registerWriter( ACTIVITY_CONTINUE_CLASS, createWriter(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_CONTINUE));
      registerWriter( ACTIVITY_BREAK_CLASS,    createWriter(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_BREAK));
      registerWriter( ACTIVITY_SUSPEND_CLASS,  createWriter(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_SUSPEND));

      // Add some writers to the registry for WS-BPEL 2.0.
      registerWriter(IMPORT_CLASS, TAG_IMPORT);
      registerWriter(DOCUMENTATION_CLASS, TAG_DOCUMENTATION);
      registerWriter(ACTIVITY_VALIDATE_CLASS, TAG_VALIDATE);
      registerWriter(ACTIVITY_EXIT_CLASS, TAG_EXIT);
      registerWriter(ASSIGN_EXTENSIBLE_ASSIGN_CLASS, TAG_EXTENSION_ASSIGN_OPERATION);
      registerWriter(EXTENSIONS_CLASS, TAG_EXTENSIONS);
      registerWriter(EXTENSION_CLASS, TAG_EXTENSION);
      registerWriter(FROM_PARTS_CLASS, TAG_FROM_PARTS);
      registerWriter(TO_PARTS_CLASS, TAG_TO_PARTS);
      registerWriter(FROM_PART_CLASS, TAG_FROM_PART);
      registerWriter(TO_PART_CLASS, TAG_TO_PART);
      registerWriter(MESSAGE_EXCHANGES_CLASS, TAG_MESSAGE_EXCHANGES);
      registerWriter(MESSAGE_EXCHANGE_CLASS, TAG_MESSAGE_EXCHANGE);

      // fixme we need to put in an activity extension registry so that we can map custom activity QNames to custom writers
      registerWriter(EXTENSION_ACTIVITY_CLASS, TAG_EXTENSION_ACTIVITY);

      registerWriter(ACTIVITY_CHILDEXTENSION_CLASS, new AeChildExtensionActivityWriter());
      
      registerWriter(ACTIVITY_IF_CLASS, TAG_IF);
      // Skip over the 'if' wrapper
      registerWriter(IF_CLASS, new AeSkipWriter());
      registerWriter(ELSEIF_CLASS, TAG_ELSEIF);
      registerWriter(ELSE_CLASS, TAG_ELSE);
      
      registerWriter(ACTIVITY_REPEAT_UNTIL_CLASS, TAG_REPEAT_UNTIL);
      registerWriter(ACTIVITY_RETHROW_CLASS, TAG_RETHROW);

      registerWriter(REPEAT_EVERY_CLASS, TAG_REPEAT_EVERY);

      registerWriter(ON_EVENT_CLASS, TAG_ON_EVENT);
      registerWriter(TERMINATION_HANDLER_CLASS, TAG_TERMINATION_HANDLER);

      registerWriter(SOURCES_CLASS, TAG_SOURCES);
      registerWriter(TARGETS_CLASS, TAG_TARGETS);

      registerWriter(JOIN_CONDITION_CLASS, TAG_JOIN_CONDITION);
      registerWriter(TRANSITION_CONDITION_CLASS, TAG_TRANSITION_CONDITION);
      registerWriter(FOR_CLASS, TAG_FOR);
      registerWriter(UNTIL_CLASS, TAG_UNTIL);

      registerWriter(CONDITION_CLASS, TAG_CONDITION);

      registerWriter(LITERAL_CLASS, TAG_LITERAL);

      registerWriter(ACTIVITY_FOREACH_CLASS, TAG_FOREACH);
      registerWriter(ACTIVITY_FOREACH_COMPLETION_CONDITION, TAG_FOREACH_COMPLETION_CONDITION);
      registerWriter(ACTIVITY_FOREACH_BRANCHES, TAG_FOREACH_BRANCHES);
      registerWriter(ACTIVITY_FOREACH_START, TAG_FOREACH_STARTCOUNTER);
      registerWriter(ACTIVITY_FOREACH_FINAL, TAG_FOREACH_FINALCOUNTER);

      registerWriter(ACTIVITY_COMPENSATE_SCOPE_CLASS, TAG_COMPENSATE_SCOPE);

      registerWriter(AeQueryDef.class, TAG_QUERY);

      // Compensation handler (write out as an extension element in BPEL 2.0)
      registerWriter(COMPENSATION_HANDLER_CLASS, new AeWSBPELCompensationHandlerWriter());
      
      registerWriter(TERMINATION_HANDLER_CLASS, new AeWSBPELTerminationHandlerWriter());
     
      // Writer for BPEL 2.0 abstract processe's opaque activity.
      // (note: this writer is registered in this 'bpel executable process' namespace to allow users to save abstract processes as executable processes (with the opaque activities)
      registerWriter(AeActivityOpaqueDef.class, createWriter(IAeBPELConstants.WSBPEL_2_0_ABSTRACT_NAMESPACE_URI, TAG_OPAQUE_ACTIVITY));
      
      // new writer to handle BPEL 2.0 <from/> or abstract process's <opaqueFrom/>
      registerWriter(AeFromDef.class, new AeWSBPELFromDefWriter());
   }
   
   /**
    * Writes an activity extension (found as the child of an extensionActivity).  The
    * technique is similar to the dispatch writer, except this class gets the namespace and local
    * part information from the unknown activity def itself.
    */
   protected class AeChildExtensionActivityWriter implements IAeDefWriter
   {
      /**
       * @see org.activebpel.rt.xml.def.io.writers.IAeDefWriter#createElement(AeBaseXmlDef, org.w3c.dom.Element)
       */
      public Element createElement(AeBaseXmlDef aBaseDef, Element aParentElement)
      {
         AeChildExtensionActivityDef actExtDef = (AeChildExtensionActivityDef) aBaseDef;

         IAeDefWriter writer = getWriterFactory().createDefWriter((AeBaseDef) aBaseDef,
               aParentElement, actExtDef.getElementName().getNamespaceURI(),
               actExtDef.getElementName().getLocalPart());
         return writer.createElement(aBaseDef, aParentElement);
      }
   }

   /**
    * Writes out a compensation handler.
    */
   protected class AeWSBPELCompensationHandlerWriter implements IAeDefWriter
   {
      /**
       * @see org.activebpel.rt.xml.def.io.writers.IAeDefWriter#createElement(AeBaseXmlDef, org.w3c.dom.Element)
       */
      public Element createElement(AeBaseXmlDef aBaseDef, Element aParentElement)
      {
         AeCompensationHandlerDef compHandlerDef = (AeCompensationHandlerDef) aBaseDef;
         String namespace = getDefaultNamespace();
         String elementName = TAG_COMPENSATION_HANDLER;
         if (compHandlerDef.getParent() instanceof AeProcessDef)
            namespace = IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ALLOW_PROCESS_COORDINATION;

         IAeDefWriter writer = getWriterFactory().createDefWriter((AeBaseDef) aBaseDef,
               aParentElement, namespace, elementName);
         return writer.createElement(aBaseDef, aParentElement);
      }
   }
   
   /**
    * Writes out a termination handler.  Need special handling in ws-bpel in order to properly
    * write out the process-level termination handler.
    */
   protected class AeWSBPELTerminationHandlerWriter implements IAeDefWriter
   {
      /**
       * @see org.activebpel.rt.xml.def.io.writers.IAeDefWriter#createElement(AeBaseXmlDef, org.w3c.dom.Element)
       */
      public Element createElement(AeBaseXmlDef aBaseDef, Element aParentElement)
      {
         AeTerminationHandlerDef termHandlerDef = (AeTerminationHandlerDef) aBaseDef;
         String namespace = getDefaultNamespace();
         String elementName = TAG_TERMINATION_HANDLER;
         if (termHandlerDef.getParent() instanceof AeProcessDef)
            namespace = IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ALLOW_PROCESS_COORDINATION;

         IAeDefWriter writer = getWriterFactory().createDefWriter((AeBaseDef) aBaseDef,
               aParentElement, namespace, elementName);
         return writer.createElement(aBaseDef, aParentElement);
      }
   }
   
   /**
    * Writes out a &lt;from&gt; (for executable processes) or &lt;opaqueFrom&gt; (for abstract processes).
    */
   protected class AeWSBPELFromDefWriter implements IAeDefWriter
   {
      /**
       * @see org.activebpel.rt.xml.def.io.writers.IAeDefWriter#createElement(AeBaseXmlDef, org.w3c.dom.Element)
       */
      public Element createElement(AeBaseXmlDef aBaseDef, Element aParentElement)
      {
         AeFromDef fromDef = (AeFromDef) aBaseDef;
         String namespace = getDefaultNamespace();
         String elementName = TAG_FROM;
         if (fromDef.isOpaque())
         {
            elementName = TAG_OPAQUE_FROM;
            namespace = IAeBPELConstants.WSBPEL_2_0_ABSTRACT_NAMESPACE_URI;
         }
         IAeDefWriter writer = getWriterFactory().createDefWriter((AeBaseDef) aBaseDef,
               aParentElement, namespace, elementName);
         return writer.createElement(aBaseDef, aParentElement);
      }
   }   
}
