// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeConflictingRequestException.java,v 1.5 2006/10/05 21:15:31 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.bpel.AeMessages;

/**
 * models a bpws:conflictingRequest
 */
public class AeConflictingRequestException extends AeBpelException
{
   /** error message */
   private static final String ERROR_MESSAGE = AeMessages.getString("AeConflictingRequestException.0"); //$NON-NLS-1$

   /**
    * Creates the exception with a bpws:conflictingRequest fault. 
    * @param aBpelNamespace
    */
   public AeConflictingRequestException(String aBpelNamespace)
   {
      super(ERROR_MESSAGE, 
               AeFaultFactory.getFactory(aBpelNamespace).getConflictingRequest());
   }
}
