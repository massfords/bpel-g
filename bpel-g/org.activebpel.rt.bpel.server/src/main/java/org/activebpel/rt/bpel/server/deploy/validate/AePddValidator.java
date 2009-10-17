// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AePddValidator.java,v 1.9 2007/12/20 19:12:52 vvelusamy Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import java.io.Reader;
import java.text.MessageFormat;
import java.util.HashSet;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.addressing.AeEndpointReferenceSourceType;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentSchemas;
import org.activebpel.rt.bpel.server.deploy.IAePddXmlConstants;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.activebpel.rt.xml.AeXMLParserErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *  Schema+ validation of pdd files.
 */
public class AePddValidator extends AeAbstractPddIterator
{
   /**
    * This class implements an <code>IAePddValidationErrorHandler</code> that will delegate the
    * reporting of the erorrs and warnings to a base error reporter.
    */
   private class AePddErrorHandler implements IAeResourceValidationErrorHandler
   {
      /** The PDD info. */
      private AePddInfo mPddInfo;
      /** The error reporter to use when an error is handled. */
      private IAeBaseErrorReporter mReporter;

      /**
       * Constructor.
       * 
       * @param aReporter
       */
      public AePddErrorHandler(AePddInfo aPddInfo, IAeBaseErrorReporter aReporter)
      {
         mPddInfo = aPddInfo;
         mReporter = aReporter;
      }

      /**
       * Reports an error to the error reporter.
       * 
       * @param aMessage
       */
      protected void reportError(String aMessage)
      {
         Object [] params = { mPddInfo.getName(), aMessage };
         mReporter.addError(AeMessages.getString("AePddValidator.REPORT_ERROR_FORMAT_SANS_LINENUMBER"), params, null); //$NON-NLS-1$
      }

      /**
       * Reports an error to the error reporter (includes the error's line number).
       * 
       * @param aMessage
       * @param aLineNumber
       */
      protected void reportError(String aMessage, int aLineNumber)
      {
         Object [] params = { mPddInfo.getName(), aMessage, new Integer(aLineNumber) };
         mReporter.addError(AeMessages.getString("AePddValidator.REPORT_ERROR_FORMAT_WITH_LINENUMBER"), params, null); //$NON-NLS-1$
      }

      /**
       * @see org.activebpel.rt.bpel.server.deploy.validate.IAeResourceValidationErrorHandler#fatalError(java.lang.String)
       */
      public void fatalError(String aMessage)
      {
         reportError(aMessage);
      }
      
      /**
       * @see org.activebpel.rt.bpel.server.deploy.validate.IAeResourceValidationErrorHandler#parseError(java.lang.String, int)
       */
      public void parseError(String aMessage, int aLineNumber)
      {
         reportError(aMessage);
      }
      
      /**
       * @see org.activebpel.rt.bpel.server.deploy.validate.IAeResourceValidationErrorHandler#parseFatalError(java.lang.String, int)
       */
      public void parseFatalError(String aMessage, int aLineNumber)
      {
         reportError(aMessage);
      }
      
      /**
       * @see org.activebpel.rt.bpel.server.deploy.validate.IAeResourceValidationErrorHandler#parseWarning(java.lang.String, int)
       */
      public void parseWarning(String aMessage, int aLineNumber)
      {
         Object [] params = { mPddInfo.getName(), aMessage, new Integer(aLineNumber) };
         mReporter.addWarning(AeMessages.getString("AePddValidator.REPORT_ERROR_FORMAT_WITH_LINENUMBER"), params, null); //$NON-NLS-1$
      }

      /**
       * @see org.activebpel.rt.bpel.server.deploy.validate.IAeResourceValidationErrorHandler#contentError(java.lang.String, org.w3c.dom.Node)
       */
      public void contentError(String aMessage, Node aNode)
      {
         reportError(aMessage);
      }

