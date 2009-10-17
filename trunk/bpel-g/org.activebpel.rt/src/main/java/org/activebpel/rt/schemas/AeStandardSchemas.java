//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/schemas/AeStandardSchemas.java,v 1.4 2007/12/14 01:02:37 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.schemas;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;
import org.activebpel.rt.util.AeSafelyViewableMap;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class provides access to a static list of "well known" schemas, accessed by the
 * targetNamespace of the schema.  Given a schema namespace, this class will give back an
 * InputStream to the content of the schema, or null if it is not a "well known" one.
 */
public class AeStandardSchemas
{
   /** The static map of "well known" schemas (namespace -> resource name). */
   static private Map sSchemaMap;

   /*
    * Static initializer for loading the contents of the above schema map.
    */
   static 
   {
      URL schemasURL = AeStandardSchemas.class.getResource("standardSchemas.xml"); //$NON-NLS-1$
      if (schemasURL != null)
      {
         try
         {
            Map map = new HashMap();
            AeXMLParserBase parser = new AeXMLParserBase();
            parser.setValidating(false);
            parser.setNamespaceAware(false);
            Document schemasDoc = parser.loadDocument(schemasURL.openStream(), null);
            NodeList nl = schemasDoc.getDocumentElement().getElementsByTagName("schemaRef"); //$NON-NLS-1$
            if (nl != null)
            {
               for (int i = 0; i < nl.getLength(); i++)
               {
                  Element schemaRef = (Element) nl.item(i);
                  String ns = schemaRef.getAttribute("namespace"); //$NON-NLS-1$
                  String loc = schemaRef.getAttribute("location"); //$NON-NLS-1$
                  map.put(ns, loc);
               }
            }
            sSchemaMap = new AeSafelyViewableMap(map);
         }
         catch (Throwable t)
         {
            AeException.logError(t, AeMessages.getString("AeStandardSchemas.ERROR_0")); //$NON-NLS-1$
         }
      }
   }
   
   /**
    * Added for unit testing only
    * @param aSchemaNamespace
    * @param aLocation
    */
   protected static void addStandardSchema(String aSchemaNamespace, String aLocation)
   {
      sSchemaMap.put(aSchemaNamespace, aLocation);
   }
   
   /**
    * Added for junit testing only
    * @param aSchemaNamespace
    * @param aLocation
    */
   protected static void removeStandardSchema(String aSchemaNamespace, String aLocation)
   {
      sSchemaMap.put(aSchemaNamespace, aLocation);
   }

   /**
    * This method returns the input stream for a standard schema given the schema namespace.
    * 
    * @param aNamespace
    */
   public static InputStream getStandardSchema(String aNamespace)
   {
      try
      {
         String location = (String) sSchemaMap.get(aNamespace);
         if (location != null)
         {
            return AeStandardSchemas.class.getResourceAsStream(location);
         }
      }
      catch (Exception e)
      {
         AeException.logError(e, AeMessages.getString("AeStandardSchemas.ERROR_1") + aNamespace); //$NON-NLS-1$
      }
      return null;
   }
   
   /**
    * Returns true if the standard schemas has an entry for the namespace.
    * @param aNamespace
    */
   public static boolean canResolve(String aNamespace)
   {
      return sSchemaMap.containsKey(aNamespace);
   }
}
