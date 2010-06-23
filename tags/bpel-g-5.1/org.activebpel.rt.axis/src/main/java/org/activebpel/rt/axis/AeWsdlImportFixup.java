//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/AeWsdlImportFixup.java,v 1.1 2008/02/21 18:14:06 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis;

import java.util.Iterator;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Types;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;

import org.activebpel.rt.base64.Base64;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeFileUtil;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.wsdl.def.castor.AeSchemaParserUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Utility class that removes all WSDL elements which do not pertain to this service deployment.
 */
public class AeWsdlImportFixup
{
   /** Static for a wsdl extension default. */
   public static final String WSDL_EXTENSION   = "wsdl"; //$NON-NLS-1$
   /** Static for an xml schema extension default. */
   public static final String SCHEMA_EXTENSION = "xsd"; //$NON-NLS-1$
   
   /** The <code>ACTIVE_BPEL</code> field contains the prefix for axis and the catalog servlet, normally active-bpel. */
   private static final String ACTIVE_BPEL = AeMessages.getString("AeWsdlImportFixup.ACTIVE_BPEL_CONTEXT"); //$NON-NLS-1$

   /**
    * Used to modify the import location references so that they may be resolved by the catalog servlet.
    * @param aTransportUrl
    * @param aDef the WSDL definition we are processing
    */
   public static void fixupImportReferences(String aTransportUrl, AeBPELExtendedWSDLDef aDef)
   {
      String parentLocation = aDef.getLocationHint();
      fixUpWsdlImportReferences( aTransportUrl, aDef.getWSDLDef(), parentLocation );
      fixUpSchemaImportReferences( aTransportUrl, aDef.getWSDLDef(), parentLocation );
   }

   /**
    * Fix up wsdl import references so that they may be resolved
    * by the catalog servlet.
    * @param aTransportUrl
    * @param aDef
    * @param aParentLocation
    */
   protected static void fixUpWsdlImportReferences( String aTransportUrl, Definition aDef, String aParentLocation )
   {
      AeImportUrl importUrl = new AeImportUrl( aTransportUrl, aParentLocation );
      for (Iterator iter=aDef.getImports().keySet().iterator(); iter.hasNext();)
      {
         List importList = aDef.getImports(iter.next().toString());
         for (Iterator impIter=importList.iterator(); impIter.hasNext();)
         {
            Import wsdlImport = (Import)impIter.next();
            String importLocation = wsdlImport.getLocationURI();
            wsdlImport.setLocationURI(importUrl.getImportUrl(importLocation, WSDL_EXTENSION));
         }
      }
   }

   /**
    * Fix up schema import references so that they may be resolved
    * by the catalog servlet.
    * @param aTransportUrl
    * @param aDef
    * @param aParentLocation
    */
   protected static void fixUpSchemaImportReferences( String aTransportUrl,
                                       Definition aDef, String aParentLocation )
   {
      AeImportUrl importUrl = new AeImportUrl( aTransportUrl, aParentLocation );

      Types schemaTypes = aDef.getTypes();
      if( schemaTypes != null && schemaTypes.getExtensibilityElements() != null )
      {
         for( Iterator iter = schemaTypes.getExtensibilityElements().iterator(); iter.hasNext(); )
         {
            ExtensibilityElement el = (ExtensibilityElement)iter.next();

            if( AeSchemaParserUtil.isSchemaQName( el.getElementType() ) )
            {
               Element rawSchemaElement = ((UnknownExtensibilityElement)el).getElement();
               NodeList imports = AeSchemaParserUtil.getSchemaImportNodeList(rawSchemaElement);

               for( int i = 0; i < imports.getLength(); i++ )
               {
                  Element impElement = ((Element)imports.item(i));
                  String location = impElement.getAttribute( AeSchemaParserUtil.SCHEMA_LOCATION );
                  if (AeUtil.notNullOrEmpty(location))
                     impElement.setAttribute(AeSchemaParserUtil.SCHEMA_LOCATION, importUrl.getImportUrl(location, SCHEMA_EXTENSION));
               }
            }
         }
      }
   }

