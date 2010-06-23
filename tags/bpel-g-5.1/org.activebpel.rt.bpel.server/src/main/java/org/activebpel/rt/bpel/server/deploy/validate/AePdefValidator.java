//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AePdefValidator.java,v 1.3 2007/06/29 14:33:01 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import java.io.Reader;
import java.text.MessageFormat;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentSchemas;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.activebpel.rt.xml.AeXMLParserErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Schema+ validation of pdef files.
 */
public class AePdefValidator
{
   /**
    * This static method can be called whenever a PDEF Document needs to be validated. The caller should
    * provide their own implementation of a resource validation error handler.
    * 
    * @param aReader
    * @param aHandler
    */
   public static void validatePdef(Reader aReader, IAeResourceValidationErrorHandler aHandler)
   {
      try
      {
         AeXMLParserErrorHandler saxHandler = new AeSaxErrorRelayHandler(aHandler);

         AeXMLParserBase parser = new AeXMLParserBase(true, true);
         parser.setErrorHandler(saxHandler);
         Document doc = parser.loadDocument(aReader, AeDeploymentSchemas.getPdefSchemas());

         doAdditionalPdefValidation(doc, aHandler);
      }
      catch (AeException e)
      {
         aHandler.fatalError(e.getLocalizedMessage());
      }
   }

   /**
    * This static method can be called whenever a PDEF Document needs to be validated. The caller should
    * provide their own implementation of a resource validation error handler.
    * 
    * @param aDocument
    * @param aHandler
    */
   public static void validatePdef(Document aDocument, IAeResourceValidationErrorHandler aHandler)
   {
      try
      {
         AeXMLParserErrorHandler saxHandler = new AeSaxErrorRelayHandler(aHandler);

         AeXMLParserBase parser = new AeXMLParserBase(true, true);
         parser.setErrorHandler(saxHandler);
         parser.validateDocument(aDocument, AeDeploymentSchemas.getPdefSchemas());

         doAdditionalPdefValidation(aDocument, aHandler);
      }
      catch (AeException e)
      {
         aHandler.fatalError(e.getLocalizedMessage());
      }
   }

   /**
    * Does some additional PDEF validation checks. This checks for problems that can not be caught by the
    * schema.
    * 
    * @param aDocument
    * @param aHandler
    */
   private static void doAdditionalPdefValidation(Document aDocument, IAeResourceValidationErrorHandler aHandler)
   {
      try
      {
         // Get a list of the role elements and check that they contain a wsa:EndpointReference child element.
         NodeList nl = aDocument.getDocumentElement().getElementsByTagName("role"); //$NON-NLS-1$
         for (int i = 0; i < nl.getLength(); i++)
         {
            Element roleElem = (Element) nl.item(i);

            int numChildren = 0;
            boolean hasEndpointRef = false;
            NodeList nl2 = roleElem.getChildNodes();
            for (int j = 0; j < nl2.getLength(); j++)
            {
               Node node = nl2.item(j);
               if (node instanceof Element)
               {
                  numChildren++;
                  // TODO doesn't have to be wsa:!
                  if ("wsa:EndpointReference".equals(node.getNodeName())) //$NON-NLS-1$
                     hasEndpointRef = true;
               }
            }

            if (numChildren != 1 || !hasEndpointRef)
            {
               String msg = AeMessages.getString("AePdefValidator.INVALID_CONTENT_IN_ROLE_ERROR"); //$NON-NLS-1$
               aHandler.contentError(msg, roleElem);
            }
         }

         // Make sure that every partner link element contains a role child element.
         nl = aDocument.getDocumentElement().getElementsByTagName("partnerLinkType"); //$NON-NLS-1$
         for (int i = 0; i < nl.getLength(); i++)
         {
            Element node = (Element) nl.item(i);
            NodeList myRoleList = node.getElementsByTagName("role"); //$NON-NLS-1$
            if (myRoleList.getLength() == 0)
            {
               String msg = AeMessages.getString("AePdefValidator.INVALID_CONTENT_IN_PLT_ERROR"); //$NON-NLS-1$
               aHandler.contentError(msg, node);
            }
         }

         // Do a bit of static validation on the wsa:EndpointReference tags, if any.
         nl = aDocument.getDocumentElement().getElementsByTagName("wsa:EndpointReference"); //$NON-NLS-1$
         for (int i = 0; i < nl.getLength(); i++)
         {
            // Do a bit of static analysis on the endpoint reference definition.
            Element refNode = (Element) nl.item(i);
            String attribValue = refNode.getAttribute("xmlns:s"); //$NON-NLS-1$
            if ("FILL_IN_NAMESPACE".equals(attribValue)) //$NON-NLS-1$
            {
               // Problem marker!
               String msg = AeMessages.getString("AePdefValidator.ENDPOINT_REF_NS_NOT_CONFIGURED_WARNING"); //$NON-NLS-1$
               aHandler.contentWarning(msg, refNode);
            }
         }
      }
      catch (Exception e)
      {
         AeException.logError(e, e.getLocalizedMessage());
         String msg = MessageFormat.format(AeMessages.getString("AePdefValidator.VALIDATION_ERROR"), new Object[] { e.getLocalizedMessage() }); //$NON-NLS-1$
         aHandler.fatalError(msg);
      }
   }
}
