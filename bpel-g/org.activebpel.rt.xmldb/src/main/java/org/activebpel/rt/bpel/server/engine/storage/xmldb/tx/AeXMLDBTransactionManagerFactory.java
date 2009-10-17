//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/tx/AeXMLDBTransactionManagerFactory.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.tx;

import java.util.Map;

import org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManagerFactory;

/**
 * Implements a transaction manager factory for the XMLDB storage layer.  This class
 * exists simply to provide a consistent type hierarchy for xml database impls.
 */
public abstract class AeXMLDBTransactionManagerFactory implements IAeTransactionManagerFactory
{
   /**
    * Constructs transaction manager factory with the specified configuration.
    *
    * @param aConfigMap
    */
   public AeXMLDBTransactionManagerFactory(Map aConfigMap)
   {
      // No configuration required.
   }
}