      /**
       * @see org.activebpel.rt.bpel.server.deploy.validate.IAeResourceValidationErrorHandler#contentWarning(java.lang.String, org.w3c.dom.Node)
       */
      public void contentWarning(String aMessage, Node aNode)
      {
         Object [] params = { mPddInfo.getName(), aMessage };
         mReporter.addWarning(AeMessages.getString("AePddValidator.REPORT_ERROR_FORMAT_SANS_LINENUMBER"), params, null); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.validate.AeAbstractPddIterator#validateImpl(org.activebpel.rt.bpel.server.deploy.validate.AePddInfo, org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr, org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter)
    */
   protected void validateImpl(AePddInfo aPddInfo, IAeBpr aBprFile, IAeBaseErrorReporter aReporter) throws AeException
   {
      AePddErrorHandler handler = new AePddErrorHandler(aPddInfo, aReporter);
      validatePdd(aPddInfo.getDoc(), handler);
   }

   /**
    * This static method can be called whenever a PDD Document needs to be validated.  The caller
    * should provide their own implementation of a resource validation error handler.
    * 
    * @param aReader
    * @param aHandler
    */
   public static void validatePdd(Reader aReader, IAeResourceValidationErrorHandler aHandler)
   {
      try
      {
         AeXMLParserErrorHandler saxHandler = new AeSaxErrorRelayHandler(aHandler);

         AeXMLParserBase parser = new AeXMLParserBase(true, true);
         parser.setErrorHandler(saxHandler);
         Document doc = parser.loadDocument(aReader, AeDeploymentSchemas.getPddSchemas());

         doAdditionalPddValidation(doc, aHandler);
      }
      catch (AeException e)
      {
         aHandler.fatalError(e.getLocalizedMessage());
      }
   }

   /**
    * This static method can be called whenever a PDD Document needs to be validated.  The caller
    * should provide their own implementation of a resource validation error handler.
    * 
    * @param aDocument
    * @param aHandler
    */
   public static void validatePdd(Document aDocument, IAeResourceValidationErrorHandler aHandler)
   {
      try
      {
         AeXMLParserErrorHandler saxHandler = new AeSaxErrorRelayHandler(aHandler);

         AeXMLParserBase parser = new AeXMLParserBase(true, true);
         parser.setErrorHandler(saxHandler);
         parser.validateDocument(aDocument, AeDeploymentSchemas.getPddSchemas());

         doAdditionalPddValidation(aDocument, aHandler);
      }
      catch (AeException e)
      {
         aHandler.fatalError(e.getLocalizedMessage());
      }
   }

   /**
    * Does some additional PDD validation checks.  This checks for problems that can not be caught
    * by the schema.
    * 
    * @param aDocument
    * @param aHandler
    */
   private static void doAdditionalPddValidation(Document aDocument, IAeResourceValidationErrorHandler aHandler)
   {
      try
      {
         checkPartnerRoles(aDocument, aHandler);
         checkPartnerLinks(aDocument, aHandler);
         checkEndpointRef(aDocument, aHandler, IAeConstants.WSA_NAMESPACE_URI);
         checkEndpointRef(aDocument, aHandler, IAeConstants.WSA_NAMESPACE_URI_2004_03);
         checkEndpointRef(aDocument, aHandler, IAeConstants.WSA_NAMESPACE_URI_2004_08);         
      }
      catch (Exception e)
      {
         String msg = MessageFormat.format(AeMessages.getString("AePddValidator.ERROR_DURING_STATIC_VALIDATION"), new Object[] { e.getLocalizedMessage() } ); //$NON-NLS-1$
         aHandler.fatalError(msg);
      }
   }

   /**
    * Checks to make sure that the user hasn't left the "FILL_IN_NAMESPACE" value
    * in the generated endpoint.
    * 
    * @param aDocument
    * @param aHandler
    */
   private static void checkEndpointRef(Document aDocument, IAeResourceValidationErrorHandler aHandler, String aWsaNamespace)
   {
      // Do a bit of static validation on the wsa:EndpointReference tags, if any.
      NodeList nl = aDocument.getDocumentElement().getElementsByTagNameNS(
            aWsaNamespace,"EndpointReference"); //$NON-NLS-1$
      for (int i = 0; i < nl.getLength(); i++)
      {
         // Do a bit of static analysis on the endpoint reference definition.
         Element refNode = (Element) nl.item(i);
         String attribValue = refNode.getAttribute("xmlns:s"); //$NON-NLS-1$
         if ("FILL_IN_NAMESPACE".equals(attribValue)) //$NON-NLS-1$
         {
            String msg = AeMessages.getString("AePddValidator.UNCONFIGURED_ENDPOINT_REF_ERROR"); //$NON-NLS-1$
            aHandler.contentWarning(msg, refNode);
         }
      }
   }

   /**
    * Ensures that each partner link has a myRole or a partnerRole or both.  And that there isn't
    * more than one entry per partnelink name.
    * 
    * @param aDocument
    * @param aHandler
    */
   private static void checkPartnerLinks(Document aDocument, IAeResourceValidationErrorHandler aHandler)
   {
      HashSet names = new HashSet();
      // Make sure that every partner link has either a partner role, or a my role, or both.
      NodeList nl = aDocument.getDocumentElement().getElementsByTagNameNS(aDocument.getDocumentElement().getNamespaceURI(), "partnerLink"); //$NON-NLS-1$
      for (int i = 0; i < nl.getLength(); i++)
      {
         Element node = (Element) nl.item(i);
         
         // check for duplicates
         String name = node.getAttribute("name") + ":" + node.getAttribute("location"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
         if(AeUtil.notNullOrEmpty(name))
         {
            if(names.contains(name))
              aHandler.contentError(AeMessages.format("AePddValidator.DUPLICATE_PARTNER_LINK_CONTENT_ERROR", name), node); //$NON-NLS-1$
            else
              names.add(name);
         }

         NodeList myRoleList = node.getElementsByTagNameNS(aDocument.getDocumentElement().getNamespaceURI(), "myRole"); //$NON-NLS-1$         
         NodeList partnerRoleList = node.getElementsByTagNameNS(aDocument.getDocumentElement().getNamespaceURI(),"partnerRole"); //$NON-NLS-1$
         // check that it has a myrole or partnerrole assignment
         if (myRoleList.getLength() == 0 && partnerRoleList.getLength() == 0)
            aHandler.contentError(AeMessages.getString("AePddValidator.INVALID_PARTNER_LINK_CONTENT_ERROR"), node); //$NON-NLS-1$
      }

      // check for duplicate my role services
      HashSet serviceNames = new HashSet();      
      NodeList myRoleList =aDocument.getDocumentElement().getElementsByTagNameNS(aDocument.getDocumentElement().getNamespaceURI(), "myRole"); //$NON-NLS-1$
      for (int j = 0; j < myRoleList.getLength(); j++)
      {
         Element myRoleEle = (Element) myRoleList.item(j);
         if (AeUtil.notNullOrEmpty( myRoleEle.getAttribute("service") )) //$NON-NLS-1$
         {
            if (serviceNames.contains( myRoleEle.getAttribute("service") )) //$NON-NLS-1$
            {
               aHandler.contentError(AeMessages.format("AePddValidator.DUPLICATE_MYROLE_SERVICE_NAME_ERROR", myRoleEle.getAttribute("service")), myRoleEle); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
               serviceNames.add( myRoleEle.getAttribute("service") ); //$NON-NLS-1$
            }
         }
      }

   }

   /**
    * Runs a few checks on the partner roles that we can't incorporate into the
    * schema. These include the following:
    * 1. if the attribute endpointReference="static" then there must be an endpoint defined
    * 2. if the attribute endpointReference!="static" then there must NOT be an endpoint defined
    * 3. if the attributes customInvokerUri and invokeHandler are both present, generate
    *    a warning since customInvokerUri is deprecated and ignored when invokeHandler 
    *    is present.
    * 4. if customInvokerUri is present and invokeHandler is missing, then add warning
    *    that they should switch to invokeHandler. 
    * 
    * @param aDocument
    * @param aHandler
    */
   private static void checkPartnerRoles(Document aDocument, IAeResourceValidationErrorHandler aHandler)
   {
      // Get a list of the partnerRole nodes to check if they are "static".
      // If they ARE, then they need a wsa:EndpointReference node.
      NodeList nl = aDocument.getDocumentElement().getElementsByTagNameNS(aDocument.getDocumentElement().getNamespaceURI(), "partnerRole"); //$NON-NLS-1$
      for (int i = 0; i < nl.getLength(); i++)
      {
         Element prElem = (Element) nl.item(i);
         AeEndpointReferenceSourceType type = AeEndpointReferenceSourceType.getByName(prElem.getAttribute("endpointReference")); //$NON-NLS-1$
         if (type == AeEndpointReferenceSourceType.STATIC)
         {
            int numChildren = 0;
            boolean hasEndpointRef = false;
            NodeList nl2 = prElem.getChildNodes();
            for (int j = 0; j < nl2.getLength(); j++)
            {
               Node node = nl2.item(j);
               if (node instanceof Element)
               {
                  numChildren++;
                  if ("EndpointReference".equals(node.getLocalName()) && //$NON-NLS-1$
                      (IAeConstants.WSA_NAMESPACE_URI.equals(node.getNamespaceURI()) ||
                       IAeConstants.WSA_NAMESPACE_URI_2004_03.equals(node.getNamespaceURI()) ||
                       IAeConstants.WSA_NAMESPACE_URI_2005_08.equals(node.getNamespaceURI()) ||
                       IAeConstants.WSA_NAMESPACE_URI_2004_08.equals(node.getNamespaceURI())))
                  {
                     hasEndpointRef = true;
                  }
               }
            }
            if (numChildren != 1 || !hasEndpointRef)
            {
               String msg = AeMessages.getString("AePddValidator.INVALID_STATIC_ENDPOINT_CONTENT_ERROR"); //$NON-NLS-1$
               aHandler.contentError(msg, prElem);
            }
         }
         else
         {
            NodeList nl2 = prElem.getChildNodes();
            if (nl2.getLength() > 0)
            {
               String msg = AeMessages.getString("AePddValidator.INVALID_NONSTATIC_ENDPOINT_CONTENT_ERROR"); //$NON-NLS-1$
               aHandler.contentError(msg, prElem);
            }
         }

         // if they have both invoke handler attributes, then issue a warning that we'll
         // only use the new one
         if (AeUtil.notNullOrEmpty(prElem.getAttribute(IAePddXmlConstants.ATT_CUSTOM_INVOKER)) && 
               AeUtil.notNullOrEmpty(prElem.getAttribute(IAePddXmlConstants.ATT_INVOKE_HANDLER)))
         {
            String msg = AeMessages.getString("AePddValidator.DEPRECATED_PARTNER_ROLE_CONTENT_ERROR"); //$NON-NLS-1$
            aHandler.contentWarning(msg, prElem);
         }
         // if they have the old one alone, then issue a deprecated warning
         else if (AeUtil.notNullOrEmpty(prElem.getAttribute(IAePddXmlConstants.ATT_CUSTOM_INVOKER)))
         {
            String msg = AeMessages.getString("AePddValidator.DEPRECATED_INVOKER_CONTENT_ERROR"); //$NON-NLS-1$
            aHandler.contentWarning(msg, prElem);
         }
      }
   }
}
