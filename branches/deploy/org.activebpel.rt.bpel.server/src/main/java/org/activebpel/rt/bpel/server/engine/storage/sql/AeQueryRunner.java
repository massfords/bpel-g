// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeQueryRunner.java,v 1.12 2007/05/08 19:20:26 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.io.CharArrayReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import javax.sql.DataSource;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.storage.AeDocumentReader;
import org.activebpel.rt.util.AeBlobInputStream;
import org.apache.commons.dbutils.QueryRunner;
import org.w3c.dom.Document;

/**
 * Extends the Commons DbUtils <code>QueryRunner</code> to handle <code>Document</code> parameters as
 * character streams and to allow specifying the SQL type of null parameters.
 */
public class AeQueryRunner extends QueryRunner
{
   // Placeholders for typed null parameters in prepared statements. Specifying
   // one of these constants for a parameter instead of the generic Java null
   // allows AeQueryRunner to call PreparedStatement#setNull(int, int) with a
   // specific SQL type.
   public static final AeSQLNull NULL_BIGINT = new AeSQLNull(Types.BIGINT, "BIGINT"); //$NON-NLS-1$

   public static final AeSQLNull NULL_BINARY = new AeSQLNull(Types.BINARY, "BINARY"); //$NON-NLS-1$

   public static final AeSQLNull NULL_BIT = new AeSQLNull(Types.BIT, "BIT"); //$NON-NLS-1$

   public static final AeSQLNull NULL_BLOB = new AeSQLNull(Types.BLOB, "BLOB"); //$NON-NLS-1$

   public static final AeSQLNull NULL_CHAR = new AeSQLNull(Types.CHAR, "CHAR"); //$NON-NLS-1$

   public static final AeSQLNull NULL_CLOB = new AeSQLNull(Types.CLOB, "CLOB"); //$NON-NLS-1$

   public static final AeSQLNull NULL_DATE = new AeSQLNull(Types.DATE, "DATE"); //$NON-NLS-1$

   public static final AeSQLNull NULL_DECIMAL = new AeSQLNull(Types.DECIMAL, "DECIMAL"); //$NON-NLS-1$

   public static final AeSQLNull NULL_DOUBLE = new AeSQLNull(Types.DOUBLE, "DOUBLE"); //$NON-NLS-1$

   public static final AeSQLNull NULL_FLOAT = new AeSQLNull(Types.FLOAT, "FLOAT"); //$NON-NLS-1$

   public static final AeSQLNull NULL_INTEGER = new AeSQLNull(Types.INTEGER, "INTEGER"); //$NON-NLS-1$

   public static final AeSQLNull NULL_LONGVARBINARY = new AeSQLNull(Types.LONGVARBINARY, "LONGVARBINARY"); //$NON-NLS-1$

   public static final AeSQLNull NULL_LONGVARCHAR = new AeSQLNull(Types.LONGVARCHAR, "LONGVARCHAR"); //$NON-NLS-1$

   public static final AeSQLNull NULL_NUMERIC = new AeSQLNull(Types.NUMERIC, "NUMERIC"); //$NON-NLS-1$

   public static final AeSQLNull NULL_OTHER = new AeSQLNull(Types.OTHER, "OTHER"); //$NON-NLS-1$

   public static final AeSQLNull NULL_REAL = new AeSQLNull(Types.REAL, "REAL"); //$NON-NLS-1$

   public static final AeSQLNull NULL_SMALLINT = new AeSQLNull(Types.SMALLINT, "SMALLINT"); //$NON-NLS-1$

   public static final AeSQLNull NULL_TIME = new AeSQLNull(Types.TIME, "TIME"); //$NON-NLS-1$

   public static final AeSQLNull NULL_TIMESTAMP = new AeSQLNull(Types.TIMESTAMP, "TIMESTAMP"); //$NON-NLS-1$

   public static final AeSQLNull NULL_TINYINT = new AeSQLNull(Types.TINYINT, "TINYINT"); //$NON-NLS-1$

   public static final AeSQLNull NULL_VARBINARY = new AeSQLNull(Types.VARBINARY, "VARBINARY"); //$NON-NLS-1$

   public static final AeSQLNull NULL_VARCHAR = new AeSQLNull(Types.VARCHAR, "VARCHAR"); //$NON-NLS-1$

   /**
    * Default constructor.
    */
   public AeQueryRunner()
   {
   }

