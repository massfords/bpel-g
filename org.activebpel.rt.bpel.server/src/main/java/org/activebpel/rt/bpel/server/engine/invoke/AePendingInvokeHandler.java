// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/invoke/AePendingInvokeHandler.java,v 1.2 2007/09/28 19:48:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.invoke;

import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.invoke.AeInvokePrepareException;
import org.activebpel.wsio.invoke.IAeInvoke;
import org.activebpel.wsio.invoke.IAeTwoPhaseInvokeHandler;

/**
 * Test class that implements an invoke handler that always creates a pending
 * invoke in the process, because this invoke handler never delivers an invoke
 * response. Access this invoke handler through the {@link AePojoHandlerFactory}
 * by specifying an invoke handler URI containing the <code>java:</code>
 * protocol scheme.
 */
public class AePendingInvokeHandler implements IAeTwoPhaseInvokeHandler
{
   /**
    * Default constructor.
    */
   public AePendingInvokeHandler()
   {
   }

   /**
    * Overrides method to throw <code>IllegalStateException</code>, because this
    * method should never be called if {@link #prepare(IAeInvoke, String)}
    * always returns <code>false</code> (which it does).
    *
    * @see org.activebpel.wsio.invoke.IAeInvokeHandler#handleInvoke(org.activebpel.wsio.invoke.IAeInvoke, java.lang.String)
    */
   public IAeWebServiceResponse handleInvoke(IAeInvoke aInvoke, String aQueryData)
   {
      throw new IllegalStateException();
   }

   /**
    * Overrides method to always return <code>false</code>, so that
    * {@link #handleInvoke(IAeInvoke, String)} will never be called.
    * 
    * @see org.activebpel.wsio.invoke.IAeTwoPhaseInvokeHandler#prepare(org.activebpel.wsio.invoke.IAeInvoke, java.lang.String)
    */
   public boolean prepare(IAeInvoke aInvoke, String aQueryData) throws AeInvokePrepareException
   {
      return false;
   }
}