    /**
    * Utility class for formatting the "fixed up" import urls for wsdl and schemas.
    */
   protected static class AeImportUrl
   {
      /** the import url template */
      private String mTransportUrl;
      /** the import url template */
      private String mImportUrl;
      /** the parent location string */
      private String mParentLocation;

      /**
       * Constructor.
       * @param aTransportUrl
       * @param aParentLocation
       */
      public AeImportUrl( String aTransportUrl, String aParentLocation )
      {
         mTransportUrl = aTransportUrl;
         int index = aTransportUrl.indexOf(ACTIVE_BPEL);
         mImportUrl = aTransportUrl.substring(0, index+ACTIVE_BPEL.length()) + "/catalog/"; //$NON-NLS-1$
         mParentLocation = aParentLocation;
      }

      /**
       * Format the import URL for the import location.
       * @param aImportLocation
       * @param aExtension the desired extension for this import type
       */
      public String getImportUrl(String aImportLocation, String aExtension)
      {
         String loc = resolveImportLocation(aImportLocation);

         // If engine available and a catalog entry for import URL then process the loc for catalog access
         if (AeEngineFactory.getEngineAdministration() != null && AeEngineFactory.getEngineAdministration().getCatalogAdmin().getCatalogInputSource(loc) != null)
         {
            loc = mImportUrl + encodeImportLocation(loc, aExtension);
            loc = AeFileUtil.createRelativeLocation(mTransportUrl, loc);
         }

         return loc;
      }
      
      /**
       * Resolve the import location relative to the parent if necessary.
       * @param aImportLocation
       */
      protected String resolveImportLocation( String aImportLocation )
      {
         String importLocation = aImportLocation;
         if (!AeUtil.isNullOrEmpty(mParentLocation))
         {
            importLocation = AeUtil.resolveImport(mParentLocation, importLocation);
         }
         return importLocation;
      }
      
   }

   ///////////////////////////////////////////
   // statics for catalog url encoding
   ///////////////////////////////////////////
   /** Static for double slash is for replace. */
   public static String DOUBLE_SLASH = "//"; //$NON-NLS-1$
   /** Static for what a double slash is to be replaced with. */
   public static String DOUBLE_SLASH_REPLACE = "_--_/"; //$NON-NLS-1$
   /** Static for what a question mark is for replace. */
   public static String QUESTION_MARK = "?"; //$NON-NLS-1$
   /** Static for what an escaped question mark is for replace. */
   public static String QUESTION_MARK_PATTERN = "\\?"; //$NON-NLS-1$
   /** Static for what a double slash is to be replaced with. */
   public static String QUESTION_MARK_REPLACE = "_-q-_"; //$NON-NLS-1$
   /** Static for what a colon is for replace. */
   public static String COLON = ":"; //$NON-NLS-1$
   /** Static for what a double slash is to be replaced with. */
   public static String COLON_REPLACE = "_-c-_"; //$NON-NLS-1$
   /** Static for what a temporary extension is preceded by. */
   public static String TEMP_EXTENSION = "_-e-_"; //$NON-NLS-1$
   /** Static for what a temporary extension pattern is for removal. */
   public static String TEMP_EXTENSION_PATTERN = TEMP_EXTENSION + ".*"; //$NON-NLS-1$

   /**
    * Encode the passed import location for access via the catalog servlet.  Note that
    * some applications (eclipse web service explorer) determine serialization
    * based on the extension so the desired extension is passed and is used if the
    * current extension doesn't match.
    * @param aImportLocation
    * @param aExtension
    * @return String the encoded uri
    */
   public static String encodeImportLocation( String aImportLocation, String aExtension)
   {
      String loc = aImportLocation;
      // replace double slashes, colons and question marks as these are common for wsdl
      loc = loc.replaceAll(DOUBLE_SLASH, DOUBLE_SLASH_REPLACE).
                replaceAll(COLON, COLON_REPLACE).
                replaceAll(QUESTION_MARK_PATTERN, QUESTION_MARK_REPLACE);

      // put in a dummy extension if it doesn't match the requested extension
      int extIndex = loc.lastIndexOf('.');
      if(extIndex >= 0)
      {
         if(extIndex <= loc.length() && ! aExtension.equals(loc.substring(extIndex+1)))
            loc += (TEMP_EXTENSION + "." + aExtension); //$NON-NLS-1$
      }
      else
      {
         loc += (TEMP_EXTENSION + "." + aExtension); //$NON-NLS-1$
      }
      return loc;
   }

