// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/AeXMLParserDefaultHandler.java,v 1.1 2004/10/16 03:51:43 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml;

import javax.wsdl.xml.WSDLLocator;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Implements a SAX <code>DefaultHandler</code> that resolves entities and
 * optionally dispatches errors to a delegate error handler.
 */
public class AeXMLParserDefaultHandler extends DefaultHandler
{
   /** The WSDL locator for locating schema resources. */
   private final WSDLLocator mWSDLLocator;

   /** The delegate SAX <code>ErrorHandler</code>. */
   private final ErrorHandler mErrorHandler;

   /**
    * Constructor for entity resolver.
    */
   public AeXMLParserDefaultHandler(WSDLLocator aWSDLLocator)
   {
      this(aWSDLLocator, null);
   }

   /**
    * Constructor for entity resolver and error handler.
    */
   public AeXMLParserDefaultHandler(WSDLLocator aWSDLLocator, ErrorHandler aErrorHandler)
   {
      mWSDLLocator = aWSDLLocator;
      mErrorHandler = aErrorHandler;
   }

   /**
    * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
    */
   public void error(SAXParseException aException) throws SAXException
   {
      if (getErrorHandler() != null)
      {
         getErrorHandler().error(aException);
      }
   }

   /**
    * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
    */
   public void fatalError(SAXParseException aException) throws SAXException
   {
      if (getErrorHandler() != null)
      {
         getErrorHandler().fatalError(aException);
      }
   }

   /**
    * Returns the delegate SAX <code>ErrorHandler</code>.
    */
   protected ErrorHandler getErrorHandler()
   {
      return mErrorHandler;
   }

   /**
    * Returns the WSDL locator for locating schema resources.
    */
   protected WSDLLocator getWSDLLocator()
   {
      return mWSDLLocator;
   }

   /**
    * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
    */
   public InputSource resolveEntity(String publicId, String systemId)
   {
      WSDLLocator locator = getWSDLLocator();
      InputSource result = null;

      if (locator != null)
      {
         result = locator.getImportInputSource(locator.getBaseURI(), systemId);
      }

      return result;
   }

   /**
    * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
    */
   public void warning(SAXParseException aException) throws SAXException
   {
      if (getErrorHandler() != null)
      {
         getErrorHandler().warning(aException);
      }
   }
}
