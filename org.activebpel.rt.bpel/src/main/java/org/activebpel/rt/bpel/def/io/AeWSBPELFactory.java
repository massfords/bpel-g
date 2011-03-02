// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/AeWSBPELFactory.java,v 1.6 2008/03/11 14:47:08 JPerrotto Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.io;

import org.activebpel.rt.bpel.def.io.readers.AeRegistryBasedBpelReader;
import org.activebpel.rt.bpel.def.io.registry.AeWSBPELDefReaderRegistry;
import org.activebpel.rt.bpel.def.io.registry.AeWSBPELDefWriterRegistry;
import org.activebpel.rt.bpel.def.io.writers.AeRegistryBasedBpelWriter;
import org.activebpel.rt.xml.def.io.AeDefIORegistry;
import org.activebpel.rt.xml.def.io.IAeDefRegistry;

/**
 * BPEL factory for WSBPEL4 2.0.
 */
public class AeWSBPELFactory extends AeAbstractBpelFactory
{
   /**
    * Default c'tor.
    */
   protected AeWSBPELFactory()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.IAeBpelFactory#createBpelReader()
    */
   public IAeBpelReader createBpelReader()
   {
      return new AeRegistryBasedBpelReader(getBpelRegistry());
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.IAeBpelFactory#createBpelWriter()
    */
   public IAeBpelWriter createBpelWriter()
   {
      return new AeRegistryBasedBpelWriter(getBpelRegistry());
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.AeAbstractBpelFactory#createBpelRegistry()
    */
   protected IAeDefRegistry createBpelRegistry()
   {
      return new AeDefIORegistry( new AeWSBPELDefReaderRegistry(), new AeWSBPELDefWriterRegistry(getFeatures()));
   }
}
