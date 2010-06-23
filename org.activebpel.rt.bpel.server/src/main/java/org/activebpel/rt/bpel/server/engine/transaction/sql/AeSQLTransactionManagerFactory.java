//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/transaction/sql/AeSQLTransactionManagerFactory.java,v 1.3 2006/05/24 23:16:33 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.transaction.sql;

import java.util.Map;

import org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManager;
import org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManagerFactory;

/**
 * Implements a transaction manager factory that constructs instances of
 * {@link org.activebpel.rt.bpel.server.engine.transaction.sql.AeSQLTransactionManager}.
 */
public class AeSQLTransactionManagerFactory implements IAeTransactionManagerFactory
{
   /**
    * Constructs transaction manager factory with the specified configuration.
    *
    * @param aConfigMap
    */
   public AeSQLTransactionManagerFactory(Map aConfigMap)
   {
      // No configuration required.
   }

   /**
    * Overrides method to return an instance of <code>AeSQLTransactionManager</code>.
    *
    * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManagerFactory#createTransactionManager()
    */
   public IAeTransactionManager createTransactionManager()
   {
      return new AeSQLTransactionManager();
   }
}
