// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAeInvokeActivity.java,v 1.2 2008/03/11 03:05:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

import org.activebpel.wsio.invoke.IAeTransmission;



/**
 * Describes an invoke object that reports whether it's a one way operation or not.
 */
public interface IAeInvokeActivity extends IAeTransmission
{

   /**
    * Returns the unique location path within the process for this receiver.
    */
   public String getLocationPath();

   /**
    * Returns the unique location id within the process for this receiver.
    */
   public int getLocationId();

   /**
    * Returns true if this invoker is targeting a one-way operation
    */
   public boolean isOneWay();

   /**
    * @return Returns true if the response receiver is currently in an executing state.
    */
   public boolean isExecuting();

}
