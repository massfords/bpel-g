//$Header$
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def; 

import java.util.Iterator;


/**
 * Interface for adding a catch or catchAll to a fault handler container.
 */
public interface IAeCatchParentDef
{
   /**
    * Gets an iterator of the list of 'catch' defs.
    */
   public Iterator getCatchDefs();
   
   /**
    * Gets the 'catchall' def.
    */
   public AeCatchAllDef getCatchAllDef();
   
   /**
    * Adds the fault handler (catch) to the collection.
    * @param aDef
    */
   public void addCatchDef(AeCatchDef aDef);

   /**
    * Setter for the default fault handler (catchall).
    * @param aDef
    */
   public void setCatchAllDef(AeCatchAllDef aDef);
}
 
