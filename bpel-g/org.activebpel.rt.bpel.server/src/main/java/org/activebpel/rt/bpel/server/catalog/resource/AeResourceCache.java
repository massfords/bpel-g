// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/catalog/resource/AeResourceCache.java,v 1.5 2008/02/28 18:35:56 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.catalog.resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.activebpel.rt.AeWSDLException;
import org.activebpel.rt.bpel.config.IAeConfigChangeListener;
import org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig;
import org.activebpel.rt.bpel.server.catalog.IAeCatalogMapping;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.wsdl.AeCatalogResourceResolver;
import org.activebpel.rt.bpel.server.wsdl.AeWsdlLocator;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeLRUObjectCache;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.wsdl.def.AeStandardSchemaResolver;
import org.activebpel.rt.wsdl.def.castor.AeURIResolver;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.reader.SchemaReader;
import org.xml.sax.InputSource;

/**
 * In memory cache for resource objects with a max upper limit.  Default value, if not in config, is 50.
 */
public class AeResourceCache implements IAeResourceCache, IAeConfigChangeListener
{
   /** Default max value. Default value is 50. */
   public static final int DEFAULT_MAX_VALUE = 50;
   /** LRU cache impl. */
   protected AeLRUObjectCache mLru;
   /** Resource stats impl. */
   protected IAeResourceStats mResourceStats;

