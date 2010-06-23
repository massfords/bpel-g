//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/IAeURNStorage.java,v 1.2 2007/04/03 20:54:32 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage; 

import java.util.Map;


/**
 * Defines the methods for storing and retrieving URN mappings. The interface resembles
 * a Map.   
 */
public interface IAeURNStorage extends IAeStorage
{
   /**
    * Gets all of the mappings for URN to URL
    */
   public Map getMappings() throws AeStorageException;
   
   /**
    * Adds the mapping. If the mapping already exists in the db then it is updated.
    * 
    * @param aURN
    * @param aURL
    */
   public void addMapping(String aURN, String aURL) throws AeStorageException;
   
   /**
    * Removes the mapping. 
    * 
    * @param aURNArray
    */
   public void removeMappings(String[] aURNArray) throws AeStorageException;
}
 