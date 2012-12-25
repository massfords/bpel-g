//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/multipart/parse/AeOpenTreeMap.java,v 1.2.4.1 2008/04/21 16:15:52 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.http.multipart.parse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.activebpel.rt.http.AeMessages;

/**
 * A map based on the <code>TreeMap</code> and <code>TreeSet</code> classes. Supports a one-to-many
 * relationship between keys and values -- one key may map to many values.
 * <p>
 * Constructors for this class allow specifying the class (that implements the <code>Set</code> interface)
 * to use as node values. It is that class that provides the "open" part of <code>AeOpenTreeMap</code>.
 * That class may contain any business logic concerning the addition and removal of values necessary to the
 * developer's task (for example: values remain ordered, <code>null</code> values not allowed, values cannot
 * be repeated, or values must be <code>String</code>s). <i>That class must be instantiatable through a
 * default constructor</i>.</p>
 * <p>
 * If the keys used in the <code>AeOpenTreeMap</code> do not implement the <code>Comparable</code>
 * interface or do not implement it in a manner <i>consistent with equals</i>, a <code>Comparator</code>
 * must be constructed to implement ordering that <b>is</b> <i>consistent with equals</i>. (For a precise
 * definition of <i>consistent with equals</i>, see the API documentation for
 * <code>java.lang.Comparable</code> or <code>java.util.Comparator</code>.)</p>
 * <p>
 * <b>Note that this implementation is not synchronized.</b>
 * </p>
 * @see java.lang.Comparable
 * @see java.util.Comparator
 * @see java.util.TreeMap
 */
public class AeOpenTreeMap extends TreeMap
{

   /** Type of <code>Set</code> to create to hold the values mapped to an individual key. */
   private Class mNodeType = null;

   /**
    * Constructor
    * <p>
    * Creates an open sorted map using <code>java.util.HashSet</code> for node values.
    * </p>
    */
   public AeOpenTreeMap()
   {
      super();
      mNodeType = (new HashSet()).getClass();
   }

   /**
    * Constructor
    * <p>
    * Creates an open sorted map using the specified comparator for sorting operations and
    * <code>java.util.HashSet</code> for node values.
    * </p>
    * @param aComparator <code>Comparator</code> to use for sorting operations on the map keys
    */
   public AeOpenTreeMap(Comparator aComparator)
   {
      super(aComparator);
      mNodeType = (new HashSet()).getClass();
   }

   /**
    * Constructor
    * <p>
    * Creates and populates initial values for the sorted map using <code>java.util.HashSet</code>.
    * </p>
    * @param aMap <code>Map</code> with initial values
    */
   public AeOpenTreeMap(Map aMap)
   {
      super(aMap);
      mNodeType = (new HashSet()).getClass();
   }

   /**
    * Constructor
    * <p>
    * Creates and populates initial values for the sorted map using <code>java.util.HashSet</code>.
    * </p>
    * @param aMap <code>Map</code> with initial values
    */
   public AeOpenTreeMap(SortedMap aMap)
   {
      super(aMap);
      mNodeType = (new HashSet()).getClass();
   }

   /**
    * Constructor Creates an open sorted map using the <code>Set</code> type specified for node values.
    * @param aNodeSet class implementing the <code>Set</code> interface to be used for nodes. This parameter
    *            is a <code>Set</code> and not a <code>Class</code> only to simplify sanity checks; it is
    *            only used to generate its <code>Class</code> object.
    */
   public AeOpenTreeMap(Set aNodeSet)
   {
      super();
      // Sanity checks
      if ( aNodeSet == null )
      {
         throw new NullPointerException(AeMessages.getString("nodeSet cannot be null.")); //$NON-NLS-1$
      }
      mNodeType = aNodeSet.getClass();
   }

   /**
    * Constructor,p> Creates an open sorted map using the <code>Set</code> type specified for node values
    * and the <code>Comparator</code> specified for ordering.
    * @param aComparator <code>Comparator</code> to use for sorting operations on the map keys
    * @param aNodeSet class implementing the <code>Set</code> interface to be used for nodes. This parameter
    *            is a <code>Set</code> and not a <code>Class</code> only to simplify sanity checks; it is
    *            only used to generate its <code>Class</code> object.
    */
   public AeOpenTreeMap(Comparator aComparator, Set aNodeSet)
   {
      super(aComparator);
      // Sanity checks
      if ( aNodeSet == null )
      {
         throw new NullPointerException(AeMessages.getString("nodeSet cannot be null.")); //$NON-NLS-1$
      }
      mNodeType = aNodeSet.getClass();
   }

   /**
    * Returns <code>true</code> if this map maps one of more keys to the specified value. More formally,
    * returns <code>true</code> if and only if this map contains at lease one mapping to a value
    * <code>v</code> such that <code>(value==null ? v==null : value.equals(v))</code>.
    * @param aValue value thats presence in this <code>Map</code> is to be tested.
    * @return <code>true</code> if the value is found in the mapping
    */
   public boolean containsValue(Object aValue)
   {
      boolean found = false;
      Iterator keys = keySet().iterator();
      Object key = null;
      Set node = null;
      Iterator vals = null;
      Object val = null;

      while ((!found) && keys != null && keys.hasNext())
      {
         key = keys.next();
         node = getNode(key);
         vals = node.iterator();
         while ((!found) && vals.hasNext())
         {
            val = vals.next();
            found = val.equals(aValue);
         }
      }
      return found;
   }

