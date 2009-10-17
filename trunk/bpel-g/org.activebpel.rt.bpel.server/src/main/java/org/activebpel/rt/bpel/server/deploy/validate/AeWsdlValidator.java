// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AeWsdlValidator.java,v 1.14 2007/05/18 00:50:47 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.wsdl.Message;
import javax.wsdl.Part;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeWSDLException;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.catalog.AeCatalogMappings;
import org.activebpel.rt.bpel.server.catalog.IAeCatalog;
import org.activebpel.rt.bpel.server.catalog.IAeCatalogMapping;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;
import org.activebpel.rt.bpel.server.wsdl.AeResourceResolver;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.w3c.dom.Document;

/**
 * Validates that the wsdl def we're working with doesn't contain any undefined messages.
 * It was discovered that WSDL4J will not complain about finding undefined messages
 * within the wsdl. It seems better to fail the deployment than to have the system
 * work with an undefined message.
 */
public class AeWsdlValidator implements IAePredeploymentValidator
{
   /**
    * @see org.activebpel.rt.bpel.server.deploy.validate.IAePredeploymentValidator#validate(org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr, org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter)
    */
   public void validate(IAeBpr aBprFile, IAeBaseErrorReporter aReporter) throws AeException
   {
      AeResourceResolver resolver = createResolver(aBprFile);
      for (Iterator iter = getResourceMappings(aBprFile); iter.hasNext(); )
      {
         IAeCatalogMapping mapping = (IAeCatalogMapping) iter.next();
         if(mapping.isWsdlEntry())
         {
            try
            {
   	         AeBPELExtendedWSDLDef def = resolver.newInstance(mapping.getLocationHint());
   	         validateDef(def, aReporter);
            }
            catch( AeWSDLException we )
            {
               Object args[] = new Object[] {mapping.getLocationHint(), we.getLocalizedMessage()};
               aReporter.addError(AeMessages.getString("AeWsdlValidator.BAD_WSDL"), args, null); //$NON-NLS-1$
            }
         }
      }
   }

   /**
    * Gets an iterator over all of the wsdl location hints found in the BPR's catalog.
    * 
    * @param aBprFile
    * @throws AeException
    */
   protected Iterator getResourceMappings(IAeBpr aBprFile) throws AeException
   {
      Document wsdlCatalog = aBprFile.getCatalogDocument();
      if (wsdlCatalog != null)
      {
         AeCatalogMappings catalog = new AeCatalogMappings( aBprFile, IAeCatalog.KEEP_EXISTING_RESOURCE );
         return catalog.getResources().values().iterator();
      }

      return Collections.EMPTY_LIST.iterator();
   }

   
   /**
    * Create a wsdl resolver and populate it with entries from the bpr file.
    * 
    * @param aBprFile
    * @throws AeException
    */
   protected AeResourceResolver createResolver(IAeBpr aBprFile) throws AeException
   {
      // populate a wsdl resolver
      AeResourceResolver resolver = new AeResourceResolver();
      Document wsdlCatalog = aBprFile.getCatalogDocument();

      if (wsdlCatalog != null)
      {
         AeCatalogMappings catalog = new AeCatalogMappings( aBprFile, IAeCatalog.KEEP_EXISTING_RESOURCE );
         
         checkForMissingFiles(catalog, aBprFile);
         IAeCatalogMapping[] mappings = (IAeCatalogMapping[])catalog.getResources().values().toArray(new IAeCatalogMapping[catalog.getResources().values().size()]);
         resolver.addEntries(mappings, IAeCatalog.KEEP_EXISTING_RESOURCE);
      }
      return resolver;
   }

   /**
    * Walks the catalog and asserts that all of the referenced files are present 
    * in the bpr.
    * @param aCatalog
    * @param aBprFile
    * @throws AeWSDLException
    */
   protected void checkForMissingFiles(AeCatalogMappings aCatalog, IAeBpr aBprFile) throws AeWSDLException
   {
      Map missingFiles = aCatalog.getMissingResources();
      if (missingFiles.size() > 0)
      {
         StringBuffer buffer = new StringBuffer();
         String delim = ""; //$NON-NLS-1$
         for(Iterator iter = missingFiles.values().iterator(); iter.hasNext(); )
         {
            IAeCatalogMapping mapping = (IAeCatalogMapping)iter.next();
            buffer.append(delim);
            buffer.append(mapping.getLocationHint());
            delim = ","; //$NON-NLS-1$
         }
         String message = AeMessages.format("AeWsdlValidator.MISSING_RESOURCE", buffer.toString()); //$NON-NLS-1$
         throw new AeWSDLException(message);
      }
   }

   /**
    * Validate the deployment.
    */
   public void validateDef(AeBPELExtendedWSDLDef aDef, IAeBaseErrorReporter aReporter)
   {
      Map messageMap = aDef.getMessages();
      for (Iterator it = messageMap.values().iterator(); it.hasNext();)
      {
         Message message = (Message) it.next();
         if (message.isUndefined())
         {
            String[] args = {aDef.getLocationHint(), message.getQName().toString()};
            aReporter.addWarning(AeMessages.getString("AeWsdlValidator.0"), args, null); //$NON-NLS-1$
         }
         else
         {
            Map partMap = message.getParts();
            for (Iterator partIter = partMap.values().iterator(); partIter.hasNext(); )
            {
               Part part = (Part) partIter.next();
               if (AeUtil.isNullOrEmpty(part.getName()))
               {
                  String[] args = {aDef.getLocationHint(), message.getQName().toString()};
                  aReporter.addWarning(AeMessages.getString("AeWsdlValidator.PART_WITH_NO_NAME_ERROR"), args, null); //$NON-NLS-1$
               }
            }
         }
      }
   }
}
