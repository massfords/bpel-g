// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/castor/AeURIResolver.java,v 1.6 2007/12/14 01:06:39 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def.castor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.wsdl.xml.WSDLLocator;

import org.activebpel.rt.wsdl.def.IAeStandardSchemaResolver;
import org.exolab.castor.net.URIException;
import org.exolab.castor.net.URILocation;
import org.exolab.castor.net.URIResolver;
import org.exolab.castor.net.util.URIResolverImpl;
import org.xml.sax.InputSource;

/**
 * Implements the Castor interface for resolving URI references to instances of
 * <code>URILocation</code>.
 */
public class AeURIResolver implements URIResolver
{
   /** The WSDL locator to use for locating schema resources. */
   private final WSDLLocator mWSDLLocator;
   /** The standard schema resolver for locating 'well known' schemas. */
   private final IAeStandardSchemaResolver mStandardResolver;
   /** The Castor URI resolver to use if the WSDL locator is not provided. */
   private URIResolver mDelegate;
   /** Collection of all unique resolution requests */
   private Set mResolvedReferences;

   /**
    * Constructs a URI resolver using the specified WSDL locator, location, and standard
    * schema resolver.
    * 
    * @param aLocator
    * @param aStandardResolver
    */
   public AeURIResolver(WSDLLocator aLocator, IAeStandardSchemaResolver aStandardResolver)
   {
      mWSDLLocator = aLocator;
      mStandardResolver = aStandardResolver;
      mResolvedReferences = new HashSet();
   }

   /**
    * Returns the Castor URI resolver to use if the WSDL locator is not
    * provided.
    */
   protected URIResolver getDelegate()
   {
      if (mDelegate == null)
      {
         mDelegate = new URIResolverImpl();
      }

      return mDelegate;
   }

   /**
    * @see org.exolab.castor.net.URIResolver#resolve(java.lang.String, java.lang.String)
    */
   public URILocation resolve(String aHref, String aDocumentBase) throws URIException
   {
      URILocation loc;
      if (mWSDLLocator != null)
      {
         try
         {
            loc = new AeURILocation(mWSDLLocator, aHref, aDocumentBase);
         }
         catch (IOException e)
         {
            throw new URIException(e.getLocalizedMessage(), e);
         }
      }
      else
         loc =  getDelegate().resolve(aHref, aDocumentBase);

      if (loc != null)
         mResolvedReferences.add(loc.getAbsoluteURI());

      return loc;
   }

   /**
    * @see org.exolab.castor.net.URIResolver#resolveURN(java.lang.String)
    */
   public URILocation resolveURN(String urn) throws URIException
   {
      InputSource iSource = null;
      if (getStandardResolver() != null)
      {
         iSource = getStandardResolver().resolve(urn);
      }
      if (iSource != null)
      {
         return new AeInputSourceURILocation(iSource, urn);
      }
      else
      {
         URILocation loc = getDelegate().resolveURN(urn);
         return loc;
      }
   }
   
   /**
    * Returns an iterator over the unique URI references we have been aaked to resolve.
    */
   public Iterator getURIReferences()
   {
      return mResolvedReferences.iterator();
   }

   /**
    * Getter for the standard schema resolver.
    */
   protected IAeStandardSchemaResolver getStandardResolver()
   {
      return mStandardResolver;
   }
}