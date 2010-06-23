//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/handlers/AeBpelRPCHandler.java,v 1.40 2007/12/11 19:53:51 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.handlers;

import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;

/**
 * The BPEL handler for web services under an Axis framework for RPC style binding. 
 */
public class AeBpelRPCHandler extends AeBpelHandler
{
   /**
    * @see org.activebpel.rt.axis.bpel.handlers.AeBpelHandler#getStyle()
    */
   protected Style getStyle()
   {
      return Style.RPC;
   }
   
   /**
    * @see org.activebpel.rt.axis.bpel.handlers.AeBpelHandler#getUse()
    */
   protected Use getUse()
   {
      return Use.ENCODED;
   }

   /**
    * @see org.activebpel.rt.axis.bpel.handlers.AeBpelHandler#getReceiveHandler()
    */
   public String getReceiveHandler()
   {
      return "soap:RPC"; //$NON-NLS-1$
   }
}
