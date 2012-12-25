//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/IAeURNSQLKeys.java,v 1.1 2006/01/03 20:34:57 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

/**
 * Constants for the URN storage SQL keys (keys into the {@link
 * org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLConfig} object).
 */
public interface IAeURNSQLKeys
{
   public static final String SQL_INSERT_MAPPING = "InsertMapping"; //$NON-NLS-1$
   public static final String SQL_UPDATE_MAPPING = "UpdateMapping"; //$NON-NLS-1$
   public static final String SQL_DELETE_MAPPING = "DeleteMapping"; //$NON-NLS-1$
   public static final String SQL_GET_MAPPINGS = "GetMappings"; //$NON-NLS-1$
}
