//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/urn/AeListMappingsBean.java,v 1.1 2005/06/22 17:17:33 MFord Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.urn; 

import java.util.ArrayList;
import java.util.List;

import org.activebpel.rt.bpeladmin.war.AeEngineManagementFactory;
import org.activebpel.rt.bpeladmin.war.web.AeAbstractAdminBean;

import bpelg.services.urnresolver.types.GetMappingsRequest;
import bpelg.services.urnresolver.types.Mappings;
import bpelg.services.urnresolver.types.Mappings.Mapping;

/**
 * Gets the URN mappings from the resolver and makes them available for the JSP.
 */
public class AeListMappingsBean extends AeAbstractAdminBean
{
   /** List of mappings */
   private List mValues = null;
   
   /**
    * Gets the mapping by offset
    * 
    * @param aOffset
    */
   public AeURNMapping getURNMapping(int aOffset)
   {
      return (AeURNMapping) getValues().get(aOffset);
   }
   
   /**
    * Gets the number of mappings
    */
   public int getURNMappingSize()
   {
      return getValues().size();
   }
   
   /**
    * Getter for the values, loads the map if it hasn't been loaded yet.
    */
   protected List getValues()
   {
      if (mValues == null)
         mValues = loadValues();
      return mValues;
   }
   
   /**
    * Gets the mappings from the resolver. They will be sorted alphabetically by URN.
    */
   protected List<AeURNMapping> loadValues()
   {
      Mappings mappings = AeEngineManagementFactory.getResolverService().getMappings(
              new GetMappingsRequest());
      
      List<AeURNMapping> values = new ArrayList<>();
      for(Mapping mapping : mappings.getMapping()) {
          values.add(new AeURNMapping(mapping.getName(), mapping.getValue()));
      }
      
      return values;
   }
   
   /**
    * Returns true if there are no mappings.
    */
   public boolean isEmpty()
   {
      return getURNMappingSize() == 0;
   }
}
 