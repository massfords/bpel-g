//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/invokers/AeInvokerFactory.java,v 1.3 2007/12/11 19:53:51 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.invokers;

import org.activebpel.rt.axis.bpel.AeMessages;
import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.constants.Style;

/**
 * Provides factorya access for RPC or Document style invokers.  Invoker instances
 * returned from this impl are thread safe.
 */
public class AeInvokerFactory
{
   private static final String INVALID_INVOKE_STYLE = "AeInvokerFactory.ERROR_1"; //$NON-NLS-1$
   
   /** RPC style invoker singleton instance. */
   private static final AeRpcStyleInvoker RPC_INVOKER = new AeRpcStyleInvoker();
   /** Document style invoker singleton instance. */
   private static final AeDocumentStyleInvoker DOC_INVOKER = new AeDocumentStyleInvoker();

   /**
    * Get the invoker.
    * @param aContext
    */
   public static IAeInvoker getInvoker( AeAxisInvokeContext aContext ) throws AxisFault
   {
      IAeInvoker invoker = null;
      String style = (String)aContext.getCall().getProperty( Call.OPERATION_STYLE_PROPERTY );
      
      if( Style.RPC.getName().equals( style ) )
      {
         invoker = RPC_INVOKER;
      }
      else if( Style.DOCUMENT.getName().equals( style ) )
      {
         invoker = DOC_INVOKER;
      }
      
      if( invoker == null )
      {
         throw new AxisFault( AeMessages.getString(INVALID_INVOKE_STYLE) );
      }
      return invoker;
   }
   
}
