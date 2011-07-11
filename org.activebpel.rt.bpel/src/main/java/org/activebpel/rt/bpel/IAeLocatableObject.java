//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAeLocatableObject.java,v 1.1 2005/08/18 21:35:51 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel; 

/**
 * Defines a common interface for bpel objects and variables, both of which are
 * referred to by location paths or ids. The location path is an xpath that
 * uniquely identifies the object within the source xml in a human readable way.
 * The location id is a unique id for that same node and is used for storage when
 * a lighter weight id is necessary.
 * 
 * For the most part, implementations will typically defer to their definition
 * objects to get the location path and id. These values are calculated when the
 * xml is deserialized into our definition objects and are the same for all 
 * instances of a given project.
 * 
 * The exception to this is when we create multiple objects from a single definition
 * object like the parallel forEach. In this case, the implementation objects will
 * have their own location paths and ids that differ from the defintion objects.
 */
public interface IAeLocatableObject
{
   /**
    * Gets the location for the object
    */
   public String getLocationPath();
   
   /**
    * Gets the location id for the object
    */
   public int getLocationId();
   
   /**
    * Returns <code>true</code> if and only if this object has a location id.
    * Must return <code>false</code> if <code>getLocationId()</code> will fail.
    */
   public boolean hasLocationId();
   
   /**
    * Setter for the location path
    * @param aPath
    */
   public void setLocationPath(String aPath);
   
   /**
    * Setter for the location id
    * @param aId
    */
   public void setLocationId(int aId);
   
   /**
    * Returns true if the object has had a custom location path set on it.
    */
   public boolean hasCustomLocationPath();
}
 