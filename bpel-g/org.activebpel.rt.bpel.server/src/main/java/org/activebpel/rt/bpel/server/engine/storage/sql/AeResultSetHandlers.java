// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeResultSetHandlers.java,v 1.18 2008/03/28 17:08:04 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.io.InputStream;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.util.AeBlobInputStream;
import org.activebpel.rt.util.AeUtil;
import org.apache.commons.dbutils.ResultSetHandler;
import org.w3c.dom.Document;

/**
 * This class consists exclusively of static methods that return instances of
 * <code>ResultSetHandler</code>.
 */
public class AeResultSetHandlers
{
   /**
    * Implements a <code>ResultSetHandler</code> that returns the first column
    * of the first row of a <code>ResultSet</code> as a <code>Document</code>.
    */
   private static final ResultSetHandler<Document> sDocumentHandler = new AeDocumentResultSetHandler();

   /**
    * Implements a <code>ResultSetHandler</code> that returns the first column
    * of the first row of a <code>ResultSet</code> as an <code>String</code>.
    */
   private static final ResultSetHandler<String> sStringHandler = new ResultSetHandler<String>()
   {
      public String handle(ResultSet rs) throws SQLException
      {
         // ResultSet#getString will return null if the column contains null.
         return rs.next() ? rs.getString(1) : null;
      }
   };
   
   /** Same as sStringHandler but reads the string from a CLOB */
   private static final ResultSetHandler<String> sClobStringHandler = new ResultSetHandler<String>()
   {
      public String handle(ResultSet rs) throws SQLException
      {
         String s = null;
         if (rs.next())
         {
            // CLOB data needs to be trimmed due to a problem in Oracle 10.1 where additional 
            // non-printable characters are added at end of CLOB that are not part of original insert
            s = AeDbUtils.getString(rs.getClob(1));
            if (s != null)
               s = s.trim();
         }
         
         return s;
      }
   };
   
   /** Same as sStringHandler but reads the string from a BLOB */
   private static final ResultSetHandler<InputStream> sBlobStreamHandler = new ResultSetHandler<InputStream>()
   {
      public InputStream handle(ResultSet rs) throws SQLException
      {
         InputStream is = null;
         if (rs.next())
         {
            is = rs.getBinaryStream(1);
            
            try
            {
               // We have to read the stream completely now, because there is
               // no guarantee that the contents will be available once the
               // result set, statement, or database connection is closed.
               is = new AeBlobInputStream(is);
            }
            catch (Exception e)
            {
               throw (SQLException) new SQLException(AeMessages.getString("AeResultSetHandlers.ERROR_SavingBlobToDisk")).initCause(e); //$NON-NLS-1$
            }
         }

         return is;
      }
   };

   /**
    * Implements a <code>ResultSetHandler</code> that returns a string array
    * of values from the the first column.  Null values are not added to the array.
    * If the result set is empty, an empty array is returned.
    */
   private static final ResultSetHandler<String[]> sStringArrayHandler = new ResultSetHandler<String[]>()
   {
      public String[] handle(ResultSet rs) throws SQLException
      {
         List<String> results = new ArrayList<>();

         while( rs.next() )
         {
            String data = rs.getString(1);
            if( !rs.wasNull() )
            {
               results.add( data );
            }
         }

         return results.toArray( new String[results.size()] );
      }
   };

   /**
    * Implements a <code>ResultSetHandler</code> that returns a map of values 
    * from the the first and second column.  Null values are not added to the array.
    * If the result set is empty, an empty array is returned.
    */
   private static final ResultSetHandler<Map<Integer,Integer>> sIntegerMapHandler = new ResultSetHandler<Map<Integer,Integer>>()
   {
      public Map<Integer,Integer> handle(ResultSet rs) throws SQLException
      {
         Map<Integer,Integer> results = new HashMap<>();

         while ( rs.next() )
         {
            int n1 = rs.getInt(1);

            if (!rs.wasNull())
            {
               int n2 = rs.getInt(2);

               if (!rs.wasNull())
               {
                  results.put(n1, n2);
               }
            }
         }
         return results;
      }
   };

