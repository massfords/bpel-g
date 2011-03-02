//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/logging/AeDeploymentLog.java,v 1.8 2005/02/08 15:36:04 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.logging; 

import java.text.MessageFormat;
import java.util.Date;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.util.AeUtil;

/**
 * Implementation of the error reporter used during the deployment of BPR's.
 */
abstract public class AeDeploymentLog implements IAeDeploymentLogger
{
   /** Indicates if there were validation warnings. */
   protected boolean mHasWarnings;
   /** Indicates if there were validation errors. */
   protected boolean mHasErrors;
   /** A running total of the number of errors logged. */
   protected int mNumErrors;
   /** A running total of the number of warnings logged. */
   protected int mNumWarnings;
   /** Container name (for logging output). */
   protected String mContainerName;
   /** pdd name currently being deployed */
   protected String mPddName;
   /** Date log was created */
   protected Date mDate = new Date();

   /**
    * No-arg constructor for subclasses to init the writer how they see fit. 
    */
   protected AeDeploymentLog()
   {
      mNumErrors = 0;
      mNumWarnings = 0;
      mPddName = null;
   }

   
   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#addError(java.lang.String, java.lang.Object[], java.lang.Object)
    */
   public void addError(String aErrorCode, Object[] aArgs, Object aNode)
   {
      addMessage(aErrorCode, aArgs, aNode);
      mHasErrors = true;
      incrementNumErrors();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#addInfo(java.lang.String, java.lang.Object[], java.lang.Object)
    */
   public void addInfo(String aInfoCode, Object[] aArgs, Object aNode)
   {
      addMessage(aInfoCode, aArgs, aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#addWarning(java.lang.String, java.lang.Object[], java.lang.Object)
    */
   public void addWarning(String aWarnCode, Object[] aArgs, Object aNode)
   {
      addMessage(AeMessages.getString("AeDeploymentLog.0") + aWarnCode, aArgs, aNode); //$NON-NLS-1$
      mHasWarnings = true;
      incrementNumWarnings();
   }


   /**
    * Increments the number of errors found for the current PDD deployment.
    */
   protected void incrementNumErrors()
   {
      mNumErrors++;
   }

   /**
    * Increments the number of errors found for the current PDD deployment.
    */
   protected void incrementNumWarnings()
   {
      mNumWarnings++;
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#hasErrors()
    */
   public boolean hasErrors()
   {
      return mHasErrors;
   }

   /**
    * Returns the number of errors found.
    */
   public int getNumErrors()
   {
      return mNumErrors;
   }

   /**
    * Returns the number of warnings found.
    */
   public int getNumWarnings()
   {
      return mNumWarnings;
   }

   /**
    * Substitute the values in aArgs for the '{n}'s in the base message text.
    * 
    * @param aBaseMsg The base message, which may contain '{n}'s.
    * @param aArgs The args used to replace '{n}'s.
    * 
    * @return Formatted message string.
    */
   protected String formatIssue( String aBaseMsg, Object[] aArgs )
   {
      String result = aBaseMsg;
      if ( aArgs != null && aBaseMsg.indexOf('{') >= 0 )
      {
         MessageFormat form = new MessageFormat( aBaseMsg );
         result = form.format( aArgs );
      }
      StringBuffer sb = new StringBuffer();
      appendContainerName(sb);
      if (getPddName() != null)
      {
         sb.append('[');
         sb.append(getPddName());
         sb.append("] "); //$NON-NLS-1$
      }
      sb.append(result);
         
      return sb.toString();
   }

   /**
    * Appends the container name to the buffer. Extracted as a method to allow
    * the persistent logger to override and not include the container name in the log.
    * @param aBuffer
    */
   protected void appendContainerName(StringBuffer aBuffer)
   {
      if (!AeUtil.isNullOrEmpty(mContainerName))
      {
         aBuffer.append('[').append(mContainerName).append("] "); //$NON-NLS-1$
      }
   }

   /**
    * Adds the message to the buffer.
    * @param aMessagePattern
    * @param aArgs
    * @param aNode
    */
   protected void addMessage(String aMessagePattern, Object[] aArgs, Object aNode)
   {
      String message = formatIssue(aMessagePattern, aArgs);
      writeMessage(message);
   }
   
   /**
    * Writes the message to the buffer and the writer if it's present.
    * @param aMessage
    */
   abstract protected void writeMessage(String aMessage);

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#hasWarnings()
    */
   public boolean hasWarnings()
   {
      return mHasWarnings;
   }
   
   /**
    * @return Returns the date.
    */
   public Date getDate()
   {
      return mDate;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger#setContainerName(java.lang.String)
    */
   public void setContainerName(String aContainerName)
   {
      mContainerName = aContainerName;
   }

   /**
    * @see org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger#resetWarningAndErrorFlags()
    */
   public void resetWarningAndErrorFlags()
   {
      mHasErrors = false;
      mHasWarnings = false;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger#setPddName(java.lang.String)
    */
   public void setPddName(String aPddName)
   {
      mPddName = aPddName;
   }
   
   /**
    * Returns the name of the container currently being deployed.
    */
   protected String getContainerName()
   {
      return mContainerName;
   }
   
   /**
    * Returns the name of the pdd file currently being deployed
    */
   protected String getPddName()
   {
      return mPddName;
   }

   /**
    * @see org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger#addInfo(java.lang.String)
    */
   public void addInfo(String aMessage)
   {
      addInfo(aMessage, null, null);
   }

   /**
    * @see org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger#processDeploymentFinished(boolean)
    */
   public void processDeploymentFinished(boolean aBool)
   {
      mPddName = null;
   }

}
 