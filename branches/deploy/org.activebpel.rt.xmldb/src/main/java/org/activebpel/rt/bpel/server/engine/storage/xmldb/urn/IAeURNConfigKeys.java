//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/urn/IAeURNConfigKeys.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.urn;

/**
 * Constants for the URN storage SQL keys (keys into the {@link
 * org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLConfig} object).
 */
public interface IAeURNConfigKeys
{
   public static final String INSERT_MAPPING = "InsertMapping"; //$NON-NLS-1$
   public static final String UPDATE_MAPPING = "UpdateMapping"; //$NON-NLS-1$
   public static final String DELETE_MAPPING = "DeleteMapping"; //$NON-NLS-1$
   public static final String GET_MAPPINGS = "GetMappings"; //$NON-NLS-1$
}
