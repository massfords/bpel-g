// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/support/IAeQueryParentDef.java,v 1.2 2006/08/18 19:50:30 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.activity.support;

/**
 * All defs that can parent a <code>query</code> construct should implement this interface.
 */
public interface IAeQueryParentDef
{
   /**
    * Called to set the query def child on the parent.
    * 
    * @param aQueryDef
    */
   public void setQueryDef(AeQueryDef aQueryDef);
   
   /**
    * Called to remove the query def from the parent.
    */
   public void removeQueryDef();
}
