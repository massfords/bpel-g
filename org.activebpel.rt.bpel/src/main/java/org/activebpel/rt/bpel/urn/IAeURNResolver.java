//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/urn/IAeURNResolver.java,v 1.2 2007/06/01 17:31:10 TZhang Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.urn; 

import java.util.Map;


/**
 * Used to resolve URN values to a URL.
 */
public interface IAeURNResolver
{
   /**
    * Gets the URL mapped to the URN.
    * 
    * @param aURN
    * @return URL value or the original URN if nothing was mapped to the URN.
    */
   public String getURL(String aURN);
   
   /**
    * Removes all of the mappings in the array.
    * 
    * @param aURNArray
    */
   public void removeMappings(String[] aURNArray);
   
   /**
    * Adds the urn to url mapping.
    * 
    * @param aURN
    * @param aURL
    */
   public void addMapping(String aURN, String aURL);
   
   /**
    * Checks if the resolver has the url mapping.
    * 
    * @param aURN
    */
   public boolean hasMapping(String aURN);
   
   /**
    * Gets all of the mappings.
    */
   public Map getMappings();
}
 