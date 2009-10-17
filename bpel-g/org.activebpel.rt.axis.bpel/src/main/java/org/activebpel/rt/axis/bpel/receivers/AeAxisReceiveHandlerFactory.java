//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/receivers/AeAxisReceiveHandlerFactory.java,v 1.2 2008/02/17 21:29:26 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.receivers; 

import org.activebpel.rt.axis.bpel.AeMessages;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeReceiveHandler;
import org.activebpel.rt.bpel.server.deploy.AeDeployConstants;
import org.activebpel.rt.bpel.server.engine.AeInvokeHandlerUri;
import org.activebpel.rt.bpel.server.engine.IAeReceiveHandlerFactory;

/**
 * A receive handler factory for Axis extensions to the AeSOAPReceiveHandler. 
 */
public class AeAxisReceiveHandlerFactory implements IAeReceiveHandlerFactory
{
   private static IAeReceiveHandler mRpcHandler = new AeRPCReceiveHandler();
   private static IAeReceiveHandler mMSGHandler = new AeMSGReceiveHandler();
   
   /**
    * Returns the appropriate receive handler for binding type. 
    * 
    * @see org.activebpel.rt.bpel.server.engine.IAeReceiveHandlerFactory#createReceiveHandler(java.lang.String)
    */
   public IAeReceiveHandler createReceiveHandler(String aProtocol) throws AeBusinessProcessException
   {
      String binding = AeInvokeHandlerUri.getInvokerString(aProtocol);
      if (AeDeployConstants.BIND_MSG.equals(binding))
      {
         return mMSGHandler;
      }
      else if (AeDeployConstants.BIND_RPC.equals(binding))
      {
         return mRpcHandler;
      }
      else if (AeDeployConstants.BIND_RPC_LIT.equals(binding))
      {
         return mRpcHandler;
      }
      else
      {
         throw new AeBusinessProcessException(AeMessages.format("AeAxisReceiveHandlerFactory.0", binding)); //$NON-NLS-1$
      }
   }   

}
 