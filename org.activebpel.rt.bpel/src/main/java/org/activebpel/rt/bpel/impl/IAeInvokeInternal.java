// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeInvokeInternal.java,v 1.6 2007/09/28 19:37:27 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.wsio.invoke.IAeInvoke;

/**
 * Extension of the IAeInvoke.
 */
public interface IAeInvokeInternal extends IAeInvoke
{
   /**
    * Returns true if the underlying BPEL invoke activity is current. That is,
    * it has not moved onto a next iteration (for example in a While loop).
    * 
    * The backed invoke activity is considered current if its transmission id
    * (use as execution iteration reference id) has not changed.
    */
   public boolean isCurrent();

   /**
    * Clean up code to dereference the underlying invoke activity.
    */
   public void dereferenceInvokeActivity();
}
