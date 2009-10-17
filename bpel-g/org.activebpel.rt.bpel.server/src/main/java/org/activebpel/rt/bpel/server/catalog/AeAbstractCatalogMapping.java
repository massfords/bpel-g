// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/catalog/AeAbstractCatalogMapping.java,v 1.2 2006/08/04 17:57:53 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.catalog;

import java.io.IOException;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.wsdl.def.IAeBPELExtendedWSDLConst;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Document;

/**
 * Base class for IAeCatalogMapping impls.
 */
abstract public class AeAbstractCatalogMapping implements IAeCatalogMapping
{
   /** Location hint. */
   private String mLocationHint;

   /** The catalog mapping type. */
   private String mTypeURI;

   /** The target namespace of this mapping item. */
   private String mTargetNamespace;
   
   /**
    * Constructor.
    * 
    * @param aLocationHint
    */
   protected AeAbstractCatalogMapping( String aLocationHint, String aTypeURI )
   {
      mLocationHint = aLocationHint;
      mTypeURI = aTypeURI;
   }

   /**
    * @see org.activebpel.rt.bpel.server.catalog.IAeCatalogMapping#getDocument()
    */
   public Document getDocument() throws AeException
   {
      try
      {
         AeXMLParserBase parser = new AeXMLParserBase(true, false);
         return parser.loadDocument(getInputSource(), null);
      }
      catch (IOException ex)
      {
         throw new AeException(ex);
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.catalog.IAeCatalogMapping#getLocationHint()
    */
   public String getLocationHint()
   {
      return mLocationHint;
   }

   /**
    * @see org.activebpel.rt.bpel.server.catalog.IAeCatalogMapping#getTypeURI()
    */
   public String getTypeURI()
   {
      return mTypeURI;
   }

   /**
    * Parses the associated document and looks for the targetNamespace attribute.  It then
    * caches it for future returns.
    * @see org.activebpel.rt.bpel.server.catalog.IAeCatalogMapping#getTargetNamespace()
    */
   public String getTargetNamespace()
   {
      try
      {
         Document doc = getDocument();
         if(doc.getDocumentElement() != null)
            mTargetNamespace = doc.getDocumentElement().getAttribute("targetNamespace"); //$NON-NLS-1$
      }
      catch (Exception ex)
      {
         // ignore parsing errors, etc caught sep
      }
      
      // if still null make empty
      if(mTargetNamespace == null)
         mTargetNamespace = ""; //$NON-NLS-1$
      
      return mTargetNamespace;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.catalog.IAeCatalogMapping#isWsdlEntry()
    */
   public boolean isWsdlEntry()
   {
      return IAeBPELExtendedWSDLConst.WSDL_NAMESPACE.equals(getTypeURI());
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.catalog.IAeCatalogMapping#isSchemaEntry()
    */
   public boolean isSchemaEntry()
   {
      return IAeConstants.W3C_XML_SCHEMA.equals(getTypeURI());
   }
}
