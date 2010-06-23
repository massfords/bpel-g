//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/IAeTransmissionTrackerSQLKeys.java,v 1.1 2006/05/24 23:16:32 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

/**
 * Constants for the transmission manager storage SQL keys (keys into the {@link
 * org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLConfig} object).
 */
public interface IAeTransmissionTrackerSQLKeys
{
   public static final String GET_ENTRY     = "GetEntry"; //$NON-NLS-1$
   public static final String INSERT_ENTRY  = "InsertEntry"; //$NON-NLS-1$
   public static final String UPDATE_ENTRY  = "UpdateEntry"; //$NON-NLS-1$
   public static final String DELETE_ENTRY  = "DeleteEntry"; //$NON-NLS-1$   
}
