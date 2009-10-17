// $Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/invoke/IAeInvokeHandler.java,v 1.4 2008/02/17 22:01:32 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio.invoke;

import org.activebpel.wsio.IAeWebServiceResponse;


/**
 * Interface for the bpel engine to process invokes on a partner.
 */
public interface IAeInvokeHandler
{
   
   /**
    * Handle the invoke call.  Query data will be null if none was specified
    * on the customInvokerUri.
    * @param aInvoke
    * @param aQueryData
    */
   public IAeWebServiceResponse handleInvoke( IAeInvoke aInvoke, String aQueryData );
   
}
