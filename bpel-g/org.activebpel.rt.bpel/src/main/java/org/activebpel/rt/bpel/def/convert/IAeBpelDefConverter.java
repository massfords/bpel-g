// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/convert/IAeBpelDefConverter.java,v 1.1 2006/08/04 20:07:41 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.convert;

import org.activebpel.rt.bpel.def.AeProcessDef;

/**
 * Interface implemented by BPEL def converters.  A BPEL def converter is responsible for 
 * converting a def from one version of BPEL to another.  For example, we have a converter
 * that is capable of modifying a BPEL 1.1 formatted def into a BPEL 2.0 formatted def.
 */
public interface IAeBpelDefConverter
{
   /**
    * Called to convert the given process def.
    *
    * @param aProcessDef
    */
   public void convert(AeProcessDef aProcessDef);
}
