//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/AeAxisServer.java,v 1.1 2008/01/16 21:01:51 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel;

import org.apache.axis.MessageContext;
import org.apache.axis.server.AxisServer;

/** 
 * Extension of the AxisServer that allows us to set the thread-local message context
 * when necessary.  This is necessary when we are invoking Axis handler chains outside of
 * the AxisServer.invoke() method.  An example of this is within a durable reply sender that
 * needs to execute the response handlers before sending the reply message.
 */
public class AeAxisServer extends AxisServer
{
   /**
    * Sets the current thread-local message context
    * @param aContext
    */
   public static void setCurrentContext(MessageContext aContext)
   {
      setCurrentMessageContext(aContext);
   }
}
