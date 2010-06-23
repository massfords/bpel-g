//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeDeferredMapFactory.java,v 1.2 2006/03/10 21:45:41 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util; 

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * A base class for building a {@link java.util.Map} whose contents won't be loaded
 * until the map is accessed. 
 */
public abstract class AeDeferredMapFactory implements InvocationHandler
{
   /** init flag to avoid building the map multiple times */
   private boolean mInitialized;
   /** map that we're delegating to */
   private Map mMap;
   
   /**
    * Method to be overridden by subclass to return our delegate map.
    */
   protected abstract Map buildMap();
   
   /**
    * Creates a proxy for the map. Invoking any method on the proxy will cause
    * the delegate map to get built.
    */
   public Map getMapProxy()
   {
      return (Map) Proxy.newProxyInstance(
            getClass().getClassLoader(),
            new Class[] { Map.class },
            this );
   }
   
   /**
    * Override the invoke method to ensure that our delegate map gets built.
    * 
    * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
    */
   public Object invoke(Object aProxy, Method aMethod, Object[] args)
         throws Throwable
   {
      init();
      return aMethod.invoke(getMap(), args);
   }
   
   /**
    * If we're not initialized, then <code>buildMap()</code> is called and the 
    * resulting map becomes our delegate. 
    */
   protected void init()
   {
      if (!isInitialized())
      {
         setMap(buildMap());
         setInitialized(true);
      }
   }
   
   /**
    * @return Returns the initialized.
    */
   protected boolean isInitialized()
   {
      return mInitialized;
   }

   /**
    * @param aInitialized The initialized to set.
    */
   protected void setInitialized(boolean aInitialized)
   {
      mInitialized = aInitialized;
   }
   
   /**
    * @return Returns the map.
    */
   protected Map getMap()
   {
      return mMap;
   }

   /**
    * @param aMap The map to set.
    */
   protected void setMap(Map aMap)
   {
      mMap = aMap;
   }
}
 