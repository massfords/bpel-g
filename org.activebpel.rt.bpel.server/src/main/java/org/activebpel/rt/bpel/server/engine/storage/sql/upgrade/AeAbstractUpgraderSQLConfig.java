//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/upgrade/AeAbstractUpgraderSQLConfig.java,v 1.2 2006/02/10 21:51:14 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql.upgrade;

import java.util.LinkedList;
import java.util.List;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLConfig;
import org.activebpel.rt.util.AeUtil;

/**
 * Extends the base <code>AeSQLConfig</code> class in order to supply additional SQL statements loaded from
 * the upgrade-sql.xml file.
 */
public abstract class AeAbstractUpgraderSQLConfig extends AeSQLConfig
{
   /** The default config to use when a statement is not found. */
   private AeSQLConfig mDefaultConfig;

   /**
    * Constructs an upgrader sql config object.
    * 
    * @param aDefaultConfig
    */
   public AeAbstractUpgraderSQLConfig(AeSQLConfig aDefaultConfig)
   {
      super(aDefaultConfig.getDatabaseType(), aDefaultConfig.getConstantOverrides());
      setDefaultConfig(aDefaultConfig);
   }

   /**
    * @return Returns the defaultConfig.
    */
   protected AeSQLConfig getDefaultConfig()
   {
      return mDefaultConfig;
   }

   /**
    * @param aDefaultConfig The defaultConfig to set.
    */
   protected void setDefaultConfig(AeSQLConfig aDefaultConfig)
   {
      mDefaultConfig = aDefaultConfig;
   }

   /**
    * First looks up the statement in its own registry.  If not found, the statement will be looked
    * up in the default config.
    * 
    * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLConfig#getSQLStatement(java.lang.String)
    */
   public String getSQLStatement(String aKey)
   {
      String sql = super.getSQLStatement(aKey);
      if (AeUtil.isNullOrEmpty(sql))
      {
         sql = getDefaultConfig().getSQLStatement(aKey);
      }
      return sql;
   }

   /**
    * First looks up the statement in its own registry.  If not found, the statement will be looked
    * up in the default config.
    * 
    * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLConfig#getLimitStatement(int)
    */
   public String getLimitStatement(int aLimitValue)
   {
      String stmt = super.getLimitStatement(aLimitValue);
      if (AeUtil.isNullOrEmpty(stmt))
      {
         stmt = getDefaultConfig().getLimitStatement(aLimitValue);
      }
      return stmt;
   }

   /**
    * First looks up the parameter in its own registry.  If not found, the parameter will be looked
    * up in the default config.
    * 
    * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLConfig#getParameter(java.lang.String)
    */
   public String getParameter(String aKey) throws AeStorageException
   {
      try
      {
         return super.getParameter(aKey);
      }
      catch (AeStorageException se)
      {
         return getDefaultConfig().getParameter(aKey);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLConfig#getStatementConfigFilenames()
    */
   protected List getStatementConfigFilenames()
   {
      String commonFileName = "upgrade-sql.xml"; //$NON-NLS-1$
      String specificFileName = "upgrade-" + mType + "-sql.xml"; //$NON-NLS-1$ //$NON-NLS-2$

      List list = new LinkedList();
      list.add(new AeFilenameClassTuple(commonFileName, getClass()));
      list.add(new AeFilenameClassTuple(specificFileName, getClass()));

      return list;
   }
}
