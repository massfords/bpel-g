// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/writers/AeBPWSBpelWriter.java,v 1.4 2008/02/17 21:37:11 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.io.writers;

import org.activebpel.rt.xml.def.io.IAeDefRegistry;

/**
 * A BPEL4WS 1.1 implementation of a bpel writer.
 */
public class AeBPWSBpelWriter extends AeRegistryBasedBpelWriter
{
   /**
    * Default c'tor.
    * 
    * @param aBpelRegistry
    */
   public AeBPWSBpelWriter(IAeDefRegistry aBpelRegistry)
   {
      super(aBpelRegistry);
   }
}
