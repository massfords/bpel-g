// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/AeXMLParserErrorHandler.java,v 1.5 2005/07/12 14:13:34 JPerrotto Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml;

import java.text.MessageFormat;

import org.activebpel.rt.AeMessages;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class used to process errors encountered during parse operation. This class extends
 * DefaultHandler so that it may be used by the SAX parser.
 */
public class AeXMLParserErrorHandler extends DefaultHandler
{
   /** Message template to log parse warnings/errors */
   protected final static String sMsgTemplate = AeMessages.getString("AeXMLParserErrorHandler.0"); //$NON-NLS-1$

   /** Flag to indicate that parse warnings have occurred */
   private boolean mParseWarnings;
   
   /** Parse warnings exception */
   private Exception mErrorException;
   
   /** Flag to indicate if parse errors and warnings should be logged. */
   private boolean mLoggingEnabled;

   /**
    * Default Constructor.
    */
   public AeXMLParserErrorHandler()
   {
      this(true);
   }

   /**
    * Constructor which takes as input a flag indicating if logging of errors is to be performed.
    */
   public AeXMLParserErrorHandler(boolean aLoggingEnabled)
   {
      mLoggingEnabled = aLoggingEnabled;
   }

   /**
    * Utility method to log any problems which occur while parsing a file.
    * @param aException exception which was thrown
    * @param aType severity of problem
    */
   protected void logError(SAXParseException aException, String aType)
   {
      setParseException(aException);
      
      if (isLoggingEnabled())
      {
         MessageFormat mf = new MessageFormat(sMsgTemplate);
         String lineNum = new Integer(aException.getLineNumber()).toString();
         String msg = mf.format(new Object[] {aType, lineNum, aException.getMessage()});

         System.out.println(msg);
      }
   }

   /**
    * Required implementation of warning handler.
    * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
    */
   public void warning(SAXParseException aException)
   {
      setParseWarnings(true);
      logError(aException, AeMessages.getString("AeXMLParserErrorHandler.1")); //$NON-NLS-1$
   }

   /**
    * Required implementation of error handler.
    * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
    */
   public void error(SAXParseException aException)
   {
      setParseWarnings(true);
      logError(aException, AeMessages.getString("AeXMLParserErrorHandler.2")); //$NON-NLS-1$
   }

   /**
    * Required implementation of fatal error handler.
    * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
    */
   public void fatalError(SAXParseException aException) throws SAXException
   {
      logError(aException, AeMessages.getString("AeXMLParserErrorHandler.3")); //$NON-NLS-1$
      throw aException;
   }
   
   /**
    * Returns flag indicating if any warnings occurred during parse. 
    */
   public boolean hasParseWarnings()
   {
      return mParseWarnings;
   }

   /**
    * Allows ability to reset warnings indicator used to monitor parse errors.
    */
   public void resetParseWarnings()
   {
      setParseWarnings(false);
      setParseException(null);
   }

   /**
    * Allow ability to set or reset the error handler to indicate if it has warnings. 
    * @param aWarnings True if warnings exist False if not
    */
   protected void setParseWarnings(boolean aWarnings)
   {
      mParseWarnings = aWarnings;
   }
   
   /**
    * Returns flag indicating if logging is enabled for parse error handler.
    */
   protected boolean isLoggingEnabled()
   {
      return mLoggingEnabled;
   }
   
   /**
    * Gets the parse warning exception.
    * @return Returns the parse warning exception.
    */
   public Exception getParseException()
   {
      return mErrorException;
   }
   
   /**
    * Sets the parse warning error exception.
    * @param aException The error exception.
    */
   private void setParseException(Exception aException)
   {
      mErrorException = aException;
   }
}
