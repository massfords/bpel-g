// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/catalog/IAeCatalogListener.java,v 1.1 2006/07/18 20:05:33 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.catalog;

/**
 * Provides notification for catalog deployment events.
 * TODO (cck) check that updates are dispatched to listeners
 */
public interface IAeCatalogListener
{
   /**
    * Notification of successful catalog deployment.
    * @param aEvent
    */
   public void onDeployment( AeCatalogEvent aEvent );
   
   /**
    * Notification of duplicate deployment attempt.
    * @param aEvent
    */
   public void onDuplicateDeployment( AeCatalogEvent aEvent );
   
   /**
    * Notification of catalog deployment removal.
    * @param aEvent
    */
   public void onRemoval( AeCatalogEvent aEvent );
}
