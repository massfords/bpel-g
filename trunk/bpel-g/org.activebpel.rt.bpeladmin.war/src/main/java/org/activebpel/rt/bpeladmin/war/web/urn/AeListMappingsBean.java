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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.bpeladmin.war.web.AeAbstractAdminBean;

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
   protected List loadValues()
   {
      Map map = getAdmin().getURNAddressResolver().getMappings();
      List list = new ArrayList();
      list.addAll(map.keySet());
      Collections.sort(list);
      
      List values = new ArrayList();
      int offset = 0;
      for (Iterator iter = list.iterator(); iter.hasNext(); offset++)
      {
         String urn = (String) iter.next();
         values.add(new AeURNMapping(urn, map.get(urn).toString()));
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
 