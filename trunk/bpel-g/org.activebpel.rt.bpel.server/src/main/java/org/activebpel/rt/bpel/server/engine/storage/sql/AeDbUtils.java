// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeDbUtils.java,v 1.7 2005/06/14 17:11:11 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUnsynchronizedCharArrayWriter;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Document;

/**
 *  Common db utility methods.
 */
public class AeDbUtils
{
   /**
    * Constant for true int (1).
    */
   public static final int TRUE = 1;

   /**
    * Constant for true int (0).
    */
   public static final int FALSE = 0;
   
   /**
    * Conevert int to boolean (1 == true, everything else is false). 
    * @param aValue
    */
   public static boolean convertIntToBoolean( int aValue )
   {
      return aValue == TRUE;
   }
   
   /**
    * Convert boolean to TRUE constant.
    * @param aValue
    */
   public static int convertBooleanToInt( boolean aValue )
   {
      return aValue ? TRUE : FALSE;
   }
   
   /**
    * Returns a <code>Document</code> loaded from the specified <code>Clob</code>.
    *
    * @param aClob
    * @return Document
    * @throws SQLException
    */
   public static Document getDocument( Clob aClob ) throws SQLException
   {
      Reader in = aClob.getCharacterStream();

      try
      {
         return getXMLParser().loadDocument(in, null);
      }
      catch (AeException e)
      {
         throw new SQLException(AeMessages.getString("AeDbUtils.ERROR_0") + e.getLocalizedMessage()); //$NON-NLS-1$
      }
      finally
      {
         AeCloser.close(in);
      }
   }
   
   /**
    * Returns parser to use to load document.
    */
   protected static AeXMLParserBase getXMLParser()
   {
      AeXMLParserBase parser = new AeXMLParserBase();
      parser.setValidating(false);
      parser.setNamespaceAware(true);

      return parser;
   }
   
   /**
    * Extract the string data from the given clob. 
    * @param aClob
    * @throws SQLException
    */
   public static String getString( Clob aClob ) throws SQLException
   {
      if (aClob == null)
         return null;

      Reader reader = aClob.getCharacterStream();
      if (reader == null)
         return null;

      AeUnsynchronizedCharArrayWriter writer = new AeUnsynchronizedCharArrayWriter();

      try
      {
         char[] buff = new char[1024*128];
         int read;
         while ((read = reader.read(buff)) != -1)
         {
            writer.write(buff, 0, read);
         }

         return writer.toString();
      }
      catch( IOException io )
      {
         AeException.logError( io, AeMessages.getString("AeDbUtils.ERROR_1") ); //$NON-NLS-1$
         throw new SQLException( AeMessages.getString("AeDbUtils.ERROR_1") + ":" + io.getLocalizedMessage() ); //$NON-NLS-1$ //$NON-NLS-2$
      }
      finally
      {
         AeCloser.close(reader);
      }
   }
   
   /**
    * Returns the specified column of the result set as a <code>Date</code>.
    *
    * @param aResultSet
    * @param aColumnName
    * @throws SQLException
    */
   public static Date getDate(ResultSet aResultSet, String aColumnName) throws SQLException
   {
      Timestamp timestamp = aResultSet.getTimestamp(aColumnName);
      return ((timestamp == null) || aResultSet.wasNull()) ? null : new Date(timestamp.getTime());
   }

   /**
    * Returns the specified column of the result set as a <code>Date</code>.  The column is of
    * type BIGINT, representing the # of millis since the epoch.
    * 
    * @param aResultSet
    * @param aColumnName
    * @throws SQLException
    */
   public static Date getDateFromMillis(ResultSet aResultSet, String aColumnName) throws SQLException
   {
      long millis = aResultSet.getLong(aColumnName);
      if (aResultSet.wasNull() || millis < 0)
      {
         return null;
      }
      return new Date(millis);
   }
}
