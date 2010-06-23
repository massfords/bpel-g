//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/transaction/sql/AeSQLTransactionManager.java,v 1.3 2006/05/24 23:16:33 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.transaction.sql;

import org.activebpel.rt.bpel.server.engine.transaction.AeTransactionManager;
import org.activebpel.rt.bpel.server.engine.transaction.IAeTransaction;
import org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManager;

/**
 * Extends
 * {@link org.activebpel.rt.bpel.server.engine.transaction.AeTransactionManager}
 * to implement a transaction manager that manages instances of
 * {@link org.activebpel.rt.bpel.server.engine.transaction.sql.AeSQLTransaction}.
 */
public class AeSQLTransactionManager extends AeTransactionManager implements IAeTransactionManager
{
   /**
    * Overrides method to return an instance of <code>AeSQLTransaction</code>
    *
    * @see org.activebpel.rt.bpel.server.engine.transaction.AeTransactionManager#createTransaction()
    */
   public IAeTransaction createTransaction()
   {
      return new AeSQLTransaction();
   }
}

