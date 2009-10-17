//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/bpr/AeAbstractBprStrategy.java,v 1.2 2006/07/18 20:05:33 ckeller Exp $
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
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Document;

/**
 * Base class for <code>IAeBprStrategy</code> implementations.  Provides
 * common accessor methods for bpr file resources.
 */
public abstract class AeAbstractBprStrategy implements IAeBprAccessor
{
   /** The deployment context. */
   private IAeDeploymentContext mDeploymentContext;
   /** The wsdd resource for Axis deployments. */
   private String mWsddResource;
   /** The pdd resource names. */
   private Collection mPddResources;
   /** The pdef resource names. */
   private Collection mPdefResources;
   /** XML parser. */
   private AeXMLParserBase mParser;
   
   /**
    * Constructor.
    * @param aDeploymentContext
    */
   protected AeAbstractBprStrategy( IAeDeploymentContext aDeploymentContext )
   {
      mDeploymentContext = aDeploymentContext;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBprAccessor#getWsddResource()
    */
   public String getWsddResource()
   {
      return mWsddResource;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBprAccessor#isWsddDeployment()
    */
   public boolean isWsddDeployment()
   {
      return getWsddResource() != null;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBprAccessor#getPddResources()
    */
   public Collection getPddResources()
   {
      return mPddResources;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBprAccessor#getPdefResources()
    */
   public Collection getPdefResources()
   {
      return mPdefResources;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBprAccessor#getResourceAsStream(java.lang.String)
    */
   public InputStream getResourceAsStream(String aResourceName)
   {
      return getDeploymentContext().getResourceAsStream(aResourceName);
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBprAccessor#getCatalogDocument()
    */
   public Document getCatalogDocument() throws AeException
   {
      Document catalog = getResourceAsDocument(CATALOG);
      if(catalog == null)
         catalog = getResourceAsDocument( WSDL_CATALOG );
      return catalog;
   }
   
   /**
    * @param aPddResources The pddResources to set.
    */
   protected void setPddResources(Collection aPddResources)
   {
      mPddResources = aPddResources;
   }

   /**
    * @param aPdefResources The pdefResources to set.
    */
   protected void setPdefResources(Collection aPdefResources)
   {
      mPdefResources = aPdefResources;
   }

   /**
    * @param aWsddResource The wsddResource to set.
    */
   protected void setWsddResource(String aWsddResource)
   {
      mWsddResource = aWsddResource;
   }

   /**
    * @return Returns the deploymentContext.
    */
   protected IAeDeploymentContext getDeploymentContext()
   {
      return mDeploymentContext;
   }
   
   /**
    * Accessor for the XML parser.
    */
   protected AeXMLParserBase getParser()
   {
      if( mParser == null )
      {
         mParser = new AeXMLParserBase();
         mParser.setValidating(false);
         mParser.setNamespaceAware(true);
      }
      return mParser;
   }
}
