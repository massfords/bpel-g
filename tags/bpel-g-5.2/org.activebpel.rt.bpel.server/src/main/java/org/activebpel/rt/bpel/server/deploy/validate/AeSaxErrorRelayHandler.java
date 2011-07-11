//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AeSaxErrorRelayHandler.java,v 1.3 2008/01/03 21:53:54 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import org.activebpel.rt.xml.AeXMLParserErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * This class is a SAX handler that will relay all SAX errors and warnings to an instance of
 * <code>IAeResourceValidationErrorHandler</code>.
 */
public class AeSaxErrorRelayHandler extends AeXMLParserErrorHandler
{
   /** The pdd validation error handler to relay the sax errors/warning to. */
   private IAeResourceValidationErrorHandler mHandler;

   /**
    * Constructor.
    * 
    * @param aHandler
    */
   public AeSaxErrorRelayHandler(IAeResourceValidationErrorHandler aHandler)
   {
      mHandler = aHandler;
   }

   /**
    * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
    */
   public void warning(SAXParseException exception)
   {
      mHandler.parseWarning(exception.getMessage(), exception.getLineNumber());
   }

   /**
    * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
    */
   public void error(SAXParseException exception)
   {
      mHandler.parseError(exception.getMessage(), exception.getLineNumber());
   }

   /**
    * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
    */
   public void fatalError(SAXParseException exception) throws SAXException
   {
      mHandler.parseFatalError(exception.getMessage(), exception.getLineNumber());
   }
}