   /**
    * Returns an <code>Iterator</code> of the values to which the specified key maps. Returns
    * <code>null</code> if map contains no mapping for the key or if the key's <code>Set</code> of values
    * is empty. The method <code>containsKey</code> may be used to distinguish these two cases.
    * @param aKey key thats associated values are to be returned
    * @return values to which the key maps
    */
   public Object get(Object aKey)
   {
      Set node = getNode(aKey);
      Iterator vals = (node == null) ? null : (node.isEmpty()) ? null : node.iterator();
      return vals;
   }

   /**
    * Returns all values, including duplicates and <code>null</code>s (if allowed). The values in this
    * <code>Collection</code> will be ordered from those mapped to the first first key to those mapped to
    * the last key. If the implementation of <code>Set</code> used for the nodes is sorted (probably by
    * implementing <code>java.util.SortedSet</code>), then the values will be sub-ordered by their ordering
    * within the <code>Set</code>.
    * @see java.util.TreeMap#firstKey()
    * @see java.util.TreeMap#lastKey()
    * @return all values stored in the object
    */
   public Collection values()
   {
      List l = new ArrayList(size());
      Iterator keys = super.keySet().iterator();
      Iterator vals = null;
      while (keys.hasNext())
      {
         vals = ((Set)get(keys.next())).iterator();
         while (vals.hasNext())
         {
            l.add(vals.next());
         }
      }
      return l;
   }

   // //////////////////////////// New Methods ///////////////////////////////

   /**
    * Returns a <code>Set</code> (implemented in the class indicated at construction) of the values mapped
    * to the key specified.
    * @param aKey key to which the set of values are mapped
    * @return <code>Set</code> of the type indicated during the construction of this map. If the key is not
    *         mapped, <code>null</code> is returned.
    */
   public Set getNode(Object aKey)
   {
      return (Set)super.get(aKey);
   }

   /**
    * Counts the number of keys that have mappings to <code>Set</code>s. As of this version, keys that map
    * to empty sets are included in this count.
    * @return number of keys in this mapping
    */
   public int countKeys()
   {
      return super.size();
   }

   /**
    * Returns a <code>Collection</code> of the unique values contained in the map, ignoring
    * <code>null</code> values (if allowed). Uniqueness is determines by the return value of each object's
    * <code>hashCode</code> method.
    * @return unique values contained in the map
    */
   public Collection uniqueValues()
   {
      HashSet hs = new HashSet();
      Iterator keys = super.keySet().iterator();
      Iterator vals = null;
      Object val = null;
      while (keys.hasNext())
      {
         vals = ((Set)get(keys.next())).iterator();
         while (vals.hasNext())
         {
            val = vals.next();
            if ( val != null )
            {
               hs.add(val);
            }
         }
      }
      return hs;
   }

   /**
    * Returns the <code>String</code> representations of all the values associated with a key as a
    * <code>String</code> using the specified separator.
    * @param aKey key to which the set of values are mapped
    * @param aSeparator <code>String</code> placed between values
    * @returns <code>String</code> representations of all the values associated with a key
    */
   public String getAsList(String aKey, String aSeparator)
   {
      StringBuffer sb = new StringBuffer();
      Iterator it = (Iterator)get(aKey);
      while (it != null && it.hasNext())
      {
         if ( sb.length() != 0 )
            sb.append(aSeparator);
         sb.append(it.next().toString());
      }
      return sb.toString();
   }

   /**
    * Associates the specified value with the specified key in this map. <code>null</code> values for the
    * key or value are discarded.
    * @return <code>null</code>. Since no values are replaced, there is no use for the return value.
    */
   public Object put(Object aKey, Object aValue)
   {
      if ( aKey == null || aValue == null )
         return null;
      Set set = getNode(aKey);
      if ( set == null )
      {
         try
         {
            set = (Set)mNodeType.newInstance();
            super.put(aKey, set);
         }
         catch (InstantiationException ie)
         {
            return null;
         }
         catch (IllegalAccessException iae)
         {
            return null;
         }
      }
      set.add(aValue);
      return null;
   }

   /**
    * Removes the specified element from this set if it is present and the <code>Set</code> specified at
    * construction supports the <code>remove</code> operation. Returns <code>true</code> if the set
    * contained the specified element (or equivalently, if the set changed as a result of the call).
    * @see java.util.Set#remove(Object)
    * @param aKey key from which to remove the mapped value
    * @param aValue value to be remove from the key
    * @return <code>true</code> if the set changed as a result of the call
    */
   public boolean removeValueFromKey(Object aKey, Object aValue)
   {
      Set node = getNode(aKey);
      if ( node == null )
         return false;

      return node.remove(aValue);
   }
}