   /**
    * Constructor.
    * @param aDataSource
    */
   public AeQueryRunner(DataSource aDataSource)
   {
      super(aDataSource);
   }

   /**
    * @see org.apache.commons.dbutils.QueryRunner#fillStatement(java.sql.PreparedStatement,
    *      java.lang.Object[])
    */
   protected void fillStatement(PreparedStatement aStatement, Object[] aParams) throws SQLException
   {
      // Many databases or JDBC drivers do not support updateable blobs or
      // clobs. Therefore, write documents by passing a character stream to
      // PreparedStatement#setCharacterStream instead of writing to a character
      // stream retrieved from a Clob. The main disadvantage is the need to
      // "realize" the document's character stream completely in order to get
      // its length to pass to PreparedStatement#setCharacterStream.

      if ( aParams != null )
      {
         for (int i = 0; i < aParams.length; ++i)
         {
            Object param = aParams[i];

            // If the parameter is an AeFastDocument, handle it here by reading
            // it with an AeDocumentReader.
            if ( param instanceof AeFastDocument )
            {
               AeFastDocument document = (AeFastDocument)param;

               setDocumentReader(aStatement, i + 1, AeDocumentReader.getDocumentReader(document));
            }
            // If the parameter is an XML Document, handle it here by reading
            // it with an AeDocumentReader.
            else if ( param instanceof Document )
            {
               Document document = (Document)param;

               setDocumentReader(aStatement, i + 1, AeDocumentReader.getDocumentReader(document));
            }
            // If the parameter is a char[] then we'll set it as a character stream on the statement
            else if ( param instanceof char[] )
            {
               char[] c = (char[])param;
               if ( c.length == 0 )
               {
                  aStatement.setString(i + 1, ""); //$NON-NLS-1$
               }
               else
               {
                  aStatement.setCharacterStream(i + 1, new CharArrayReader(c), c.length);
               }
            }
            else if ( param == null )
            {
               // If this parameter is a generic null, then call setNull with
               // basically an unknown type (Types.OTHER).
               aStatement.setNull(i + 1, Types.OTHER);

               // Display warning stack trace on console.
               AeException.logError(new AeWarning(),
                     AeMessages.getString("AeQueryRunner.ERROR_0") + (i + 1) + "!"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else if ( param instanceof AeSQLNull )
            {
               // If this parameter is a typed null, then call setNull with a
               // specific SQL type.
               aStatement.setNull(i + 1, ((AeSQLNull)param).getSQLType());
            }
            else if ( param instanceof Date )
            {
               // Convert java.util.Date to java.sql.Timestamp.
               long millis = ((Date)param).getTime();
               aStatement.setTimestamp(i + 1, new Timestamp(millis));
            }
            else if ( param instanceof AeBlobInputStream )
            {
               AeBlobInputStream stream = (AeBlobInputStream) param;
               // This is binary blob data set the stream
               aStatement.setBinaryStream(i + 1, stream, (int) stream.length());
            }
            else
            {
               aStatement.setObject(i + 1, param);
            }
         }
      }
   }

   /**
    * Sets the specified parameter in the specified prepared statement from a document reader.
    * @param aStatement
    * @param aParameterIndex
    * @param aDocumentReader
    */
   protected void setDocumentReader(PreparedStatement aStatement, int aParameterIndex,
         AeDocumentReader aDocumentReader) throws SQLException
   {
      aStatement.setCharacterStream(aParameterIndex, aDocumentReader, aDocumentReader.getLength());
   }

   /**
    * Implements placeholders for typed null parameter values in prepared statements.
    */
   protected static class AeSQLNull
   {
      /**
       * The SQL type: one of the type constants defined in <code>java.sql.Types</code>.
       */
      private final int mSQLType;

      /** Result for {@link #toString()}. */
      private final String mToString;

      /**
       * Constructor.
       * @param aSQLType
       */
      public AeSQLNull(int aSQLType, String aName)
      {
         mSQLType = aSQLType;
         mToString = "AeSQLNull(" + aName + ")"; //$NON-NLS-1$ //$NON-NLS-2$
      }

      /**
       * Returns the SQL type.
       */
      public int getSQLType()
      {
         return mSQLType;
      }

      public String toString()
      {
         return mToString;
      }
   }

   /**
    * Convenience class to represent a warning stack trace.
    */
   protected static class AeWarning extends Exception
   {
   }
}
