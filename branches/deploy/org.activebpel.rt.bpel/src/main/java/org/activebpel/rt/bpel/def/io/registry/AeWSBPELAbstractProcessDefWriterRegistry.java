//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/registry/AeWSBPELAbstractProcessDefWriterRegistry.java,v 1.6 2008/03/11 14:47:08 JPerrotto Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.registry;

import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.io.writers.def.AeWSBPELAbstractProcessWriterVisitor;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.io.writers.IAeDefWriter;
import org.activebpel.rt.xml.def.io.writers.IAeDefWriterFactory;
import org.w3c.dom.Element;

/**
 * A WSBPEL 2.0 Abstract process's implementation of a Def Writer Registry.
 */
public class AeWSBPELAbstractProcessDefWriterRegistry extends AeWSBPELDefWriterRegistry
{
   /**
    * Creates the writer def factory that the dispatch writer will use to create visitors to dispatch to.
    */
   private static final IAeDefWriterFactory sFactory = new IAeDefWriterFactory()
      {
         /**
          * @see org.activebpel.rt.xml.def.io.writers.IAeDefWriterFactory#createDefWriter(org.activebpel.rt.xml.def.AeBaseXmlDef, org.w3c.dom.Element, java.lang.String, java.lang.String)
          */
         public IAeDefWriter createDefWriter(AeBaseXmlDef aDef, Element aParentElement,
               String aNamespaceUri, String aTagName)
         {
            return new AeWSBPELAbstractProcessWriterVisitor(aDef, aParentElement, aNamespaceUri, aTagName, true);
         }
      };

   /**
    * Default ctor.
    */
   public AeWSBPELAbstractProcessDefWriterRegistry()
   {
      super(IAeBPELConstants.WSBPEL_2_0_ABSTRACT_NAMESPACE_URI, sFactory);
   }

}
