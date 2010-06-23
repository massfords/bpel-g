// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeJNDIDataSource.java,v 1.13 2006/06/15 18:45:18 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;

/**
 * Implements a JNDI version of an AeDataSource.  This implementation uses JNDI
 * to look up the configured data source with the given JNDI name.
 */
public class AeJNDIDataSource extends AeDataSource
{
   /** The JNDI name for this data source. */
   private String mJNDIName;
   /** The data source username. */
   private String mUsername;
   /** The data source password; */
   private String mPassword;

   /**
    * Constructs a JNDI data source.  Uses information in the engine configuration map
    * to initialize its state (JNDI name, username, password).
    *
    * @param aConfig The engine configuration map for this data source.
    * @param aSQLConfig The SQL configuration.
    */
   public AeJNDIDataSource(Map aConfig, AeSQLConfig aSQLConfig) throws AeException
   {
      super("JNDI", aSQLConfig); //$NON-NLS-1$

      try
      {
         String jndiName = (String) aConfig.get(IAeEngineConfiguration.DS_JNDI_NAME_ENTRY);
         String username = (String) aConfig.get(IAeEngineConfiguration.DS_USERNAME_ENTRY);
         String password = (String) aConfig.get(IAeEngineConfiguration.DS_PASSWORD_ENTRY);

         setJNDIName(jndiName);
         setUsername(username);
         setPassword(password);
      }
      catch (Exception e)
      {
         throw new AeException(AeMessages.getString("AeJNDIDataSource.ERROR_1"), e); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeDataSource#createDelegate()
    */
   public DataSource createDelegate() throws AeStorageException
   {
      DataSource ds = lookupDataSource(mJNDIName);
      if (ds == null)
         throw new AeStorageException(AeMessages.getString("AeJNDIDataSource.ERROR_2")); //$NON-NLS-1$

      return ds;
   }

   /**
    * Looks up a DataSource using JNDI.
    *
    * @param aJNDIPath The JNDI path of the DataSource.
    * @return A DataSource or null if not found.
    */
   protected DataSource lookupDataSource(String aJNDIPath)
   {
      try
      {
         Context initialContext = new InitialContext();
         return (DataSource) initialContext.lookup(aJNDIPath);
      }
      catch (NamingException e)
      {
         return null;
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeDataSource#getConnection()
    */
   public Connection getConnection() throws SQLException
   {
      Connection conn;
      if (getUsername() != null)
      {
         conn = getConnection(getUsername(), getPassword());
      }
      else
      {
         conn = super.getConnection();
      }
      // uncomment the next line to debug connections
      // conn = new AeDebugConnection(conn);
      return conn;
   }

   /** Sets the JNDI name for this data source. */
   protected void setJNDIName(String jNDIName)
   {
      mJNDIName = jNDIName;
   }

   /** Returns the JNDI name for this data source. */
   protected String getJNDIName()
   {
      return mJNDIName;
   }

   /** Sets the username for this data source. */
   protected void setUsername(String username)
   {
      mUsername = username;
   }

   /** Returns the username for this data source. */
   protected String getUsername()
   {
      return mUsername;
   }

   /** Sets the password for this data source. */
   protected void setPassword(String password)
   {
      mPassword = password;
   }

   /** Returns the password for this data source. */
   protected String getPassword()
   {
      return mPassword;
   }

	public boolean isWrapperFor(Class<?> aIface) throws SQLException {
		return getDelegate().isWrapperFor(aIface);
	}
	
	public <T> T unwrap(Class<T> aIface) throws SQLException {
		return getDelegate().unwrap(aIface);
	}
}
