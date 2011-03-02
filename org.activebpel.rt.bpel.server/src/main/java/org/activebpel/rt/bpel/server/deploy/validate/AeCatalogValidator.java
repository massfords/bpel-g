// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AeCatalogValidator.java,v 1.1 2006/07/18 20:05:32 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentSchemas;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.activebpel.rt.xml.AeXMLParserErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Validates that all of the wsdl entries in the catalog.xml (previously wsdlCatalog.xml) file
 * are present in the bpr file.
 */
public class AeCatalogValidator implements IAePredeploymentValidator
{
   /**
    * @see org.activebpel.rt.bpel.server.deploy.validate.IAePredeploymentValidator#validate(org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr, org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter)
    */
   public void validate(IAeBpr aBprFile, IAeBaseErrorReporter aReporter)
   throws AeException
   {
      Document catalogDoc = aBprFile.getCatalogDocument();
      if( catalogDoc == null )
      {
         aReporter.addWarning( AeMessages.getString("AeCatalogValidator.1"), new String[]{aBprFile.getBprFileName()}, null);  //$NON-NLS-1$
      }
      else
      {
         validateCatalogDocument(catalogDoc, aReporter);
         validate(aBprFile, catalogDoc, aReporter, WSDL_ENTRY_ELEMENT );
         validate(aBprFile, catalogDoc, aReporter, SCHEMA_ENTRY_ELEMENT );
         validate(aBprFile, catalogDoc, aReporter, OTHER_ENTRY_ELEMENT );
      }
   }

   /**
    * This is called whenever a Catalog Document needs to be schema validated.
    * 
    * @param aDocument
    * @param aReporter
    */
   protected void validateCatalogDocument(Document aDocument, IAeBaseErrorReporter aReporter)
   {
      // if the document has a namespace then validate the document against the valid catalog schemas
      // otherwise assume it is a legacy catalog and just use the elements without validating the document
      if(AeUtil.notNullOrEmpty(aDocument.getDocumentElement().getNamespaceURI()))
      {
         IAeResourceValidationErrorHandler handler = new AeResourceValidationErrorHandler(AeMessages.getString("AeCatalogValidator.2"), aReporter); //$NON-NLS-1$
         AeXMLParserErrorHandler saxHandler = new AeSaxErrorRelayHandler(handler);
         try
         {
            AeXMLParserBase parser = new AeXMLParserBase(true, true);
            parser.setErrorHandler(saxHandler);
            parser.validateDocument(aDocument, AeDeploymentSchemas.getCatalogSchemas());
         }
         catch (AeException e)
         {
            handler.fatalError(e.getLocalizedMessage());
         }
      }
   }

   /**
    * Validates the classpath attributes for the entries with the specified tag name.
    *
    * @param aBprFile
    * @param aCatalogDoc
    * @param aReporter
    * @param aTagName
    * @throws AeException
    */
   protected void validate(IAeBpr aBprFile, Document aCatalogDoc, IAeBaseErrorReporter aReporter, String aTagName)
   throws AeException
   {
      NodeList entries = aCatalogDoc.getDocumentElement().getElementsByTagNameNS("*", aTagName); //$NON-NLS-1$
      for( int i = 0; i < entries.getLength(); i++ )
      {
         Element entry = (Element)entries.item(i);
         String classpath = entry.getAttribute( CLASSPATH_ATTR );
         if( !aBprFile.exists(classpath) )
         {
            aReporter.addError( AeMessages.getString("AeCatalogValidator.0"), new String[]{classpath, aBprFile.getBprFileName().toString()}, null); //$NON-NLS-1$
         }
      }
   }
}
