// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/AeWSDLLocator.java,v 1.3 2006/02/17 14:53:12 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeWSDLFactory;

import javax.wsdl.xml.WSDLLocator;

import org.xml.sax.InputSource;

/**
 * Helper class used by WSDL reader to obtain WSDL files which are located in
 * the WSR file.
 */
public class AeWSDLLocator implements WSDLLocator
{
   /** The base WSDL file given during contruction */
   private final String mBaseURI;
   /** The last import file which was requested */
   private String mLastImportURI;
   /** The WSDL factory used to load */
   private final IAeWSDLFactory mWSDLFactory;

   /**
    * WSDL locator object used by WSDL reader to load WSDL files
    * @param aFactory the factory from which to load WSDL resources
    * @param aBaseURI the URI for the base WSDL file
    */
   public AeWSDLLocator(IAeWSDLFactory aFactory, String aBaseURI)
   {
      mWSDLFactory = aFactory;
      mBaseURI = aBaseURI;
   }

   /**
    * @see javax.wsdl.xml.WSDLLocator#getBaseInputSource()
    */
   public InputSource getBaseInputSource()
   {
      return getInputSource(getBaseURI());
   }

   /**
    * @see javax.wsdl.xml.WSDLLocator#getBaseURI()
    */
   public String getBaseURI()
   {
      return mBaseURI;
   }

   /**
    * @see javax.wsdl.xml.WSDLLocator#getImportInputSource(java.lang.String, java.lang.String)
    */
   public InputSource getImportInputSource(String aParentLocation, String aImportLocation)
   {
      mLastImportURI = aImportLocation;
      return getInputSource(aImportLocation);
   }

   /**
    * @see javax.wsdl.xml.WSDLLocator#getLatestImportURI()
    */
   public String getLatestImportURI()
   {
      return mLastImportURI;
   }

   /**
    * Returns the input source for the given WSDL URI or null if not found.
    * @param aURI the WSDL file we are looking for
    */
   private InputSource getInputSource(String aURI)
   {
      InputSource inSource = null;
      try
      {
         inSource = mWSDLFactory.getWSDLSource(aURI);
         inSource.setSystemId(aURI);
      }
      catch(AeException e)
      {
         e.printStackTrace();
      }

      return inSource;
   }

	@Override
	public void close() {
	}
}
