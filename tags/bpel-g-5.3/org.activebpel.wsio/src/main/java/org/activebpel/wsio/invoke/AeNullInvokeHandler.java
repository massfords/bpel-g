// $Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/invoke/AeNullInvokeHandler.java,v 1.1 2007/07/26 14:06:33 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.wsio.invoke;

import org.activebpel.wsio.IAeWebServiceResponse;

/**
 * Null invoke handler - this invoke handler simply returns null
 * from handleInvoke().  Useful, for example, for handling one-way
 * invokes.
 */
public class AeNullInvokeHandler implements IAeInvokeHandler
{
   /**
    * C'tor.
    */
   public AeNullInvokeHandler()
   {
   }

   /**
    * @see org.activebpel.wsio.invoke.IAeInvokeHandler#handleInvoke(org.activebpel.wsio.invoke.IAeInvoke, java.lang.String)
    */
   public IAeWebServiceResponse handleInvoke(IAeInvoke aInvoke, String aQueryData)
   {
      return null;
   }
}
