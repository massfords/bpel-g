//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/config/IAeStorageChangeListener.java,v 1.1 2005/04/22 21:26:51 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.config; 

import java.util.Map;

/**
 * Listener interface for objects interested in receiving notice of storage constants
 * changing after the engine starts. 
 */
public interface IAeStorageChangeListener
{

   /**
    * The Map contains name/value pairs that are used to override existing properties
    * in the storage config files.
    *  
    * @param aMap
    */
   public void storageConstantsChanged(Map aMap);

}
 