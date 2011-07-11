//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/convert/AeBPWSToWSBPELAbstractProcessConverter.java,v 1.2 2006/09/28 14:22:15 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.convert;

import org.activebpel.rt.bpel.def.IAeBPELConstants;

/**
 * Def convert for converting bpws 1.1. abstract processes to wsbpel 2.0 abstract processes.
 */
public class AeBPWSToWSBPELAbstractProcessConverter extends AeBPWSToWSBPELConverter
{

   /**
    * Default ctor.
    */
   public AeBPWSToWSBPELAbstractProcessConverter()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.def.convert.AeAbstractBpelDefConverter#getNewBpelNamespace()
    */
   protected String getNewBpelNamespace()
   {
      return IAeBPELConstants.WSBPEL_2_0_ABSTRACT_NAMESPACE_URI;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.convert.AeAbstractBpelDefConverter#getNewBpelNamespacePrefix()
    */
   protected String getNewBpelNamespacePrefix()
   {
      return IAeBPELConstants.ABSTRACT_PROC_PREFIX;
   }   
}
