//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeDebugConnection.java,v 1.1 2006/06/15 18:45:18 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

public class AeDebugConnection implements Connection
{
   private final Connection mDelegate;
   private int mCloseCount;
   private static final Hashtable<AeDebugConnection, RuntimeException> sOpenConnections = new Hashtable<AeDebugConnection, RuntimeException>();
   
   public AeDebugConnection(Connection aDelegate)
   {
      mDelegate = aDelegate;
      mCloseCount = 0;
      sOpenConnections.put(this, new RuntimeException("Opened Connection on thread: " + Thread.currentThread().getName())); //$NON-NLS-1$
   }

   public int getHoldability() throws SQLException
   {
      return mDelegate.getHoldability();
   }

   public int getTransactionIsolation() throws SQLException
   {
      return mDelegate.getTransactionIsolation();
   }

   public void clearWarnings() throws SQLException
   {
      mDelegate.clearWarnings();
   }

   public void close() throws SQLException
   {
      mCloseCount++;
      mDelegate.close();
      sOpenConnections.remove(this);
      mCloseCount++;
   }
   
   protected void finalize() throws Throwable
   {
      if (mCloseCount != 2)
      {
         throw new RuntimeException("** Unexpected close count: " + mCloseCount); //$NON-NLS-1$
      }
      super.finalize();
   }

   public void commit() throws SQLException
   {
      mDelegate.commit();
   }

   public void rollback() throws SQLException
   {
      mDelegate.rollback();
   }

   public boolean getAutoCommit() throws SQLException
   {
      return mDelegate.getAutoCommit();
   }

   public boolean isClosed() throws SQLException
   {
      return mDelegate.isClosed();
   }

   public boolean isReadOnly() throws SQLException
   {
      return mDelegate.isReadOnly();
   }

   public void setHoldability(int aHoldability) throws SQLException
   {
      mDelegate.setHoldability(aHoldability);
   }

   public void setTransactionIsolation(int aLevel) throws SQLException
   {
      mDelegate.setTransactionIsolation(aLevel);
   }

   public void setAutoCommit(boolean autoCommit) throws SQLException
   {
      mDelegate.setAutoCommit(autoCommit);
   }

   public void setReadOnly(boolean aReadOnly) throws SQLException
   {
      mDelegate.setReadOnly(aReadOnly);
   }

   public String getCatalog() throws SQLException
   {
      return mDelegate.getCatalog();
   }

   public void setCatalog(String aCatalog) throws SQLException
   {
      mDelegate.setCatalog(aCatalog);

   }

   public DatabaseMetaData getMetaData() throws SQLException
   {
      return mDelegate.getMetaData();
   }

   public SQLWarning getWarnings() throws SQLException
   {
      return mDelegate.getWarnings();
   }

   public Savepoint setSavepoint() throws SQLException
   {
      return mDelegate.setSavepoint();
   }

   public void releaseSavepoint(Savepoint aSavepoint) throws SQLException
   {
      mDelegate.releaseSavepoint(aSavepoint);
   }

   public void rollback(Savepoint aSavepoint) throws SQLException
   {
      mDelegate.rollback(aSavepoint);
   }

   public Statement createStatement() throws SQLException
   {
      return mDelegate.createStatement();
   }

   public Statement createStatement(int aResultSetType, int aResultSetConcurrency) throws SQLException
   {
      return mDelegate.createStatement(aResultSetType, aResultSetConcurrency);
   }

   public Statement createStatement(int aResultSetType, int aResultSetConcurrency, int aResultSetHoldability)
         throws SQLException
   {
      return mDelegate.createStatement(aResultSetType, aResultSetConcurrency, aResultSetHoldability);
   }

   public Map<String,Class<?>> getTypeMap() throws SQLException
   {
      return mDelegate.getTypeMap();
   }

   public String nativeSQL(String aSql) throws SQLException
   {
      return mDelegate.nativeSQL(aSql);
   }

