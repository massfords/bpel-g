//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.exist/src/org/activebpel/rt/bpel/server/engine/storage/exist/tx/AeExistTransactionManagerFactory.java,v 1.2 2007/08/17 00:59:50 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.exist.tx;

import java.util.Map;

import org.activebpel.rt.bpel.server.engine.storage.xmldb.tx.AeXMLDBTransactionManagerFactory;
import org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManager;

/**
 * Implements a transaction manager factory for the Exist storage layer.
 */
public class AeExistTransactionManagerFactory extends AeXMLDBTransactionManagerFactory
{
   /**
    * Constructs transaction manager factory with the specified configuration.
    *
    * @param aConfigMap
    */
   public AeExistTransactionManagerFactory(Map aConfigMap)
   {
      super(aConfigMap);
   }

   /**
    * Overrides method to return a <code>AeExistTransactionManager</code> instance.
    * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManagerFactory#createTransactionManager()
    */
   public IAeTransactionManager createTransactionManager()
   {
      return new AeExistTransactionManager();
   }
}
