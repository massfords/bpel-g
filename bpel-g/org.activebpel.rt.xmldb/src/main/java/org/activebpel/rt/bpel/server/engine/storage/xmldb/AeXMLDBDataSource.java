//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/AeXMLDBDataSource.java,v 1.2 2008/02/17 21:59:05 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb;

import java.util.Map;

import org.activebpel.rt.AeException;

/**
 * The XMLDB data source.
 */
public abstract class AeXMLDBDataSource implements IAeXMLDBDataSource
{
   /**
    * Constructs a data source.
    *
    * @param aConfig The engine configuration map for this data source.
    */
   public AeXMLDBDataSource(Map aConfig) throws AeException
   {
   }

   /**
    * Gets a new XMLDB connection.
    */
   public abstract IAeXMLDBConnection getNewConnection() throws AeXMLDBException;
}
