//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/upgrade/AeAbstractSQLUpgrader.java,v 1.2 2006/02/24 16:37:31 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql.upgrade;

import java.sql.Connection;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeAbstractSQLStorage;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLConfig;
import org.activebpel.rt.bpel.server.engine.storage.upgrade.IAeStorageUpgrader;
import org.activebpel.rt.util.AeCloser;

/**
 * This class represents an abstract SQL storage upgrader class.  It provides common functionality
 * for all SQL storage upgraders.
 */
public abstract class AeAbstractSQLUpgrader extends AeAbstractSQLStorage implements IAeStorageUpgrader
{
   /** The SQL statement prefix for all SQL statements used in this class. */
   private static final String SQLSTATEMENT_PREFIX = "Upgrade."; //$NON-NLS-1$

   /** The name of this upgrade. */
   private String mUpgradeName;

   /**
    * Constructs an abstract SQL storage upgrader.
    * 
    * @param aUpgradeName The name of the upgrader (found in the PropertyName column)
    * @param aSQLConfig
    */
   public AeAbstractSQLUpgrader(String aUpgradeName, AeSQLConfig aSQLConfig)
   {
      super(SQLSTATEMENT_PREFIX, aSQLConfig);
      setUpgradeName(aUpgradeName);

      // Change the SQL config we will be using.
      setConfig(wrapSQLConfig(aSQLConfig));
   }

   /**
    * Wraps the SQL config object in a new SQL config that will supply its own keys as well as
    * proxying the keys provided.  Default implementation does not wrap the SQL config, but 
    * simple returns the one passed in.
    * 
    * @param aSQLConfig
    */
   public AeSQLConfig wrapSQLConfig(AeSQLConfig aSQLConfig)
   {
      return aSQLConfig;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.upgrade.IAeStorageUpgrader#upgrade()
    */
   public void upgrade() throws AeStorageException
   {
      Connection conn = null;
      try
      {
         conn = getCommitControlConnection();

         // Attempt to remove the upgrade directive from the meta info table.  If the removal 
         // succeeds, then we have acquired the lock - so go ahead and do the upgrade.
         int cols = update(conn, IAeUpgraderSQLKeys.DELETE_UPGRADE_DIRECTIVE, new Object[] { getUpgradeName() });
         if (cols == 1)
         {
            doUpgrade(conn);
         }

         // Now commit the transaction - this will finalize the upgrade and release the lock we have
         // on this entry in the meta info table.  Anyone else waiting to do this same operation will
         // see that their delete failed and skip to the next upgrade.
         conn.commit();
      }
      catch (Throwable t)
      {
         AeException.logError(t, t.getLocalizedMessage());
      }
      finally
      {
         AeCloser.close(conn);
      }
   }
   
   /**
    * Internal method to do the actual upgrade work.  This method is given a Connection object
    * that it should use to do the upgrade.
    * 
    * @param aConnection
    * @throws AeException
    */
   protected abstract void doUpgrade(Connection aConnection) throws AeException;

   /**
    * @return Returns the upgradeName.
    */
   protected String getUpgradeName()
   {
      return mUpgradeName;
   }

   /**
    * @param aUpgradeName The upgradeName to set.
    */
   protected void setUpgradeName(String aUpgradeName)
   {
      mUpgradeName = aUpgradeName;
   }
}
