// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/wsdl/AeWsdlLocator.java,v 1.17 2007/05/10 21:18:22 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.wsdl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.wsdl.xml.WSDLLocator;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.util.AeUtil;
import org.xml.sax.InputSource;

/**
 * Helper class used by WSDL reader to resolve wsdl imports.
 * 
 * TODO (EPW) Move this class from rt.bpel.server to rt
 */
public class AeWsdlLocator implements WSDLLocator
{
   /** The last import file which was requested */
   private String mLastImportURI;
   /** the wsdl file location */
   private String mLocation;
   /** The factory for mapping import locations to local resources */
   private IAeResourceResolver mResolver;

   /**
    * Constructor.
    * @param aResolver used to lookup mappings
    * @param aLocationHint used as a key to resolve the top level read
    */
   public AeWsdlLocator( IAeResourceResolver aResolver, String aLocationHint )
   {
      mResolver = aResolver;
      mLocation = aLocationHint;
   }

   /**
    * @see javax.wsdl.xml.WSDLLocator#getBaseInputSource()
    */
   public InputSource getBaseInputSource()
   {
      // determine if this is coming from our catalog
      // bpr archive or the outside world and handle
      // appropriately
      if( getResolver().hasMapping(mLocation) )
      {
         return loadResolverResource( mLocation );
      }
      else
      {
         return loadExternalResource(mLocation);
      }
   }

   /**
    * Load the wsdl from the wsdl factory.
    * @param aLocationHint
    */
   protected InputSource loadResolverResource( String aLocationHint )
   {
      InputSource inSrc = null;
      try
      {
         inSrc = getResolver().getInputSource(aLocationHint);
         inSrc.setSystemId(aLocationHint);
      }
      catch (IOException e)
      {
         // WSDLLocator interface doesn't provide
         // any type of error notification
         AeException.logError( e, AeMessages.getString("AeWsdlLocator.ERROR_0") + aLocationHint ); //$NON-NLS-1$
      }
      return inSrc;
   }

   /**
    * @see javax.wsdl.xml.WSDLLocator#getBaseURI()
    */
   public String getBaseURI()
   {
      return mLocation;
   }

   /**
    * @see javax.wsdl.xml.WSDLLocator#getImportInputSource(java.lang.String, java.lang.String)
    */
   public InputSource getImportInputSource(String aParentLocation, String aImportLocation)
   {
      InputSource input = null;
      // if the resolver has a mapping for the import location than use it
      if (getResolver().hasMapping(aImportLocation))
      {
         input = loadResolverResource(aImportLocation);
         mLastImportURI = aImportLocation;
      }
      else
      {
         // If the specified parent location is null, then use the original base
         // URI for the parent location.
         String parentLocation = AeUtil.isNullOrEmpty(aParentLocation) ? getBaseURI() : aParentLocation;
   
         // Calculate the import URI from the parent and (possibly relative) import locations.
         String importURI = AeUtil.resolveImport(parentLocation, aImportLocation);
   
         // Save the import URI for getLatestImportURI.
         mLastImportURI = importURI;
   
         // if the parent was loaded from the local bpr file
         // check the factory to see if we have any of
         // its imports
         // otherwise - all imports of an external
         // wsdl are expected to come from the
         // outside as well
         if( getResolver().hasMapping( parentLocation ) )
         {
            input = getInputSource( importURI );
         }
         else
         {
            input = loadExternalResource( importURI );
         }
      }
      return input;
   }

   /**
    * Retrieve an InputSource from the real world.
    * @param aActualLocation 
    * @throws RuntimeException wraps any type of IOException
    */
   protected InputSource loadExternalResource( String aActualLocation )
   throws RuntimeException
   {
      try
      {
         URL url = new URL( aActualLocation );
         InputSource source = new InputSource( url.openStream() );
         source.setSystemId( url.toExternalForm() );
         return source;
      }
      catch (MalformedURLException e)
      {
         AeException.logError(e);
         throw new RuntimeException( aActualLocation + AeMessages.getString("AeWsdlLocator.ERROR_1"), e); //$NON-NLS-1$
      }
      catch (IOException e)
      {
         AeException.logError(e);
         throw new RuntimeException( AeMessages.getString("AeWsdlLocator.ERROR_2") + aActualLocation, e); //$NON-NLS-1$
      }
   }

   /**
    * Attempts to resolve the import location to a local
    * location (in the bpr).  If none is found, it expects
    * that the importLocation arg is a fully qualified url
    * and it will return an InputSource to it.
    * @param aImportLocation
    */
   protected InputSource getInputSource( String aImportLocation )
   {
      if( getResolver().hasMapping(aImportLocation) )
      {
         return loadResolverResource(aImportLocation);
      }
      return loadExternalResource( aImportLocation );
   }

   /**
    * This value is used as a key by com.ibm.wsdl.xml.WSDLReaderImpl
    * to cache imports.
    * @see javax.wsdl.xml.WSDLLocator#getLatestImportURI()
    */
   public String getLatestImportURI()
   {
      return mLastImportURI;
   }

   /**
    * Accessor for wsdl resolver.
    */
   protected IAeResourceResolver getResolver()
   {
      return mResolver;
   }

	@Override
	public void close() {
	}
}
