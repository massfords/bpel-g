// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/IAeValidationProblemReporter.java,v 1.1 2008/03/20 16:00:22 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation;

/**
 * Interface for reporting errors, warnings and informational messages found 
 * during the analysis a bpel process.
 */
public interface IAeValidationProblemReporter extends IAeBaseErrorReporter
{
   /**
    * Report a problem to the error reporter.
    * 
    * @param aProblemCode - A problem code that identifies the error.
    * @param aMessage - The problem message text
    * @param aArgs - Object array containing substitution args for '{n}'s. 
    * @param aNode - The node on which the error occurred.
    */
   public void reportProblem(String aProblemCode, String aMessage, Object[] aArgs, Object aNode);
   
}
