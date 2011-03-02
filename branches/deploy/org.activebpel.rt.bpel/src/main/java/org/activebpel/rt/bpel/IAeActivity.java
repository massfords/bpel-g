// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAeActivity.java,v 1.4 2005/12/06 22:27:12 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

import org.activebpel.rt.bpel.impl.IAeBpelObject;

/** Describes the interface used for interacting with business process activities */
public interface IAeActivity extends IAeBpelObject
{
   /**
    * Terminates the activity without the activity completing abnormally or 
    * executing its fault handler. This is used for the root activity within
    * a loop activity that gets terminated as a result of a continue or break
    * activity executing within the loop. 
    * @throws AeBusinessProcessException
    */
   public void terminateEarly() throws AeBusinessProcessException;
}
