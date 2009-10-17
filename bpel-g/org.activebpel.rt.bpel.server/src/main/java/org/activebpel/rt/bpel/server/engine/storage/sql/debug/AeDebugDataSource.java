//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/debug/AeDebugDataSource.java,v 1.3 2008/02/17 21:38:50 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql.debug;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeDataSource;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLConfig;
import org.activebpel.rt.util.AeUtil;

public class AeDebugDataSource extends AeDataSource implements DataSource, Runnable
{
   /**
    * Reference to the delegate data source.
    */
   private DataSource mDelegateDataSource = null;
   
   /**
    * Sleep, time in milisecs.
    */
   private long mSleep = 60000L;
   
   /**
    * Slow query threadshold (in ms). Default is 1000ms.
    */
   private long mSlowQueryThreshold = 1000L;
   
   /**
    * List on information connections that were not closed.
    */
   private List mOpenConnectionList;
   
   /**
    * List on information connections that were slow.
    */
   private List mSlowQueryConnectionList;
   
   
   /**
    * number of active connections.
    */
   private int mActiveConnectionCount = 0;
   
   /**
    * Print stack trace details.
    */
   private boolean mShowStack = false;
   
   /**
    * Default ctor.
    * @param aConfig
    * @param aSQLConfig
    * @throws AeException
    */
   public AeDebugDataSource(Map aConfig, AeSQLConfig aSQLConfig) throws AeException
   {
      super("AeDebugDataSource", aSQLConfig); //$NON-NLS-1$
      mOpenConnectionList = new ArrayList();
      mSlowQueryConnectionList = new ArrayList();
      setDelegateDataSource(createDelegateDataSource(aConfig, aSQLConfig) );
      // get the thread sleep interval
      String sleep = (String) aConfig.get("sleepsec");  //$NON-NLS-1$
      if (AeUtil.notNullOrEmpty(sleep))
      {
         try
         {
            long ms = Long.parseLong(sleep) * 1000L;
            if (ms < 10000)
            {
               ms = 10000;
            }
            setSleep(ms);
         }
         catch(Exception e)
         {            
         }
      }
      // get slow query
      String queryms = (String) aConfig.get("slowqueryms");  //$NON-NLS-1$
      if (AeUtil.notNullOrEmpty(queryms))
      {
         try
         {
            long ms = Long.parseLong(queryms);
            if (ms < 50)
            {
               ms = 50;
            }
            setSlowQueryThreshold(ms);
         }
         catch(Exception e)
         {            
         }
      }
      
      // stack trace details
      String show = (String) aConfig.get("showstack");  //$NON-NLS-1$
      mShowStack = show != null && "true".equalsIgnoreCase(show); //$NON-NLS-1$
      
      // start thread
      Thread thread = new Thread(this, "AeDebugDataSource"); //$NON-NLS-1$
      thread.setDaemon(true);
      thread.start();
   }
   
