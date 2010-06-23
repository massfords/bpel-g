// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeConflictingReceiveException.java,v 1.4 2006/06/26 16:50:30 mford Exp $
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
public class AeConflictingReceiveException extends AeBpelException
{
   private static final String ERROR_MESSAGE = AeMessages.getString("AeConflictingReceiveException.0"); //$NON-NLS-1$
   /**
    * Creates the exception with a bpws:conflictingRequest fault. 
    */
   public AeConflictingReceiveException(String aNamespace)
   {
      super(ERROR_MESSAGE, 
               AeFaultFactory.getFactory(aNamespace).getConflictingReceive());
   }
}
