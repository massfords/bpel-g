//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/debug/AeDebugConnection.java,v 1.1 2006/06/15 23:05:33 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql.debug;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

/**
 * Debug connection that keeps track of unclosed connections during object finalization.
 *
 */
public class AeDebugConnection implements Connection
{
   /**
    * Underlying connection.
    */
   private Connection mDelegate;
   /**
    * Rerefence close connection count.
    */
   private int mCloseCount;
   
   /**
    * Runtime stack information when the connection was obtained.
    */
   private RuntimeException mRuntimeInfo;
   
   /**
    * Owner data source;
    */
   private AeDebugDataSource mDebugDataSource;
   
   /**
    * Connection create time.
    */
   private long mCreateTime;
   
   /**
    * Slow query threadshold (in ms). Default is 1000ms.
    */
   private long mSlowQueryThreshold = 1000L;
   
   /**
    * 
    * @param aDelegate
    */
   public AeDebugConnection(Connection aDelegate)
   {
      this(null, null, aDelegate);
   }
   
   /**
    * 
    * @param aRuntimeInfo
    * @param aDelegate
    */
   public AeDebugConnection(RuntimeException aRuntimeInfo, Connection aDelegate)
   {
      this(null, aRuntimeInfo, aDelegate);
   }
   
   /**
    * Default ctor.
    * @param aDelegate
    */
   public AeDebugConnection(AeDebugDataSource aDebugDataSource, RuntimeException aRuntimeInfo, Connection aDelegate)
   {
      mDebugDataSource = aDebugDataSource;
      mRuntimeInfo = aRuntimeInfo;
      mDelegate = aDelegate;
      mCloseCount = 0;
      if (mDebugDataSource != null)
      {
         mDebugDataSource.incConnectionRefCount();
      }
      mCreateTime = System.currentTimeMillis();
   }

   /**
    * @return Returns the slowQueryThreshold.
    */
   public long getSlowQueryThreshold()
   {
      return mSlowQueryThreshold;
   }

   /**
    * @param aSlowQueryThreshold The slowQueryThreshold to set.
    */
   public void setSlowQueryThreshold(long aSlowQueryThreshold)
   {
      mSlowQueryThreshold = aSlowQueryThreshold;
   }

   /**
    * @see java.sql.Connection#getHoldability()
    */
   public int getHoldability() throws SQLException
   {
      return mDelegate.getHoldability();
   }

   /**
    * @see java.sql.Connection#getTransactionIsolation()
    */
   public int getTransactionIsolation() throws SQLException
   {
      return mDelegate.getTransactionIsolation();
   }

   /**
    * @see java.sql.Connection#clearWarnings()
    */
   public void clearWarnings() throws SQLException
   {
      mDelegate.clearWarnings();
   }

   /**
    * @see java.sql.Connection#close()
    */
   public void close() throws SQLException
   {
      long elapsed = System.currentTimeMillis() - mCreateTime;
      mCloseCount++;
      mDelegate.close();
      mCloseCount++;      
      if (mDebugDataSource != null)
      {
         mDebugDataSource.decConnectionRefCount();
      }
      // Catch slow queries (actually a collection of queries used by this connection).
      if (elapsed > getSlowQueryThreshold())
      {
         String s = "** DEBUG: Slow query connection: " + elapsed + " ms  on thread: " + Thread.currentThread().getName(); //$NON-NLS-1$ //$NON-NLS-2$
         System.out.println(s);
         RuntimeException re = new RuntimeException(s);
         if (mDebugDataSource != null)
         {
            mDebugDataSource.getSlowQueryConnectionList().add(re);
         }
      }      
   }
   
   /**
    * @see java.lang.Object#finalize()
    */
   protected void finalize() throws Throwable
   {
      if (mCloseCount != 2)
      {
         System.out.println("** Connection not closed. Got close ref count: " + mCloseCount); //$NON-NLS-1$
         if (mDebugDataSource != null && mRuntimeInfo != null)
         {
            mDebugDataSource.getOpenConnectionList().add(mRuntimeInfo);
         }
         else if (mRuntimeInfo != null)
         {
            mRuntimeInfo.printStackTrace();
         }
         else
         {
            throw new RuntimeException("** Connection not closed. Got close ref count: " + mCloseCount); //$NON-NLS-1$
         }
      }
      super.finalize();
   }

