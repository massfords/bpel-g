// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/schemas/AeWSResourceSchemas.java,v 1.1 2007/12/17 16:41:43 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsresource.validation.schemas;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeCloser;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.reader.SchemaReader;
import org.xml.sax.InputSource;

/**
 * Helper class for loading schemas used for deploying bpel processes to the engine. 
 * todo (cck) do these schemas really need to be loaded statically (performance?)
 */
public class AeWSResourceSchemas
{
   /** set of the wsdl schemas */
   private static final Set WSDL_SCHEMAS = new HashSet();

   static
   {
      // Load the WSDL schemas
      WSDL_SCHEMAS.add(loadSchema("wsdl11.xsd")); //$NON-NLS-1$
//      new AeStandardSchemas().getStandardSchema(IAeConstants.W3C_XML_SCHEMA)
//      WSDL_SCHEMAS.add(loadSchema("XMLSchema.xsd")); //$NON-NLS-1$
      WSDL_SCHEMAS.add(loadSchema("partner-link.xsd")); //$NON-NLS-1$
      // FIXMEQ (builders) load soap schema - imports the wsdl schema - need a resolver
//      WSDL_SCHEMAS.add(loadSchema("soap-2003-02-11.xsd")); //$NON-NLS-1$
   }
   
   /**
    * Gets an iterator containing the pdd schemas as castor schema objects
    */
   public static Iterator getWsdlSchemas()
   {
      return WSDL_SCHEMAS.iterator();      
   }

   /**
    * Loads schemas relative to this class's packaging.
    * 
    * @param aPath
    */
   private static Schema loadSchema(String aPath)
   {
      InputStream in = null;
      try
      {
         in = AeWSResourceSchemas.class.getResourceAsStream(aPath);
         SchemaReader schemaReader = new SchemaReader(new InputSource(in));
         
         return schemaReader.read();
      }
      catch(IOException e)
      {
         AeException.logError(e, "Error loading schema: " + aPath); //$NON-NLS-1$
         throw new InternalError("Error loading schema: " + aPath); //$NON-NLS-1$
      }
      finally
      {
         AeCloser.close(in);
      }
   }
}
