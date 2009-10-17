//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/bpr/IAeBprAccessor.java,v 1.2 2006/07/18 20:05:33 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.bpr;

import java.io.InputStream;
import java.util.Collection;

import org.activebpel.rt.AeException;
import org.w3c.dom.Document;

/**
 * Represents the strategy for accessing resources within a bpr deployment.
 */
public interface IAeBprAccessor
{
   /** wsdlCatalog xml file location - this file name has been deprecated and people should use catalog.xml instead */
   public static final String WSDL_CATALOG = "META-INF/wsdlCatalog.xml"; //$NON-NLS-1$
   /** Catalog xml file location - the catalog file has resource names and locations in the bpr file which should be added to the global catalog */
   public static final String CATALOG = "META-INF/catalog.xml"; //$NON-NLS-1$
   
   /**
    * Initialize the bpr strategy. 
    * @throws AeException
    */
   public void init() throws AeException;
   
   /**
    * Return true if the underlying deployment is a wsr deployment.
    */
   public boolean isWsddDeployment();
   
   /**
    * Return the name of the wsdd resource.
    */
   public String getWsddResource();
   
   /**
    * Return the collection of pdef resource names.
    */
   public Collection getPdefResources();
   
   /**
    * Return the collection of pdd resource names.
    */
   public Collection getPddResources();
   
   /**
    * Return the catalog xml file <code>Document</code>.
    * @throws AeException
    */
   public Document getCatalogDocument() throws AeException;
   
   /**
    * Return true if the bpr has access to this resource.
    * @param aResourceName
    */
   public boolean hasResource( String aResourceName );
   
   /**
    * Get the <code>InputStream</code> to the named resource.
    * @param aResourceName
    */
   public InputStream getResourceAsStream( String aResourceName );
   
   /**
    * Access the named resource as a <code>Document</code>.
    * @param aResourceName
    * @throws AeException
    */
   public Document getResourceAsDocument( String aResourceName ) throws AeException;
}
