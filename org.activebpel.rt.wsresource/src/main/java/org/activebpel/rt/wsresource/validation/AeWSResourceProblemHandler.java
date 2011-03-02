// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/AeWSResourceProblemHandler.java,v 1.6 2008/02/15 17:40:16 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.wsresource.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.xml.sax.SAXParseException;

/**
 * Implementation of a ws resource validation problem handler.
 */
public class AeWSResourceProblemHandler implements IAeWSResourceProblemHandler
{
   /** The collected problems, if any. */
   private Collection mProblems;

   /**
    * C'tor.
    */
   public AeWSResourceProblemHandler()
   {
      setProblems(new ArrayList());
   }

   /**
    * @see org.activebpel.rt.wsresource.validation.IAeWSResourceProblemHandler#reportProblem(javax.xml.namespace.QName, int, java.lang.String, java.lang.String)
    */
   public void reportProblem(QName aProblemId, int aSeverity, String aProblemMessage, String aXPath)
   {
      getProblems().add(new AeWSResourceProblem(aProblemId, aSeverity, aProblemMessage, aXPath));
   }

   /**
    * @param aErrors the problems to set
    */
   protected void setProblems(Collection aErrors)
   {
      mProblems = aErrors;
   }

   /**
    * @see org.activebpel.rt.wsresource.validation.IAeWSResourceProblemHandler#reportFatalError(java.lang.Exception)
    */
   public void reportFatalError(Exception aException)
   {
      getProblems().add(new AeWSResourceProblem(new QName("", "fatalError"), //$NON-NLS-1$ //$NON-NLS-2$
            IAeWSResourceValidationPreferences.SEVERITY_ERROR, aException.getLocalizedMessage(), 0));
   }

   /**
    * @see org.activebpel.rt.wsresource.validation.IAeWSResourceProblemHandler#reportParseProblem(org.xml.sax.SAXParseException,
    *      int)
    */
   public void reportParseProblem(SAXParseException aException, int aSeverity)
   {
      getProblems().add(new AeWSResourceProblem(new QName("", "parseProblem"), //$NON-NLS-1$ //$NON-NLS-2$
            aSeverity, aException.getLocalizedMessage(), aException.getLineNumber()));

   }

   /**
    * @return Returns the problems.
    */
   public Collection getProblems()
   {
      return mProblems;
   }

   /**
    * Returns true if any problems were found during validation.
    */
   public boolean hasProblems()
   {
      return getProblems().size() > 0;
   }

   /**
    * Returns true if any problems with the given severity were
    * found during validation.
    *
    * @param aSeverity
    */
   public boolean hasProblems(int aSeverity)
   {
      for (Iterator iter = getProblems().iterator(); iter.hasNext(); )
      {
         AeWSResourceProblem problem = (AeWSResourceProblem) iter.next();
         if (problem.getSeverity() == aSeverity)
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Returns true if errors were reported.
    */
   public boolean hasErrors()
   {
      return hasProblems(IAeWSResourceValidationPreferences.SEVERITY_ERROR);
   }

   /**
    * Returns true if warnings were reported.
    */
   public boolean hasWarnings()
   {
      return hasProblems(IAeWSResourceValidationPreferences.SEVERITY_WARNING);
   }
}
