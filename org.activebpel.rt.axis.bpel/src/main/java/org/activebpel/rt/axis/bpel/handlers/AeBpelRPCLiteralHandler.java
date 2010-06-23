//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/handlers/AeBpelRPCLiteralHandler.java,v 1.3 2006/02/03 19:49:21 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.handlers; 

import org.apache.axis.constants.Use;

/**
 * RPC Literal version of the RPC handler. 
 */
public class AeBpelRPCLiteralHandler extends AeBpelRPCHandler
{
   /**
    * @see org.activebpel.rt.axis.bpel.handlers.AeBpelHandler#getUse()
    */
   protected Use getUse()
   {
      return Use.LITERAL;
   }
}
