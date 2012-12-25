// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/convert/AeBPWSToWSBPELConverter.java,v 1.7 2006/11/16 23:34:46 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.convert;

import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.convert.visitors.AeBPWSToWSBPELCatchVisitor;
import org.activebpel.rt.bpel.def.convert.visitors.AeBPWSToWSBPELCorrelationInitiateVisitor;
import org.activebpel.rt.bpel.def.convert.visitors.AeBPWSToWSBPELExpressionVisitor;
import org.activebpel.rt.bpel.def.convert.visitors.AeBPWSToWSBPELExtensionActivityVisitor;
import org.activebpel.rt.bpel.def.convert.visitors.AeBPWSToWSBPELOnAlarmVisitor;
import org.activebpel.rt.bpel.def.convert.visitors.AeBPWSToWSBPELOnEventVisitor;

/**
 * A BPEL Def converter that converts a def from version 1.1 (BPWS) to version 2.0 (WS-BPEL 2.0).
 */
public class AeBPWSToWSBPELConverter extends AeAbstractBpelDefConverter
{
   /**
    * Default c'tor.
    */
   public AeBPWSToWSBPELConverter()
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.convert.AeAbstractBpelDefConverter#getNewBpelNamespace()
    */
   protected String getNewBpelNamespace()
   {
      return IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.convert.AeAbstractBpelDefConverter#getNewBpelNamespacePrefix()
    */
   protected String getNewBpelNamespacePrefix()
   {
      return IAeBPELConstants.WSBPEL_2_0_PREFIX;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.convert.AeAbstractBpelDefConverter#doConversion()
    */
   protected void doConversion()
   {
      getProcessDef().accept(new AeBPWSToWSBPELExpressionVisitor());
      getProcessDef().accept(new AeBPWSToWSBPELOnEventVisitor());
      getProcessDef().accept(new AeBPWSToWSBPELOnAlarmVisitor());
      getProcessDef().accept(new AeBPWSToWSBPELCatchVisitor());
      getProcessDef().accept(new AeBPWSToWSBPELCorrelationInitiateVisitor());
      getProcessDef().accept(new AeBPWSToWSBPELExtensionActivityVisitor());
   }
}
