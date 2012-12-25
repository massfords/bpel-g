// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/multipart/parse/AeServletRequestDataSource.java,v 1.3 2008/02/17 21:54:15 MFord Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.http.multipart.parse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.activebpel.rt.AeException;
import org.activebpel.rt.http.AeHttpServletRequest;
import org.activebpel.rt.util.AeAutoCloseBlobInputStream;

/**
 * Provides a datasource for http multipart mime body part parser
 * @see AeHttpServletRequest
 * @see AeHttpMultipartParser
 * @see javax.activation.DataHandler
 */
public class AeServletRequestDataSource implements DataSource
{
   /** HttpServletRequest content MIME type */
   private String mContentType;

   /** the data source name */
   private String mName;

   /** HttpServletRequest input stream */
   private InputStream mDataSource;

   /**
    * Constructor
    * @param aContentType
    * @param aName
    * @param aData
    * @throws AeException
    */
   public AeServletRequestDataSource(String aContentType, String aName, InputStream aData) throws AeException
   {
      mContentType = aContentType;
      mName = aName;
      try
      {
         mDataSource = new AeAutoCloseBlobInputStream(aData);
      }
      catch (FileNotFoundException ex)
      {
         throw new AeException(ex);
      }
   }

   /**
    * @see javax.activation.DataSource#getContentType()
    */
   public String getContentType()
   {
      return mContentType;
   }

   /**
    * @see javax.activation.DataSource#getInputStream()
    */
   public InputStream getInputStream() throws IOException
   {
      return mDataSource;
   }

   /**
    * @see javax.activation.DataSource#getName()
    */
   public String getName()
   {
      return mName;
   }

   /**
    * Overrides method to return <code>null</code>.
    * @see javax.activation.DataSource#getOutputStream()
    */
   public OutputStream getOutputStream() throws IOException
   {
      return null;
   }
}
