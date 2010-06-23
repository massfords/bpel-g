//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/config/IAeUpdatableEngineConfig.java,v 1.16 2008/02/26 01:47:20 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.config;

import java.util.Collection;
import java.util.Map;

import org.activebpel.rt.bpel.impl.function.AeInvalidFunctionContextException;

/**
 * Interface for the runtime mutable engine settings.
 */
public interface IAeUpdatableEngineConfig extends IAeEngineConfiguration
{
   /**
    * Register a custom <code>FunctionContext</code> with the BPEL engine and persist it.
    * Callers should ensure that the class specified is available on the 
    * classpath for the engine.
    * 
    * @param aName Arbitrary (unique) name for the grouping.
    * @param aNamespace
    * @param aClassName Classname for a class that implements the <code>FunctionContext</code> interface.
    * @param aLocation Location where the class can be found.
    */
   public void addNewFunctionContext(String aName, String aNamespace, String aClassName, String aLocation)
         throws AeInvalidFunctionContextException, AeFunctionContextExistsException;
   
   /**
    * Unregister the custom <code>FunctionContext</code> objects with
    * the given names and remove them from storage.
    * @param aContextNames
    */
   public void deleteFunctionContexts( Collection aContextNames );

   /**
    * Set the time the engine will hold onto unmatched correlated receives
    * before rejecting them. This is to address the race condition that is possible
    * with one-way invokes and a resulting asynchronous callback. Briefly, it's
    * possible for a response from a one-way invoke to arrive before the intended
    * receive activity has executed. The spec says that the implementation needs
    * to support that receive arriving prior to the execution of the invoke.
    * The details can be found in Issue 33 of the WS-BPEL issues list.
    */
   public void setUnmatchedCorrelatedReceiveTimeout( int aTime );
   
   /**
    * Setter for the logging filter
    * @param aFilterName
    */
   public void setLoggingFilter( String aFilterName );
   
   /**
    * If true the bpel engine should allow xpath queries which return 0 nodes.
    */
   public void setAllowEmptyQuerySelection( boolean aFlag );

   /**
    * If true the bpel engine should automatically create XPath nodes that don't exist.
    * @param aFlag
    */
   public void setAllowCreateXPath( boolean aFlag );
   
   /**
    * Determine if engine should validate all inbound and outbound messages.
    * These are the messages used in receive, reply, invoke and onMessage.
    */
   public void setValidateServiceMessages( boolean aFlag );

   /**
    * Sets the minimum number of threads to have in the work manager's thread pool.
    */
   public void setWorkManagerThreadPoolMin( int aMin );

   /**
    * Sets the maximum number of threads to have in the work manager's thread pool
    */
   public void setWorkManagerThreadPoolMax( int aMax );
   
   /**
    * Set the maximum number of resources (WSDL Defs, xsl, other)that will be cached
    * in memory.
    * @param aMax
    */
   public void setResourceCacheMax( int aMax );

   /**
    * Add a listener to be notified after the internal state
    * of the config has been updated.
    * @param aListener
    */
   public void addConfigChangeListener( IAeConfigChangeListener aListener );
   
   /**
    * Remove a listener.
    * @param aListener
    */
   public void removeConfigChangeListener( IAeConfigChangeListener aListener );
   
   /**
    * Adds a listener for storage changes
    * @param aListener
    */
   public void addStorageChangeListener(IAeStorageChangeListener aListener);
   
   /**
    * Removes a listener for storage changes.
    * @param aListener
    */
   public void removeStorageChangeListener(IAeStorageChangeListener aListener);
   
   /**
    * Notify any listeners that they need to update their setting.
    */
   public void update();
   
   /**
    * If set to true then unauthenticated requests to services deployed with 
    * an allowedRoles attribute will fail. 
    * 
    * @param aFlag
    */
   public void setAllowedRolesEnforced(boolean aFlag);
   
   /**
    * If set to true, existing resources (mapped to same location hint) will be 
    * replaced.  
    * 
    * @param aFlag
    */
   public void setResourceReplaceEnabled( boolean aFlag );
   
   /**
    * If set to true, any non-service flow processes will be suspended 
    * if they encounter an uncaught fault.
    * 
    * @param aFlag
    */
   public void setSuspendProcessOnUncaughtFault( boolean aFlag );

   /**
    * If set to <code>true</code>, any process will be suspended if it has a
    * non-durable invoke pending during process recovery.
    *
    * @param aFlag
    */
   public void setSuspendProcessOnInvokeRecovery(boolean aFlag);

   /**
    * Sets the maximum number of work requests to schedule per-process.
    */
   public void setProcessWorkCount(int aProcessWorkCount);
   
   /**
    * Sets the max number of seconds to wait before timing out an invoke.
    * @param aTimeout
    */
   public void setWebServiceInvokeTimeout(int aTimeout);
   
   /**
    * Sets the max number of seconds to wait before timing out a request sent to the engine.
    * @param aTimeout
    */
   public void setWebServiceReceiveTimeout(int aTimeout);
   
   /**
    * Retrieve the map entry from the config.
    * 
    * @param aName The config entry name.
    * @param aCreateFlag If true and the named map is null, it will be created
    *           and added to the config map.
    */
   public Map getMapEntry(String aName, boolean aCreateFlag);
   
   /**
    * Retrieve the map entry from the config.
    * 
    * @param aName The config entry name.
    * @param aCreateFlag If true and the named map is null, it will be created
    *           and added to the config map.
    * aConfigEntry Map to containing entries.
    */   
   public Map getMapEntry(String aName, boolean aCreateFlag, Map aConfigEntry);

   /**
    * Descend into the config maps for the given path to set the value.
    * 
    * @param aPath The path to the map. The path format will be something like foo/bar/debug where foo and bar
    *           are key names for map and debug is the key that will be added to the bar map (with the setting
    *           as its value).
    * @param aSetting The value of the custom param.
    */
   public void addEntryByPath(String aPath, String aSetting);
}
