// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AeWsdlCircRefValidator.java,v 1.13 2006/09/26 18:17:30 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.catalog.AeCatalogMappings;
import org.activebpel.rt.bpel.server.catalog.IAeCatalog;
import org.activebpel.rt.bpel.server.catalog.IAeCatalogMapping;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Validates that there are no circular references in the 
 * wsdl imports.
 */
public class AeWsdlCircRefValidator implements IAePredeploymentValidator
{
   /** Error message template. */
   private String ERROR_MSG = 
      AeMessages.getString("AeWsdlCircRefValidator.0"); //$NON-NLS-1$
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.validate.IAePredeploymentValidator#validate(org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr, org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter)
    */
   public void validate(IAeBpr aBprFile, IAeBaseErrorReporter aReporter)
      throws AeException
   {
      AeCatalogMappings catalog = new AeCatalogMappings( aBprFile, IAeCatalog.KEEP_EXISTING_RESOURCE );
      for( Iterator iter = catalog.getResources().values().iterator(); iter.hasNext(); )
      {
         IAeCatalogMapping mapping = (IAeCatalogMapping) iter.next();
         Document wsdlDom = mapping.getDocument();

         HashSet imports = new HashSet();
         imports.add(mapping.getLocationHint());
         try
         {
            searchForCircularRefs( wsdlDom, catalog.getResources(), imports, aBprFile );
         }
         catch (AeCircularRefException e)
         {
            String[] args = {e.getCircularRef(), mapping.getLocationHint(), aBprFile.getBprFileName().toString()};
            aReporter.addError(ERROR_MSG, args, null);
            return;
         }
      }
   }
     
   /**
    * Recursive search through the wsdl imports to look for
    * circular references.
    * @param aWsdlDom The current wsdl dom.
    * @param aWsdlCatalog The wsdlCatalog.xml mappings.
    * @param aImports The set of current imports.
    * @param aBpr The deployment bpr.
    * @throws AeCircularRefException 
    * @throws AeException
    */
   protected void searchForCircularRefs( Document aWsdlDom, 
      Map aWsdlCatalog, Set aImports, IAeBpr aBpr )
         throws AeCircularRefException, AeException    
   {
      NodeList imports = aWsdlDom.getElementsByTagNameNS( aWsdlDom.getDocumentElement().getNamespaceURI(), IMPORT_ELEMENT );
      for( int i = 0; i < imports.getLength(); i++ )
      {
         // not checking schema imports  
         Element importElement = (Element)imports.item(i);
         
         if( isWsdlImport( importElement ) )
         {
            String location = importElement.getAttribute( LOCATION_ATTR );

            if( aImports.contains( location ) )
            {
               throw new AeCircularRefException(location);
            }

            aImports.add(location);
         
            String key = AeCatalogMappings.makeKey(location);
            if( aWsdlCatalog.containsKey( key ) )
            {
               IAeCatalogMapping mapping = (IAeCatalogMapping)aWsdlCatalog.get( key );
               Document importedWsdl = mapping.getDocument();
               
               // this condition is possible if the wsdl is in the
               // wsdlCatalog - but not present - the error should be 
               // picked up by the wsdl catalog validator
               if( importedWsdl != null )
               {
                  searchForCircularRefs(importedWsdl, aWsdlCatalog, aImports, aBpr);
               }
               aImports.remove(location);
            }
         }
      }
   }
   
   /**
    * Returns true if the import element's parent is the
    * definitions element (ignores schema imports).
    * @param aImportElement
    */
   protected boolean isWsdlImport( Element aImportElement )
   {
      Element parent = (Element)aImportElement.getParentNode();
      return DEFINITIONS_ELEMENT.equals(parent.getTagName());
   }
   
   /**
    * Exception for notification of circular reference
    * in wsdl imports.
    */
   static class AeCircularRefException extends Exception
   {
      /** The offending import location */
      private String mCircularRef;

      /**
       * Constructor.
       * @param aBadRef The offending import location.
       */
      public AeCircularRefException(String aBadRef)
      {
         mCircularRef = aBadRef;
      }
      
      /**
       * Accessor for the circular ref.
       */
      public String getCircularRef()
      {
         return mCircularRef;
      }
   }
}
