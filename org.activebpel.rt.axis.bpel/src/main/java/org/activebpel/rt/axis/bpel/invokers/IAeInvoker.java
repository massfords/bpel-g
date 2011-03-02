//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/invokers/IAeInvoker.java,v 1.2 2007/12/11 19:53:51 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.invokers;

import java.rmi.RemoteException;

import org.activebpel.rt.AeException;

/**
 * Interface for Axis invokers.  
 * Implementations handle the various binding styles for RPC and document
 */
public interface IAeInvoker
{
   
   public void invoke( AeAxisInvokeContext aContext ) throws AeException, RemoteException;

}
