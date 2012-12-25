// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/IAeBpelWriter.java,v 1.4 2006/11/04 16:34:29 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.w3c.dom.Document;

/**
 * Interface for working with BPEL serializers.
 */
public interface IAeBpelWriter
{
   /**
    * Serializes the <code>AeProcessDef</code> to it's xml
    * representation.
    * @param aProcessDef
    * @return BPEL dom
    * @throws AeBpelException
    */
   public Document writeBPEL(AeProcessDef aProcessDef, boolean aUsePrefixes)
         throws AeBusinessProcessException;
}
