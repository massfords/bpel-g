// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeDeploymentException.java,v 1.2 2004/07/08 13:10:03 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import org.activebpel.rt.bpel.AeBusinessProcessException;

/**
 * Indicates an exception having to do with the deployment of the application.
 */
public class AeDeploymentException extends AeBusinessProcessException
{
   /**
    * Constructs the exception with a string message
    * @param aInfo
    */
   public AeDeploymentException(String aInfo)
   {
      super(aInfo);
   }

   /**
    * Constructs the exception with a string message and root cause
    * @param aInfo
    * @param aRootCause
    */
   public AeDeploymentException(String aInfo, Throwable aRootCause)
   {
      super(aInfo, aRootCause);
   }

}
