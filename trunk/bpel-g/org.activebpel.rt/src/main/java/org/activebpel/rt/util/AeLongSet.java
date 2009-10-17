// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeLongSet.java,v 1.2 2004/10/29 21:02:36 PCollins Exp $
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
 * <code>long</code> values for members.
 */
public class AeLongSet extends AeTypedSet
{
   /**
    * Default constructor.
    */
   public AeLongSet()
   {
      this(new HashSet());
   }

   /**
    * Constructor.
    *
    * @param aSet The <code>Set</code> to use.
    */
   public AeLongSet(Set aSet)
   {
      super( aSet );
   }

   /*======================================================================
    * Convenience methods for long keys
    *======================================================================
    */

   /**
    * Returns <code>add(new Long(aLong))</code>.
    */
   public boolean add(long aLong)
   {
      return mSet.add(new Long(aLong));
   }

   /**
    * Returns <code>contains(new Long(aLong))</code>.
    */
   public boolean contains(long aLong)
   {
      return mSet.contains(new Long(aLong));
   }

   /**
    * Returns <code>remove(new Long(aLong))</code>.
    */
   public boolean remove(long aLong)
   {
      return mSet.remove(new Long(aLong));
   }
}
