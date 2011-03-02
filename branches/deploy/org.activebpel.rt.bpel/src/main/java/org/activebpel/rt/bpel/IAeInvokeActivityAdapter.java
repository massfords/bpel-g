//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAeInvokeActivityAdapter.java,v 1.1 2008/03/11 03:05:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel; 

import org.activebpel.rt.bpel.impl.activity.IAeImplAdapter;

/**
 * Adapter class provided by extensions that want to use the engine's reliable
 * transport framework. 
 */
public interface IAeInvokeActivityAdapter extends IAeImplAdapter
{
   /**
    * Returns true if this invoker is targeting a one-way operation
    */
   public boolean isOneWay();
}
 