   /**
    * @see java.sql.Connection#commit()
    */
   public void commit() throws SQLException
   {
      mDelegate.commit();
   }

   /**
    * @see java.sql.Connection#rollback()
    */
   public void rollback() throws SQLException
   {
      mDelegate.rollback();
   }

   /**
    * @see java.sql.Connection#getAutoCommit()
    */
   public boolean getAutoCommit() throws SQLException
   {
      return mDelegate.getAutoCommit();
   }

   /**
    * @see java.sql.Connection#isClosed()
    */
   public boolean isClosed() throws SQLException
   {
      return mDelegate.isClosed();
   }

   /**
    * @see java.sql.Connection#isReadOnly()
    */
   public boolean isReadOnly() throws SQLException
   {
      return mDelegate.isReadOnly();
   }

   /**
    * @see java.sql.Connection#setHoldability(int)
    */
   public void setHoldability(int aHoldability) throws SQLException
   {
      mDelegate.setHoldability(aHoldability);
   }

   /**
    * @see java.sql.Connection#setTransactionIsolation(int)
    */
   public void setTransactionIsolation(int aLevel) throws SQLException
   {
      mDelegate.setTransactionIsolation(aLevel);
   }

   /**
    * @see java.sql.Connection#setAutoCommit(boolean)
    */
   public void setAutoCommit(boolean autoCommit) throws SQLException
   {
      mDelegate.setAutoCommit(autoCommit);
   }

   /**
    * @see java.sql.Connection#setReadOnly(boolean)
    */
   public void setReadOnly(boolean aReadOnly) throws SQLException
   {
      mDelegate.setReadOnly(aReadOnly);
   }

   /**
    * @see java.sql.Connection#getCatalog()
    */
   public String getCatalog() throws SQLException
   {
      return mDelegate.getCatalog();
   }

   /**
    * @see java.sql.Connection#setCatalog(java.lang.String)
    */
   public void setCatalog(String aCatalog) throws SQLException
   {
      mDelegate.setCatalog(aCatalog);

   }

   /**
    * @see java.sql.Connection#getMetaData()
    */
   public DatabaseMetaData getMetaData() throws SQLException
   {
      return mDelegate.getMetaData();
   }

   /**
    * @see java.sql.Connection#getWarnings()
    */
   public SQLWarning getWarnings() throws SQLException
   {
      return mDelegate.getWarnings();
   }

   /**
    * @see java.sql.Connection#setSavepoint()
    */
   public Savepoint setSavepoint() throws SQLException
   {
      return mDelegate.setSavepoint();
   }

   /**
    * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
    */
   public void releaseSavepoint(Savepoint aSavepoint) throws SQLException
   {
      mDelegate.releaseSavepoint(aSavepoint);
   }

   /**
    * @see java.sql.Connection#rollback(java.sql.Savepoint)
    */
   public void rollback(Savepoint aSavepoint) throws SQLException
   {
      mDelegate.rollback(aSavepoint);
   }

   /**
    * @see java.sql.Connection#createStatement()
    */
   public Statement createStatement() throws SQLException
   {
      return mDelegate.createStatement();
   }

   /**
    * @see java.sql.Connection#createStatement(int, int)
    */
   public Statement createStatement(int aResultSetType, int aResultSetConcurrency) throws SQLException
   {
      return mDelegate.createStatement(aResultSetType, aResultSetConcurrency);
   }

   /**
    * @see java.sql.Connection#createStatement(int, int, int)
    */
   public Statement createStatement(int aResultSetType, int aResultSetConcurrency, int aResultSetHoldability)
         throws SQLException
   {
      return mDelegate.createStatement(aResultSetType, aResultSetConcurrency, aResultSetHoldability);
   }

   /**
    * @see java.sql.Connection#getTypeMap()
    */
   public Map getTypeMap() throws SQLException
   {
      return mDelegate.getTypeMap();
   }

   /**
    * @see java.sql.Connection#nativeSQL(java.lang.String)
    */
   public String nativeSQL(String aSql) throws SQLException
   {
      return mDelegate.nativeSQL(aSql);
   }

   /**
    * @see java.sql.Connection#prepareCall(java.lang.String)
    */
   public CallableStatement prepareCall(String aSql) throws SQLException
   {
      return mDelegate.prepareCall(aSql);
   }

