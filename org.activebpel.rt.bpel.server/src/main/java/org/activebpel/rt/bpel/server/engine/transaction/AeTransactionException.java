//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/transaction/AeTransactionException.java,v 1.3 2006/05/24 23:16:32 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.transaction;

import org.activebpel.rt.bpel.AeBusinessProcessException;

/**
 * Standard exception for the transaction package.
 */
public class AeTransactionException extends AeBusinessProcessException
{
   /** Constructor. */
   public AeTransactionException(String aInfo)
   {
      super(aInfo);
   }

   /** Constructor. */
   public AeTransactionException(String aInfo, Throwable aRootCause)
   {
      super(aInfo, aRootCause);
   }

   /** Constructor. */
   public AeTransactionException(Throwable aRootCause)
   {
      super(aRootCause.getLocalizedMessage(), aRootCause);
   }
}
