//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/registry/AeWSBPELAbstractProcessDefReaderRegistry.java,v 1.3 2007/09/26 02:21:03 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.registry;

import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.io.readers.def.AeWSBPELAbstractProcessReaderVisitor;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.io.readers.IAeReaderFactory;
import org.activebpel.rt.xml.def.io.readers.IAeReportingDefReader;
import org.w3c.dom.Element;

/**
 * An implementation of the Def Reader Registry for WS-BPEL 2.0 Abstract processes. This implementation extends the 
 * WS BPEL 2.0 (executable) implementation.
 */
public class AeWSBPELAbstractProcessDefReaderRegistry extends AeWSBPELDefReaderRegistry
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
            return new AeWSBPELAbstractProcessReaderVisitor((AeBaseDef) aParentDef, aElement);
         }
      };

   /**
    * Default ctor.
    */
   public AeWSBPELAbstractProcessDefReaderRegistry()
   {
      super(IAeBPELConstants.WSBPEL_2_0_ABSTRACT_NAMESPACE_URI, sFactory);
   }
}
