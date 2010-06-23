// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/wsdl/AeCatalogResourceResolver.java,v 1.1 2006/08/04 17:57:53 ckeller Exp $
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

import org.activebpel.rt.bpel.server.catalog.AeCatalogMappings;
import org.activebpel.rt.bpel.server.catalog.IAeCatalogMapping;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.xml.sax.InputSource;

/**
 * Interacts with the catalog for mappings to resolve resource references.
 */
public class AeCatalogResourceResolver implements IAeResourceResolver
{
   /**
    * Constructor.
    */
   public AeCatalogResourceResolver()
   {
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
    * Implements method by calling catalog to resolve mapping. 
    * @see org.activebpel.rt.bpel.server.wsdl.IAeResourceResolver#getInputSource(java.lang.String)
    */
   public InputSource getInputSource( String aLocationHint ) throws IOException
   {
      IAeCatalogMapping mapping = AeEngineFactory.getCatalog().getMappingForLocation(aLocationHint); 
      if(mapping != null)
         return mapping.getInputSource();
      return null;
   }
   
   /**
    * Implements method by calling catalog to see if it has the mapping for this location.
    * @see org.activebpel.rt.bpel.server.wsdl.IAeResourceResolver#hasMapping(java.lang.String)
    */
   public boolean hasMapping( String aLocationHint )
   {
      IAeCatalogMapping mapping = AeEngineFactory.getCatalog().getMappingForLocation(aLocationHint); 
      return mapping != null;
   }
}
