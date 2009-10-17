//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/upgrade/AeSQLUpgrader.java,v 1.2 2006/02/24 16:37:31 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql.upgrade;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeAbstractSQLStorage;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLConfig;
import org.activebpel.rt.bpel.server.engine.storage.upgrade.IAeStorageUpgrader;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 * This class is responsible for querying the AeMetaInfo table for patches that need to be
 * applied to the data in the database.  If there are patch entries in that table, then
 * this class will remove them one by one and execute their corresponding upgrade code.
 * The assumption is that the DB schema has already been patched, but some data migration
 * is needed.  Each patch entry in the meta info table represents a single data migration
 * operation.
 */
public class AeSQLUpgrader extends AeAbstractSQLStorage 
{
   /** The SQL statement prefix for all SQL statements used in this class. */
   private static final String SQLSTATEMENT_PREFIX = "Upgrade."; //$NON-NLS-1$

   /**
    * Constructs a SQL upgrader.
    * 
    * @param aSQLConfig
    */
   public AeSQLUpgrader(AeSQLConfig aSQLConfig)
   {
      super(SQLSTATEMENT_PREFIX, aSQLConfig);
   }

   /**
    * This method is called to begin the upgrade process.  If no patch entries are found in
    * the meta info table, this method will do nothing.  If patch entries are found, each one
    * will be processed in turn.
    */
   public void upgrade() throws AeException
   {
      List patchList = (List) query(IAeUpgraderSQLKeys.GET_UPGRADE_DIRECTIVES, null, new AePatchListResultSetHandler());
      for (Iterator iter = patchList.iterator(); iter.hasNext(); )
      {
         AeUpgradeDirective directive = (AeUpgradeDirective) iter.next();
         try
         {
            IAeStorageUpgrader upgrader = directive.createUpgrader();
            upgrader.upgrade();
         }
         catch (Throwable t)
         {
            throw new AeException(AeMessages.getString("AeSQLUpgrader.ERROR_UPGRADING_DATABASE") + directive.getClassname(), t); //$NON-NLS-1$
         }
      }
   }

   /**
    * Implements a result set handler that returns a list of patch entries in the AeMetaInfo
    * table.
    */
   private class AePatchListResultSetHandler implements ResultSetHandler
   {
      /**
       * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
       */
      public Object handle(ResultSet aResult) throws SQLException
      {
         List rval = new ArrayList();
         while (aResult.next())
         {
            String name = aResult.getString("PropertyName"); //$NON-NLS-1$
            String val = aResult.getString("PropertyValue"); //$NON-NLS-1$
            rval.add(new AeUpgradeDirective(name, val));
         }
         return rval;
      }
   }

   /**
    * Represents a single "PATCH_*" row in the AeMetaInfo table.  Each row will have a name that
    * begins with "PATCH_" and a value that is a class that implements IAeSQLStorageUpgrader.
    */
   private class AeUpgradeDirective
   {
      /** The name of the upgrade. */
      private String mName;
      /** The class to use for upgrading. */
      private String mClassname;

      /**
       * Constructs an upgrade directive.
       * 
       * @param aName
       * @param aClassname
       */
      public AeUpgradeDirective(String aName, String aClassname)
      {
         setName(aName);
         setClassname(aClassname);
      }

      /**
       * @return Returns the classname.
       */
      public String getClassname()
      {
         return mClassname;
      }

      /**
       * @param aClassname The classname to set.
       */
      protected void setClassname(String aClassname)
      {
         mClassname = aClassname;
      }

      /**
       * @return Returns the name.
       */
      public String getName()
      {
         return mName;
      }

      /**
       * @param aName The name to set.
       */
      protected void setName(String aName)
      {
         mName = aName;
      }
      
      /**
       * Creates a SQL storage upgrader configured for this upgrade directive.  The SQL storage
       * upgrader is specified by the value of the AeMetaInfo row that this object was created
       * from.  The value is the fully qualified name of a class that implements the 
       * <code>IAeSQLStorageUpgrader</code> interface.
       */
      public IAeStorageUpgrader createUpgrader() throws AeException
      {
         try
         {
            Class c = Class.forName(getClassname());
            Constructor constructor = c.getConstructor(new Class[] { String.class, AeSQLConfig.class });
            return (IAeStorageUpgrader) constructor.newInstance(new Object [] { getName(), getSQLConfig() });
         }
         catch (Throwable t)
         {
            throw new AeException(t);
         }
      }
   }
}
