//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.exist/src/org/activebpel/rt/bpel/server/engine/storage/exist/tx/AeExistTransactionManager.java,v 1.2 2007/08/17 00:59:50 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.exist.tx;

import org.activebpel.rt.bpel.server.engine.storage.xmldb.tx.AeXMLDBTransactionManager;
import org.activebpel.rt.bpel.server.engine.transaction.AeTransactionException;
import org.activebpel.rt.bpel.server.engine.transaction.IAeTransaction;

/**
 * Transaction manager implementation for the Exist storage.
 */
public class AeExistTransactionManager extends AeXMLDBTransactionManager
{
   /**
    * Overrides method to return a <code>IAeExistTransaction</code> implementation.
    * 
    * @see org.activebpel.rt.bpel.server.engine.transaction.AeTransactionManager#createTransaction()
    */
   protected IAeTransaction createTransaction() throws AeTransactionException
   {
      return new AeExistTransaction();
   }
}
