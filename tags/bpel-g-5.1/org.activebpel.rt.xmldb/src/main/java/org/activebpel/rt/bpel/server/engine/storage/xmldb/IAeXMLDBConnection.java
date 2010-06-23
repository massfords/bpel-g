// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/IAeXMLDBConnection.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.xmldb;

/**
 * A connection to an XML database.  This is equivalent to a jdbc connection.
 */
public interface IAeXMLDBConnection
{
   /**
    * Closes the connection.
    */
   public void close();

   /**
    * Commit the changes.
    *
    * @throws AeXMLDBException
    */
   public void commit() throws AeXMLDBException;

   /**
    * Roll-back the connection.
    *
    * @throws AeXMLDBException
    */
   public void rollback() throws AeXMLDBException;

   /**
    * Returns the native connection for this DB connection.  For example,
    * this would return a TConnection for a Tamino implementation.
    */
   public Object getNativeConnection();
}