   /**
    * Default contructor.
    */
   public AeResourceCache()
   {
      mLru = new AeLRUObjectCache( DEFAULT_MAX_VALUE ); 
      updateConfig( AeEngineFactory.getEngineConfig().getUpdatableEngineConfig() );
      AeEngineFactory.getEngineConfig().getUpdatableEngineConfig().addConfigChangeListener( this );
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.catalog.resource.IAeResourceCache#updateResource(org.activebpel.rt.bpel.server.catalog.resource.IAeResourceKey, java.lang.Object)
    */
   public void updateResource( IAeResourceKey aKey, Object aObj )
   {
      removeResource( aKey );
      mLru.cache( aKey, aObj );
   }

   /**
    * TODO (cck) This method should use a factory based on type to create the appropriate object.
    * @see org.activebpel.rt.bpel.server.catalog.resource.IAeResourceCache#getResource(org.activebpel.rt.bpel.server.catalog.resource.IAeResourceKey)
    */
   public Object getResource(IAeResourceKey aKey) throws AeResourceException
   {
      // update stats
      if( getResourceStats() != null )
         getResourceStats().logTotalRead();

      if(aKey.isWsdlEntry())
         return getDefFromCache( aKey );
      
      if(aKey.isSchemaEntry())
         return getSchemaDefFromCache( aKey );
      
      // TODO (cck) is InputSource the best return type for non-wsdl cached locations we need a factory?
      return getInputSourceForLocation( aKey );
   }

   /**
    * Utility method to locate def.  First check is via the 
    * LRU cache and if the def is not there, it is reloaded
    * using the wsdl locator and then cached again.
    * @param aKey
    */
   protected AeBPELExtendedWSDLDef getDefFromCache( IAeResourceKey aKey )
   throws AeResourceException
   {
      AeBPELExtendedWSDLDef def = (AeBPELExtendedWSDLDef)mLru.get(aKey);
      if( def == null )
      {
         def = getDefForLocation(aKey);
         mLru.cache(aKey, def);

         // update stats
         if( getResourceStats() != null )
            getResourceStats().logDiskRead();
      }

      return def;
   }
   
   /**
    * Utility method to locate def.  First check is via the 
    * LRU cache and if the def is not there, it is reloaded
    * using the wsdl locator and then cached again.
    * @param aKey
    */
   protected Schema getSchemaDefFromCache( IAeResourceKey aKey )
   throws AeResourceException
   {
      Schema def = (Schema)mLru.get(aKey);
      if( def == null )
      {
         def = getSchemaDefForLocation(aKey);
         mLru.cache(aKey, def);

         // update stats
         if( getResourceStats() != null )
            getResourceStats().logDiskRead();
      }

      return def;
   }
   
   /**
    * Access the def via the catalog aware locator object.
    * @param aKey
    */
   protected Schema getSchemaDefForLocation( IAeResourceKey aKey ) throws AeResourceException
   {
      try
      {
         AeCatalogResourceResolver catalogResolver = new AeCatalogResourceResolver(); 
         AeWsdlLocator locator = new AeWsdlLocator(catalogResolver, aKey.getLocation());
         SchemaReader reader = new SchemaReader(aKey.getLocation());
         InputSource source = catalogResolver.getInputSource(aKey.getLocation());
         if(source != null)
            reader = new SchemaReader(source);
         else
            reader = new SchemaReader(aKey.getLocation());
         reader.setURIResolver(new AeURIResolver(locator, AeStandardSchemaResolver.newInstance()));
         return reader.read();
      }
      catch (Exception ex)
      {
         throw new AeResourceException(ex);
      }
   }

   /**
    * Access the def via the cached wsdl locator object.
    * @param aKey
    */
   protected AeBPELExtendedWSDLDef getDefForLocation( IAeResourceKey aKey ) throws AeResourceException
   {
      AeWsdlLocator locator = new AeWsdlLocator(new AeCatalogResourceResolver(), aKey.getLocation());
      String locationHint = locator.getBaseURI();
      try
      {
         return new AeBPELExtendedWSDLDef(locator, locationHint, AeStandardSchemaResolver.newInstance());
      }
      catch (AeWSDLException ex)
      {
         throw new AeResourceException(ex);
      }
   }

   /**
    * Returns an input source for the passed key
    * @param aKey
    */
   protected InputSource getInputSourceForLocation( IAeResourceKey aKey ) throws AeResourceException
   {
      InputSource source = new InputSource(getStreamForLocation(aKey));
      source.setSystemId(aKey.getLocation());
      return source;
   }

   /**
    * @param aKey
    */
   protected InputStream getStreamForLocation( IAeResourceKey aKey ) throws AeResourceException
   {
      InputStream stream = null;
      Reader reader = null;
      try
      {
         byte[] bytes = (byte[])mLru.get(aKey);
         if( bytes == null )
         {
            InputSource source = getInputSource(aKey.getLocation()) ;
            stream = source.getByteStream();
            if(stream != null)
            {
               // read the stream into a buffer and save the buffer in cache
               int readLen, totalLen = 0;
               byte[] buffer = new byte[32768];
               while((readLen = stream.read(buffer)) > 0)
               {
                  totalLen += readLen;
                  if(bytes == null)
                  {
                     byte[] newBuf = new byte[readLen];
                     System.arraycopy(buffer, 0, newBuf, 0, readLen);
                     bytes = newBuf;
                  }
                  else
                  {
                     byte[] newBuf = new byte[totalLen];
                     System.arraycopy(bytes, 0, newBuf, 0, bytes.length);
                     System.arraycopy(buffer, 0, newBuf, bytes.length, readLen);
                     bytes = newBuf;
                  }
               }
               
               // just in case it is an empty file
               if(bytes.length == 0)
                  bytes = new byte[0];
            }
            else
            {
               reader = source.getCharacterStream();
               // read the stream into a buffer and save the buffer in cache
               int readLen, totalLen = 0;
               char[] buffer = new char[32768];
               char[] chars = null;
               while((readLen = reader.read(buffer)) > 0)
               {
                  totalLen += readLen;
                  if(chars == null)
                  {
                     char[] newBuf = new char[readLen];
                     System.arraycopy(buffer, 0, newBuf, 0, readLen);
                     chars = newBuf;
                  }
                  else
                  {
                     char[] newBuf = new char[totalLen];
                     System.arraycopy(chars, 0, newBuf, 0, chars.length);
                     System.arraycopy(buffer, 0, newBuf, chars.length, readLen);
                     chars = newBuf;
                  }
               }
               
               // just in case it is an empty file
               if(chars.length == 0)
                  chars = new char[0];
               
               // convert to byte array for caching
               String encoding = "utf-8"; //$NON-NLS-1$
               if(source.getEncoding() != null)
                  encoding = source.getEncoding();
               bytes = (new String(chars)).getBytes(encoding);
            }
            
            mLru.cache(aKey, bytes);
            
            // update stats
            if( getResourceStats() != null )
               getResourceStats().logDiskRead();
         }
         return new ByteArrayInputStream(bytes);
      }
      catch (Exception ex)
      {
         throw new AeResourceException(ex);
      }
      finally
      {
         AeCloser.close(stream);
         AeCloser.close(reader);
      }
   }

   /**
    * Implements method by calling catalog to resolve mapping. 
    * @see org.activebpel.rt.bpel.server.wsdl.IAeResourceResolver#getInputSource(java.lang.String)
    */
   public InputSource getInputSource( String aLocationHint ) throws Exception
   {
      InputSource source;
      
      IAeCatalogMapping mapping = AeEngineFactory.getCatalog().getMappingForLocation(aLocationHint); 
      if(mapping != null)
      {
         source = mapping.getInputSource();
      }
      else
      {
         URL url = new URL( aLocationHint );
         source = new InputSource( url.openStream() );
         source.setSystemId( url.toExternalForm() );
      }
      
      return source;
      
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.catalog.resource.IAeResourceCache#removeResource(org.activebpel.rt.bpel.server.catalog.resource.IAeResourceKey)
    */
   public Object removeResource(IAeResourceKey aKey)
   {
      // remove the object from the cache (it may not be in memory)
      Object obj = mLru.remove( aKey );

      return obj;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.catalog.resource.IAeResourceCache#setMaxCacheSize(int)
    */
   public void setMaxCacheSize( int aMaxValue )
   {
      mLru.setMaxSize( aMaxValue );
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.catalog.resource.IAeResourceCache#getMaxCacheSize()
    */
   public int getMaxCacheSize()
   {
      return mLru.getMaxSize();
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.catalog.resource.IAeResourceCache#getResourceStats()
    */
   public IAeResourceStats getResourceStats()
   {
      return mResourceStats;
   }
   
   /**
    * Setter for wsdl stats impl.
    * @param aStats
    */
   public void setResourceStats( IAeResourceStats aStats )
   {
      mResourceStats = aStats;
   }
   
   /**
    * @see org.activebpel.rt.bpel.config.IAeConfigChangeListener#updateConfig(org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig)
    */
   public void updateConfig(IAeUpdatableEngineConfig aConfig)
   {
      setMaxCacheSize( aConfig.getResourceCacheMax() );
   }
   
   /**
    * Clear entries out of the cache.
    */
   public void clear()
   {
      mLru.clear();
   }
}
