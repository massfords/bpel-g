//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/AeWSBPELAbstractProcessFactory.java,v 1.5 2007/10/12 16:09:48 ppatruni Exp $
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
import org.activebpel.rt.bpel.def.io.registry.AeWSBPELAbstractProcessDefReaderRegistry;
import org.activebpel.rt.bpel.def.io.registry.AeWSBPELAbstractProcessDefWriterRegistry;
import org.activebpel.rt.bpel.def.io.writers.AeRegistryBasedBpelWriter;
import org.activebpel.rt.xml.def.io.AeDefIORegistry;
import org.activebpel.rt.xml.def.io.IAeDefRegistry;

/**
 * Factory for WSBPEL 2.0 Abstract processes.
 *
 */
public class AeWSBPELAbstractProcessFactory extends AeAbstractBpelFactory
{

   /**
    * Default c'tor.
    */   
   public AeWSBPELAbstractProcessFactory()
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
      return new AeDefIORegistry( new AeWSBPELAbstractProcessDefReaderRegistry(), new AeWSBPELAbstractProcessDefWriterRegistry());
   }
}
