// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/bpr/IAeBpr.java,v 1.3 2006/08/04 17:57:53 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.bpr;

import java.io.InputStream;
import java.util.Collection;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource;
import org.w3c.dom.Document;

import bpelg.services.deploy.types.catalog.Catalog;
import bpelg.services.deploy.types.pdd.Pdd;

/**
 * Interface for wrapping the deployment of a BPR. 
 */
public interface IAeBpr
{
   /**
    * Returns the deployment context for this bpr.
    */
   public IAeDeploymentContext getDeploymentContext();
   
   /**
    * Return the short name of the bpr deployment.
    */
   public String getBprFileName();
   
   /**
    * Return a collection of names for the pdd resources or
    * an empty collection if none are found.  
    */
   public Collection<AePddResource> getPddResources();
   
   /**
    * Return a deployment source for the given pdd resource name.
    * @param aPdd The pdd resource.
    * @throws AeException
    */
   public IAeDeploymentSource getDeploymentSource(Pdd aPdd) throws AeException;   

   /**
    * Return the catalog for this BPR.
    * @throws AeException
    */
   public Catalog getCatalogDocument() throws AeException;
   
   /**
    * Returns true if the given resource exists within the BPR.
    * @param aResourceName A BPR resource.
    */
   public boolean exists( String aResourceName );
   
   /**
    * Return the named resource as a document object.
    * @param aResourceName
    * @throws AeException
    */
   public Document getResourceAsDocument( String aResourceName ) throws AeException;
   
   /**
    * Returns a names resource from the BPR
    * @param aResourceName
    */
   public InputStream getResourceAsStream(String aResourceName);
   
}