   public CallableStatement prepareCall(String aSql) throws SQLException
   {
      return mDelegate.prepareCall(aSql);
   }

   public CallableStatement prepareCall(String aSql, int aResultSetType, int aResultSetConcurrency)
         throws SQLException
   {
      return mDelegate.prepareCall(aSql, aResultSetType, aResultSetConcurrency);
   }

   public CallableStatement prepareCall(String aSql, int aResultSetType, int aResultSetConcurrency,
         int aResultSetHoldability) throws SQLException
   {
      return mDelegate.prepareCall(aSql, aResultSetType, aResultSetConcurrency, aResultSetHoldability);
   }

   public PreparedStatement prepareStatement(String aSql) throws SQLException
   {
      return mDelegate.prepareStatement(aSql);
   }

   public PreparedStatement prepareStatement(String aSql, int autoGeneratedKeys) throws SQLException
   {
      return mDelegate.prepareStatement(aSql, autoGeneratedKeys);
   }

   public PreparedStatement prepareStatement(String aSql, int aResultSetType, int aResultSetConcurrency)
         throws SQLException
   {
      return mDelegate.prepareStatement(aSql, aResultSetType, aResultSetConcurrency);
   }

   public PreparedStatement prepareStatement(String aSql, int aResultSetType, int aResultSetConcurrency,
         int aResultSetHoldability) throws SQLException
   {
      return mDelegate.prepareStatement(aSql, aResultSetType, aResultSetConcurrency, aResultSetHoldability);
   }

   public PreparedStatement prepareStatement(String aSql, int[] aColumnIndexes) throws SQLException
   {
      return mDelegate.prepareStatement(aSql, aColumnIndexes);
   }

   public Savepoint setSavepoint(String aName) throws SQLException
   {
      return mDelegate.setSavepoint(aName);
   }

   public PreparedStatement prepareStatement(String aSql, String[] aColumnNames) throws SQLException
   {
      return mDelegate.prepareStatement(aSql, aColumnNames);
   }
   
   public static void printStackTraces()
   {
      for(Enumeration en = sOpenConnections.elements(); en.hasMoreElements(); )
      {
         RuntimeException ex = (RuntimeException)en.nextElement();
         ex.printStackTrace();
      }
   }

	public Array createArrayOf(String aArg0, Object[] aArg1) throws SQLException {
		return mDelegate.createArrayOf(aArg0, aArg1);
	}
	
	public Blob createBlob() throws SQLException {
		return mDelegate.createBlob();
	}
	
	public Clob createClob() throws SQLException {
		return mDelegate.createClob();
	}
	
	public NClob createNClob() throws SQLException {
		return mDelegate.createNClob();
	}
	
	public SQLXML createSQLXML() throws SQLException {
		return mDelegate.createSQLXML();
	}
	
	public Struct createStruct(String aArg0, Object[] aArg1) throws SQLException {
		return mDelegate.createStruct(aArg0, aArg1);
	}
	
	public Properties getClientInfo() throws SQLException {
		return mDelegate.getClientInfo();
	}
	
	public String getClientInfo(String aArg0) throws SQLException {
		return mDelegate.getClientInfo(aArg0);
	}
	
	public boolean isValid(int aArg0) throws SQLException {
		return mDelegate.isValid(aArg0);
	}
	
	public void setClientInfo(Properties aArg0) throws SQLClientInfoException {
		mDelegate.setClientInfo(aArg0);
	}
	
	public void setClientInfo(String aArg0, String aArg1)
			throws SQLClientInfoException {
		mDelegate.setClientInfo(aArg0, aArg1);
	}
	
	public void setTypeMap(Map<String, Class<?>> aMap) throws SQLException {
		mDelegate.setTypeMap(aMap);
	}
	
	public boolean isWrapperFor(Class<?> aArg0) throws SQLException {
		return mDelegate.isWrapperFor(aArg0);
	}
	
	public <T> T unwrap(Class<T> aArg0) throws SQLException {
		return mDelegate.unwrap(aArg0);
	}

}
