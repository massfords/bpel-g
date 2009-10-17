// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeIntSet.java,v 1.1 2004/10/29 21:02:36 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Wraps a <code>Set</code> with convenience methods to simplify using
 * <code>int</code> values for members.
 */
public class AeIntSet extends AeTypedSet
{
   /**
    * Default constructor.
    */
   public AeIntSet()
   {
      this(new HashSet());
   }

   /**
    * Constructor.
    *
    * @param aSet The <code>Set</code> to use.
    */
   public AeIntSet(Set aSet)
   {
      super( aSet );
   }

   /*======================================================================
    * Convenience methods for int keys
    *======================================================================
    */

   /**
    * Returns <code>add(new Integer(aInt))</code>.
    */
   public boolean add(int aInt)
   {
      return mSet.add(new Integer(aInt));
   }

   /**
    * Returns <code>contains(new Integer(aInt))</code>.
    */
   public boolean contains(int aInt)
   {
      return mSet.contains(new Integer(aInt));
   }

   /**
    * Returns <code>remove(new Integer(aInt))</code>.
    */
   public boolean remove(int aInt)
   {
      return mSet.remove(new Integer(aInt));
   }
}
