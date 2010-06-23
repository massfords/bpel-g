//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/admin/server/AeActiveBpelDeployBPRSkeleton.java,v 1.3 2007/01/29 23:04:46 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the 
// proprietary property of Active Endpoints, Inc.  Viewing or use of 
// this information is prohibited without the express written consent of 
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.admin.server; 

import java.rmi.RemoteException;

import org.activebpel.rt.axis.bpel.admin.types.AesDeployBprType;
import org.activebpel.rt.axis.bpel.admin.types.AesStringResponseType;

/**
 * Server binding for deploying bprs. Subclass of the full remote debug class that only exposes the deployBpr method.
 */
public class AeActiveBpelDeployBPRSkeleton extends AeActiveBpelAdminSkeleton
{
   /**
    * @see org.activebpel.rt.axis.bpel.admin.server.AeActiveBpelAdminSkeleton#deployBpr(org.activebpel.rt.axis.bpel.admin.types.AesDeployBprType)
    */
   public AesStringResponseType deployBpr(AesDeployBprType input) throws RemoteException
   {
      return super.deployBpr(input);
   }
}