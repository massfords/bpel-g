//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/castor/AeWSDLSchemaURILocation.java,v 1.1.26.1 2008/04/21 16:05:13 ppatruni Exp $
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
import java.io.Reader;
import java.io.StringReader;

import org.activebpel.rt.xml.AeXMLParserBase;
import org.exolab.castor.net.URILocation;
import org.w3c.dom.Element;

/**
 * This class represents a WSDL schema location.  A WSDL schema location is a 
 * &lt;schema&gt; element defined within a WSDL file.  When loading schemas from
 * WSDL, the engine will pass them to Castor for parsing and validation one at
 * a time.  This is problematic when a type definition in one schema refers to
 * a type defined in a different schema from the same WSDL file.  To solve this
 * problem, we insert import statements for all schema elements in the WSDL file
 * defined above the one being processed.  These import statements have 
 * "schemaLocation" attributes defined as "[WSDL URI]%id=N", where N is the 
 * schema element's location in the WSDL.  So for example, if we define a WSDL
 * file called myFirstWSDL.wsdl, and it has four schema elements in it, the third
 * schema element will have a "schemaLocation" that looks something like:<br/>
 * <br/>
 * /path/to/wsdl/myFirstWSDL.wsdl&amp;id=2<br/>
 * <br/>
 * This URI location class represents the above location by keep a reference to
 * the schema Element in memory, as well as the location, doc base, and base URI.
 */
public class AeWSDLSchemaURILocation extends URILocation
{
   /** Separator for project-relative paths and URL paths. */
   private static final String PATH_SEPARATOR = "/"; //$NON-NLS-1$

   /** A &lt;schema&gt; element extracted from the WSDL file. */
   private Element mSchemaElement;
   /** The URI location of the schema element (see class javadoc). */
   private String mLocation;
   /** The base URI - this is basically a cached value. */
   private String mBaseURI;

   /**
    * Constructs a schema URI location from the schema xml element, and URI location.
    * 
    * @param aSchemaElement
    * @param aLocation
    */
   public AeWSDLSchemaURILocation(Element aSchemaElement, String aLocation)
   {
      mSchemaElement = aSchemaElement;
      mLocation = aLocation;
   }

   /**
    * @see org.exolab.castor.net.URILocation#getAbsoluteURI()
    */
   public String getAbsoluteURI()
   {
      return mLocation;
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
      String schemaStr = AeXMLParserBase.documentToString(mSchemaElement);
      return new StringReader(schemaStr);
   }

   /**
    * @see org.exolab.castor.net.URILocation#getRelativeURI()
    */
   public String getRelativeURI()
   {
      return mLocation;
   }

}
