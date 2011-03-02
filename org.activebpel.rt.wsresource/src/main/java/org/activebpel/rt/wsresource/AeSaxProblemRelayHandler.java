// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/AeSaxProblemRelayHandler.java,v 1.1 2008/01/03 22:01:20 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.wsresource;

import org.activebpel.rt.wsresource.validation.IAeWSResourceProblemHandler;
import org.activebpel.rt.wsresource.validation.IAeWSResourceValidationPreferences;
import org.activebpel.rt.xml.AeXMLParserErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This class is a proxy between the IAeWSResourceProblemHandler and an 
 * AeXMLParserErrorHandler to allow the SAX Parser to report parse problems
 * through the IAeWSResourceProblemHandler instance.
 */
public class AeSaxProblemRelayHandler extends AeXMLParserErrorHandler
{
   /** instance of the WSResource problem handler used for reporting rule violations */
   private IAeWSResourceProblemHandler mProblem;
   
   /**
    * C'tor
    * 
    * @param aProblem
    */
   public AeSaxProblemRelayHandler(IAeWSResourceProblemHandler aProblem)
   {
      setProblem(aProblem);
   }

   /**
    * 
    * @see org.activebpel.rt.xml.AeXMLParserErrorHandler#error(org.xml.sax.SAXParseException)
    */
   public void error(SAXParseException aException)
   {
      getProblemHandler().reportParseProblem(aException, IAeWSResourceValidationPreferences.SEVERITY_ERROR);
   }

   /**
    * 
    * @see org.activebpel.rt.xml.AeXMLParserErrorHandler#fatalError(org.xml.sax.SAXParseException)
    */
   public void fatalError(SAXParseException aException) throws SAXException
   {
      getProblemHandler().reportParseProblem(aException, IAeWSResourceValidationPreferences.SEVERITY_ERROR);
   }

   /**
    * 
    * @see org.activebpel.rt.xml.AeXMLParserErrorHandler#warning(org.xml.sax.SAXParseException)
    */
   public void warning(SAXParseException aException)
   {
      getProblemHandler().reportParseProblem(aException, IAeWSResourceValidationPreferences.SEVERITY_WARNING);
   }

   /**
    * 
    * @return return the IAeWSResourceProblemHandler this class was constructed with.
    */
   protected IAeWSResourceProblemHandler getProblemHandler()
   {
      return mProblem;
   }

   /**
    * Set the IAeWSResourceProblemHandler
    * 
    * @param aProblem
    */
   protected void setProblem(IAeWSResourceProblemHandler aProblem)
   {
      mProblem = aProblem;
   }
   
}
