// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeNewDeploymentInfo.java,v 1.2 2005/06/17 21:51:13 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002, 2003, 2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.net.URL;

import org.w3c.dom.Document;

/**
 * Wrapper for deployment information, url, metadata, etc.
 */
public class AeNewDeploymentInfo
{
   /** holds onto meta data document, which describes deployment. */
   private Document mMetaData;
   
   /** holds onto the deployment context url. */
   private URL mURL;
   
   /** staging deployment url */
   private URL mTempURL;
   
   /**
    * Accessor for deployment URL.
    */
   public URL getURL()
   {
      return mURL;
   }

   /**
    * Accessor for wsdd document.
    */
   public Document getMetaData()
   {
      return mMetaData;
   }

   /**
    * Setter for wsdd document.
    */
   public void setMetaData(Document aDocument)
   {
      mMetaData = aDocument;
   }

   /**
    * Setter for the deployment url.
    */
   public void setURL(URL aUrl)
   {
      mURL = aUrl;
   }
   
   /**
    * Accessor for the temp deployment url.
    */
   public URL getTempURL()
   {
      return mTempURL;
   }
   
   /**
    * Setter for the temp/staging url.
    * @param aURL
    */
   public void setTempURL( URL aURL )
   {
      mTempURL = aURL;
   }
}
