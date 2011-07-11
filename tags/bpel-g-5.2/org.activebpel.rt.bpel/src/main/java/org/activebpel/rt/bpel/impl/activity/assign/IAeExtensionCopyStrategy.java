//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/IAeExtensionCopyStrategy.java,v 1.1 2007/11/10 03:36:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign; 

import org.activebpel.rt.bpel.impl.AeBpelException;

/**
 * Provides interface for the copy operation to communicate with a to-spec
 * extension that handles copying data. 
 */
public interface IAeExtensionCopyStrategy
{
   /**
    * Copies the data from the from impl to the destination
    * @param aCopyOperation - provided as a context for the copy 
    * @param aFromData
    * @throws AeBpelException
    */
   public void copy(IAeCopyOperation aCopyOperation, Object aFromData) throws AeBpelException;

}
 