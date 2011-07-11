// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/IAeWSResourceProblemHandler.java,v 1.3 2008/01/03 22:00:59 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.wsresource.validation;

import javax.xml.namespace.QName;

import org.xml.sax.SAXParseException;


/**
 * Interface that must be implemented by web service resource problem
 * handlers.  The problem handler is called when a web service resource
 * validator identifies a problem.
 */
public interface IAeWSResourceProblemHandler
{
   /**
    * Called to report a problem with a web service resource.  The
    * problem includes a human readable problem message and an xpath 
    * that identifies the XML node in the web service resource that
    * caused the problem.
    * 
    * @param aProblemId
    * @param aSeverity
    * @param aProblemMessage
    * @param aXPath
    */
   public void reportProblem(QName aProblemId, int aSeverity, String aProblemMessage, String aXPath);
   
   /** 
    * Called to report a parse problem with a web service resource.
    * The parse problem includes a <code>Exception</code> and
    * the severity of the problem. 
    * 
    * @param aException - 
    * @param aSeverity
    */
   public void reportParseProblem(SAXParseException aException, int aSeverity);
   
   /**
    * Called to report any other fatal error during Resource validation, which doesn't
    * fall into a parse problem or rules validation issue. 
    * 
    * @param aException
    */
   public void reportFatalError(Exception aException);
   
}
