//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/castor/AeInputSourceURILocation.java,v 1.3 2006/06/26 16:46:46 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def.castor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.exolab.castor.net.URILocation;
import org.xml.sax.InputSource;

/**
 * This class implements a Castor URILocation from a valid InputSource and base href.
 */
public class AeInputSourceURILocation extends URILocation
{
   /** Separator for project-relative paths and URL paths. */
   private static final String PATH_SEPARATOR = "/"; //$NON-NLS-1$

   /** The InputSource containing the actual resource. */
   private final InputSource mInputSource;
   /** The base href of this resource. */
   private final String mBaseHref;

   /** Cached value of the base URI. */
   private transient String mBaseURI;

   /**
    * Constructs a URI location given a URL.
    * 
    * @param aInputSource
    * @param aBaseHref
    */
   public AeInputSourceURILocation(InputSource aInputSource, String aBaseHref)
   {
      mInputSource = aInputSource;
      mBaseHref = aBaseHref;
   }

   /**
    * @see org.exolab.castor.net.URILocation#getBaseURI()
    */
   public String getBaseURI()
   {
      if (mBaseURI == null)
      {
         mBaseURI = getAbsoluteURI();

         // The Castor-compatible document base is the portion up to and
         // including the last path separator.
         int i = mBaseURI.lastIndexOf(PATH_SEPARATOR);
         if (i >= 0)
         {
            mBaseURI = mBaseURI.substring(0, i + 1);
         }
      }

      return mBaseURI;
   }

   /**
    * @see org.exolab.castor.net.URILocation#getReader()
    */
   public Reader getReader() throws IOException
   {
      // Check for a character stream from the input source first.
      Reader reader = mInputSource.getCharacterStream();

      // if no charaacter stream reader then go for a byte stream applying
      // the encoding directly if available
      if (reader == null)
      {
         // Check for a byte stream from the input source.
         if(mInputSource.getEncoding() == null)
            reader = new InputStreamReader(mInputSource.getByteStream());
         else
            reader = new InputStreamReader(mInputSource.getByteStream(), mInputSource.getEncoding());
      }

      return reader;
   }

   /**
    * @see org.exolab.castor.net.URILocation#getRelativeURI()
    */
   public String getRelativeURI()
   {
      return mBaseHref;
   }

   /**
    * @see org.exolab.castor.net.URILocation#getAbsoluteURI()
    */
   public String getAbsoluteURI()
   {
      return mBaseHref;
   }
}
