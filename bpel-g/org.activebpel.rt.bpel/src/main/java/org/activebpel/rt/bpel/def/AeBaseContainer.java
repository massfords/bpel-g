// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/AeBaseContainer.java,v 1.5 2006/08/18 19:50:30 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base class for def objects that have a collection of other objects which need
 * to be visited as a collection and/or individually.  
 */
abstract public class AeBaseContainer extends AeBaseDef
{
   /**
     * 
     */
    private static final long serialVersionUID = 5943567398933667871L;
/** HashMap used for associating names to objects */
   private LinkedHashMap mMap;

   /**
    * Private getter forces subclasses to use the collection mutator methods below.
    */
   private Map getMap()
   {
      if (mMap == null)
      {
         mMap = new LinkedHashMap();
      }
      return mMap;
   }

   /**
    * Gets the named object
    */
   protected Object get(String aKey)
   {
      return getMap().get(aKey);
   }

   /**
    * Adds a named object to this collection.
    */
   protected void add(String aKey, Object aValue)
   {
      getMap().put(aKey, aValue);
   }
   
   /**
    * Adds an object to the collection. This is here for subclasses that don't
    * need to have names associated with the objects.
    */
   protected void add(Object aValue)
   {
      getMap().put(aValue, aValue);
   }

   /**
    * Removes a named object from the collection.
    * 
    * @param aKey
    * @param aValue
    */
   protected void remove(String aKey, Object aValue)
   {
      getMap().remove(aKey);
   }
   
   /**
    * Removes an object from the collection.
    * 
    * @param aValue
    */
   protected void remove(Object aValue)
   {
      getMap().remove(aValue);
   }

   /**
    * Gets the size of the collection
    */
   public int getSize()
   {
      return getMap().size();
   }

   /**
    * Gets an iterator over the values in the collection.
    */
   public Iterator getValues()
   {
      return getMap().values().iterator();
   }
   
   /**
    * Returns true if the container is empty.
    */
   public boolean isEmpty()
   {
      return getSize() == 0;
   }
}
