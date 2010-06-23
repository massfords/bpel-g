//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/AeXMLParserSchemaHandler.java,v 1.10 2008/02/27 20:41:55 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.xml.WSDLLocator;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;
import org.activebpel.rt.xml.schema.AeSchemaUtil;
import org.exolab.castor.xml.schema.Schema;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

/**
 * This class implements an XML parser default handler.  It extends the AE default handler
 * and simply adds the ability to resolve schema entities from a list of schemas.
 */
public class AeXMLParserSchemaHandler extends AeXMLParserDefaultHandler
{
   /** A map from namespace to <code>Schema</code> object. */
   private HashMap mSchemaMap = new HashMap();

   /**
    * Constructs a schema xml handler given the wsdl locator and schema list.
    * 
    * @param aWSDLLocator
    * @param aSchemaList
    */
   // fixme (MF) change to accept a map of namespaces to byte[]
   public AeXMLParserSchemaHandler(WSDLLocator aWSDLLocator, List aSchemaList)
   {
      this(aWSDLLocator, null, aSchemaList);
   }

   /**
    * Constructs a schema xml handler given the wsdl locator, error handler, and list
    * of schemas.
    * 
    * @param aWSDLLocator
    * @param aErrorHandler
    */
   // fixme (MF) change to accept a map of namespaces to byte[]
   public AeXMLParserSchemaHandler(WSDLLocator aWSDLLocator, ErrorHandler aErrorHandler, List aSchemaList)
   {
      super(aWSDLLocator, aErrorHandler);
      
      buildSchemaMap(aSchemaList);
   }

   /**
    * Overrides method to resolve schema imports with no location.  The schema will be looked up
    * by its namespace from the list of schemas passed in to the handler during construction.
    * 
    * @see org.activebpel.rt.xml.AeXMLParserDefaultHandler#resolveEntity(java.lang.String, java.lang.String)
    */
   public InputSource resolveEntity(String aPublicId, String aSystemId)
   {
      if (aPublicId == null)
      {
         Schema schema = findSchema(aSystemId);
         if (schema != null)
         {
            try
            {
               String schemaStr = AeSchemaUtil.serializeSchema(schema, false);
               InputSource is = new InputSource(new StringReader(schemaStr));
               is.setSystemId(aSystemId);
               
               return is;
            }
            catch (Exception e)
            {
               AeException.logError(e, AeMessages.getString("AeXMLParserSchemaHandler.ERROR_0")); //$NON-NLS-1$
            }
         }
      }
      return super.resolveEntity(aPublicId, aSystemId);
   }

   /**
    * This method builds the internal schema map, which is a map from schema target namespace
    * to Schema object.
    * 
    * @param aSchemaList
    */
   protected void buildSchemaMap(List aSchemaList)
   {
      for (Iterator iter = aSchemaList.iterator(); iter.hasNext(); )
      {
         Schema schema = (Schema) iter.next();
         mSchemaMap.put(schema.getTargetNamespace(), schema);
      }
   }

   /**
    * Returns a schema for a given namespace or null if not found.
    * 
    * @param aNamespace
    */
   protected Schema findSchema(String aNamespace)
   {
      return (Schema) mSchemaMap.get(aNamespace);
   }
}
