// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/bpr/AeBprContext.java,v 1.15 2008/02/29 21:14:33 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.bpr;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

import org.activebpel.rt.bpel.server.deploy.AeAbstractDeploymentContext;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;

/**
 *  Provides access to deployment context resources.
 */
public class AeBprContext extends AeAbstractDeploymentContext
{
   /** deployment context (url) classloader */
   private ClassLoader mContextLoader;
   /** temp/working url */
   private URL mTempLocation;
   
   /**
    * Constructor.
    * @param aURL the deployment url - points to the bpr archive
    * @param aLoader the context class loader to extract resources from the bpr archive
    */
   public AeBprContext( URL aURL, URL aTempLocation, ClassLoader aLoader )
   {
      super( aURL );
      mTempLocation = aTempLocation;
      mContextLoader = aLoader;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getResourceClassLoader()
    */
   public ClassLoader getResourceClassLoader()
   {
      return mContextLoader;
   }

   /**
    * Gets input stream for given resource  
    * @param aResource resource we want stream for
    */
   public InputStream getResourceAsStream(String aResource)
   {
      InputStream stream = getResourceClassLoader().getResourceAsStream( aResource );
      try
      {
         if(stream != null)
         {
            byte[] bytes = AeUtil.toByteArray(stream);            
            return new ByteArrayInputStream(bytes);
         }
         return null;
      }
      catch (Exception ex)
      {
         throw new IllegalStateException(ex.getLocalizedMessage(), ex);
      }
      finally
      {
         AeCloser.close(stream);         
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getResourceURL(java.lang.String)
    */
   public URL getResourceURL( String aContextResource )
   {
      return getResourceClassLoader().getResource( aContextResource );
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getTempDeploymentLocation()
    */
   public URL getTempDeploymentLocation()
   {
      return mTempLocation;
   }
}
