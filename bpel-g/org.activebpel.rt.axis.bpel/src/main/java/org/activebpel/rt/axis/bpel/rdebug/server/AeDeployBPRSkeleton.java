//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/rdebug/server/AeDeployBPRSkeleton.java,v 1.1 2005/01/26 15:38:35 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.rdebug.server; 

import java.rmi.RemoteException;

import org.activebpel.rt.bpel.AeBusinessProcessException;

/**
 * Server binding for deploying bprs. Subclass of the full remote debug class
 * that only exposes the deployBpr method. 
 */
public class AeDeployBPRSkeleton extends AeRemoteDebugSkeleton
{
   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#deployBpr(java.lang.String, java.lang.String)
    */
   public String deployBpr(String aBprFilename, String aBase64File)
         throws RemoteException, AeBusinessProcessException
   {
      return super.deployBpr(aBprFilename, aBase64File);
   }
}
 