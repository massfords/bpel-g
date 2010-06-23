//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/AeWsdlReferenceTracker.java,v 1.3 2006/08/04 18:05:50 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis;

import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.axis.IAeWsdlReference;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

/**
 * Adds <code>IAeWsdlReference</code> instances as <code>IAeCatalogListener</code>
 * objects to the wsdl catalog on registration.  Removes <code>IAeWsdlReference</code>
 * instances from the wsdl catalog on unregistration (should be called when the
 * Axis service is removed to ensure that the <code>IAeWsdlReference</code> is
 * eligible for garbage collection). 
 */
public class AeWsdlReferenceTracker
{
   
   /** Map of service names to <code>IAeWsdlReference</code> objects. */
   private static Map mReferences = new HashMap();
   
   /**
    * Register the <code>IAeWsdlReference</code> as a <code>IAeCatalogListener</code> with
    * the catalog.
    * @param aServiceName
    * @param aReference
    */
   public static void registerReference( String aServiceName, IAeWsdlReference aReference )
   {
      unregisterReference( aServiceName );
      mReferences.put( aServiceName, aReference );
      AeEngineFactory.getCatalog().addCatalogListener( aReference );

   }
   
   /**
    * Remove the <code>IAeWsdlReference</code> mapped to the given service name
    * as a <code>IAeCatalogListener</code>.
    * @param aServiceName
    */
   public static void unregisterReference( String aServiceName )
   {
      IAeWsdlReference wsdlRef = (IAeWsdlReference)mReferences.remove( aServiceName );
      if( wsdlRef != null )
      {
         AeEngineFactory.getCatalog().removeCatalogListener( wsdlRef );
      }
   }
}
