//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/AeMismatchedAssignmentException.java,v 1.3 2006/09/20 22:12:05 TZhang Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign; 

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeFaultFactory;

/**
 * Exception for the standard BPEL fault bpel:mismatchedAssignmentFailure
 */
public class AeMismatchedAssignmentException extends AeBpelException
{
   /** Error message for the exception */
   private static final String ERROR_MESSAGE = AeMessages.getString("AeMismatchedAssignmentException.Message"); //$NON-NLS-1$

   /**
    * Creates the exception with a bpws:mismatchedAssignmentFailure fault. 
    */
   public AeMismatchedAssignmentException(String aNamespace)
   {
      this(aNamespace, null);
   }
   
   /**
    * Creates the exception with a bpws:mismatchedAssignmentFailure fault. 
    */
   public AeMismatchedAssignmentException(String aNamespace, Throwable aThrowable)
   {
      super(ERROR_MESSAGE, AeFaultFactory.getFactory(aNamespace).getMismatchedAssignmentFailure(), aThrowable);
   }
}
 