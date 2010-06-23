//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/upgrade/IAeUpgraderSQLKeys.java,v 1.1 2005/03/17 21:11:57 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql.upgrade;

/**
 * Constants for the storage upgrade SQL keys (keys into the SQLConfig object).
 */
public interface IAeUpgraderSQLKeys
{
   /** The SQL statement key for getting the list of upgrade directives from the meta info table. */
   public static final String GET_UPGRADE_DIRECTIVES = "GetUpgradeDirectives"; //$NON-NLS-1$
   /** The SQL statement key for deleting a patch/upgrade entry in the meta info table. */
   public static final String DELETE_UPGRADE_DIRECTIVE = "DeleteUpgradeDirective"; //$NON-NLS-1$
}