   /**
    * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
    */
   public CallableStatement prepareCall(String aSql, int aResultSetType, int aResultSetConcurrency)
         throws SQLException
   {
      return mDelegate.prepareCall(aSql, aResultSetType, aResultSetConcurrency);
   }
   
   /**  
    * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
    */
   public CallableStatement prepareCall(String aSql, int aResultSetType, int aResultSetConcurrency,
         int aResultSetHoldability) throws SQLException
   {
      return mDelegate.prepareCall(aSql, aResultSetType, aResultSetConcurrency, aResultSetHoldability);
   }

   /**
    * @see java.sql.Connection#prepareStatement(java.lang.String)
    */
   public PreparedStatement prepareStatement(String aSql) throws SQLException
   {
      return mDelegate.prepareStatement(aSql);
   }

   /**
    * @see java.sql.Connection#prepareStatement(java.lang.String, int)
    */
   public PreparedStatement prepareStatement(String aSql, int autoGeneratedKeys) throws SQLException
   {
      return mDelegate.prepareStatement(aSql, autoGeneratedKeys);
   }

   /**
    * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
    */
   public PreparedStatement prepareStatement(String aSql, int aResultSetType, int aResultSetConcurrency)
         throws SQLException
   {
      return mDelegate.prepareStatement(aSql, aResultSetType, aResultSetConcurrency);
   }

   /**
    * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
    */
   public PreparedStatement prepareStatement(String aSql, int aResultSetType, int aResultSetConcurrency,
         int aResultSetHoldability) throws SQLException
   {
      return mDelegate.prepareStatement(aSql, aResultSetType, aResultSetConcurrency, aResultSetHoldability);
   }

   /**
    * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
    */
   public PreparedStatement prepareStatement(String aSql, int[] aColumnIndexes) throws SQLException
   {
      return mDelegate.prepareStatement(aSql, aColumnIndexes);
   }

   /**
    * @see java.sql.Connection#setSavepoint(java.lang.String)
    */
   public Savepoint setSavepoint(String aName) throws SQLException
   {
      return mDelegate.setSavepoint(aName);
   }

   /**
    * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
    */
   public PreparedStatement prepareStatement(String aSql, String[] aColumnNames) throws SQLException
   {
      return mDelegate.prepareStatement(aSql, aColumnNames);
   }

	@Override
	public Array createArrayOf(String aArg0, Object[] aArg1) throws SQLException {
		return mDelegate.createArrayOf(aArg0, aArg1);
	}
	
	@Override
	public Blob createBlob() throws SQLException {
		return mDelegate.createBlob();
	}
	
	@Override
	public Clob createClob() throws SQLException {
		return mDelegate.createClob();
	}
	
	@Override
	public NClob createNClob() throws SQLException {
		return mDelegate.createNClob();
	}
	
	@Override
	public SQLXML createSQLXML() throws SQLException {
		return mDelegate.createSQLXML();
	}
	
	@Override
	public Struct createStruct(String aArg0, Object[] aArg1) throws SQLException {
		return mDelegate.createStruct(aArg0, aArg1);
	}
	
	@Override
	public Properties getClientInfo() throws SQLException {
		return mDelegate.getClientInfo();
	}
	
	@Override
	public String getClientInfo(String aArg0) throws SQLException {
		return mDelegate.getClientInfo(aArg0);
	}
	
	@Override
	public boolean isValid(int aArg0) throws SQLException {
		return mDelegate.isValid(aArg0);
	}
	
	@Override
	public void setClientInfo(Properties aArg0) throws SQLClientInfoException {
		mDelegate.setClientInfo(aArg0);
	}
	
	@Override
	public void setClientInfo(String aArg0, String aArg1)
			throws SQLClientInfoException {
		mDelegate.setClientInfo(aArg0, aArg1);
	}
	
	@Override
	public void setTypeMap(Map<String, Class<?>> aMap) throws SQLException {
		mDelegate.setTypeMap(aMap);
	}
	
	@Override
	public boolean isWrapperFor(Class<?> aArg0) throws SQLException {
		return mDelegate.isWrapperFor(aArg0);
	}
	
	@Override
	public <T> T unwrap(Class<T> aArg0) throws SQLException {
		return mDelegate.unwrap(aArg0);
	}
}
