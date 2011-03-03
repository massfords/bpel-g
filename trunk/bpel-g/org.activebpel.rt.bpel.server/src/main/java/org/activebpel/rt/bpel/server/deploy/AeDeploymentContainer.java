// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeDeploymentContainer.java,v 1.13 2008/02/17 21:38:45 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.deploy.bpr.AePddResource;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;
import org.w3c.dom.Document;

import bpelg.services.deploy.types.catalog.Catalog;
import bpelg.services.deploy.types.pdd.Pdd;

/**
 * Deployment container impl.
 */
public class AeDeploymentContainer implements IAeDeploymentContainer
{
   /** Deployment context. */
   protected IAeDeploymentContext mContext;
   /** Bpr file. */
   protected IAeBpr mBprFile;
   /** File name string - used for logging. */
   protected String mFileName;
   /** Deployment id url. */
   protected URL mUrlForId;
   /** Service deployment information */
   protected ArrayList mServiceInfo;

   /**
    * Constructor.
    * @param aContext
    * @param aBprFile
    * @param aUrl
    */
   public AeDeploymentContainer( IAeDeploymentContext aContext, IAeBpr aBprFile, 
      URL aUrl )
   {
      mContext = aContext;
      mBprFile = aBprFile;      
      mUrlForId = aUrl;
      if (aUrl != null)
         mFileName = aUrl.getFile().replace('\\', '/');
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#exists(java.lang.String)
    */
   public boolean exists(String aResourceName)
   {
      if (mBprFile == null)
         return false;

      return mBprFile.exists( aResourceName );
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#getBprFileName()
    */
   public String getBprFileName()
   {
      return mFileName;
   }
   
   /**
    * Sets the value of the bpr filename.  This is useful when uploading a bpr via the
    * console - at that point we have the local name of the file, but the temp file on
    * the server's disk will likely be different.
    * 
    * @param aBprFilename
    */
   public void setBprFilename(String aBprFilename)
   {
      mFileName = aBprFilename;
   }

   public IAeDeploymentSource getDeploymentSource(Pdd aPdd)
      throws AeException
   {
      if (mBprFile == null)
         return null;
      
      return mBprFile.getDeploymentSource(aPdd);
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#getResourceAsDocument(java.lang.String)
    */
   public Document getResourceAsDocument(String aResourceName) throws AeException
   {
      if (mBprFile == null)
         return null;
      
      return mBprFile.getResourceAsDocument( aResourceName );
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#getPddResources()
    */
   public Collection<AePddResource> getPddResources()
   {
      if (mBprFile == null)
         return Collections.EMPTY_SET;
      
      return mBprFile.getPddResources();
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#getCatalogDocument()
    */
   public Catalog getCatalogDocument() throws AeException
   {
      if (mBprFile == null)
         return null;
      
      return mBprFile.getCatalogDocument();
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getDeploymentId()
    */
   public IAeDeploymentId getDeploymentId()
   {
      return new AeDeploymentId(mUrlForId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getDeploymentLocation()
    */
   public URL getDeploymentLocation()
   {
      if (mContext == null)
         return null;
      
      return mContext.getDeploymentLocation();
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getResourceAsStream(java.lang.String)
    */
   public InputStream getResourceAsStream(String aResourceName)
   {
      if (mContext == null)
         return null;

      return mContext.getResourceAsStream( aResourceName );
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getResourceURL(java.lang.String)
    */
   public URL getResourceURL(String aResourceName)
   {
      if (mContext == null)
         return null;
      
      return mContext.getResourceURL( aResourceName );
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getShortName()
    */
   public String getShortName()
   {
      if (mFileName == null)
         return null;

      return mFileName.substring( mFileName.lastIndexOf('/')+1 );
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getTempDeploymentLocation()
    */
   public URL getTempDeploymentLocation()
   {
      if (mContext == null)
         return null;

      return mContext.getDeploymentLocation();
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getResourceClassLoader()
    */
   public ClassLoader getResourceClassLoader()
   {  
      if (mContext == null)
         return null;
      
      return mContext.getResourceClassLoader();
   }

   /**
    * Implements method by returning this as it is itself the deployment context.
    * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#getDeploymentContext()
    */
   public IAeDeploymentContext getDeploymentContext()
   {
      return this;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer#getServiceDeploymentInfo()
    */
   public IAeServiceDeploymentInfo[] getServiceDeploymentInfo()
   {
      if (mServiceInfo == null)
         return null;
      
      IAeServiceDeploymentInfo[] info = new IAeServiceDeploymentInfo[mServiceInfo.size()];
      return (IAeServiceDeploymentInfo[]) mServiceInfo.toArray(info);
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer#setServiceDeploymentInfo(org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo[])
    */
   public void setServiceDeploymentInfo(IAeServiceDeploymentInfo[] aServiceInfo)
   {
      mServiceInfo = new ArrayList();
      for (int i = 0; i < aServiceInfo.length; i++)
      {
         mServiceInfo.add(aServiceInfo[i]);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer#addServiceDeploymentInfo(org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo[])
    */
   public void addServiceDeploymentInfo(IAeServiceDeploymentInfo[] aServiceInfo)
   {
      if (mServiceInfo == null)
      {
         mServiceInfo = new ArrayList();
      }
      
      for (int i = 0; i < aServiceInfo.length; i++)
      {
         mServiceInfo.add(aServiceInfo[i]);
      }
   }
}