   /**
    * Passed a string encoded for imports it will be decoded and returned.
    * @param aLocation
    * @return String containing the decoded location
    */
   public static String decodeLocation(String aLocation)
   {
      if(aLocation.startsWith(B64_PREFIX))
      {
         aLocation = b64DecodeLocation(aLocation);
      }
      else
      {
         // put all double slashes, colons and question marks back and take off extra extension if it was needed
         aLocation = aLocation.replaceAll(DOUBLE_SLASH_REPLACE, DOUBLE_SLASH).
                               replaceAll(COLON_REPLACE, COLON).
                               replaceAll(QUESTION_MARK_REPLACE, QUESTION_MARK).
                               replaceAll(TEMP_EXTENSION_PATTERN, ""); //$NON-NLS-1$
      }
      return aLocation;
   }
   
   ///////////////////////////////////////////////////////////////////////////////
   // Base 64 encoding rules, note this works except for relative imports
   ///////////////////////////////////////////////////////////////////////////////

   /** Static for what a temporary extension pattern is for removal. */
   public static String B64_PREFIX = "b64/"; //$NON-NLS-1$

   /**
    * Encode the passed import location for access via the catalog servlet via base64 encoding.
    * @param aImportLocation
    * @param aExtension
    * @return String the encoded uri
    */
   public static String b64EncodeImportLocation( String aImportLocation, String aExtension)
   {
      try
      {
         String encodedLoc = Base64.encodeBytes(aImportLocation.getBytes("UTF-8"), Base64.DONT_BREAK_LINES).trim(); //$NON-NLS-1$
         String filename = produceImportLocationFilename(aImportLocation, aExtension);
         return B64_PREFIX + encodedLoc + "/" + filename;  //$NON-NLS-1$
      }
      catch (Exception ex)
      {
         // just returned passed location if we can't encode
         return aImportLocation;
      }
   }
   
   /**
    * Constructs a filename for the passed import location with the passed extension.
    * @param aImportLocation
    * @param aExtension
    * @return the filename to use for this import location
    */
   public static String produceImportLocationFilename(String aImportLocation, String aExtension)
   {
      String path = aImportLocation.indexOf('?') >= 0 ? aImportLocation.substring(0, aImportLocation.indexOf('?')) : aImportLocation;
      int lastSlash = path.lastIndexOf('/');
      String filename = lastSlash >= 0 && lastSlash < path.length() ? path.substring(lastSlash+1) : "";  //$NON-NLS-1$

      // put in a dummy extension if it doesn't match the requested extension
      int extIndex = filename.lastIndexOf('.');
      if(extIndex >= 0)
      {
         if(extIndex <= filename.length() && ! aExtension.equals(filename.substring(extIndex+1)))
            filename += ("." + aExtension); //$NON-NLS-1$
      }
      else
      {
         filename += ("." + aExtension); //$NON-NLS-1$
      }
      return filename;
   }

   /**
    * Passed a string base64 encoded location for imports it will be decoded and returned.
    * @param aLocation
    * @return String containing the decoded location
    */
   public static String b64DecodeLocation(String aLocation)
   {
      try
      {
         aLocation = aLocation.substring(B64_PREFIX.length(), aLocation.lastIndexOf('/'));
         aLocation = new String(Base64.decode(aLocation), "UTF-8") ; //$NON-NLS-1$
         return aLocation;
      }
      catch (Exception ex)
      {
         // just return passed aLocation if we can't decode
         return aLocation;
      }
   }
   
}