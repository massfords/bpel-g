// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/main/AeErrorReporterConsole.java,v 1.3 2005/02/08 15:36:05 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate.main;

import java.text.MessageFormat;

import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.AeMessages;

/**
 *  Sends all errors/messages to System.out.
 */
public class AeErrorReporterConsole implements IAeBaseErrorReporter
{
   /** Error message indicator. */
   protected boolean mHasErrors;
   /** Warning message indicator */
   protected boolean mHasWarnings;

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#addError(java.lang.String, java.lang.Object[], java.lang.Object)
    */
   public void addError(String aErrorCode, Object[] aArgs, Object aNode)
   {
      display( AeMessages.getString("AeErrorReporterConsole.0") + aErrorCode, aArgs ); //$NON-NLS-1$
      mHasErrors = true;
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#addInfo(java.lang.String, java.lang.Object[], java.lang.Object)
    */
   public void addInfo(String aInfoCode, Object[] aArgs, Object aNode)
   {
      display( AeMessages.getString("AeErrorReporterConsole.1") + aInfoCode, aArgs ); //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#addWarning(java.lang.String, java.lang.Object[], java.lang.Object)
    */
   public void addWarning(String aWarnCode, Object[] aArgs, Object aNode)
   {
      display( AeMessages.getString("AeErrorReporterConsole.2") + aWarnCode, aArgs ); //$NON-NLS-1$
      mHasWarnings = true;
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#hasErrors()
    */
   public boolean hasErrors()
   {
      return mHasErrors;
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#hasWarnings()
    */
   public boolean hasWarnings()
   {
      return mHasWarnings;
   }
   
   /**
    * Handles any message formatting and writes the
    * results to the console.
    * @param aMessage A message template.
    * @param aArgs Message parameters.
    */
   protected void display( String aMessage, Object[] aArgs )
   {
      if( aArgs != null )
      {
         System.out.println( MessageFormat.format(aMessage,aArgs) );
      }
   }
}
