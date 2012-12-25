//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.exist/src/org/activebpel/rt/bpel/server/engine/storage/exist/IAeExistDataSource.java,v 1.4 2007/10/03 12:40:33 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.exist;

import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBDataSource;

/**
 * An interface that defines the set of methods needed to give access to the Exist database.
 */
public interface IAeExistDataSource extends IAeXMLDBDataSource
{
   /** A key into the config map (the URL to the Exist server). */
   public static final String URL = "URL";  //$NON-NLS-1$
   /** A key into the config map (the Exist database to use). */
   public static final String DATABASE = "Database";  //$NON-NLS-1$
   /** A key into the config map (the Exist collection to use). */
   public static final String COLLECTION = "Collection"; //$NON-NLS-1$
   /** A key into the config map (the Exist collection to use). */
   public static final String EMBEDDED = "Embedded"; //$NON-NLS-1$
   /** A key into the config map (the Exist collection to use). */
   public static final String INITIALIZE_EMBEDDED = "InitEmbedded"; //$NON-NLS-1$
   /** A key into the config map (the Exist collection to use). */
   public static final String DB_LOCATION = "DBLocation"; //$NON-NLS-1$
   /** A key into the config map (the size of the connection pool). */
   public static final String CONNECTION_POOL_SIZE = "PoolSize"; //$NON-NLS-1$

   /**
    * Returns the name of the collection this data source is configured for.
    */
   public String getCollectionName();
}
