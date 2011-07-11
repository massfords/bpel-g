//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/addressing/IAeStorageBackedURNResolver.java,v 1.1 2007/05/22 00:19:13 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.addressing; 

import org.activebpel.rt.bpel.urn.IAeURNResolver;

/**
 * Extends the URN resolver by adding a reload method that reloads the mappings
 * from its storage. 
 */
public interface IAeStorageBackedURNResolver extends IAeURNResolver
{
   /**
    * Reloads the mappings from the underlying storage layer.
    */
   public void reload();
}
 