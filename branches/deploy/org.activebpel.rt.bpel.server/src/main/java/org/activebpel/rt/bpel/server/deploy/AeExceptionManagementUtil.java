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
import org.activebpel.rt.util.AeUtil;
import org.w3c.dom.Document;

/**
 * Utility class for process exception management.
 */
public class AeExceptionManagementUtil
{

   /**
    * Extract the <code>AeExceptionManagementType</code> from the process
    * deployment descriptor (.pdd) document.
    * @param aPddDoc
    */
   public static AeExceptionManagementType getExceptionManagementType( Document aPddDoc )
   {
      AeExceptionManagementType type = AeExceptionManagementType.ENGINE;

      String processSpecificType = 
         aPddDoc.getDocumentElement().getAttribute(
               IAePddXmlConstants.ATT_SUSPEND_PROCESS_ON_UNCAUGHT_FAULT);

      // if an override for process exception management was specified in 
      // the process deployment descriptor create the appropriate type
      if( AeUtil.notNullOrEmpty(processSpecificType) )
      {
         if( AeUtil.toBoolean(processSpecificType) )
         {
            type = AeExceptionManagementType.SUSPEND;
         }
         else
         {
            type = AeExceptionManagementType.NORMAL;
         }
      }
      return type;
   }
   
   /**
    * Return true if the process should be suspended if it encounters an
    * uncaught fault based on its persisten type and exception management
    * type.
    * @param aExceptionManagementType
    * @param aPersistenceType
    */
   public static boolean isSuspendOnUncaughtFaultEnabled( 
         AeExceptionManagementType aExceptionManagementType, 
         AeProcessPersistenceType aPersistenceType )
   {
      boolean suspendMe = false;
      
      // if the process is a service flow, we will never suspend it
      if( AeProcessPersistenceType.NONE != aPersistenceType )
      {
         if( AeExceptionManagementType.ENGINE == aExceptionManagementType )
         {
            suspendMe = AeEngineFactory.getEngineConfig().isSuspendProcessOnUncaughtFault();
         }
         else
         {
            suspendMe = AeExceptionManagementType.SUSPEND == aExceptionManagementType; 
         }
      }
      return suspendMe;
   }
}