   /**
    * Creates the underlying datasource.
    * @param aConfig
    * @param aSQLConfig
    *
    * @throws AeException
    */
   protected DataSource createDelegateDataSource(Map aConfig, AeSQLConfig aSQLConfig) throws AeException
   {
      String className = (String) aConfig.get("delegateClass");  //$NON-NLS-1$
      if (AeUtil.isNullOrEmpty(className))
      {
         throw new AeException("Delegate Classname is missing"); //$NON-NLS-1$
      }
      try
      {
         Class clazz = Class.forName(className);
         Constructor constructor = clazz.getConstructor( new Class [] { Map.class, AeSQLConfig.class } );
         return (AeDataSource) constructor.newInstance(new Object[] { aConfig, aSQLConfig });
      }
      catch (Exception e)
      {
         if (e instanceof AeException)
         {
            throw (AeException) e;
         }
         else
         {
            throw new AeException("Error creating delegate data source " + className, e); //$NON-NLS-1$
         }
      }      
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeDataSource#createDelegate()
    */
   public DataSource createDelegate() throws AeStorageException
   {
      return this;
   }
   
   /**
    * @return Returns the delegate.
    */
   private DataSource getDelegateDataSource()
   {
      return mDelegateDataSource;
   }

   /**
    * @param aDelegate The delegate to set.
    */
   private void setDelegateDataSource(DataSource aDelegate)
   {
      mDelegateDataSource = aDelegate;
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
    * @return Returns the sleep.
    */
   private long getSleep()
   {
      return mSleep;
   }

   /**
    * @param aSleep The sleep to set.
    */
   private void setSleep(long aSleep)
   {
      mSleep = aSleep;
   }   
   
   /**
    * @return Returns the openConnectionList.
    */
   public List getOpenConnectionList()
   {
      return mOpenConnectionList;
   }
   
   /**
    * @return Returns the slowQueryConnectionList.
    */
   public List getSlowQueryConnectionList()
   {
      return mSlowQueryConnectionList;
   }
      
   
   /**
    * @return Returns the activeConnectionCount.
    */
   public int getActiveConnectionCount()
   {
      return mActiveConnectionCount;
   }

   /**
    * Increases the connection count by 1.
    */
   public synchronized void incConnectionRefCount()
   {
      mActiveConnectionCount++;
   }

   /**
    * Decreases the connection count by 1.
    */
   public synchronized void decConnectionRefCount()
   {
      mActiveConnectionCount--;
   }
   
   /**
    * @see java.lang.Runnable#run()
    */
   public void run()
   {
      while (true)
      {
         try
         {
            Thread.sleep( getSleep() );
         }
         catch(Exception e)
         {            
         }
         printDetails();
      }
   }
   
   /**
    * Prints the current details.
    */
   public void printDetails()
   {
      List openList = new ArrayList( getOpenConnectionList() );
      List slowList = new ArrayList( getSlowQueryConnectionList() );
      StringBuffer sb = new StringBuffer();
      sb.append("\n");  //$NON-NLS-1$
      sb.append("==============================================\n");  //$NON-NLS-1$
      sb.append("BEGIN AeDebugDataSource Details  \n");  //$NON-NLS-1$
      sb.append(" " + (new Date()).toString() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
      sb.append(" Sleep ms: " + getSleep() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
      sb.append(" SlowQuery Threshold ms: " + getSlowQueryThreshold() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
      sb.append(" Active connections in use: " + getActiveConnectionCount() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
      sb.append(" Unclosed connections : " + openList.size() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
      sb.append(" Slow query connections : " + slowList.size() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
      if (mShowStack)
      {
         if (openList.size() > 0)
         {
            printDetails("OpenConnection", openList, sb); //$NON-NLS-1$
         }
         
         if (slowList.size() > 0)
         {
            printDetails("Slow Query", slowList, sb); //$NON-NLS-1$
         }
         sb.append("==============================================\n");  //$NON-NLS-1$
      }      
      sb.append("END AeDebugDataSource Details  \n");  //$NON-NLS-1$
      sb.append("==============================================\n");  //$NON-NLS-1$
      System.out.println(sb.toString());      
   }
   
   /**
    * Prints the details for the given list.
    * @param aTitle
    * @param aList
    */
   protected void printDetails(String aTitle, List aList, StringBuffer aSb)
   {
      aSb.append("==============================================\n");  //$NON-NLS-1$
      aSb.append(" Number of " + aTitle +  "(s): " + aList.size() + "\n\n"); //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$
      for (int i = 0; i < aList.size(); i++ )
      {
         aSb.append("----------------------------------------------\n");  //$NON-NLS-1$
         aSb.append(" " + aTitle + " # : " + (i+1) + "\n"); //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$
         RuntimeException re = (RuntimeException) aList.get(i);
         StringWriter sw = new StringWriter();
         re.printStackTrace( new PrintWriter( sw ));            
         aSb.append(sw.toString());  
      }
   }
   
   /*======================================================================
    * javax.sql.DataSource methods
    *======================================================================
    */

   /**
    * @see javax.sql.DataSource#getConnection()
    */
   public Connection getConnection() throws SQLException
   {
      RuntimeException re = new RuntimeException("getConnection on Thread: " + Thread.currentThread().getName());  //$NON-NLS-1$
      Connection delegate = getDelegateDataSource().getConnection();
      AeDebugConnection dc =  new AeDebugConnection(this, re, delegate);
      dc.setSlowQueryThreshold( getSlowQueryThreshold() );
      return dc;
   }

   /**
    * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
    */
   public Connection getConnection(String aUsername, String aPassword) throws SQLException
   {
      RuntimeException re = new RuntimeException("getConnection on Thread: " + Thread.currentThread().getName());  //$NON-NLS-1$
      Connection delegate =  getDelegateDataSource().getConnection(aUsername, aPassword);
      AeDebugConnection dc =  new AeDebugConnection(this, re, delegate);
      dc.setSlowQueryThreshold( getSlowQueryThreshold() );
      return dc;
   }

   /**
    * @see javax.sql.DataSource#getLoginTimeout()
    */
   public int getLoginTimeout() throws SQLException
   {
      return getDelegateDataSource().getLoginTimeout();
   }

   /**
    * @see javax.sql.DataSource#getLogWriter()
    */
   public PrintWriter getLogWriter() throws SQLException
   {
      return getDelegateDataSource().getLogWriter();
   }

   /**
    * @see javax.sql.DataSource#setLoginTimeout(int)
    */
   public void setLoginTimeout(int aSeconds) throws SQLException
   {
      getDelegateDataSource().setLoginTimeout(aSeconds);
   }

   /**
    * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
    */
   public void setLogWriter(PrintWriter aWriter) throws SQLException
   {
      getDelegateDataSource().setLogWriter(aWriter);
   }

	@Override
	public boolean isWrapperFor(Class<?> aIface) throws SQLException {
		return getDelegateDataSource().isWrapperFor(aIface);
	}
	
	@Override
	public <T> T unwrap(Class<T> aIface) throws SQLException {
		return getDelegateDataSource().unwrap(aIface);
	}   

}
