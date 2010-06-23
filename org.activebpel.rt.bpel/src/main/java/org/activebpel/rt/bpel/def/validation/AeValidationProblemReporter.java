// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/AeValidationProblemReporter.java,v 1.1 2008/03/20 16:00:22 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.util.AeUtil;

/**
 * An problem reporter to report problems using a problem code.  This class delegates all
 * problems to an {@link IAeBaseErrorReporter} using an xml backed code repository to convert
 * codes to severity.
 */
public class AeValidationProblemReporter implements IAeValidationProblemReporter
{
   /** reporter in which all reportProblem calls will be delegated to */
   private IAeBaseErrorReporter mDelegate = null;
   /** the namespace for the set of codes/severity that will used for reporting */
   private String mNamespace = null;
   
   /**
    * C'tor
    * @param aDelegate
    */
   public AeValidationProblemReporter(IAeBaseErrorReporter aDelegate, String aBpelNamespace)
   {
      setDelegate(aDelegate);
      setNamespace(aBpelNamespace);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeValidationProblemReporter#reportProblem(java.lang.String, java.lang.String, java.lang.Object[], java.lang.Object)
    */
   public void reportProblem(String aProblemCode, String aMessage, Object[] aArgs, Object aNode)
   {
      // get the severity for the given code
      String severity = AeValidatorCodeRegistry.getProblemSeverity(getNamespace(), aProblemCode, AeValidatorCodeRegistry.ERROR);
      
      //delegate the message to the correct method of the IAeBaseErrorReporter
      
      if (AeUtil.compareObjects(AeValidatorCodeRegistry.ERROR, severity))
      {
         getDelegate().addError(aMessage, aArgs, aNode);
      }
      else if (AeUtil.compareObjects(AeValidatorCodeRegistry.WARNING, severity))
      {
         getDelegate().addWarning(aMessage, aArgs, aNode);
      }
      else if (AeUtil.compareObjects(AeValidatorCodeRegistry.INFO, severity))
      {
         getDelegate().addInfo(aMessage, aArgs, aNode);
      }
      else if (AeUtil.compareObjects(AeValidatorCodeRegistry.SKIP, severity))
      {
         // NO-OP, for skip
      }
      else
      {
         //for unrecognized problem codes report an error, this should never happen
         AeException.logWarning(AeMessages.format("AeValidationProblemReporter.PROBLEM_CODE_NOT_RECOGNIZED", aProblemCode)); //$NON-NLS-1$
         getDelegate().addError(aMessage, aArgs, aNode);
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#addError(java.lang.String, java.lang.Object[], java.lang.Object)
    */
   public void addError(String aErrorCode, Object[] aArgs, Object aNode)
   {
      getDelegate().addError(aErrorCode, aArgs, aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#addInfo(java.lang.String, java.lang.Object[], java.lang.Object)
    */
   public void addInfo(String aInfoCode, Object[] aArgs, Object aNode)
   {
      getDelegate().addInfo(aInfoCode, aArgs, aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#addWarning(java.lang.String, java.lang.Object[], java.lang.Object)
    */
   public void addWarning(String aWarnCode, Object[] aArgs, Object aNode)
   {
      getDelegate().addWarning(aWarnCode, aArgs, aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#hasErrors()
    */
   public boolean hasErrors()
   {
      return getDelegate().hasErrors();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#hasWarnings()
    */
   public boolean hasWarnings()
   {
      return getDelegate().hasWarnings();
   }

   /**
    * @return Returns the delegate.
    */
   protected IAeBaseErrorReporter getDelegate()
   {
      return mDelegate;
   }

   /**
    * @param aDelegate the delegate to set
    */
   protected void setDelegate(IAeBaseErrorReporter aDelegate)
   {
      mDelegate = aDelegate;
   }

   /**
    * @return Returns the namespace.
    */
   public String getNamespace()
   {
      return mNamespace;
   }

   /**
    * @param aNamespace the namespace to set
    */
   public void setNamespace(String aNamespace)
   {
      mNamespace = aNamespace;
   }

}
