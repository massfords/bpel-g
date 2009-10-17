// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/deploy/AeResourceProvider.java,v 1.1 2006/03/09 14:28:20 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002, 2003, 2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.deploy;

import java.io.InputStream;
import java.net.URL;

import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.utils.XMLUtils;

/**
 * This class extends the axis file provider so we can create our own deployment
 * context, which can then be classloader aware.
 */
public class AeResourceProvider extends FileProvider
{
   /** The configuration we load. */
   protected URL mConfigResource;

   /** Input stream cache */
   protected InputStream mInputStreamCache;

   /**
    * Constructs a resource provider from the passed config url.
    * @param aConfigResource The config file resource to load
    */
   public AeResourceProvider(URL aConfigResource)
   {
      super((InputStream) null);
      mConfigResource = aConfigResource;
   }

   /**
    * Override input stream setter to sync protected cache.
    * @see org.apache.axis.configuration.FileProvider#setInputStream(java.io.InputStream)
    */
   public void setInputStream(InputStream aStream)
   {
      super.setInputStream(aStream);
      mInputStreamCache = aStream;
   }

   /**
    * Configures the given AxisEngine with the given descriptor 
    * @see org.apache.axis.EngineConfiguration#configureEngine(org.apache.axis.AxisEngine)
    */
   public void configureEngine(AxisEngine aEngine) throws ConfigurationException
   {
      buildDeployment();
      getDeployment().configureEngine(aEngine);
      aEngine.refreshGlobalOptions();
   }

   /**
    * @return New deployment, which is classloader context aware.
    * @throws ConfigurationException
    */
   public synchronized AeBprDeployment buildDeployment() throws ConfigurationException
   {
      if (getDeployment() == null)
      {
         try
         {
            if (mInputStreamCache == null)
            {
               setInputStream(mConfigResource.openStream());
            }

            setDeployment(new AeBprDeployment(XMLUtils.newDocument(mInputStreamCache).getDocumentElement()));

            setInputStream(null);

            if (getDeployment().getGlobalConfiguration() == null)
            {
               WSDDGlobalConfiguration config = new WSDDGlobalConfiguration();
               config.setOptionsHashtable(new java.util.Hashtable());
               getDeployment().setGlobalConfiguration(config);
            }

         }
         catch (Exception e)
         {
            throw new ConfigurationException(e);
         }
      }
      return getMyDeployment();
   }

   /**
    * Override, since we will rebuild deployment on startup.
    * @todo should we write out cache?
    * @see org.apache.axis.EngineConfiguration#writeEngineConfig(org.apache.axis.AxisEngine)
    */
   public void writeEngineConfig(AxisEngine engine)
   {
      //
   }

   /** 
    * Helper method for casting our deployment to the type we create.
    */
   public AeBprDeployment getMyDeployment()
   {
      return (AeBprDeployment) getDeployment();
   }

}