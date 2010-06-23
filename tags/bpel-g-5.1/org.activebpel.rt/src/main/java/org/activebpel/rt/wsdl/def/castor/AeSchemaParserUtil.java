//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/castor/AeSchemaParserUtil.java,v 1.12 2008/01/18 22:30:19 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def.castor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.xml.WSDLLocator;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.wsdl.def.AeStandardSchemaResolver;
import org.activebpel.rt.xml.schema.AeSchemaUtil;
import org.exolab.castor.net.URIResolver;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.reader.SchemaReader;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Utility methods for fixing up schema imports (either from wsdl files or
 * from nested imports) so that schemas are accessible via URL.
 */
public class AeSchemaParserUtil
{
   // constants for schema parsing
   private static final QName SCHEMA_QNAME = new QName(IAeConstants.W3C_XML_SCHEMA, "schema");  //$NON-NLS-1$ 
   private static final String IMPORT = "import"; //$NON-NLS-1$
   public static final String SCHEMA_LOCATION = "schemaLocation"; //$NON-NLS-1$

   /**
    * Given the schema location, load the schema object and serialize it to a string.
    * @param aDef
    * @throws IOException
    */
   public static String getSchemaAsString(AeBPELExtendedWSDLDef aDef, String aSchemaLocation) throws IOException
   {
      Schema schema = loadSchema(aDef.getWSDLDef(), aDef.getLocator(), aSchemaLocation);
      return (schema == null ? null : AeSchemaUtil.serializeSchema(schema, false));
   }
   
   /**
    * Return a <code>NodeList</code> of schema import elements.
    * @param aElement
    */
   public static NodeList getSchemaImportNodeList( Element aElement )
   {
      return aElement.getElementsByTagNameNS( SCHEMA_QNAME.getNamespaceURI(), IMPORT );
   }
   
   /**
    * Return true if the qname arg matches the {http://www.w3.org/2001/XMLSchema}schema qname.
    * @param aType
    */
   public static boolean isSchemaQName( QName aType )
   {
      return SCHEMA_QNAME.equals( aType );
   }

   /**
    * Load the schema object given the import schema location.
    * @param aWsdlDef
    * @param aWsdlLocator
    * @param aSchemaLocation
    * @throws IOException
    */
   protected static Schema loadSchema(Definition aWsdlDef, WSDLLocator aWsdlLocator, String aSchemaLocation)
         throws IOException
   {
      Schema schema = null;
      Types types = aWsdlDef.getTypes();
      if( (types != null) && !types.getExtensibilityElements().isEmpty() )
      {
         InputSource importInputSrc = aWsdlLocator.getImportInputSource(aWsdlDef.getDocumentBaseURI(), aSchemaLocation);
         SchemaReader reader = new SchemaReader(importInputSrc);

         URIResolver resolver = new AeWSDLSchemaResolver(aWsdlLocator, aWsdlDef, AeStandardSchemaResolver.newInstance());
         reader.setURIResolver(resolver);
         schema = reader.read();
      }

      return schema;
   }

   /**
    * Loads a schema relative to a class's packaging.
    * 
    * @param aPath
    * @param aClass
    */
   public static Schema loadSchema(String aPath, Class aClass)
   {
      InputStream in = null;
      try
      {
         in = aClass.getResourceAsStream(aPath);
         return loadSchema(new InputSource(in));
      }
      catch(IOException e)
      {
         AeException.logError( e, AeMessages.getString("AeSchemaParserUtil.SchemaError") + aPath ); //$NON-NLS-1$
         throw new InternalError(AeMessages.getString("AeSchemaParserUtil.SchemaNotPackagedWithProject") + aPath); //$NON-NLS-1$
      }
      finally
      {
         AeCloser.close(in);
      }
   }

   /**
    * Loads a castor schema object
    * @param aSchemaFile
    * @return castor schema
    * @throws IOException
    */
   public static Schema loadSchema(File aSchemaFile) throws IOException
   {
      FileInputStream in = null;
      try
      {
         in =  new FileInputStream(aSchemaFile);
         return loadSchema(new InputSource(in));
      }
      catch(IOException e)
      {
         AeException.logError( e, AeMessages.getString("AeSchemaParserUtil.SchemaError") + aSchemaFile.getAbsolutePath() ); //$NON-NLS-1$
         throw e;
      }
      finally
      {
         AeCloser.close(in);
      }      
   } 
   
   /**
    * Load schema from given input source
    * @param aInputSource
    * @return castor Schema object
    */
   public static Schema loadSchema(InputSource aInputSource) throws IOException
   {
      SchemaReader schemaReader = new SchemaReader(aInputSource);         
      return schemaReader.read();
   }   
   
   /**
    * Extract schema definition from passed extensibility element for parsing by
    * castor.
    * 
    * @param aExtElement the schema extensibility element.
    * @return Element the extracted schema element.
    */
   public static Element extractSchemaElement(
         UnknownExtensibilityElement aExtElement)
   {
      // copy all namespace attributes from parents to schema root if they don't
      // exist
      Node parent = aExtElement.getElement();
      Element element = (Element) parent.cloneNode(true);
      while ((parent = parent.getParentNode()) != null)
      {
         if( parent.getNodeType() == Node.ELEMENT_NODE )
         {
            NamedNodeMap attrs = parent.getAttributes();
            for( int i = 0; i < attrs.getLength(); ++i )
            {
               Attr attr = (Attr) attrs.item(i);
               if( IAeConstants.W3C_XMLNS.equals(attr.getNamespaceURI()) )
               {
                  if( !element.hasAttribute(attr.getNodeName()) )
                     element.setAttributeNS(IAeConstants.W3C_XMLNS, attr
                           .getNodeName(), attr.getNodeValue());
               }
            }
         }
      }

      // castor has an issue dealing with imports that don't have namespaces
      // prefixes
      // so this will fix them up
      int prefixCount = 1;
      for( Node child = element.getFirstChild(); child != null; child = child
            .getNextSibling() )
      {
         if( child.getNodeType() == Node.ELEMENT_NODE
               && "import".equals(child.getLocalName()) ) //$NON-NLS-1$
         {
            Element schema = (Element) child;
            String namespace = schema.getAttribute("namespace"); //$NON-NLS-1$
            if( !AeUtil.isNullOrEmpty(namespace) )
            {
               String prefix = AeXmlUtil.getPrefixForNamespace(schema,
                     namespace);
               if( AeUtil.isNullOrEmpty(prefix) )
               {
                  prefix = "ae__temp_ns" + prefixCount++; //$NON-NLS-1$
                  element.setAttributeNS(IAeConstants.W3C_XMLNS,
                        "xmlns:" + prefix, namespace); //$NON-NLS-1$
               }
            }
         }
      }
      return element;
   }
   
   /**
    * Reads the schema from the input source.
    * @param aInputSource contains the src xml for the schema
    * @param aURIResolver optional uri resolver or null to use castor's default 
    * @throws IOException
    */
   public static Schema readSchema(InputSource aInputSource, URIResolver aURIResolver) throws IOException
   {
      SchemaReader reader = new SchemaReader(aInputSource);
      if (aURIResolver != null)
         reader.setURIResolver(aURIResolver);
      return reader.read();
   }
}