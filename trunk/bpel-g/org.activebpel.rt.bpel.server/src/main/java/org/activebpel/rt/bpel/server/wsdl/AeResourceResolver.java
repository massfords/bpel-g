// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/wsdl/AeResourceResolver.java,v 1.1 2006/08/04 17:57:53 ckeller Exp $
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
import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.AeWSDLException;
import org.activebpel.rt.bpel.server.catalog.AeCatalogMappings;
import org.activebpel.rt.bpel.server.catalog.IAeCatalogMapping;
import org.activebpel.rt.util.AeSafelyViewableMap;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.wsdl.def.AeStandardSchemaResolver;
import org.xml.sax.InputSource;

/**
 * Contains a map of location hints to actual mappings
 * for resources deployed in a bpr (used during deployment validation).
 */
public class AeResourceResolver implements IAeResourceResolver
{
   /** Location hint key to catalog mapping resource location resolution. */
   private Map mMap;
   
   /**
    * Constructor.
    */
   public AeResourceResolver()
   {
      setMap(new AeSafelyViewableMap(new HashMap()));
   }
   
   /**
    * Add an array of entries into the resolver.
    * @param aMappings
    * @param aOverwriteFlag If true, then previous entries mapped to the same
    * location hint will be replaced.
    */
   public void addEntries( IAeCatalogMapping[] aMappings, boolean aOverwriteFlag )
   {
      for( int i = 0; i < aMappings.length; i++ )
      {
         addEntry( aMappings[i], aOverwriteFlag );
      }
   }
   
   /**
    * Add an entry into the resolver.
    * @param aMapping The mapping to add.
    * @param aOverwriteFlag If true, then the previous entry mapped to the same
    * location hint will be replaced.
    */
   protected void addEntry( IAeCatalogMapping aMapping, boolean aOverwriteFlag )
   {
      String locationKey = aMapping.getLocationHint();
      boolean add = aOverwriteFlag || !hasMapping(locationKey);
      
      if( add )
      {
         addMapping( locationKey, aMapping );
      }
   }
   
   /**
    * Convenienve method for formatting keys.
    * @param aLocationHint
    */
   protected String formatKey( String aLocationHint )
   {
      return AeCatalogMappings.makeKey( aLocationHint );
   }
   
   /**
    * Remove an existing mapping with the given location hint.
    * @param aLocationHint
    */
   public void removeEntry( String aLocationHint )
   {
      removeMapping(aLocationHint);
   }
   
   /**
    * Load the wsdl def object from its context deployment.
    * Any imports will be resolved at this time.
    * @param aLocationHint
    * @throws AeWSDLException
    */
   public AeBPELExtendedWSDLDef newInstance( String aLocationHint )
   throws AeWSDLException
   {
      AeWsdlLocator locator = new AeWsdlLocator( this, aLocationHint );
      AeBPELExtendedWSDLDef def = new AeBPELExtendedWSDLDef(locator, aLocationHint, AeStandardSchemaResolver.newInstance());
      return def;
   }
   
   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.wsdl.IAeResourceResolver#getInputSource(java.lang.String)
    */
   public InputSource getInputSource( String aLocationHint ) throws IOException
   {
      return getMapping(aLocationHint).getInputSource();
   }
   
   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.wsdl.IAeResourceResolver#hasMapping(java.lang.String)
    */
   public boolean hasMapping( String aLocationHint )
   {
      return getMapping(aLocationHint) != null;
   }
   
   /**
    * Gets a mapping after applying the formatting for the key
    * @param aLocationHint
    */
   protected IAeCatalogMapping getMapping(String aLocationHint)
   {
      return (IAeCatalogMapping) getMap().get(formatKey(aLocationHint));
   }
   
   /**
    * Adds a mapping of this location hint to the value.
    * @param aLocationHint hint is passed through formatKey() in order to replace "\" with "/"
    * @param aValue
    */
   protected void addMapping(String aLocationHint, IAeCatalogMapping aValue)
   {
      getMap().put(formatKey(aLocationHint), aValue);
   }
   
   /**
    * Removes the mapping after formatting the key
    * @param aLocationHint
    */
   protected void removeMapping(String aLocationHint)
   {
      getMap().remove(formatKey(aLocationHint));
   }
   
   /**
    * Setter for the map
    * @param map
    */
   protected void setMap(Map map)
   {
      mMap = map;
   }

   /**
    * Getter for the map
    */
   protected Map getMap()
   {
      return mMap;
   }
}
