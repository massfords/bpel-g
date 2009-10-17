//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/AeBPWSFactory.java,v 1.3 2007/09/26 02:21:03 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io;

import org.activebpel.rt.bpel.def.io.readers.AeRegistryBasedBpelReader;
import org.activebpel.rt.bpel.def.io.registry.AeBPWSDefReaderRegistry;
import org.activebpel.rt.bpel.def.io.registry.AeBPWSDefWriterRegistry;
import org.activebpel.rt.bpel.def.io.writers.AeBPWSBpelWriter;
import org.activebpel.rt.xml.def.io.AeDefIORegistry;
import org.activebpel.rt.xml.def.io.IAeDefRegistry;

/**
 * Default factory for BPEL4WS 1.1.
 */
public class AeBPWSFactory extends AeAbstractBpelFactory
{
   /**
    * Default c'tor.
    */
   protected AeBPWSFactory()
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
      return new AeBPWSBpelWriter(getBpelRegistry());
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.AeAbstractBpelFactory#createBpelRegistry()
    */
   protected IAeDefRegistry createBpelRegistry()
   {
      return new AeDefIORegistry(new AeBPWSDefReaderRegistry(), new AeBPWSDefWriterRegistry());
   }
}
 
