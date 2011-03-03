//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeExceptionManagementUtil.java,v 1.1 2005/08/31 22:09:34 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

import bpelg.services.deploy.types.pdd.PersistenceType;
import bpelg.services.deploy.types.pdd.SuspendFlag;

/**
 * Utility class for process exception management.
 */
public class AeExceptionManagementUtil
{

   /**
    * Return true if the process should be suspended if it encounters an
    * uncaught fault based on its persisten type and exception management
    * type.
    * @param aSuspendFlag
    * @param aPersistenceType
    */
	// FIXME deploy - fix this in the schema, this looks weird
   public static boolean isSuspendOnUncaughtFaultEnabled( 
         SuspendFlag aSuspendFlag, 
         PersistenceType aPersistenceType )
   {
      boolean suspendMe = false;
      
      // if the process is a service flow, we will never suspend it
      if( PersistenceType.NONE != aPersistenceType )
      {
         if( aSuspendFlag == null )
         {
            suspendMe = AeEngineFactory.getEngineConfig().isSuspendProcessOnUncaughtFault();
         }
         else
         {
            suspendMe = aSuspendFlag == SuspendFlag.TRUE; 
         }
      }
      return suspendMe;
   }
}
