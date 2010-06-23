// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/catalog/AeCatalogBprMapping.java,v 1.1 2006/08/04 17:57:53 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.catalog;

import java.io.IOException;

import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;
import org.xml.sax.InputSource;

/**
 * Represents the mapping info for a catalog resource in a bpr file.
 */
public class AeCatalogBprMapping extends AeAbstractCatalogMapping
{
   /** Bpr containing the entry. */
   private IAeBpr mBpr;
   /** Classpath location from catalog deployment. */
   private String mClasspath;

   /**
    * Constructor.
    * @param aBpr
    * @param aLocationHint
    * @param aTypeURI
    * @param aClasspath
    */
   public AeCatalogBprMapping( IAeBpr aBpr, String aLocationHint, String aTypeURI, String aClasspath )
   {
      super( aLocationHint, aTypeURI );
      mClasspath = aClasspath;
      mBpr = aBpr;
   }

   /**
    * @return Returns the classpath.
    */
   public String getClasspath()
   {
      return mClasspath;
   }

   /**
    * Returns true if the entry exists. 
    */
   public boolean exists()
   {
      return mBpr.exists(getClasspath());
   }
   
   /**
    * Returns input source from the bpr file. 
    */
   public InputSource getInputSource() throws IOException
   {
      InputSource in = new InputSource(mBpr.getResourceAsStream(getClasspath()));
      in.setSystemId(getLocationHint());
      return in;
   }         
}