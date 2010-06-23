// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/AeWSDLFactory.java,v 1.8 2006/07/18 19:54:32 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;
import org.activebpel.rt.IAeWSDLFactory;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.AeXMLParserBase;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * This provider allows you to load wsdl files, which may be cached locally.
 */
public class AeWSDLFactory implements IAeWSDLFactory
{
   /** Tag in WSDL catalog which is the WSDL entry item */
   private static String WSDL_ENTRY_TAG = "wsdlEntry"; //$NON-NLS-1$
   /** Tag in WSDL catalog which is the URL for the WSDL entry */
   private static String WSDL_URL_TAG = "url"; //$NON-NLS-1$
   /** Tag in WSDL catalog which is the classpath for the WSDL entry */
   private static String WSDL_CLASSPATH_TAG = "classpath"; //$NON-NLS-1$
   /** Tag in WSDL catalog which is the namespace for the WSDL entry */
   private static String WSDL_NAMESPACE_TAG = "namespace"; //$NON-NLS-1$

   /** Hash map of url to classpath locations for wsdl  */
   private static HashMap sUrl2Classpath = new HashMap();

   /** Hash map of url to classpath locations for wsdl  */
   private static HashMap sNamespace2Url = new HashMap();
   
   /**
    * Creates a WSDL provider, given the stream for the catalog. The catalog
    * contains a mapping of URI to resource location. 
    * @param aCatalog input stream for catalog
    */
   public AeWSDLFactory(InputStream aCatalog)
   {
      loadWSDLCatalog(aCatalog);
   }

   /**
    * @see org.activebpel.rt.IAeWSDLFactory#getWSDLSource(java.lang.String)
    */
   public InputSource getWSDLSource(String aWsdlUrl) throws AeException
   {
      InputStream stream = null;

      try
      {
         // Check if WSDL has catalog entry for the local classpath otherwise open URL
         String classpath = (String)sUrl2Classpath.get(aWsdlUrl.toLowerCase());
         if(classpath != null)
            stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(classpath); 
         else
            stream = new URL(aWsdlUrl).openStream();
      }
      catch (Exception ex)
      {
         throw new AeException(AeMessages.format("AeWSDLFactory.ERROR_4", aWsdlUrl), ex); //$NON-NLS-1$
      }

      return new InputSource(stream);
   }

   /**
    * Returns the wsdl source for a passed namespace or null if none.
    */
   public InputSource getWSDLForNamespace(String aNamespace) throws AeException
   {
      Object obj = sNamespace2Url.get(aNamespace);
      if(obj != null)
         return getWSDLSource(obj.toString());
      return null;
   }

   /**
    * @see org.activebpel.rt.IAeWSDLFactory#getWSDLLocationForNamespace(java.lang.String)
    */
   public String getWSDLLocationForNamespace(String aNamespace) throws AeException
   {
      return (String)sNamespace2Url.get(aNamespace);
   }

   /**
    * Load the wsdl local catalog file into our internal mapping.
    * @param aCatalog input stream for catalog of WSDL mappings
    */
   private void loadWSDLCatalog(InputStream aCatalog)
   {
      if(aCatalog == null)
         return;
         
      AeXMLParserBase parser = new AeXMLParserBase();
      parser.setValidating(false);
      parser.setNamespaceAware(true);
      
      try
      {
         Document doc = parser.loadDocument(aCatalog, null);
         if(doc.getDocumentElement() != null)
         {
            for(Node node=doc.getDocumentElement().getFirstChild(); node != null; node = node.getNextSibling())
            {
               if(node.getNodeType() == Node.ELEMENT_NODE)
               {
                  if(WSDL_ENTRY_TAG.equals(node.getLocalName()))
                  {
                     Element elem = (Element)node;
                     String namespace = elem.getAttribute(WSDL_NAMESPACE_TAG);
                     String url = elem.getAttribute(WSDL_URL_TAG).toLowerCase();
                     String classpath = elem.getAttribute(WSDL_CLASSPATH_TAG);
                     sUrl2Classpath.put(url, classpath);
                     if(! AeUtil.isNullOrEmpty(namespace) )
                        sNamespace2Url.put(namespace, url);
                  }
               }
            }
         }
      }
      catch(AeException ex)
      {
         AeException.logError(ex, AeMessages.getString("AeWSDLFactory.ERROR_5")); //$NON-NLS-1$
      }
   }

}
