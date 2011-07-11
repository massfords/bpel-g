// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/catalog/IAeCatalog.java,v 1.3 2008/01/11 19:40:17 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.catalog;

import org.activebpel.rt.bpel.server.catalog.resource.IAeResourceCache;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentId;
import org.activebpel.rt.wsdl.IAeWSDLProvider;

/**
 * Global Catalog interface.
 * 
 * All access to resources should be via this interface.
 */
public interface IAeCatalog extends IAeWSDLProvider
{
   /** constants for replace wsdl flag */
   public static final boolean KEEP_EXISTING_RESOURCE = false;
   
   /**
    * Adds an array of catalog mapping entries.
    * @param aDeploymentId the id for the deployment or null if none to be recorded
    * @param aMappings the mappings to add
    * @param aReplaceFlag If this flag is set to true any previous wsdl entries
    * mapped to the same location hint will be over written.
    */
   public void addCatalogEntries( IAeDeploymentId aDeploymentId, IAeCatalogMapping[] aMappings, boolean aReplaceFlag );
   
   /**
    * Get a mapping to our in-memory map of catalog items. or null if it doesn't exist
    * @param aLocation
    * @return IAeCatalogMapping if it exists, null if not
    */
   public IAeCatalogMapping getMappingForLocation(String aLocation);

   /**
    * Remove the deployment context.
    * @param aDeploymentId
    */
   public void remove( IAeDeploymentId aDeploymentId );
   
   /**
    * Accessor for the resource cache.
    */
   public IAeResourceCache getResourceCache();
   
   /**
    * Add the catalog listener.
    * @param aListener
    */
   public void addCatalogListener( IAeCatalogListener aListener );
   
   /**
    * Remove the catalog listner.
    * @param aListener
    */
   public void removeCatalogListener( IAeCatalogListener aListener );
   
   /**
    * clears the catalog 
    */
   public void clear();
}
