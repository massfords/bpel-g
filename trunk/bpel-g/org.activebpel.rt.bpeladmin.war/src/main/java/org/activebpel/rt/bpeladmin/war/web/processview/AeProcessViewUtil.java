//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeProcessViewUtil.java,v 1.9 2008/02/17 21:43:07 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//                PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview;

import java.io.File;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Document;

/**
 * Contains convienience methods to load a XML Document object.
 */
public class AeProcessViewUtil
{

   /**
    * Clones and returns the copy of the original Document,
    * @param aDocument original document.
    * @return cloned document.
    */
   public static Document cloneDocument(Document aDocument) throws AeException
   {
      AeXMLParserBase parser = new AeXMLParserBase();
      parser.setNamespaceAware(false);
      parser.setValidating(false);
      Document doc = parser.createDocument();
      doc.appendChild( doc.importNode(aDocument.getDocumentElement(), true) );
      return doc;
   }

   /**
    * Creates and returns a Document given a handle to the xml file.
    * @param aXmlFile File to the xml document.
    * @return Document object.
    */
   public static Document domFromFile(File aXmlFile)
      throws AeException
   {
      Document rDoc;
      AeXMLParserBase parser = new AeXMLParserBase();
      parser.setNamespaceAware(true);
      parser.setValidating(false);
      rDoc = parser.loadDocument(aXmlFile.getAbsolutePath(), null);
      return rDoc;
   }

   /**
    * Creates and returns a Document given the xml as a string.
    * @param aXmlSource valid xml document content.
    * @return Document object.
    */
   public static Document domFromString(String aXmlSource) throws AeException
   {
      try
      {
         AeXMLParserBase parser = new AeXMLParserBase();
         parser.setNamespaceAware(true);
         parser.setValidating(false);
         Document dom = parser.loadDocumentFromString(aXmlSource, null);
         return dom;
      }
      catch(AeException e)
      {
         throw e;
      }
   }

   /**
    * Converts a Document object to a string.
    * @param aDocument BPEL dom
    * @return String version of the dom or null if unable to convert.
    */
   public static String stringFromDom(Document aDocument, boolean indent)
   {
      try
      {
         return AeXMLParserBase.documentToString(aDocument, indent);
      }
      catch(Throwable t)
      {
         // ignore
         return null;
      }
   }

   /**
    * Formats a string so that the first character is uppper case, followed by all lower
    * case characters.
    * @param aLabel
    * @return formated string
    */
   public static String formatLabel(String aLabel)
   {
      String rVal = AeUtil.getSafeString(aLabel);
      if (rVal.length() > 1)
      {
         rVal = rVal.substring(0,1).toUpperCase() + rVal.substring(1);
      }
      else if (rVal.length() == 1)
      {
         rVal = rVal.toUpperCase();
      }
      return rVal;
   }
}
