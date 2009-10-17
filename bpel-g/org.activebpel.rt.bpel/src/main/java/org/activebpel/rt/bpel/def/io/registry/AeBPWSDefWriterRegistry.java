// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/registry/AeBPWSDefWriterRegistry.java,v 1.9 2008/02/17 21:37:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.registry;

import java.util.Iterator;

import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.activity.support.AeLiteralDef;
import org.activebpel.rt.bpel.def.activity.support.AeQueryDef;
import org.activebpel.rt.bpel.def.io.IAeBpelClassConstants;
import org.activebpel.rt.bpel.def.io.IAeBpelLegacyConstants;
import org.activebpel.rt.bpel.def.io.writers.def.AeBPWSWriterVisitor;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.activebpel.rt.xml.def.io.writers.IAeDefWriter;
import org.activebpel.rt.xml.def.io.writers.IAeDefWriterFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This impl simply maps the AeDef class object to its associated writer.
 */
public class AeBPWSDefWriterRegistry extends AeAbstractBpelWriterRegistry implements IAeBPELConstants, IAeBpelClassConstants, IAeBpelLegacyConstants
{
   /** factory for creating writers */
   private static final IAeDefWriterFactory sFactory = new IAeDefWriterFactory()
      {
         /**
          * @see org.activebpel.rt.xml.def.io.writers.IAeDefWriterFactory#createDefWriter(org.activebpel.rt.xml.def.AeBaseXmlDef, org.w3c.dom.Element, java.lang.String, java.lang.String)
          */
         public IAeDefWriter createDefWriter(AeBaseXmlDef aDef, Element aParentElement, String aNamespaceUri, String aTagName)
         {
            return new AeBPWSWriterVisitor(aDef, aParentElement, aNamespaceUri, aTagName);
         }
      };

   /**
    * Default constructor.
    */
   public AeBPWSDefWriterRegistry()
   {
      super(IAeBPELConstants.BPWS_NAMESPACE_URI, sFactory);
   }

   /**
    * Populates the registry with entries.
    */
   protected void init()
   {
      super.init();
      
      registerWriter( ACTIVITY_COMPENSATE_SCOPE_CLASS,  TAG_COMPENSATE );
      registerWriter( ACTIVITY_IF_CLASS,          TAG_SWITCH );
      registerWriter( ACTIVITY_TERMINATE_CLASS,   TAG_TERMINATE );
      registerWriter( ACTIVITY_THROW_CLASS,       TAG_THROW );
      registerWriter( ACTIVITY_FOREACH_CLASS,  createWriter(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_FOREACH));

      registerWriter( IF_CLASS,                   TAG_CASE );
      registerWriter( ELSEIF_CLASS,               TAG_CASE );
      registerWriter( ELSE_CLASS,                 TAG_OTHERWISE );
      registerWriter( MESSAGE_EXCHANGES_CLASS, createWriter(IAeBPELConstants.ABX_2_0_NAMESPACE_URI, TAG_MESSAGE_EXCHANGES));
      registerWriter( MESSAGE_EXCHANGE_CLASS,  createWriter(IAeBPELConstants.ABX_2_0_NAMESPACE_URI, TAG_MESSAGE_EXCHANGE));
      registerWriter( ACTIVITY_FOREACH_COMPLETION_CONDITION, createWriter(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_FOREACH_COMPLETION_CONDITION));
      registerWriter( ACTIVITY_FOREACH_BRANCHES, createWriter(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_FOREACH_BRANCHES));
      registerWriter( ACTIVITY_FOREACH_START,  createWriter(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_FOREACH_STARTCOUNTER));
      registerWriter( ACTIVITY_FOREACH_FINAL,  createWriter(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_FOREACH_FINALCOUNTER));
      registerWriter( ON_EVENT_CLASS,             TAG_ON_MESSAGE );

      registerWriter( PARTNER_CLASS,              TAG_PARTNER );

      registerWriter( EXTENSION_ACTIVITY_CLASS,   SKIP_WRITER );
      
      registerWriter( SOURCES_CLASS,              SKIP_WRITER );
      registerWriter( TARGETS_CLASS,              SKIP_WRITER );

      registerWriter( JOIN_CONDITION_CLASS,       SKIP_WRITER );
      registerWriter( TRANSITION_CONDITION_CLASS, SKIP_WRITER );
      registerWriter( FOR_CLASS,                  SKIP_WRITER );
      registerWriter( UNTIL_CLASS,                SKIP_WRITER );

      registerWriter( CONDITION_CLASS,            SKIP_WRITER);

      registerWriter( LITERAL_CLASS,              new AeBPWSLiteralWriter());
      registerWriter( AeQueryDef.class,           SKIP_WRITER);
      
      registerWriter( AeExtensionAttributeDef.class, SKIP_WRITER);
   }

   /**
    * A special writer that is able to write out the literal for a bpel4ws 1.1 def.
    */
   protected class AeBPWSLiteralWriter implements IAeDefWriter
   {
      /**
       * @see org.activebpel.rt.xml.def.io.writers.IAeDefWriter#createElement(AeBaseXmlDef, org.w3c.dom.Element)
       */
      public Element createElement(AeBaseXmlDef aBaseDef, Element aParentElement)
      {
         AeLiteralDef def = (AeLiteralDef) aBaseDef;
         for (Iterator iter = def.getChildNodes().iterator(); iter.hasNext(); )
         {
            Node node = (Node) iter.next();
            
            Node importedNode = aParentElement.getOwnerDocument().importNode(node, true);
            aParentElement.appendChild(importedNode);
         }

         return aParentElement;
      }
   }
}
