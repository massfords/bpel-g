// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/logging/AeSequentialClobStream.java,v 1.5 2005/02/10 16:42:03 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.logging;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLConfig;
import org.activebpel.rt.util.AeCloser;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 * Provides a single InputStream interface to a sequence of Clobs. 
 */
public class AeSequentialClobStream extends Reader implements ResultSetHandler
{
   private static final String SQL_GET_PROCESS_LOG_STREAM = "SequentialClobStream.GetProcessLogStream"; //$NON-NLS-1$

   /** query runner used to execute the queries */
   private QueryRunner mQueryRunner;
   
   /** process id */
   private Long mProcessId;
   
   /** value of the counter column for the last clob we read */
   private int mCounter;
   
   /** current reader that we're delegating to */
   private Reader mCurrentStream;
   
   /** sql statement to execute to get our data */
   private String mSqlStatement;
    
   /**
    * Creates the sequential clob stream with all of its required params
    * @param aQueryRunner The query runner to execute the stmt
    * @param aProcessId The process id to use in the stmt
    */
   public AeSequentialClobStream(AeSQLConfig aSQLConfig, QueryRunner aQueryRunner, long aProcessId)
   {
      mSqlStatement = aSQLConfig.getSQLStatement(SQL_GET_PROCESS_LOG_STREAM) + aSQLConfig.getLimitStatement(1);
      mQueryRunner = aQueryRunner;
      mProcessId = new Long(aProcessId);
   }
   
   /**
    * @see java.io.Reader#read(char[], int, int)
    */
   public int read(char[] aCbuf, int aOff, int aLen) throws IOException
   {
      makeStreamReady();
      
      int result = -1;
      
      try
      {
         while (mCurrentStream != null && (result = mCurrentStream.read(aCbuf, aOff, aLen)) == -1)
            prepNextStream();
      }
      catch (IOException e)
      {
         close();
         throw e;
      }
      
      return result;
   }

   /**
    * Closes the current stream and makes the next stream ready. If there are no
    * additional streams, then the current stream field will be set to null indicating
    * that there's nothing left to read.
    */
   private void prepNextStream() throws IOException
   {
      closeCurrentStream();
      makeStreamReady();
   }

   /**
    * Closes the current stream and nulls out the reference.
    */
   private void closeCurrentStream()
   {
      AeCloser.close(mCurrentStream);
      mCurrentStream = null;
   }

   /**
    * Attempts to get the next stream ready if there is one available from the clob.
    * @throws IOException
    */
   private void makeStreamReady() throws IOException
   {
      if (mCurrentStream == null)
      {
         Object[] args = {mProcessId, new Integer(mCounter)};
         try
         {
            mCurrentStream = (Reader) mQueryRunner.query(
                     mSqlStatement, 
                     args, 
                     this);
         }
         catch (SQLException e)
         {
            throw new IOException(e.getMessage());
         }
      }
   }
   
   /**
    * @see java.io.InputStream#close()
    */
   public void close() throws IOException
   {
      closeCurrentStream();
   }
   
   /**
    * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
    */
   public Object handle(ResultSet rs) throws SQLException
   {
      Reader input = null;
      
      if (rs.next())
      {
         mCounter = rs.getInt(2);

         Reader inFromClob = rs.getClob(1).getCharacterStream();
         char[] buffer = new char[1024*4];
         int read;
         StringWriter writer = new StringWriter();

         try
         {
            while( (read = inFromClob.read(buffer)) != -1)
            {
               writer.write(buffer, 0, read);                                 
            }

            input = new StringReader(writer.toString());
         }
         catch (IOException e)
         {
            throw new SQLException(e.getMessage());
         }
         finally
         {
            AeCloser.close(inFromClob);
         }
      }
      
      return input;
   }
}