   /**
    * Implements a <code>ResultSetHandler</code> that returns the first column
    * of the first row of a <code>ResultSet</code> as an <code>Integer</code>.
    */
   private static final ResultSetHandler<Integer> sIntegerHandler = new ResultSetHandler<Integer>()
   {
      public Integer handle(ResultSet rs) throws SQLException
      {
         Integer result = null;

         if (rs.next())
         {
            int n = rs.getInt(1);

            if (!rs.wasNull())
            {
               result = n;
            }
         }

         return result;
      }
   };

   /**
    * Implements a <code>ResultSetHandler</code> that returns the first column
    * of the first row of a <code>ResultSet</code> as an <code>Float</code>.
    */
   private static final ResultSetHandler<Float> sFloatHandler = new ResultSetHandler<Float>()
   {
      public Float handle(ResultSet rs) throws SQLException
      {
         Float result = null;

         if (rs.next())
         {
            float f = rs.getFloat(1);

            if (!rs.wasNull())
            {
               result = f;
            }
         }

         return result;
      }
   };

   /**
    * Implements a <code>ResultSetHandler</code> that returns the first column
    * of the first row of a <code>ResultSet</code> as a <code>Long</code>.
    */
   private static final ResultSetHandler<Long> sLongHandler = new ResultSetHandler<Long>()
   {
      public Long handle(ResultSet rs) throws SQLException
      {
         Long result = null;

         if (rs.next())
         {
            long n = rs.getLong(1);

            if (!rs.wasNull())
            {
               result = n;
            }
         }

         return result;
      }
   };

   /**
    * Implements a <code>ResultSetHandler</code> that returns the first column
    * of the first row of a <code>ResultSet</code> as a <code>Date</code>.
    */
   private static final ResultSetHandler<Date> sDateHandler = new ResultSetHandler<Date>()
   {
      public Date handle(ResultSet rs) throws SQLException
      {
         // ResultSet#getDate returns null if the column contains null.
         return rs.next() ? rs.getDate(1) : null;
      }
   };

   /**
    * Implements a <code>ResultSetHandler</code> that returns the first two
    * columns of the first row of a <code>ResultSet</code> as a
    * <code>QName</code>.
    */
   private static final ResultSetHandler<QName> sQNameHandler = new ResultSetHandler<QName>()
   {
      public QName handle(ResultSet rs) throws SQLException
      {
         if (rs.next())
         {
            String namespaceURI = rs.getString(1);
            String localPart = rs.getString(2);

            if (!AeUtil.isNullOrEmpty(namespaceURI) && !AeUtil.isNullOrEmpty(localPart))
            {
               return new QName(namespaceURI, localPart);
            }
         }

         return null;
      }
   };

   /**
    * Implements a <code>ResultSetHandler</code> that converts the first column
    * of the <code>ResultSet</code> to an <code>AeLongSet</code>.
    */
   private static final ResultSetHandler<Set<Long>> sLongSetHandler = new ResultSetHandler<Set<Long>>()
   {
      public Set<Long> handle(ResultSet rs) throws SQLException
      {
         Set<Long> set = new HashSet<>();

         while (rs.next())
         {
            long n = rs.getLong(1);

            if (!rs.wasNull())
            {
               set.add(n);
            }
         }

         return set;
      }
   };

   /**
    * Implements a <code>ResultSetHandler</code> that converts the first column
    * of the <code>ResultSet</code> to an <code>AeIntSet</code>.
    */
   private static final ResultSetHandler<Set<Integer>> sIntSetHandler = new ResultSetHandler<Set<Integer>>()
   {
      public Set<Integer> handle(ResultSet rs) throws SQLException
      {
         Set<Integer> set = new HashSet<>();

         while (rs.next())
         {
            int n = rs.getInt(1);

            if (!rs.wasNull())
            {
               set.add(n);
            }
         }

         return set;
      }
   };

   /**
    * Returns a <code>ResultSetHandler</code> that returns the first column
    * of the first row of a <code>ResultSet</code> as a <code>Document</code>.
    */
   public static ResultSetHandler<Document> getDocumentHandler()
   {
      return sDocumentHandler;
   }

