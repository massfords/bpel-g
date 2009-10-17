// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/catalog/resource/IAeResourceCache.java,v 1.2 2006/08/04 17:57:53 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.catalog.resource;


/**
 *  Resource cache interface.
 */
public interface IAeResourceCache
{
   /**
    * Locate a specific object via a key.
    * @param aKey
    */
   public Object getResource( IAeResourceKey aKey ) throws AeResourceException;
   
   /**
    * Remove the resource from the cache.
    * @param aKey
    */
   public Object removeResource( IAeResourceKey aKey );
   
   /**
    * Replace any existing entries mapped to the given key with the new object.
    * @param aKey
    * @param aObject
    */
   public void updateResource( IAeResourceKey aKey, Object aObject);
   
   /**
    * Setter for the max cache size.
    * @param aSize The number of objects to cache. Set to -1 for unlimited size.
    */
   public void setMaxCacheSize( int aSize );
   
   /**
    * Getter for the max cache size.
    */
   public int getMaxCacheSize();
   
   /**
    * Clear entries out of the cache.
    */
   public void clear();
   
   /**
    * Return stat data on resource access.
    */
   public IAeResourceStats getResourceStats();
}
