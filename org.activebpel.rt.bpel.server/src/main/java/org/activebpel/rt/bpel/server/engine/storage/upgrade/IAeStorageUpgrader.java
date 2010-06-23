//$Header$
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.upgrade;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;

/**
 * A storage upgrader is responsible for upgrading a storage object to the latest
 * version.
 */
public interface IAeStorageUpgrader
{
   /**
    * Upgrades a storage to the newest version.  What this means will be very specific to
    * the implementing class.
    */
   public void upgrade() throws AeStorageException;
}
