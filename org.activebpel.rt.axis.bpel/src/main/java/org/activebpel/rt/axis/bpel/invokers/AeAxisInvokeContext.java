//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/invokers/AeAxisInvokeContext.java,v 1.1 2007/12/11 19:53:51 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.invokers;

import org.activebpel.rt.bpel.server.engine.invoke.AeInvokeContext;
import org.apache.axis.client.Call;

/**
 * Context information necessary to execute and invoke.
 */
public class AeAxisInvokeContext extends AeInvokeContext
{
   /** The client Call object. */
   private Call mCall;
   
   /**
    * Default Constructor.
    */
   public AeAxisInvokeContext()
   {
   }
   
   /**
    * @return Returns the call.
    */
   public Call getCall()
   {
      return mCall;
   }

   /**
    * @param aCall The call to set.
    */
   public void setCall(Call aCall)
   {
      mCall = aCall;
   }
}
