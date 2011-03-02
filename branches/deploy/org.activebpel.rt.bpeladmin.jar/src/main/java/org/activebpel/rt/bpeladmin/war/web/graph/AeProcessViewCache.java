// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/graph/AeProcessViewCache.java,v 1.2 2007/04/11 17:54:59 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.graph;

import java.util.LinkedHashMap;
import java.util.Map;

import org.activebpel.rt.bpeladmin.war.web.processview.AeProcessViewBase;

/**
 * Simple linked list map which removes the eldest entry when the number of
 * entries is &gt; 10.
 */
public class AeProcessViewCache extends LinkedHashMap
{
   /**
    * Returns a process view object from the cache.
    */
   public AeProcessViewBase get(String aKey)
   {
      return (AeProcessViewBase) super.get(aKey);
   }

   /**
    * Overrides method to limit sessions to 10.
    *
    * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
    */
   protected boolean removeEldestEntry(Map.Entry eldest)
   {
      return size() > 10;
   }
}