   /**
    * Returns a <code>ResultSetHandler</code> that returns the first column of
    * the first row of a <code>ResultSet</code> as an <code>String</code>.
    */
   public static ResultSetHandler<String> getStringHandler()
   {
      return sStringHandler;
   }
   
   /**
    * Similar to the String handler but expects the column we're reading from to
    * be a CLOB. The entire contents of the CLOB will be returned as a single
    * String.
    */
   public static ResultSetHandler<String> getClobStringHandler()
   {
      return sClobStringHandler;
   }

   /**
    * Returns a<code>ResultSetHandler</code> that returns the first column of
    * the first row of a <code>ResultSet</code> as a <code>BLOB</code>. The contents of the BLOB will be returned as an InputStream.
    */
   public static ResultSetHandler<InputStream> getBlobStreamHandler()
   {
      return sBlobStreamHandler;
   }
   /**
    * Implements a <code>ResultSetHandler</code> that returns a string array
    * of values from the the first column.  Null values are not added to the array.
    * If the result set is empty, an empty array is returned.
    */
   public static ResultSetHandler<String[]> getStringArrayHandler()
   {
      return sStringArrayHandler;
   }

   /**
    * Implements a <code>ResultSetHandler</code> that returns a map of values 
    * from the the first and second columns.  Null values are not added to the array.
    * If the result set is empty, an empty array is returned.
    */
   public static ResultSetHandler<Map<Integer,Integer>> getIntegerMapHandler()
   {
      return sIntegerMapHandler;
   }

   /**
    * Returns a <code>ResultSetHandler</code> that returns the first column of
    * the first row of a <code>ResultSet</code> as an <code>Integer</code>.
    */
   public static ResultSetHandler<Integer> getIntegerHandler()
   {
      return sIntegerHandler;
   }

   /**
    * Returns a <code>ResultSetHandler</code> that returns the first column of
    * the first row of a <code>ResultSet</code> as a <code>Long</code>.
    */
   public static ResultSetHandler<Long> getLongHandler()
   {
      return sLongHandler;
   }

   /**
    * Returns a <code>ResultSetHandler</code> that returns the first column of
    * the first row of a <code>ResultSet</code> as a <code>Date</code>.
    */
   public static ResultSetHandler<Date> getDateHandler()
   {
      return sDateHandler;
   }

   /**
    * Returns a <code>ResultSetHandler</code> that returns the first two
    * columns of the first row of a <code>ResultSet</code> as a
    * <code>QName</code>.
    */
   public static ResultSetHandler<QName> getQNameHandler()
   {
      return sQNameHandler;
   }

   /**
    * Returns a <code>ResultSetHandler</code> that converts the first column
    * of the <code>ResultSet</code> to an <code>AeLongSet</code>.
    */
   public static ResultSetHandler<Set<Long>> getLongSetHandler()
   {
      return sLongSetHandler;
   }

   /**
    * Returns a <code>ResultSetHandler</code> that converts the first column
    * of the <code>ResultSet</code> to an <code>AeIntSet</code>.
    */
   public static ResultSetHandler<Set<Integer>> getIntSetHandler()
   {
      return sIntSetHandler;
   }

   /**
    * Returns a <code>ResultSetHandler</code> that converts the first column
    * of the <code>ResultSet</code> to an <code>Float</code>.
    */
   public static ResultSetHandler<Float> getFloatHandler()
   {
      return sFloatHandler;
   }

   /**
    * Implements a <code>ResultSetHandler</code> that returns the first column
    * of the first row of a <code>ResultSet</code> as a <code>Document</code>.
    */
   public static class AeDocumentResultSetHandler implements ResultSetHandler<Document>
   {
      /**
       * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
       */
      public Document handle(ResultSet rs) throws SQLException
      {
         Document result = null;

         if (rs.next())
         {
            Clob clob = rs.getClob(1);

            if (!rs.wasNull())
            {
               result = AeDbUtils.getDocument(clob);
            }
         }

         return result;
      }
   };
}
