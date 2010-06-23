//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AeDeploymentFileValidator.java,v 1.3 2005/02/08 15:35:58 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;

/**
 * This class handles some very basic file contents validation for
 * WSR and BPR deployments.
 */
public class AeDeploymentFileValidator
{
   
   private static final String INVALID_BPR = AeMessages.getString("AeDeploymentFileValidator.0"); //$NON-NLS-1$
   private static final String INVALID_WSR = AeMessages.getString("AeDeploymentFileValidator.1"); //$NON-NLS-1$
   
   /**
    * Preliminary validation of the deployment file.  Ensure that any BPR deployments DO NOT
    * contain any .wsdd files and that WSR deployments contain a .wsdd file.
    * @param aContainer
    * @param aBprFlag
    * @param aLogger
    */
   public static void validateFileType( IAeDeploymentContainer aContainer, boolean aBprFlag, IAeDeploymentLogger aLogger )
   {
      // if this is supposed to be a bpr deployment and the file contains
      // a .wsdd file, then log an error
      if( aBprFlag  )
      {
         if( aContainer.isWsddDeployment() )
         {
            aLogger.addError(INVALID_BPR, null, null );
         }
      }
      // otherwise if this is a wsr deployment and no META-INF/wsdd file
      // has been detected then log an error
      else if( !aContainer.isWsddDeployment() )
      {
         aLogger.addError(INVALID_WSR, null, null );
      }
   }
}
